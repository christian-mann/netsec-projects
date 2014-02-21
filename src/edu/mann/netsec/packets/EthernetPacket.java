import java.nio.ByteBuffer;

public class EthernetPacket extends Packet {
	private byte[] preamble;
	private byte[] sfd;
	private MACAddress dstAddr;
	private MACAddress srcAddr;
	private byte[] vlan;
	private short type;
	private ByteBuffer payload;
	private int checksum;

	public EthernetPacket(ByteBuffer raw) {
		super(raw);
		this.parseData(raw);
	}

	public void parseData(ByteBuffer data) {
		this.preamble = new byte[7];
		data.get(this.preamble);

		this.sfd = new byte[1];
		data.get(this.sfd);

		byte[] addr = new byte[6];
		data.get(addr);
		this.dstAddr = new MACAddress(addr);

		data.get(addr);
		this.srcAddr = new MACAddress(addr);

		this.vlan = new byte[4];
		data.get(this.vlan);

		this.type = data.getShort();

		this.payload = data.duplicate();

		// checksum
		checksum = data.getInt(data.remaining() - 4);
	}
}

class MACAddress {

	private byte[] addr;
	
	public MACAddress(byte[] addr) {
		if (addr.length != 6) {
			throw new IllegalArgumentException("MAC address must be 6 bytes long");
		}
		this.addr = addr;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < 6; i++) {
			if(i != 0) sb.append(":");
			sb.append(String.format("%X", addr[i]));
		}
		return sb.toString();
	}
}

