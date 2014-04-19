package edu.mann.netsec.ids.snort.options;

import edu.mann.netsec.ids.snort.SnortCondition;
import edu.mann.netsec.packets.Packet;
import edu.mann.netsec.packets.TCPPacket;

public class SnortConditionTCPSeq extends SnortCondition {

	@Override
	public String toString() {
		return "SnortConditionTCPSeq[seq=" + seq + "]";
	}

	private int seq;

	public SnortConditionTCPSeq(String value) {
		this.seq = Integer.parseInt(value);
	}

	@Override
	public boolean allowPacket(Packet p) {
		TCPPacket tcp = (TCPPacket) p.ancestorByType("tcp");
		return tcp != null && tcp.seqNum == this.seq;
	}

}
