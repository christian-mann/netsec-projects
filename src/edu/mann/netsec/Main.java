package edu.mann.netsec;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.MutuallyExclusiveGroup;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.internal.ArgumentParserImpl;
import edu.mann.netsec.packets.EthernetPacket;
import edu.mann.netsec.packets.IPAddress;
import edu.mann.netsec.packets.Packet;
import edu.mann.netsec.packets.PacketSource;
import edu.mann.netsec.utils.FilePacketSource;

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
		
		int cPackets = 0;
		PrintStream output = (PrintStream)options.get("output");
		
		for (ByteBuffer bb : ps) {
			Packet p = new EthernetPacket(bb);
			do {
				if (options.get("type") == null || p.getType().equals(options.get("type"))) {
					output.println(p.prettyPrint());
				}
				p = p.childPacket();
			} while (p != null);
			
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
			.type(IPAddress.class)
			.help("Print only packets with source address equal to saddr");
		ap.addArgument("-d", "--dst")
			.metavar("daddr")
			.type(IPAddress.class)
			.help("Print only packets with destination address equal to daddr");
		ap.addArgument("--sord")
			.nargs(2)
			.metavar("saddress", "daddress")
			.help("Print only packets where the source address matches arg1 or the destination address matches arg2");
		ap.addArgument("--sandd")
			.nargs(2)
			.metavar("saddress", "daddress")
			.help("Print only packets where the source address matches arg1 and the destination address matches arg2");
		ap.addArgument("--sport")
			.nargs(2)
			.type(Integer.class)
			.setDefault(0, 65535)
			.metavar("port1", "port2")
			.help("Print only packets where the source port is in the range port1-port2");
		ap.addArgument("--dport")
			.nargs(2)
			.type(Integer.class)
			.setDefault(0, 65535)
			.metavar("port1", "port2")
			.help("Print only packets where the destination port is in the range port1-port2");
		
		Namespace options = ap.parseArgs(args);
		return options;
	}

}
