package edu.mann.netsec.ids.snort;

import edu.mann.netsec.packets.Packet;

public class SnortCondition extends SnortOption {

	public boolean acceptPacket(Packet p) {
		return false;
	}
}
