package edu.mann.netsec.ids.snort.options;

import edu.mann.netsec.ids.snort.SnortCondition;
import edu.mann.netsec.packets.IPPacket;
import edu.mann.netsec.packets.Packet;

public class SnortConditionIPFragBits extends SnortCondition {

	@Override
	public String toString() {
		return "SnortConditionIPFragBits[mf=" + mf + ", df=" + df
				+ ", reserved=" + reserved + "]";
	}

	private boolean mf;
	private boolean df;
	private boolean reserved;
	public SnortConditionIPFragBits(String value) {
		this.mf = value.contains("M");
		this.df = value.contains("D");
		this.reserved = value.contains("R");
	}

	@Override
	public boolean allowPacket(Packet p) {
		IPPacket ip = (IPPacket) p.ancestorByType("ip");
		return ip != null &&
				ip.moreFragments == this.mf &&
				ip.dontFragment == this.df &&
				ip.reserved == this.reserved;
	}

}
