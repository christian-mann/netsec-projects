package edu.mann.netsec.packets.filter;

import edu.mann.netsec.packets.Packet;

public class TypePacketFilter implements PacketFilter {

	private String type;
	
	public TypePacketFilter(String type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return "TypePacketFilter[type=" + type + "]";
	}

	@Override
	public boolean allowPacket(Packet p) {
		return p.getType().equals(this.type);
	}

}
