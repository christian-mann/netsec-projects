package edu.mann.netsec.packets;

import java.util.Arrays;

public class IPAddress extends Address {

	private byte[] addr;

	
	public IPAddress(byte[] addr) {
		if (addr.length != 4) {
			throw new IllegalArgumentException("IP address must be 4 bytes long");
		}
		this.addr = addr;
	}
	
	public IPAddress(String s) {
		String[] parts = s.split("\\."); // regex
		if (parts.length != 4) {
			throw new IllegalArgumentException("IP address must be of the form a.b.c.d");
		}
		this.addr = new byte[4];
		for (int i = 0; i < 4; i++) {
			addr[i] = (byte)Short.parseShort(parts[i]);
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < 4; i++) {
			if(i != 0) sb.append(".");
			sb.append(String.format("%d", (int)(this.addr[i] & 0xFF)));
		}
		return sb.toString();
	}
	
	public IPAddress logicalAnd(IPAddress other) {
		byte[] newAddr = new byte[4];
		for (int i = 0; i < newAddr.length; i++) {
			newAddr[i] = (byte) (this.addr[i] & other.addr[i]);
		}
		return new IPAddress(newAddr);
	}
	
	public boolean equals(IPAddress other) {
		return Arrays.equals(this.addr, other.addr);
	}
}
