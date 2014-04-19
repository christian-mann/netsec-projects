package edu.mann.netsec.ids.snort.options;

import edu.mann.netsec.ids.snort.SnortCondition;
import edu.mann.netsec.packets.IPPacket;
import edu.mann.netsec.packets.Packet;

public class SnortConditionIPTTL extends SnortCondition {

	@Override
	public String toString() {
		return "SnortConditionIPTTL[ttl=" + ttl + "]";
	}

	private int ttl;

	public SnortConditionIPTTL(String value) {
		this.ttl = Integer.parseInt(value);
	}

	@Override
	public boolean allowPacket(Packet p) {
		IPPacket ip = (IPPacket) p.ancestorByType("ip");
		return ip != null && ip.timeToLive == this.ttl;
	}

}
