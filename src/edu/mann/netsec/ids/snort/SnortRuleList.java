package edu.mann.netsec.ids.snort;

import java.util.LinkedList;

import edu.mann.netsec.packets.Packet;

public class SnortRuleList extends LinkedList<SnortRule> {
	public void handlePacket(Packet p) {
		for (SnortRule rule : this) {
			if (rule.matches(p)) {
				rule.handlePacket(p);
			}
		}
	}
}
