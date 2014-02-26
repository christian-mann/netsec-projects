package edu.mann.netsec;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import edu.mann.netsec.packets.PacketSource;

public class NetworkPacketSource implements PacketSource {

	public NetworkPacketSource(String string) {
		
	}
	
	@Override
	public Iterator<ByteBuffer> iterator() {
		return (new ArrayList<ByteBuffer>()).iterator();
	}

}
