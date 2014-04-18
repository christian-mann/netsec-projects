package edu.mann.netsec.packets;

public interface PacketHandler<T> {

	public void handlePacket(T p);
}
