package edu.mann.netsec.ids.snort;

import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;

import edu.mann.netsec.ids.snort.options.SnortOptionFragBits;
import edu.mann.netsec.ids.snort.options.SnortOptionIPOption;
import edu.mann.netsec.packets.ICMPPacket;
import edu.mann.netsec.packets.IPPacket;
import edu.mann.netsec.packets.Packet;
import edu.mann.netsec.packets.TCPPacket;
import edu.mann.netsec.packets.filter.PacketFilter;

public abstract class SnortOption implements PacketFilter {

	public static SnortOption fromStrings(String key, final String value) {
		switch(key) {
		case "ttl":
			return new SnortOptionIPTTL(value);
		case "tos":
			return new SnortOptionIPTOS(value);
		case "id":
			return new SnortOptionIPID(value);
		case "fragoffset":
			return new SnortOptionIPFragOffset(value);
		case "ipoption":
			return new SnortOptionIPOption(value);
		case "fragbits":
			return new SnortOptionFragBits(value);
		case "dsize":
			return new SnortOptionDSize(value);
		case "flags":
			return new SnortOptionTCPFlags(value);
		case "seq":
			return new SnortOptionTCPSeq(value);
		case "ack":
			return new SnortOptionTCPAck(value);
		case "itype":
			return new SnortOptionICMPType(value);
		case "icode":
			return new SnortOptionICMPCode(value);
		case "content":
			return new SnortOptionContent(value);
		case "sameip":
			return new SnortOptionIPSameIP(value);
		}
	}

	public static List<SnortOption> parseRuleOptions(String s) throws SnortInvalidOptionException {

		List<SnortOption> rules = new LinkedList<>();

		String next = s;
		while (!next.equals("")) {

			// get the first key
			String key = next.substring(0, next.indexOf(":"));
			next = next.substring(key.length() + 1);
			// get the first value
			Pair<String, String> valuePair = extractWithQuotes(next);
			String value = valuePair.getValue0();
			next = valuePair.getValue1();

			// add to list
			rules.add(SnortOption.fromStrings(key, value));

			next = chompLeft(next);
			// eat semicolon
			next = next.substring(1);
			next = chompLeft(next);
		}
		return rules;
	}

	public static Pair<String, String> extractWithQuotes(String in) throws SnortInvalidOptionException {
		StringBuilder sb = new StringBuilder();
		boolean stringMode = false;
		boolean backslash = false;
		int i = 0;
		for (i = 0; i < in.length(); i++) {
			char c = in.charAt(i);

			if (!stringMode) {
				if (c == '"') {
					stringMode = true;
				} else if (c == ';') {
					break;
				} else {
					sb.append(c);
				}
			} else if (stringMode && !backslash) {
				if (c == '"') {
					stringMode = false;
				} else if (c == '\\') {
					backslash = true;
				} else {
					sb.append(c);
				}
			} else if (stringMode && backslash) {
				backslash = false;
				// parse escape character
				switch(c) {
				case 't':
					sb.append('\t');
					break;
				case 'n':
					sb.append('\n');
					break;
				case '\\':
					sb.append('\\');
					break;
				case '"':
					sb.append('"');
					break;
				default:
					throw new SnortInvalidOptionException(in);
				}
			}
		}

		if (stringMode) throw new SnortInvalidOptionException(in);

		return new Pair<String, String>(sb.toString(), in.substring(i));
	}

	public static String chompLeft(String in) {
		for (int i = 0; i < in.length(); i++) {
			if (in.codePointAt(i) != ' ') {
				return in.substring(i);
			}
		}
		return "";
	}
}
