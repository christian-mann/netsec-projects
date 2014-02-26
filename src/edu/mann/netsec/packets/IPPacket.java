package edu.mann.netsec.packets;

import java.nio.ByteBuffer;

import edu.mann.netsec.utils.GridFormatter;
import edu.mann.netsec.utils.Utils;

public class IPPacket extends Packet {
	private ByteBuffer data;
	
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
	private IPAddress srcAddress;
	private IPAddress dstAddress;
	private ByteBuffer payload;


	public IPPacket(ByteBuffer data) {
		this.data = data.duplicate();
		this.parseData(data);
	}

	public Packet childPacket() {
		switch(this.protocol) {
			case 1:
				return new ICMPPacket(this.payload);
			case 6:
				return new TCPPacket(this.payload);
			case 7:
				return new UDPPacket(this.payload);
			default:
				return null;
		}
	}
	
	public ByteBuffer getData() {
		return this.data.duplicate();
	}
	
	@Override
	public String getType() {
		return "ip";
	}
	
	public void parseData(ByteBuffer data) {
		byte b;
		
		b = data.get();
		this.version = Utils.intFromBits(b, 0, 4);
		this.ihl = Utils.intFromBits(b, 4, 8);

		this.typeOfService = data.get();
		this.totalLength = data.getShort();
		this.identification = data.getShort();
		
		b = data.get();
		this.reserved = Utils.intFromBits(b, 0, 1) == 1;
		this.dontFragment = Utils.intFromBits(b, 1, 2) == 1;
		this.moreFragments = Utils.intFromBits(b, 2, 3) == 1;

		byte c = data.get();
		this.fragmentOffset = Utils.intFromBits(new byte[]{b, c}, 3, 16);

		this.timeToLive = data.get();
		this.protocol = data.get();
		this.headerChecksum = data.getShort();

		this.srcAddress = new IPAddress(new byte[]{data.get(), data.get(), data.get(), data.get()});

		this.dstAddress = new IPAddress(new byte[]{data.get(), data.get(), data.get(), data.get()});
		// TODO options/padding

		this.payload = data.slice();
	}

	public String prettyPrint() {
		GridFormatter gf = new GridFormatter();
		gf.append(4, String.format("ver=%d", this.version));
		gf.append(4, String.format("ihl=%d", this.ihl));
		gf.append(8, String.format("type=%d", this.typeOfService));
		gf.append(16, String.format("length=%d", this.totalLength));
		gf.append(16, String.format("identification=%d", this.identification));
		gf.append(1, this.reserved ? "  " : "   ");
		gf.append(1, this.dontFragment ? "DF" : "  ");
		gf.append(1, this.moreFragments ? "MF" : "  ");
		gf.append(13, String.format("offset=%d", this.fragmentOffset));
		gf.append(8, String.format("ttl=%d", this.timeToLive));
		gf.append(8, String.format("proto=%d", this.protocol));
		gf.append(16, this.checksumValid() ? "checksum valid" : "checksum invalid");
		gf.append(32, String.format("srcIP = %s", this.srcAddress.toString()));
		gf.append(32, String.format("dstIP = %s", this.dstAddress.toString()));

		return gf.format(32);
	}

	private boolean checksumValid() {
		return true;
	}
}