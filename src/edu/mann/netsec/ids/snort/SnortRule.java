package edu.mann.netsec.ids.snort;

import edu.mann.netsec.packets.Packet;
import edu.mann.netsec.packets.PacketHandler;

public class SnortRule implements PacketHandler {

	private static final String ALERT = "alert";
	private static final String PASS = "pass";
	private String action;

	public static SnortRule parse(String s) {
		SnortRule rule = new SnortRule();
		
		String[] tokens = s.split(" +");
		
		String sAction = tokens[0];
		if (sAction.equals("alert")) {
			rule.action = SnortRule.ALERT;
		} else if (sAction.equals("pass")) {
			rule.action = SnortRule.PASS;
		}
		
		
		return rule;
	}

	@Override
	public void handlePacket(Object p) {
		// TODO Auto-generated method stub
		
	}

	public boolean matches(Packet p) {
		// TODO Auto-generated method stub
		return false;
	}
}
