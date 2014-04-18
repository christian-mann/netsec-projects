package edu.mann.netsec;

import java.io.PrintStream;
import java.io.PrintWriter;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.MutuallyExclusiveGroup;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.internal.ArgumentParserImpl;
import edu.mann.netsec.packets.FilePacketSource;

/**
 * Starts the IDS component of the project.	
 * @author christian
 *
 */
public class MainIDS {
	
	public static void main(String[] args) {
		Namespace options = parseArguments(args);
		
	}

	private static Namespace parseArguments(String[] args) {
		ArgumentParserImpl ap = (ArgumentParserImpl)ArgumentParsers.newArgumentParser("", true);
		ap.addArgument("-g", "--debug")
			.action(Arguments.storeTrue())
			.help("Enable debug output.");
		ap.addArgument("-c", "--count")
			.metavar("COUNT")
			.type(Integer.class)
			.help("Exit after printing COUNT packets.");
		
		MutuallyExclusiveGroup groupPacketSource = ap.addMutuallyExclusiveGroup("Packet Source");
		groupPacketSource.addArgument("-f", "--file")
			.metavar("FILE")
			.type(FilePacketSource.class)
			.help("Read packets from FILE (reads from network by default");
		groupPacketSource.addArgument("-i", "--interface")
			.help("Interface to read from. Invalid if -r is set.");
		
		ap.addArgument("-o", "--output")
			.metavar("FILE")
			.type(PrintStream.class)
			.help("Save output to FILE.");
		
		ap.addArgument("ruleFile")
			.metavar("RULEFILE")
			.help("Use RULEFILE for IDS (Snort) rules.");
		
		try {
			Namespace options = ap.parseArgs(args);
			return options;
		} catch (ArgumentParserException e) {
			ap.printHelp(new PrintWriter(System.err, true));
			System.exit(5);
		}
		
		return null;
	}
	
}
