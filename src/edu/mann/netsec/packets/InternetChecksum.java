package edu.mann.netsec.packets;

import java.nio.ByteBuffer;


public class InternetChecksum {
	
	public static boolean isValid(ByteBuffer data) {
		return isValid(data, data.position(), data.limit());
	}
	
	public static boolean isValid(ByteBuffer data, int start, int len) {
		data = data.duplicate();
		int sum = 0;
		
		data.position(start);
		data.limit(len);
		
		do {
			int s = data.getShort() & 0xFFFF;
			sum += s;
			// carry bit
			sum = sum + (sum >> 16);
			sum = sum & 0xFFFF;
		} while (data.hasRemaining());
		return sum == 0xFFFF;
	}
}
