package edu.mann.netsec.packets;

import java.nio.ByteBuffer;

import edu.mann.netsec.utils.GridFormatter;

public class ICMPPacket extends Packet {

	private ByteBuffer data;
	private byte type;
	private byte code;
	private short checksum;

	public ICMPPacket(ByteBuffer data) {
		this.data = data.duplicate();
		this.parseData(data);
	}
	
	@Override
	public Packet childPacket() {
		return null;
	}
	
	@Override
	public ByteBuffer getData() {
		return this.data.duplicate();
	}

	public String getType() {
		return "icmp";
	}

	public void parseData(ByteBuffer data) {
		this.type = data.get();
		this.code = data.get();
		this.checksum = data.getShort();
	}

	@Override
	public String prettyPrint() {
		GridFormatter gf = new GridFormatter();
		gf.append(8, "type="+this.type);
		gf.append(8, "code="+this.code);
		gf.append(16, "checksum valid");
		return gf.format(32);
	}

}