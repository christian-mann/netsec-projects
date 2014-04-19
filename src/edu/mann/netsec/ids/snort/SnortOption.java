package edu.mann.netsec.ids.snort;

import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;

import edu.mann.netsec.ids.snort.options.SnortConditionContent;
import edu.mann.netsec.ids.snort.options.SnortConditionICMPCode;
import edu.mann.netsec.ids.snort.options.SnortConditionICMPType;
import edu.mann.netsec.ids.snort.options.SnortConditionIPFragBits;
import edu.mann.netsec.ids.snort.options.SnortConditionIPFragOffset;
import edu.mann.netsec.ids.snort.options.SnortConditionIPID;
import edu.mann.netsec.ids.snort.options.SnortConditionIPSameIP;
import edu.mann.netsec.ids.snort.options.SnortConditionIPTOS;
import edu.mann.netsec.ids.snort.options.SnortConditionIPTTL;
import edu.mann.netsec.ids.snort.options.SnortConditionPayloadSize;
import edu.mann.netsec.ids.snort.options.SnortConditionTCPAck;
import edu.mann.netsec.ids.snort.options.SnortConditionTCPFlags;
import edu.mann.netsec.ids.snort.options.SnortConditionTCPSeq;
import edu.mann.netsec.packets.filter.PacketFilter;
import edu.mann.netsec.utils.Utils;

public abstract class SnortOption implements PacketFilter {

	public static SnortOption fromStrings(String key, final String value) throws SnortInvalidOptionException {
		switch(key) {
		case "ttl":
			return new SnortConditionIPTTL(value);
		case "tos":
			return new SnortConditionIPTOS(value);
		case "id":
			return new SnortConditionIPID(value);
		case "fragoffset":
			return new SnortConditionIPFragOffset(value);
//		case "ipoption":
//			return new SnortConditionIPOption(value);
		case "fragbits":
			return new SnortConditionIPFragBits(value);
		case "dsize":
			return new SnortConditionPayloadSize(value);
		case "flags":
			return new SnortConditionTCPFlags(value);
		case "seq":
			return new SnortConditionTCPSeq(value);
		case "ack":
			return new SnortConditionTCPAck(value);
		case "itype":
			return new SnortConditionICMPType(value);
		case "icode":
			return new SnortConditionICMPCode(value);
		case "content":
			return new SnortConditionContent(value);
		case "sameip":
			return new SnortConditionIPSameIP(value);
		default:
			throw new SnortInvalidOptionException(key + " is not a valid snort option key");
		}
	}

	public static List<SnortOption> parseRuleOptions(String s) throws SnortInvalidOptionException {

		List<SnortOption> rules = new LinkedList<>();
		
		if (s.charAt(0) == '(') {
			s = s.substring(1, s.length() - 1);
		}

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
		boolean pipeMode = false;
		Character lastChar = null; // only used in pipe mode
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
			} else if (stringMode && !backslash && !pipeMode) {
				if (c == '"') {
					stringMode = false;
				} else if (c == '\\') {
					backslash = true;
				} else if (c == '|') {
					pipeMode = true;
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
				case '|':
					sb.append('|');
					break;
				case ';':
					sb.append(';');
				case '"':
					sb.append('"');
					break;
				default:
					throw new SnortInvalidOptionException(in);
				}
			} else if (stringMode && !backslash && pipeMode) {
				if (lastChar == null) {
					if (c == ' ') {
						// pass, ignore
					} else if (c == '|') {
						pipeMode = false;
					} else {
						lastChar = c;
					}
				} else {
					// lastChar != null
					try {
						byte character = Utils.byteFromHex("" + lastChar + c);
						// byte to string
						// http://stackoverflow.com/questions/6684665/java-byte-array-to-string-to-byte-array
						sb.append(new String(new byte[]{character}));
						lastChar = null;
					} catch(IllegalArgumentException e) {
						throw new SnortInvalidOptionException("character "+c+"not expected here -- " + e);
					}
				}
			}
		}

		if (stringMode || lastChar != null) throw new SnortInvalidOptionException(in);

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
