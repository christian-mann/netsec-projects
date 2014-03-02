package edu.mann.netsec.packets;

import java.nio.ByteBuffer;

public interface PacketSink {

	public abstract boolean writePacket(Packet p)
			throws IllegalArgumentException, SecurityException,
			ReflectiveOperationException;

	public abstract boolean writePacket(ByteBuffer bb)
			throws IllegalArgumentException, SecurityException,
			ReflectiveOperationException;

}