package edu.mann.netsec.packets;

public class IPOption extends Object {

	private byte[] data;
	
	public IPOption(byte[] data) {
		this.data = data;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (byte b : data) {
			sb.append(String.format("%02x ", b));
		}
		return "<IP option data: " + sb.toString() + ">";
	}
}
