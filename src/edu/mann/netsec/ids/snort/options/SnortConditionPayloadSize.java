package edu.mann.netsec.ids.snort.options;

import edu.mann.netsec.ids.snort.SnortCondition;
import edu.mann.netsec.packets.Packet;

public class SnortConditionPayloadSize extends SnortCondition {

	@Override
	public String toString() {
		return "SnortConditionPayloadSize[size=" + size + "]";
	}

	private int size;

	public SnortConditionPayloadSize(String value) {
		this.size = Integer.parseInt(value);
	}

	@Override
	public boolean allowPacket(Packet p) {
		return p.getPayload().remaining() == this.size;
	}

}
