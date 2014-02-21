import java.nio.ByteBuffer;

public class UDPPacket extends IPPacket {
	private short srcPort;
	private short dstPort;
	private short length; // in bytes
	private short checksum;
	private ByteBuffer payload;

	public UDPPacket(IPPacket e) {
		ByteBuffer data = e.payload();
		this.parseData(data);
	}

	
	public void parseData(ByteBuffer data) {
		byte b;

		this.srcPort = data.getShort();
		this.dstPort = data.getShort();
		this.length = data.getShort();
		this.checksum = data.getShort();

		this.payload = data.duplicate();
		// TODO check checksum value
	}

	public String prettyPrint() {
		GridFormatter gf = new GridFormatter();
		gf.append(16, String.format("srcPort = %d", this.srcPort));
		gf.append(16, String.format("dstPort = %d", this.dstPort));
		gf.append(16, String.format("length = %d", this.length));
		if (this.checksumValid) {
			gf.append(16, "chksum valid");
		} else {
			gf.append(16, "chksum invalid!");
		}
		return gf.format(32);
	}
}
