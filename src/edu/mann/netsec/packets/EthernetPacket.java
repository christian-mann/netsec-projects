package edu.mann.netsec.packets;

import java.nio.ByteBuffer;

import edu.mann.netsec.utils.GridFormatter;

public class EthernetPacket extends Packet {
	private ByteBuffer data;
	
	private MACAddress dstAddr;
	private MACAddress srcAddr;
	private short type;
	private ByteBuffer payload;
	private int checksum;

	public EthernetPacket(ByteBuffer raw) {
		this.data = raw.duplicate();
		this.parseData(raw);
	}

	public Packet childPacket() {
		return new IPPacket(this.payload.duplicate());
	}
	
	public ByteBuffer getData() {
		return this.data.duplicate();
	}

	public String getType() {
		return "eth";
	}

	public void parseData(ByteBuffer data) {
		byte[] addr = new byte[6];
		data.get(addr);
		this.dstAddr = new MACAddress(addr);

		data.get(addr);
		this.srcAddr = new MACAddress(addr);

		this.type = data.getShort();

		this.payload = data.slice();

		// checksum
		checksum = data.getInt(data.remaining() - 4);
	}

	public ByteBuffer payload() {
		return this.payload.duplicate();
	}
	
	public String prettyPrint() {
		GridFormatter gf = new GridFormatter();
		gf.append(6*8, "dst = " + this.dstAddr.toString());
		gf.append(6*8, "src = " + this.srcAddr.toString());
		//gf.append(16, Short.toString(this.type));
		return gf.format(48);
	}
}

