package edu.mann.netsec.packets;

import java.nio.ByteBuffer;

import edu.mann.netsec.utils.GridFormatter;

public class UDPPacket extends Packet {
	private ByteBuffer data;
	
	private short srcPort;
	private short dstPort;
	private short length; // in bytes
	private short checksum;
	private ByteBuffer payload;

	public UDPPacket(ByteBuffer data) {
		this.data = data.duplicate();
		this.parseData(data);
	}
	
	@Override
	public Packet childPacket() {
		return null;
	}
	
	public ByteBuffer getData() {
		return this.data.duplicate();
	}

	
	public String getType() {
		return "udp";
	}
	
	public void parseData(ByteBuffer data) {
		this.srcPort = data.getShort();
		this.dstPort = data.getShort();
		this.length = data.getShort();
		this.checksum = data.getShort();

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
