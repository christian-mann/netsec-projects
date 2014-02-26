package edu.mann.netsec.packets.filter;

import edu.mann.netsec.packets.Packet;

public class AndPacketFilter implements PacketFilter {

	protected PacketFilter[] filters;
	public AndPacketFilter(PacketFilter... ps) {
		this.filters = ps;
	}
	
	@Override
	public boolean allowPacket(Packet p) {
		for (PacketFilter pf : filters) {
			if (pf != null && !pf.allowPacket(p)) {
				return false;
			}
		}
		return true;
	}

}
