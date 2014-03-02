package edu.mann.netsec;

import java.nio.ByteBuffer;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.internal.ArgumentParserImpl;
import edu.mann.netsec.packets.FilePacketSource;
import edu.mann.netsec.packets.NetworkPacketSink;
import edu.mann.netsec.packets.PacketSink;
import edu.mann.netsec.packets.PacketSource;

public class MainGenerator {

	/**
	 * @param args
	 * @throws ReflectiveOperationException if the packet driver fails to work
	 * @throws ArgumentParserException if arguments are invalid
	 */
	public static void main(String[] args) throws ReflectiveOperationException, ArgumentParserException {
		Namespace options = parseArguments(args);
		
		PacketSource ps = (PacketSource)options.get("filename");
		PacketSink sink = (PacketSink)options.get("interface");
		for (ByteBuffer bb : ps) {
			System.out.println("Writing packet of length " + bb.remaining());
			sink.writePacket(bb);
		}
	}

	private static Namespace parseArguments(String[] args) throws ReflectiveOperationException, ArgumentParserException {
		ArgumentParserImpl ap = (ArgumentParserImpl)ArgumentParsers.newArgumentParser("");
		ap.addArgument("filename")
			.metavar("FILE")
			.type(FilePacketSource.class)
			.help("File to read packets from");
		
		ap.addArgument("-i", "--interface")
			.metavar("INTERFACE")
			.type(NetworkPacketSink.class)
			.help("Interface to write packets onto")
			.setDefault(NetworkPacketSink.fromPrompt());
		
		return ap.parseArgs(args);
	}
}
