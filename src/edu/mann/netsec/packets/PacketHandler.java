package edu.mann.netsec.packets;

public interface PacketHandler<T extends Packet> {

	public void handlePacket(T p);
}
