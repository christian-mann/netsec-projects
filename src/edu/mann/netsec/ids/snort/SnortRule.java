package edu.mann.netsec.ids.snort;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.mann.netsec.packets.IPAddress;
import edu.mann.netsec.packets.Packet;
import edu.mann.netsec.packets.PacketHandler;
import edu.mann.netsec.packets.filter.AndPacketFilter;
import edu.mann.netsec.packets.filter.DstAddressPacketFilter;
import edu.mann.netsec.packets.filter.DstPortPacketFilter;
import edu.mann.netsec.packets.filter.OrPacketFilter;
import edu.mann.netsec.packets.filter.PacketFilter;
import edu.mann.netsec.packets.filter.SrcAddressPacketFilter;
import edu.mann.netsec.packets.filter.SrcPortPacketFilter;
import edu.mann.netsec.packets.filter.TypePacketFilter;

public class SnortRule implements PacketHandler<Packet> {

	private static final String ALERT = "alert";
	private static final String PASS = "pass";
	
	private String action;
	private PacketFilter filter;
	private PacketHandler<Packet> handler;
	
	public SnortRule() {
		this.handler = new PacketHandler<Packet>() {
			
			@Override
			public void handlePacket(Packet p) {
				System.out.println(p.prettyPrint());
			}
		};
	}

	public static SnortRule parse(String s) throws SnortInvalidRuleException, SnortInvalidOptionException {
		SnortRule rule = new SnortRule();
		
		String[] tokens = s.split(" +");
		
		String sAction = tokens[0];
		if (sAction.equals("alert")) {
			rule.action = SnortRule.ALERT;
		} else if (sAction.equals("pass")) {
			rule.action = SnortRule.PASS;
		}
		
		boolean bidirectional;
		if (tokens[4].equals("->")) {
			bidirectional = false;
		} else if (tokens[4].equals("<>")) {
			bidirectional = true;
		} else {
			throw new SnortInvalidRuleException("Did not understand rule: " + s);
		}
		
		// construct filters... oh boy
		List<PacketFilter> filters = new ArrayList<PacketFilter>();
		filters.add(new TypePacketFilter(tokens[1]));
		filters.add(new SrcAddressPacketFilter(tokens[2]));
		filters.add(new SrcPortPacketFilter(tokens[3]));
		filters.add(new DstAddressPacketFilter(tokens[5]));
		filters.add(new DstPortPacketFilter(tokens[6]));
		PacketFilter forwardTCPFilter = new AndPacketFilter(filters.toArray(new PacketFilter[filters.size()]));
		
		PacketFilter tcpFilter;
		
		if (bidirectional) {
			filters = new ArrayList<PacketFilter>();
			filters.add(new TypePacketFilter(tokens[1]));
			filters.add(new DstAddressPacketFilter(tokens[2]));
			filters.add(new DstPortPacketFilter(tokens[3]));
			filters.add(new SrcAddressPacketFilter(tokens[5]));
			filters.add(new SrcPortPacketFilter(tokens[6]));
			PacketFilter reverseTCPFilter = new AndPacketFilter(filters.toArray(new PacketFilter[filters.size()]));
			tcpFilter = new OrPacketFilter(forwardTCPFilter, reverseTCPFilter);
		} else {
			tcpFilter = new OrPacketFilter(forwardTCPFilter);
		}
		
		PacketFilter optionsFilter;
		
		// rebuild the poor options section
		StringBuilder sbOptions = new StringBuilder();
		for (int i = 7; i < tokens.length; i++) {
			if (i != 7) sbOptions.append(" ");
			sbOptions.append(tokens[i]);
		}
		String sOptions = sbOptions.toString();
		
		List<SnortOption> options = SnortOption.parseRuleOptions(sOptions);
		
		optionsFilter = new AndPacketFilter(options.toArray(new PacketFilter[options.size()]));
		
		rule.filter = new AndPacketFilter(tcpFilter, optionsFilter);
		
		System.out.println(rule);
		return rule;
	}
	
	
	@Override
	public String toString() {
		return "SnortRule [action=" + action + ", filter=" + filter
				+ ", handler=" + handler + "]";
	}

	@Override
	public void handlePacket(Packet p) {
		if (this.filter.allowPacket(p)) {
			this.handler.handlePacket(p);
		}
	}

	public boolean matches(Packet p) {
		return this.filter.allowPacket(p);
	}
}
