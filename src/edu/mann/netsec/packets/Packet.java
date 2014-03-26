package edu.mann.netsec.packets;

import java.nio.ByteBuffer;
import java.util.Date;

public abstract class Packet {
	
	private Date timestamp;
	
	public Packet() {
		this.setTimestamp(new Date());
	}
	
	public abstract Packet childPacket();

	public abstract ByteBuffer getData();
	
	public abstract String getType();
	
	public abstract void parseData(ByteBuffer data);
	
	public abstract String prettyPrint();
	
	public String toString() {
		ByteBuffer bbData = this.getData();
		StringBuilder sb = new StringBuilder();
		
		int i = 0;
		while (bbData.hasRemaining()) {
			if (i % 16 == 0 && i != 0) {
				sb.append("\n");
			}
			sb.append(String.format("%02x ", bbData.get()));
			i += 1;
		}
		return sb.toString();
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
}
