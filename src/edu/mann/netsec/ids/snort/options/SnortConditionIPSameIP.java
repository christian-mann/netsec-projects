package edu.mann.netsec.ids.snort.options;

import edu.mann.netsec.ids.snort.SnortCondition;
import edu.mann.netsec.packets.IPPacket;
import edu.mann.netsec.packets.Packet;

public class SnortConditionIPSameIP extends SnortCondition {

	@Override
	public String toString() {
		return "SnortConditionIPSameIP[]";
	}

	public SnortConditionIPSameIP(String value) {
		super();
	}

	@Override
	public boolean allowPacket(Packet p) {
		IPPacket ipPacket = (IPPacket)p.ancestorByType("ip");
		if (ipPacket == null) {
			// this isn't even over IP
			return false;
		} else {
			return (ipPacket.srcAddress.equals(ipPacket.dstAddress));
		}
	}

}
