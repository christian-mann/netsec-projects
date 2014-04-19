package edu.mann.netsec;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.MutuallyExclusiveGroup;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.internal.ArgumentParserImpl;
import net.sourceforge.argparse4j.internal.HelpScreenException;
import edu.mann.netsec.ids.snort.SnortInvalidOptionException;
import edu.mann.netsec.ids.snort.SnortInvalidRuleException;
import edu.mann.netsec.ids.snort.SnortRuleList;
import edu.mann.netsec.packets.EthernetPacket;
import edu.mann.netsec.packets.FilePacketSource;
import edu.mann.netsec.packets.NetworkPacketSource;
import edu.mann.netsec.packets.Packet;
import edu.mann.netsec.packets.PacketSource;
import edu.mann.netsec.packets.TCPPacket;

/**
 * Starts the IDS component of the project.	
 * @author christian
 *
 */
public class MainIDS {
	
	public static void main(String[] args) throws ReflectiveOperationException, IOException, SnortInvalidRuleException, SnortInvalidOptionException {
		Namespace options = parseArguments(args);
		
		SnortRuleList rules = SnortRuleList.parseFile((File) options.get("ruleFile"));
		
		PacketSource ps;
		if (options.get("read_from") != null) {
			ps = (PacketSource)options.get("read_from");
		} else if (options.get("interface") != null) {
			ps = (PacketSource)options.get("interface");
		} else {
			ps = NetworkPacketSource.fromPrompt();
		}
		
		for (ByteBuffer bb : ps) {
			Packet p = new EthernetPacket(bb);
			Packet subPacket = p;
			while (subPacket != null) {
				System.out.println(subPacket.toString());
				rules.handlePacket(subPacket);
				subPacket = subPacket.childPacket();
			}
		}
	}

	private static Namespace parseArguments(String[] args) {
		ArgumentParserImpl ap = (ArgumentParserImpl)ArgumentParsers.newArgumentParser("", true);
		ap.addArgument("-g", "--debug")
			.action(Arguments.storeTrue())
			.help("Enable debug output.");
		ap.addArgument("-v", "--verbose")
			.action(Arguments.storeTrue())
			.help("Enables verbose output.");
		ap.addArgument("-c", "--count")
			.metavar("COUNT")
			.type(Integer.class)
			.help("Exit after printing COUNT packets.");
		
		MutuallyExclusiveGroup groupPacketSource = ap.addMutuallyExclusiveGroup("Packet Source");
		groupPacketSource.addArgument("-r", "--read_from")
			.metavar("FILE")
			.type(FilePacketSource.class)
			.help("Read packets from FILE (reads from network by default)");
		groupPacketSource.addArgument("-i", "--interface")
			.help("Interface to read from. Invalid if -r is set.");
		
		ap.addArgument("-o", "--output")
			.metavar("FILE")
			.type(PrintStream.class)
			.help("Save output to FILE.");
		
		ap.addArgument("ruleFile")
			.metavar("RULEFILE")
			.type(Arguments.fileType().verifyCanRead())
			.help("Use RULEFILE for IDS (Snort) rules.");
		
		try {
			Namespace options = ap.parseArgs(args);
			return options;
		} catch (HelpScreenException e) {
			System.exit(0);
		} catch (ArgumentParserException e) {
			ap.printHelp(new PrintWriter(System.err, true));
			System.exit(5);
		}
		
		return null;
	}
	
}
