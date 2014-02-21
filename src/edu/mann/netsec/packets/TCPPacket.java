import java.nio.ByteBuffer;
import java.net.Inet4Address;

public class TCPPacket extends IPPacket {
	private short srcPort;
	private short dstPort;
	private int seqNum;
	private int ackNum;
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
	private short windowSize;
	private short checksum;
	private short urgentPointer;
	private ByteBuffer payload;

	public TCPPacket(IPPacket e) {
		ByteBuffer data = e.payload();

		this.parseData(data);
	}

	public TCPPacket childPacket() {
		return null;
	}
	
	public void parseData(ByteBuffer data) {
		byte b;

		this.srcPort = data.getShort();
		this.dstPort = data.getShort();
		this.seqNum = data.getInt();
		this.ackNum = data.getInt();
		
		b = data.get();
		this.dataOffset = b >> 4;
		// flags
		this.ns = b & (byte)0x01;
		b = data.get();
		this.cwr = b & (byte)0x80;
		this.ece = b & (byte)0x40;
		this.urg = b & (byte)0x20;
		this.ack = b & (byte)0x10;
		this.psh = b & (byte)0x08;
		this.rst = b & (byte)0x04;
		this.syn = b & (byte)0x02;
		this.fin = b & (byte)0x01;

		this.windowSize = data.getShort();
		this.checksum = data.getShort();
		this.urgentPointer = data.getShort();

		// TODO options/padding
		this.payload = data.duplicate();
		// TODO check checksum value
	}

	public String prettyPrint() {
		GridFormatter gf = new GridFormatter();
		gf.append(16, String.format("srcPort = %d", this.srcPort));
		gf.append(16, String.format("dstPort = %d", this.dstPort));
		gf.append(32, String.format("seq = %d", this.seqNum));
		gf.append(32, String.format("ack = %d", this.ackNum));
		gf.append(4, String.format("Data\nOffset\n%d", this.dataOffset));
		gf.append(3, String.format("Res."));
		gf.append(1, this.ns ? "NS" : "  ");
		gf.append(1, this.cwr ? "CWR" : "   ");
		gf.append(1, this.ece ? "ECE" : "   ");
		gf.append(1, this.urg ? "URG" : "   ");
		gf.append(1, this.ack ? "ACK" : "   ");
		gf.append(1, this.psh ? "PSH" : "   ");
		gf.append(1, this.syn ? "SYN" : "   ");
		gf.append(1, this.fin ? "FIN" : "   ");
		gf.append(16, String.format("windowSize = %d", this.windowSize));
		if(this.checksumValid) {
			gf.append(16, String.format("checksum valid"));
		} else {
			gf.append(16, String.format("checksum invalid"));
		}
		gf.append(16, String.format("urgent offset = %d", this.urgentPointer));

		return gf.format(32);
	}
}
