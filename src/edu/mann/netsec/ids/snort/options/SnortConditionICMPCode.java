package edu.mann.netsec.ids.snort.options;

import edu.mann.netsec.ids.snort.SnortCondition;
import edu.mann.netsec.ids.snort.SnortOption;
import edu.mann.netsec.packets.ICMPPacket;
import edu.mann.netsec.packets.Packet;

public class SnortConditionICMPCode extends SnortCondition {

	private byte code;

	@Override
	public String toString() {
		return "SnortConditionICMPCode[code=" + code + "]";
	}

	public SnortConditionICMPCode(String value) {
		this.code = Byte.parseByte(value);
	}

	@Override
	public boolean allowPacket(Packet p) {
		ICMPPacket icmp = (ICMPPacket)p.ancestorByType("icmp");
		return icmp != null && icmp.code == this.code;
	}

}
