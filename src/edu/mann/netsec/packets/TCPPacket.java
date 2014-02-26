package edu.mann.netsec.packets;

import java.nio.ByteBuffer;

import edu.mann.netsec.utils.GridFormatter;
import edu.mann.netsec.utils.Utils;

public class TCPPacket extends Packet {
	private ByteBuffer data;
	
	public int srcPort; /* unsigned short */
	public int dstPort; /* unsigned short */
	private long seqNum; /* unsigned int */
	private long ackNum; /* unsigned int */
	private int dataOffset;
	private boolean ns;
	private boolean cwr;
	private boolean ece;
	private boolean urg;
	private boolean ack;
	private boolean psh;
	private boolean rst;
	private boolean syn;
	private boolean fin;
	private int windowSize; /* unsigned short */
	private int checksum; /* unsigned short */
	private int urgentPointer; /* unsigned short */
	private ByteBuffer payload;

	public TCPPacket(ByteBuffer data) {
		this.data = data.duplicate();
		this.parseData(data);
	}

	public TCPPacket childPacket() {
		return null;
	}
	
	public ByteBuffer getData() {
		return data.duplicate();
	}
	
	public String getType() {
		return "tcp";
	}
	
	public void parseData(ByteBuffer data) {
		byte b;

		this.srcPort = data.getShort() & 0xffff;
		this.dstPort = data.getShort() & 0xffff;
		this.seqNum = data.getInt() & 0xffffffffL;
		this.ackNum = data.getInt() & 0xffffffffL;
		
		b = data.get();
		this.dataOffset = Utils.intFromBits(b, 0, 4);
		// flags
		this.ns = Utils.intFromBits(b, 7, 8) != 0;
		b = data.get();
		this.cwr = (b & (byte)0x80) != 0;
		this.ece = (b & (byte)0x40) != 0;
		this.urg = (b & (byte)0x20) != 0;
		this.ack = (b & (byte)0x10) != 0;
		this.psh = (b & (byte)0x08) != 0;
		this.rst = (b & (byte)0x04) != 0;
		this.syn = (b & (byte)0x02) != 0;
		this.fin = (b & (byte)0x01) != 0;

		this.windowSize = data.getShort() & 0xffff;
		this.checksum = data.getShort() & 0xffff;
		this.urgentPointer = data.getShort() & 0xffff;

		// TODO options/padding
		this.payload = data.slice();
		// TODO check checksum value
	}

	public String prettyPrint() {
		GridFormatter gf = new GridFormatter();
		gf.append(16, String.format("srcPort = %d", this.srcPort));
		gf.append(16, String.format("dstPort = %d", this.dstPort));
		gf.append(32, String.format("seq = %d", this.seqNum));
		gf.append(32, String.format("ack = %d", this.ackNum));
		gf.append(4, String.format("Data\nOffset:\n%d", this.dataOffset));
		gf.append(3, String.format("")); // reserved
		gf.append(1, this.ns ? "NS" : "  ");
		gf.append(1, this.cwr ? "CWR" : "   ");
		gf.append(1, this.ece ? "ECE" : "   ");
		gf.append(1, this.urg ? "URG" : "   ");
		gf.append(1, this.ack ? "ACK" : "   ");
		gf.append(1, this.psh ? "PSH" : "   ");
		gf.append(1, this.rst ? "RST" : "   ");
		gf.append(1, this.syn ? "SYN" : "   ");
		gf.append(1, this.fin ? "FIN" : "   ");
		gf.append(16, String.format("windowSize = %d", this.windowSize));
		if(this.checksumValid()) {
			gf.append(16, String.format("checksum valid"));
		} else {
			gf.append(16, String.format("checksum invalid"));
		}
		gf.append(16, String.format("urgent offset = %d", this.urgentPointer));

		return gf.format(32);
	}
	
	private boolean checksumValid() {
		return true;
	}
}
