package edu.mann.netsec.packets;

import java.nio.ByteBuffer;

import edu.mann.netsec.utils.GridFormatter;

public class UDPPacket extends Packet {
	private ByteBuffer data;
	
	public int srcPort;
	public int dstPort;
	private int length; // in bytes
	private int checksum;
	private ByteBuffer payload;

	public UDPPacket(ByteBuffer data) {
		this.data = data.duplicate();
		this.parseData(data);
	}
	
	@Override
	public Packet childPacket() {
		if (this.payload.remaining() == 0) return null;
		else return new RawPacket(this.payload.duplicate());
	}
	
	public ByteBuffer getData() {
		return this.data.duplicate();
	}

	
	public String getType() {
		return "udp";
	}
	
	public void parseData(ByteBuffer data) {
		this.srcPort = data.getShort() & 0xFFFF;
		this.dstPort = data.getShort() & 0xFFFF;
		this.length = data.getShort() & 0xFFFF;
		this.checksum = data.getShort() & 0xFFFF;

		this.payload = data.slice();
		// TODO check checksum value
	}

	public String prettyPrint() {
		GridFormatter gf = new GridFormatter();
		gf.append(16, String.format("srcPort = %d", this.srcPort));
		gf.append(16, String.format("dstPort = %d", this.dstPort));
		gf.append(16, String.format("length = %d", this.length));
		if (this.checksumValid()) {
			gf.append(16, "chksum valid");
		} else {
			gf.append(16, "chksum invalid!");
		}
		return gf.format(32);
	}


	private boolean checksumValid() {
		return true;
	}
}
