package edu.mann.netsec.packets.filter;

import edu.mann.netsec.packets.Packet;

public interface PacketFilter {
	public boolean allowPacket(Packet p);
}
