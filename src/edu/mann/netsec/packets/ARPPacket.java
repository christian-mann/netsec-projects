package edu.mann.netsec.packets;

import java.nio.ByteBuffer;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import edu.mann.netsec.utils.GridFormatter;

enum ARPOperation {
	REQUEST,
	REPLY
}
public class ARPPacket extends Packet {

	private ByteBuffer data;
	
	private int htype;
	private int ptype;
	private int hlen;
	private int plen;
	private ARPOperation oper;
	
	private MACAddress sha;
	private IPAddress spa;
	
	private MACAddress tha;
	private IPAddress tpa;
	
	public ARPPacket(ByteBuffer data) {
		this.data = data.duplicate();
		
		this.parseData(data);
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
		this.htype = data.getShort() & 0xFFFF;
		this.ptype = data.getShort() & 0xFFFF;
		this.hlen = data.get() & 0xFF;
		this.plen = data.get() & 0xFF;
		
		switch(data.getShort() & 0xFFFF) {
		case 1:
			this.oper = ARPOperation.REQUEST;
			break;
		case 2:
			this.oper = ARPOperation.REPLY;
			break;
		}
		
		if (this.hlen != 6 || this.plen != 4) {
			throw new NotImplementedException();
		}
		
		byte[] addr;
		
		addr = new byte[6];
		data.get(addr);
		this.sha = new MACAddress(addr);
		
		addr = new byte[4];
		data.get(addr);
		this.spa = new IPAddress(addr);
		
		addr = new byte[6];
		data.get(addr);
		this.tha = new MACAddress(addr);
		
		addr = new byte[4];
		data.get(addr);
		this.tpa = new IPAddress(addr);
	}

	@Override
	public String prettyPrint() {
		GridFormatter gf = new GridFormatter();
		gf.append(16, String.format("htype = %d", this.htype));
		gf.append(16, String.format("ptype = 0x%x", this.ptype));
		
		gf.append(8, String.format("hlen = %d", this.hlen));
		gf.append(8, String.format("plen = %d", this.plen));
		gf.append(16, String.format("operation = %s", this.oper.toString()));
		
		gf.append(32, String.format("sender MAC = %s", this.sha.toString()));
		
		gf.append(16, String.format("<sender MAC>"));
		gf.append(16, String.format("sender IP = %s", this.spa.toString()));
		
		gf.append(16, String.format("<sender IP>"));
		gf.append(16, String.format("<target MAC>"));
		
		gf.append(32, String.format("target MAC = %s", this.tha.toString()));
		gf.append(32, String.format("target IP = %s", this.tpa.toString()));
		
		return gf.format(32);
	}

}
