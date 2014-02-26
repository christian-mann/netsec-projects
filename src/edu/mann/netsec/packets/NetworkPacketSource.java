package edu.mann.netsec.packets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;

public class NetworkPacketSource implements PacketSource {

	public NetworkPacketSource(String string) {
		
	}
	
	@Override
	public Iterator<ByteBuffer> iterator() {
		return (new ArrayList<ByteBuffer>()).iterator();
	}

}
