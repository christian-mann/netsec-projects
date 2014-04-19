package edu.mann.netsec.ids.snort.options;

import edu.mann.netsec.ids.snort.SnortCondition;
import edu.mann.netsec.packets.ICMPPacket;
import edu.mann.netsec.packets.Packet;

public class SnortConditionICMPType extends SnortCondition {

	private byte type;

	public SnortConditionICMPType(String value) {
		this.type = Byte.parseByte(value);
	}

	@Override
	public boolean allowPacket(Packet p) {
		ICMPPacket icmp = (ICMPPacket) p.ancestorByType("icmp");
		return icmp != null && icmp.type == this.type;
	}

	@Override
	public String toString() {
		return "SnortConditionICMPType[type=" + type + "]";
	}

}
