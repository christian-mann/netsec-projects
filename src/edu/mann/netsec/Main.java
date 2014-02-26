package edu.mann.netsec;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.MutuallyExclusiveGroup;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.internal.ArgumentParserImpl;
import edu.mann.netsec.packets.EthernetPacket;
import edu.mann.netsec.packets.FilePacketSource;
import edu.mann.netsec.packets.NetworkPacketSource;
import edu.mann.netsec.packets.Packet;
import edu.mann.netsec.packets.PacketSource;
import edu.mann.netsec.packets.filter.AndPacketFilter;
import edu.mann.netsec.packets.filter.DstAddressPacketFilter;
import edu.mann.netsec.packets.filter.DstPortPacketFilter;
import edu.mann.netsec.packets.filter.PacketFilter;
import edu.mann.netsec.packets.filter.SandDPacketFilter;
import edu.mann.netsec.packets.filter.SorDPacketFilter;
import edu.mann.netsec.packets.filter.SrcAddressPacketFilter;
import edu.mann.netsec.packets.filter.SrcPortPacketFilter;

public class Main {

	public static final String filename = "dat/http.dat";
	
	public static void main(String[] args) throws ArgumentParserException, IOException {
		Namespace options = parseArguments(args);
		System.out.println(options);
		
		PacketSource ps;
		if (options.get("read_from") != null) {
			ps = (PacketSource)options.get("read_from");
		} else if (options.get("interface") != null) {
			ps = (PacketSource)options.get("interface");
		} else {
			ps = (PacketSource) new NetworkPacketSource("eth1");
		}
		
		// combine packet filters
		List<PacketFilter> filters = new ArrayList<PacketFilter>();
		filters.add((PacketFilter)options.get("src"));
		filters.add((PacketFilter)options.get("dst"));
		filters.add((PacketFilter)options.get("sord"));
		filters.add((PacketFilter)options.get("sandd"));
		filters.add((PacketFilter)options.get("sport"));
		filters.add((PacketFilter)options.get("dport"));
		PacketFilter pf = new AndPacketFilter(filters.toArray(new PacketFilter[filters.size()]));
		
		int cPackets = 0;
		PrintStream output = (PrintStream)options.get("output");
		for (ByteBuffer bb : ps) {
			Packet p = new EthernetPacket(bb);
			if (pf.allowPacket(p)) {
				do {
					if (options.get("type") == null || p.getType().equals(options.get("type"))) {
						output.println(p.prettyPrint());
					}
					p = p.childPacket();
				} while (p != null);
			}
			
			output.println("#####################");
			output.println();
			
			cPackets += 1;
			
			if (cPackets >= options.getInt("count")) {
				break;
			}
		}
		
	}

	private static Namespace parseArguments(String[] args)
			throws ArgumentParserException, IOException {
		ArgumentParserImpl ap = (ArgumentParserImpl)ArgumentParsers.newArgumentParser("", false);
		ap.addArgument("-c", "--count")
			.metavar("COUNT")
			.type(Integer.class)
			.help("Exit after reading COUNT packets.");
		
		MutuallyExclusiveGroup groupPacketSource = ap.addMutuallyExclusiveGroup();
		groupPacketSource.addArgument("-r", "--read-from")
			.metavar("FILE")
			.setDefault(new FilePacketSource(filename))
			.type(FilePacketSource.class)
			.help("Read packets from FILE (reads from network by default).");
		groupPacketSource.addArgument("-i", "--interface")
			.help("Interface to read from. Invalid if -r is set.");
		
		ap.addArgument("-o", "--output")
			.metavar("FILE")
			.type(PrintStream.class)
			.setDefault(System.out)
			.help("Save output to FILE");
		ap.addArgument("-t", "--type")
			.choices("eth", "ip", "icmp", "tcp", "udp")
			.help("Print only packets of the specified type");
		ap.addArgument("-h", "--header-only")
			.action(Arguments.storeTrue())
			.help("Print header info only as specified by -t");
		ap.addArgument("-s", "--src")
			.metavar("saddr")
			.type(SrcAddressPacketFilter.class)
			.help("Print only packets with source address equal to saddr");
		ap.addArgument("-d", "--dst")
			.metavar("daddr")
			.type(DstAddressPacketFilter.class)
			.help("Print only packets with destination address equal to daddr");
		ap.addArgument("--sord")
			.metavar("saddress,daddress")
			.type(SorDPacketFilter.class)
			.help("Print only packets where the source address matches saddress or the destination address matches daddress. Comma separated.");
		ap.addArgument("--sandd")
			.nargs(2)
			.type(SandDPacketFilter.class)
			.metavar("saddress,daddress")
			.help("Print only packets where the source address matches saddress and the destination address matches daddress. Comma separated.");
		ap.addArgument("--sport")
			.type(SrcPortPacketFilter.class)
			.setDefault(new SrcPortPacketFilter(0, 65535))
			.metavar("port1-port2")
			.help("Print only packets where the source port is in the range [port1, port2]. Hyphen separated.");
		ap.addArgument("--dport")
			.type(DstPortPacketFilter.class)
			.setDefault(new DstPortPacketFilter(0, 65535))
			.metavar("port1-port2")
			.help("Print only packets where the destination port is in the range [port1, port2]. Hyphen separated.");
		
		Namespace options = ap.parseArgs(args);
		return options;
	}

}
