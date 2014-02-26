package edu.mann.netsec.packets;

public class MACAddress extends Address {

	private byte[] addr;
	
	public MACAddress(byte[] addr) {
		if (addr.length != 6) {
			throw new IllegalArgumentException("MAC address must be 6 bytes long");
		}
		this.addr = addr.clone();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < 6; i++) {
			if(i != 0) sb.append(":");
			sb.append(String.format("%02X", addr[i]));
		}
		return sb.toString();
	}
}