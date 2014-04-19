package edu.mann.netsec.ids.snort.options;

import edu.mann.netsec.ids.snort.SnortCondition;
import edu.mann.netsec.packets.IPPacket;
import edu.mann.netsec.packets.Packet;

public class SnortConditionIPTOS extends SnortCondition {

	@Override
	public String toString() {
		return "SnortConditionIPTOS[tos=" + tos + "]";
	}

	private int tos;

	public SnortConditionIPTOS(String value) {
		this.tos = Integer.parseInt(value);
	}

	@Override
	public boolean allowPacket(Packet p) {
		IPPacket ip = (IPPacket) p.ancestorByType("ip");
		return ip != null && ip.typeOfService == this.tos;
	}

}
