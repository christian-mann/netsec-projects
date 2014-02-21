import java.nio.ByteBuffer;

public abstract class Packet {

	public abstract Packet parentPacket();

	public abstract Packet childPacket();

	public abstract String prettyPrint();
}
