package edu.mann.netsec.packets;

import java.nio.ByteBuffer;

public class ARPPacket extends Packet {

	private ByteBuffer data;
	
	public ARPPacket(ByteBuffer data) {
		this.data = data.duplicate();
		// TODO Auto-generated constructor stub
	}

	@Override
	public Packet childPacket() {
		return null;
	}

	@Override
	public ByteBuffer getData() {
		return data.duplicate();
	}

	@Override
	public String getType() {
		return "arp";
	}

	@Override
	public void parseData(ByteBuffer data) {
		// TODO Auto-generated method stub

	}

	@Override
	public String prettyPrint() {
		// TODO Auto-generated method stub
		return null;
	}

}
