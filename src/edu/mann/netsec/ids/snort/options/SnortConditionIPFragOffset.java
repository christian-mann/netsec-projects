package edu.mann.netsec.ids.snort.options;

import edu.mann.netsec.ids.snort.SnortCondition;
import edu.mann.netsec.packets.IPPacket;
import edu.mann.netsec.packets.Packet;

public class SnortConditionIPFragOffset extends SnortCondition {

	@Override
	public String toString() {
		return "SnortConditionIPFragOffset[fragOffset=" + fragOffset + "]";
	}

	private int fragOffset;

	public SnortConditionIPFragOffset(String value) {
		this.fragOffset = Integer.parseInt(value);
	}

	@Override
	public boolean allowPacket(Packet p) {
		IPPacket ip = (IPPacket) p.ancestorByType("ip");
		return ip != null && ip.fragmentOffset == this.fragOffset;
	}

}
