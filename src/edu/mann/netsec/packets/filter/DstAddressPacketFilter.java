package edu.mann.netsec.packets.filter;

import edu.mann.netsec.packets.IPAddress;
import edu.mann.netsec.packets.IPPacket;
import edu.mann.netsec.packets.Packet;

public class DstAddressPacketFilter implements PacketFilter {

	@Override
	public String toString() {
		return "DstAddressPacketFilter[" + dstIP + "]";
	}

	private IPAddress dstIP;

	public DstAddressPacketFilter(String s) {
		this(new IPAddress(s));
	}
	
	public DstAddressPacketFilter(IPAddress ipAddress) {
		this.dstIP = ipAddress;
	}

	@Override
	public boolean allowPacket(Packet p) {
		// look for the ip packet
		while (p.getType() != "ip") {
			p = p.childPacket();
			if (p == null) return false;
		}
		if (p instanceof IPPacket) {
			IPPacket ipp = (IPPacket)p;
			if (ipp.dstAddress.equals(this.dstIP)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

}
