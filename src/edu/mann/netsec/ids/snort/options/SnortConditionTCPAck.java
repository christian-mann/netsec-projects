package edu.mann.netsec.ids.snort.options;

import edu.mann.netsec.ids.snort.SnortCondition;
import edu.mann.netsec.packets.Packet;
import edu.mann.netsec.packets.TCPPacket;

public class SnortConditionTCPAck extends SnortCondition {

	@Override
	public String toString() {
		return "SnortConditionTCPAck[ack=" + ack + "]";
	}

	private int ack;

	public SnortConditionTCPAck(String value) {
		this.ack = Integer.parseInt(value);
	}

	@Override
	public boolean allowPacket(Packet p) {
		TCPPacket tcp = (TCPPacket) p.ancestorByType("tcp");
		return tcp != null && tcp.ackNum == this.ack;
	}

}
