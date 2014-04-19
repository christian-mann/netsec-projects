package edu.mann.netsec.packets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.mann.netsec.Main;
import edu.mann.netsec.utils.GridFormatter;
import edu.mann.netsec.utils.Utils;

public class IPPacket extends Packet {
	@Override
	public String toString() {
		String className = this.isFragment() ? "IPFragment" : "IPPacket";
		return className + "[version=" + version + ", ihl="
		+ ihl + ", typeOfService=" + typeOfService + ", totalLength="
		+ totalLength + ", identification=" + identification
		+ ", reserved=" + reserved + ", dontFragment=" + dontFragment
		+ ", moreFragments=" + moreFragments + ", fragmentOffset="
		+ fragmentOffset + ", timeToLive=" + timeToLive + ", protocol="
		+ protocol + ", headerChecksum=" + headerChecksum
		+ ", srcAddress=" + srcAddress + ", dstAddress=" + dstAddress
		+ ", options=" + options
		+ ", fragments=" + (fragments==null ? 0 : fragments.size())
		+ ", sid=" + sid + "]";
	}

	private ByteBuffer data;

	private int version;
	private int ihl; // num of 32-bit words forming the header
	public int typeOfService;
	int totalLength; // in bytes
	public int identification;
	public boolean reserved;
	public boolean dontFragment;
	public boolean moreFragments;
	public int fragmentOffset;
	public int timeToLive;
	int protocol;
	private int headerChecksum;
	public IPAddress srcAddress; // need public for packet filter
	public IPAddress dstAddress; // need public for packet filter

	private List<IPOption> options;

	public Collection<IPPacket> fragments;

	public int sid;


	public IPPacket(ByteBuffer data) {
		this.data = data.duplicate();
		this.parseData(data);
	}

	public Packet childPacket() {
		Packet child = null;
		if (this.isFragment()) {
			// this doesn't have a direct child
			// so we throw it in the IPQueue
			// and see what we get back out
			IPQueue.addFragment(this);
			IPPacket ipp = IPQueue.getPacket();
			if (ipp == null) return null;
			ipp.parent = this; // sure, why not
			return ipp; // might be null
		}
		if (this.payload.remaining() == 0) return null;
		else switch(this.protocol) {
		case 1:
			child = new ICMPPacket(this.payload.duplicate());
			break;
		case 6:
			child = new TCPPacket(this.payload.duplicate());
			break;
		case 17:
			child = new UDPPacket(this.payload.duplicate());
			break;
		default:
			child = new RawPacket(this.payload.duplicate());
			break;
		}
		child.parent = this;
		return child;
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
		// use total length to limit buffer
		this.identification = data.getShort() & 0xFFFF;

		b = data.get();
		this.reserved = Utils.intFromBits(b, 0, 1) == 1;
		this.dontFragment = Utils.intFromBits(b, 1, 2) == 1;
		this.moreFragments = Utils.intFromBits(b, 2, 3) == 1;

		byte c = data.get();
		this.fragmentOffset = Utils.intFromBits(new byte[]{b, c}, 3, 16) * 8;

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

		int payloadLength = (this.totalLength - this.ihl * 4);
		data.limit(data.position() + payloadLength);

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
		if (this.fragments == null || this.fragments.isEmpty()) {
			gf.append(16, String.format("checksum=0x%04X ", this.headerChecksum & 0xFFFF) + (this.checksumValid() ? "(valid)" : "(invalid)"));
		} else {
			gf.append(16, String.format("checksum=<reassembled>"));
		}
		gf.append(32, String.format("srcIP = %s", this.srcAddress.toString()));
		gf.append(32, String.format("dstIP = %s", this.dstAddress.toString()));

		for (IPOption op : this.options) {
			gf.append(32, op.toString());
		}

		StringBuilder fragBuilder = new StringBuilder();
		//		if (this.fragments != null && !this.fragments.isEmpty()) {
		//			for (IPPacket f : this.fragments) {
		//				fragBuilder.append(f.prettyPrint());
		//				fragBuilder.append("\n");
		//			}
		//		}
		if (this.isFragment()) {
			return "IP Fragment:\n" + gf.format(32) + (new RawPacket(this.payload.duplicate()).prettyPrint());
		} else {
			return gf.format(32) + fragBuilder.toString();	
		}
	}

	private boolean checksumValid() {
		ByteBuffer header = this.data.duplicate();
		header.limit(this.ihl * 4);

		return InternetChecksum.isValid(header);
	}

	public boolean isFragment() {
		return (this.moreFragments || this.fragmentOffset > 0);
	}

	public ByteBuffer getPayload() {
		return this.payload.duplicate();
	}

	public int headerLength() {
		return this.ihl * 4;
	}
}
