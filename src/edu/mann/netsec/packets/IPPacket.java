package edu.mann.netsec.packets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

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
	public IPAddress srcAddress; // need public for packet filter
	public IPAddress dstAddress; // need public for packet filter
	private ByteBuffer payload;

	private List<IPOption> options;


	public IPPacket(ByteBuffer data) {
		this.data = data.duplicate();
		this.parseData(data);
	}

	public Packet childPacket() {
		if (this.payload.remaining() == 0) return null;
		switch(this.protocol) {
			case 1:
				return new ICMPPacket(this.payload);
			case 6:
				return new TCPPacket(this.payload);
			case 17:
				return new UDPPacket(this.payload);
			default:
				return new RawPacket(this.payload);
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
		this.totalLength = data.getShort() & 0xFFFF;
		this.identification = data.getShort() & 0xFFFF;
		
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

		// so far we've parsed 5 ints worth of data
		this.options = new ArrayList<IPOption>();
		for (int i = 5; i < this.ihl; i++) {
			byte[] optionData = new byte[4];
			data.get(optionData);
			this.options.add(new IPOption(optionData));
		}

		this.payload = data.slice();
	}

	public String prettyPrint() {
		GridFormatter gf = new GridFormatter();
		gf.append(4, String.format("ver=%d", this.version));
		gf.append(4, String.format("ihl=%d", this.ihl));
		gf.append(8, String.format("type=%d", this.typeOfService));
		gf.append(16, String.format("length=%d", this.totalLength));
		gf.append(16, String.format("identification=0x%X", this.identification));
		gf.append(1, this.reserved ? "  " : "   ");
		gf.append(1, this.dontFragment ? "DF" : "  ");
		gf.append(1, this.moreFragments ? "MF" : "  ");
		gf.append(13, String.format("offset=%d", this.fragmentOffset));
		gf.append(8, String.format("ttl=%d", this.timeToLive));
		gf.append(8, String.format("proto=%d", this.protocol));
		gf.append(16, String.format("checksum=0x%x ", this.headerChecksum & 0xFFFF) + (this.checksumValid() ? "(valid)" : "(invalid)"));
		gf.append(32, String.format("srcIP = %s", this.srcAddress.toString()));
		gf.append(32, String.format("dstIP = %s", this.dstAddress.toString()));

		for (IPOption op : this.options) {
			gf.append(32, op.toString());
		}
		return gf.format(32);
	}

	private boolean checksumValid() {
		int sum = 0;
		ByteBuffer header = this.data.duplicate();
		header.limit(this.ihl * 4);

		do {
			int s = header.getShort() & 0xFFFF;
			sum += s;
			// carry bit
			sum = sum + (sum >> 16);
			sum = sum & 0xFFFF;
		} while (header.hasRemaining());
		return sum == 0xFFFF;
	}
}