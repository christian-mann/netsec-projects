package edu.mann.netsec.packets;

public class TCPOption extends Object {

	private byte[] data;
	
	public TCPOption(byte[] data) {
		this.data = data;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (byte b : data) {
			sb.append(String.format("%02x ", b));
		}
		return "<TCP option data: " + sb.toString() + ">";
	}
}
