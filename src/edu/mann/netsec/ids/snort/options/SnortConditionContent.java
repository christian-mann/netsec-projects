package edu.mann.netsec.ids.snort.options;

import edu.mann.netsec.ids.snort.SnortCondition;
import edu.mann.netsec.packets.Packet;

public class SnortConditionContent extends SnortCondition {

	private byte[] pattern;

	public SnortConditionContent(String value) {
		this.pattern = value.getBytes();
	}

	@Override
	public boolean allowPacket(Packet p) {
		byte[] payload = p.getPayload().array();
		
		for (int off = 0; off < payload.length - pattern.length; off++) {
			boolean match = true;
			for (int i = 0; i < pattern.length; i++) {
				if (payload[off + i] != pattern[i]) {
					match = false;
					break;
				}
			}
			if (match) return true;
		}
		
		return false;
	}

}
