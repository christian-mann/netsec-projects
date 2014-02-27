package edu.mann.netsec.packets;

import java.nio.ByteBuffer;

import jnr.netdb.Service;

import edu.mann.netsec.utils.GridFormatter;
import edu.mann.netsec.utils.Utils;

public class UDPPacket extends Packet {
	
	static {
		Utils.eatNetDBWarning();
	}
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
	}

	public String prettyPrint() {
		GridFormatter gf = new GridFormatter();
		
		Service srcService = Service.getServiceByPort(this.srcPort, "udp");
		Service dstService = Service.getServiceByPort(this.dstPort, "udp");
		if (srcService != null) {
			gf.append(16, String.format("srcPort = %d (%s)", this.srcPort, srcService.getName()));
		} else {
			gf.append(16, String.format("srcPort = %d", this.srcPort));
		}
		if (dstService != null) {
			gf.append(16, String.format("dstPort = %d (%s)", this.dstPort, dstService.getName()));
		} else {
			gf.append(16, String.format("dstPort = %d", this.dstPort));
		}
		gf.append(16, String.format("length = %d", this.length));
		gf.append(16, String.format("checksum=0x%04X ", this.checksum & 0xFFFF));
		return gf.format(32);
	}
}
