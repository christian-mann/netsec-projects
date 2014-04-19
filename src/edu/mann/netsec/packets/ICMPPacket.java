package edu.mann.netsec.packets;

import java.nio.ByteBuffer;

import edu.mann.netsec.utils.GridFormatter;

public class ICMPPacket extends Packet {

	private ByteBuffer data;
	public byte type;
	public byte code;
	private short checksum;
	private int otherHeader;
	private ByteBuffer payload;

	public ICMPPacket(ByteBuffer data) {
		this.data = data.duplicate();
		this.parseData(data);
	}
	
	@Override
	public Packet childPacket() {
		if (payload.remaining() > 0) return new RawPacket(payload.duplicate());
		else return null;
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
		this.otherHeader = data.getInt();
		
		this.payload = data.slice();
	}

	@Override
	public String prettyPrint() {
		GridFormatter gf = new GridFormatter();
		gf.append(8, "type="+this.type);
		gf.append(8, "code="+this.code);
		gf.append(16, String.format("checksum=0x%04X", this.checksum) + (this.checksumValid() ? "(valid)" : "(invalid)"));
		gf.append(32, String.format("otherHeader=0x%08X", this.otherHeader));
		return gf.format(32);
	}

	private boolean checksumValid() {
		return InternetChecksum.isValid(this.data);
	}

}