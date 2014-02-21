import java.nio.ByteBuffer;

public class IPPacket extends EthernetPacket {
	private int version;
	private int ihl; // num of 32-bit words forming the header
	private int typeOfService;
	private int totalLength; // in bytes
	private int identification;
	private boolean reserved;
	private boolean dontFragment;
	private boolean moreFragments;
	private int fragmentOffset;
	private int timeToLive;
	private int protocol;
	private int headerChecksum;
	private byte[] srcAddress;
	private byte[] dstAddress;
	private ByteBuffer payload;


	public IPAddress(EthernetPacket e) {
		ByteBuffer data = e.payload();

		this.parseData(data);
	}

	public IPPacket childPacket() {
		switch(this.protocol) {
			case 1:
				return new ICMPPacket(this.payload);
			case 6:
				return new TCPPacket(this.payload);
			case 7:
				return new UDPPacket(this.payload);
			default:
				return null; // yes, I know that's not quite semantic.
		}
	}
	
	public void parseData(ByteBuffer data) {
		byte b;
		
		b = data.get();
		this.version = Utils.intFromBytes(b, 0, 3);
		this.ihl = Utils.intFromBytes(b, 4, 7);

		this.typeOfService = data.get();
		this.totalLength = data.getShort();
		this.identification = data.getShort();
		
		b = data.get();
		this.reserved = b | (byte)0x80;
		this.dontFragment = b | (byte)0x40;
		this.moreFragments = b | (byte)0x20;

		byte c = data.get();
		this.fragmentOffset = Utils.intFromBits({b, c}, 3, 15);

		this.timeToLive = data.getByte();
		this.protocol = data.getByte();
		this.headerChecksum = data.getShort();

		this.srcAddress = new byte[4];
		for(int i = 0; i < 4; i++) {
			this.srcAddress[i] = data.getByte();
		}

		this.dstAddress = new byte[4];
		for(i = 0; i < 4; i++) {
			this.dstAddress[i] = data.getByte();
		}
		// TODO options/padding

		this.payload = data.duplicate();
	}

	public String prettyPrint() {
		GridFormatter gf = new GridFormatter();
		gf.append(4, String.format("ver=%d", this.version));
		gf.append(4, String.format("ihl=%d", this.ihl));
		gf.append(8, String.format("type=%d", this.typeOfService));
		gf.append(16, String.format("length=%d", this.length));
		gf.append(16, String.format("identification=%d", this.identification));
		gf.append(1, this.reserved ? "  " : "   ");
		gf.append(1, this.dontFragment ? "DF" : "  ");
		gf.append(1, this.moreFragments ? "MF" : "  ");
		gf.append(13, String.format("offset=%d", this.fragmentOffset));
		gf.append(8, String.format("ttl=%d", this.timeToLive));
		gf.append(8, String.format("proto=%d", this.protocol));
		gf.append(16, this.checksumValid ? "checksum valid" : "checksum invalid");
		// src ip address
		StringBuilder sbSrc = new StringBuilder();
		for(int i = 0; i < 4; i++) {
			if(i > 0) sbSrc.append(".");
			sbSrc.append(String.format("%d", srcAddress[i]));
		}
		gf.append(32, String.format("srcIP = %s", sbSrc.toString()));

		// dst ip address
		StringBuilder sbDst = new StringBuilder();
		for(i = 0; i < 4; i++) {
			if(i > 0) sbDst.append(".");
			sbDst.append(String.format("%d", dstAddress[i]));
		}
		gf.append(32, String.format("dstIP = %s", sbDst.toString()));

		return gf.format(32);
	}
}
