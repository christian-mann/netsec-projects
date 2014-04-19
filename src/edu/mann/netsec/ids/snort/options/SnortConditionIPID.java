package edu.mann.netsec.ids.snort.options;

import edu.mann.netsec.ids.snort.SnortCondition;
import edu.mann.netsec.packets.IPPacket;
import edu.mann.netsec.packets.Packet;

public class SnortConditionIPID extends SnortCondition {

	@Override
	public String toString() {
		return "SnortConditionIPID[id=" + id + "]";
	}

	private int id;

	public SnortConditionIPID(String value) {
		this.id = Integer.parseInt(value);
	}

	@Override
	public boolean allowPacket(Packet p) {
		IPPacket ip = (IPPacket) p.ancestorByType("ip");
		return ip != null && ip.identification == this.id;
	}

}
