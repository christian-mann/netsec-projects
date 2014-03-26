============================
Network Security - Project 1
============================

Christian Mann
March 3 2014

Design
======

Capturing
---------
To work with raw packets, the instructor created a JNI wrapper to libpcap and libnet (named ``SimplePacketDriver``) that I used to interface almost directly with the network card. Unfortunately, the class was in the default package, and Java does not allow imports from a package to the default package. The only way to solve this issue was reflection, so that is what I used. I wrapped any calls I needed in another class, called ``SPDDriver``, which proxies the majority of its calls to an instance of ``SimplePacketDriver`` via reflection. In this way all of the ugliness is hidden in one corner, just as it should be.

Dissecting
----------

Class hierarchy is one of the first design considerations that emerges when embarking on a project like this one. Clearly a Packet will be either a superclass or an interface that other types of packets extend from or implement, but should these packets inherit from each other in a long convoluted chain? Instead of determining the solution to this problem, I worked on something else for a while.

The obvious choice for a class structure would have been to have each layer inherit from the preceding layer -- e.g. ``TCPPacket`` would subclass ``IPPacket``. However, there is nothing that dictates that a TCP packet must be carried over IP (RFC 793#1.4p3). In fact, TCP is regularly carried over both IPv4 and IPv6. Therefore it seems more natural to adopt the idea of one layer "carrying" or encapsulating another layer, each packet with its payload packet.

To this end, therefore, each packet type merely inherited from a base ``Packet`` class, and contained a method ``childPacket()`` that returned its payload as a ``Packet``, or ``null`` if it did not contain any additional data. Each packet type is responsible for determining what type its subpacket is based on demultiplexing keys contained within its header, e.g. IP has a ``protocol`` field with clearly defined values, and TCP has a similar idea with ports.

In this way one could conceivably carry such unusual configurations as UDP over HTTP, or TCP over DNS, if the demultiplexing fields supported such curiosities. In order to easily identify the protocol in use, a ``getType()`` method is implemented in each of the ``Packet`` classes that returns a lowercase string describing the packet -- e.g. ``"eth"`` or ``"tcp"``.

Lastly, one packet type is implemented for representing raw data, such as telnet data. There is a ``RawPacket`` class that serves as a catch-all and a default value when no other dissector is registered.

IP Reassembly
-------------

When a network's Maximum Transmission Unit (MTU) is smaller than the size of a packet, the network layer (IP) *fragments* the packet into smaller subpackets, each fitting within the network MTU. There are three fields in the IP protocol to accomplish this: One ``identification`` field, that serves to correlate related packets; one ``fragmentOffset`` field, that denotes where in the overall packet this subpacket should be placed; and one ``moreFragments`` field, that denotes whether this is the last fragment for the packet. Therefore if either the ``moreFragments`` bit is set or the ``fragmentOffset`` field is greater than zero, the packet is actually a fragment.

This packet capture tool uses the above method to determine whether a given packet is a fragment. If it discovers one (see ``EthernetPacket#childPacket()``), then it registers it with the packet reassembly facility and then checks whether any fully-assembled packets are ready.

In order to assemble packets, a new ``IPFragmentQueue`` is instantiated for each new (``srcIP``, ``dstIP``, ``identification``, ``protocol``) tuple seen (see ``IPQueue#matches(IPPacket)``). When a packet is seen that matches an existing queue, it adds it to the queue. Once the final packet has been seen (by the above metric), the queue is marked as *determined*. When a queue receives a request to reassemble its packet, it starts by checking whether it has seen its final packet, and then checks whether there are any unresolved gaps between packets. If both of these conditions are false, then it allocates space according to ``packetSize = finalPacket.totalSize + finalPacket.fragmentOffset``. If this is greater than ``65535``, the maximum IP packet size, then it enters an error case according to the given specification. Otherwise, it reassembles the packet by copying the fragments into the allocated buffer in order of receipt. This matches Linux semantics; this could be changed to be reversed to match Windows semantics. After copying these fragments, it constructs a new IP packet from the reassembled datagram and passes it to the higher level, whether that's TCP, UDP or just ``RawPacket``.

In order to prevent Jolt attacks, each packet has a timestamp associated with it. In the event that a packet takes too long to fully arrive at the host, it can be aborted, in order to preserve resources. However, if this timeout does not exactly match the host's timeout, the IDS could fall prey to an IDS evasion attack whereby the packets that the IDS sees are not the exact packets that the host sees. To this end, each IP packet is tagged with a field denoting whether it has been reassembled, and whether the packets overlapped. In the future this could be expanded to also include a field marking when time ran out on reassembly.

File I/O
--------
The program is able to read packets from the data files given to us by the instructor, but only in the exact format given -- errant spaces will often break its parsing.

Perhaps more usefully, it is also able to read packet data from ``.pcap`` files. There is no need to explicitly specify which on the command line; the program will determine this from the header on each packet. It also determines whether the file is little-endian or big-endian from the header and adjusts accordingly.

As for output to a file, that is only performed in the format given by the data files. There may be errant spaces in the output that were not present in the input.

When the program is running in generator mode, it only outputs to the screen the size of the packets written.

Printing
--------
I was not content with simplicity for this section; I wanted to make it easy to add new dissectors to support new packet types in the future. Thus I worked to implement the tabular output formatting of headers that was specified in the project assignment in a general way.

I created a class ``GridBox`` to represent a cell in the table. It accepted a string and a width in its constructor and attempted to typeset the string in the width provided; it eventually determined a minimum height required to display the entire argument. These instances of ``GridBox`` are given to a ``GridFormatter``, along with a width to wrap the row at. The ``GridFormatter`` then coordinates which component ``GridBox`` goes on which row, and coordinates them such that all of the boxes on the same row are the same height. With this infrastructure in place, it is able to construct a beautiful table of each cell, laid out perfectly -- almost. The one major limitation is that if a cell would span a wrap boundary, the ``GridFormatter`` is not clever enough to actually wrap it, and so it throws an error.

Of course, the ``GridFormatter`` went through extensive testing, though no unit tests were written.

Each packet class uses a ``GridFormatter`` instance to construct a human-readable representation of itself, by passing in its coordinates. The only exception to this scheme is ``RawPacket``, which constructs a representation of itself that looks similar to the output of ``hexdump -C``: 16 hex values, separated by space, then the 16 ASCII characters represented by these bytes (or dots for unprintable characters). This repeats itself until the packet is exhausted.

One problem arose with this implementation for instances of ``ARPPacket``: the fields did not have sizes that fit into any wrapping scheme -- nothing rounded to 32 or 16 bits. Thus some fields were left blank. In the future, one could improve the ``GridFormatter`` class to support this by modifying or adding new instances of ``GridBox``.

There is a rather inelegant section of code that is used to implement the ``-h`` command-line argument, which requires that the system print all header info up until the packet type given in the ``-t`` argument; e.g. if the string "ip" was given and a TCP/IP/Ethernet packet came in, everything up to and including the IP header is printed, but not the TCP header or its payload. In order to do this, every subpacket is checked against the ``TypePacketFilter`` and only printed if it matches. Because ``PacketFilter#allowPacket`` is recursive, this has the correct semantic.

Filtering
---------
I constructed a very structured approach to filtering. There are a few "base" filter classes, such as ``SrcAddressPacketFilter`` that operate on a packet and determine whether, in this case, the source address matches the argument given in the constructor. These all implement the ``PacketFilter`` interface, which contains the ``boolean allowPacket(Packet)`` method.

I then created a class ``AndPacketFilter`` that takes :math:`N` packet filters. Its ``allowPacket`` method returns the logical conjunction of each of its packet filter arguments. I also created a ``OrPacketFilter`` similarly, though it retufrns the *disjunction* of its arguments. In the future, a ``NotPacketFilter`` could also be created to invert the operation of its argument. In this way, very complex packet filters can be constructed.

These packet filters were used to provide the ``--sord`` and ``--sand`` command-line arguments. The infrastructure given above is not exposed to the user, but could be in future revisions.

Of course, the ``--sport`` and ``--dport`` command-line arguments were implemented similarly, with ``SrcPortPacketFilter`` and ``DstPortPacketFilter`` respectively.

Lastly, a ``TypePacketFilter`` ensures that any packets that pass through it either are of a certain type (e.g. "ip") or contain a packet that is of that type, or contain a packet that contains a packet that is of that type, etc. This was used to provide the ``-t`` or ``--packet-type`` command-line arguments.

All given command-line packet filters are used to construct a ``AndPacketFilter``, which is used to filter packets at the top-level.

Example Traffic
===============
The telnet session is captured at ``doc/telnet.out``. The client IP address was ``192.168.1.66``. The username used to log in was ``group15`` and the password was ``192.168.1.66``.

The failed ftp login is captured at ``doc/ftp-failed.out``. The client IP address was ``192.168.1.62``.

The file transferred to ``192.168.1.42`` was named ``FTP-GROUP14.NFO``. It was transferred by client IP ``192.168.1.62`` using username ``group14`` and password ``192.168.1.62``. The logs are at ``doc/ftp-success.out``.

The web server transferred a file at address ``/cs7493/`` with content located at ``doc/cs7493.htm``. It was transferred to client at IP ``192.168.1.22``. These logs are located at ``doc/http.out``.

Because I did not implement a DNS dissector, I used Wireshark to extract information from the DNS records. The DNS server at ``192.168.1.14`` reports ``iodine`` to be ``iodine.ssac.utulsa.edu`` at ``192.168.1.62``, and the DNS server at ``192.168.1.46`` reports ``hydrogen`` to be ``hydrogen.group1.ssac.utulsa.edu`` at ``192.168.1.10``.

The ARP request/reply is available at ``doc/arp.out``. The reported MAC address of 192.168.1.200 is ``00:22:15:61:E3:F4``.
