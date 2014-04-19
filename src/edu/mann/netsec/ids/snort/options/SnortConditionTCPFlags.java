package edu.mann.netsec.ids.snort.options;

import edu.mann.netsec.ids.snort.SnortCondition;
import edu.mann.netsec.packets.Packet;
import edu.mann.netsec.packets.TCPPacket;

public class SnortConditionTCPFlags extends SnortCondition {

	@Override
	public String toString() {
		return "SnortConditionTCPFlags[fin=" + fin + ", syn=" + syn + ", rst="
				+ rst + ", psh=" + psh + ", ack=" + ack + ", urg=" + urg
				+ ", cwr=" + cwr + ", ece=" + ece + "]";
	}

	private boolean fin;
	private boolean syn;
	private boolean rst;
	private boolean psh;
	private boolean ack;
	private boolean urg;
	private boolean cwr;
	private boolean ece;

	public SnortConditionTCPFlags(String value) {
		this.fin = value.contains("F");
		this.syn = value.contains("S");
		this.rst = value.contains("R");
		this.psh = value.contains("P");
		this.ack = value.contains("A");
		this.urg = value.contains("U");
		this.cwr = value.contains("C");
		this.ece = value.contains("E");
	}

	@Override
	public boolean allowPacket(Packet p) {
		TCPPacket tcp = (TCPPacket)p.ancestorByType("tcp");
		return tcp != null &&
				tcp.fin == this.fin &&
				tcp.syn == this.syn &&
				tcp.rst == this.rst &&
				tcp.psh == this.psh &&
				tcp.ack == this.ack &&
				tcp.urg == this.urg &&
				tcp.cwr == this.cwr &&
				tcp.ece == this.ece;
	}

}
