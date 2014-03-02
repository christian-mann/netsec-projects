package edu.mann.netsec.packets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import jnr.netdb.Service;
import edu.mann.netsec.utils.GridFormatter;
import edu.mann.netsec.utils.Utils;

public class TCPPacket extends Packet {
	
	static {
		Utils.eatNetDBWarning();
	}
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

	private List<TCPOption> options;

	public TCPPacket(ByteBuffer data) {
		this.data = data.duplicate();
		this.parseData(data);
	}

	public Packet childPacket() {
		if (this.payload.remaining() == 0) return null;
		else return new RawPacket(this.data.duplicate());
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

		this.options = new ArrayList<TCPOption>();
		// at this point we've consumed 20 bytes of the header
		for (int bytesConsumed = 20; bytesConsumed < this.dataOffset*4; bytesConsumed += 4) {
			// TODO these are not real options
			byte optionData[] = new byte[4];
			data.get(optionData);
			this.options.add(new TCPOption(optionData));
		}
		
		this.payload = data.slice();
	}

	public String prettyPrint() {
		GridFormatter gf = new GridFormatter();
		Service srcService = Service.getServiceByPort(this.srcPort, "tcp");
		Service dstService = Service.getServiceByPort(this.dstPort, "tcp");
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
		gf.append(32, String.format("seq = %d", this.seqNum));
		gf.append(32, String.format("ack = %d", this.ackNum));
		gf.append(4, String.format("Header\nLength:\n%d int", this.dataOffset));
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
		gf.append(16, String.format("checksum = 0x%X", this.checksum));
		gf.append(16, String.format("urgent offset = %d", this.urgentPointer));
		
		//options
		for (TCPOption op : this.options) {
			gf.append(32, op.toString());
		}

		return gf.format(32);
	}
}
