package edu.mann.netsec.packets;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class RawPacket extends Packet {

	private ByteBuffer data;

	public RawPacket(ByteBuffer data) {
		this.data = data.duplicate();
		this.parseData(data);
	}

	@Override
	public Packet childPacket() {
		return null;
	}

	@Override
	public ByteBuffer getData() {
		return this.data.duplicate();
	}

	@Override
	public String getType() {
		return "raw";
	}

	@Override
	public void parseData(ByteBuffer data) {
		// nothing!
	}

	public String prettyPrint(boolean printSummary) {
		// this is supposed to look like hexdump, etc
		StringBuilder sb = new StringBuilder();
		
		ArrayList<Byte> row = new ArrayList<Byte>();
		for (int i = 0; data.remaining() > 0; i++) {
			// byte data first
			byte b = data.get();
			row.add(b);
			sb.append(String.format("%02x ", b));
			if (i % 8 == 7 && printSummary) {
				sb.append(" ");
			}
			if (i % 16 == 15) {
				if (printSummary) {
					for (byte bt : row) {
						char c = (char)(bt & 0xFF);
						if (' ' <= c && c <= '~')
							sb.append(c);
						else
							sb.append('.');
					}
				}
				row.clear();
				sb.append("\n");
			}
		}
		
		// fix the last row
		if (!row.isEmpty()) {
			int printed = row.size() * 3 + (row.size() >= 8 ? 1 : 0);
			if (printSummary)
				for (int i = 0; i < 50 - printed; i++) {
					sb.append(" ");
				}
			
			if (printSummary) {
				for (byte bt : row) {
					char c = (char)(bt & 0xFF);
					if (' ' <= c && c <= '~')
						sb.append(c);
					else
						sb.append('.');
				}
			}
			row.clear();
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
	@Override
	public String prettyPrint() {
		return this.prettyPrint(true);
	}

}
