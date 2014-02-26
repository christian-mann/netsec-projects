package edu.mann.netsec.packets;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import edu.mann.netsec.utils.Utils;

public class FilePacketSource implements PacketSource {

	private ArrayList<ByteBuffer> buffers;
	private File f;
	
	public FilePacketSource(String filename) throws IOException {
		this(new File(filename));
	}
	
	public FilePacketSource(File f) throws IOException {
		this.f = f;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(f));
			String line = null;
			ArrayList<ByteBuffer> packets = new ArrayList<ByteBuffer>();
			ByteArrayOutputStream packet = new ByteArrayOutputStream();
			while ((line = reader.readLine()) != null) {
				if (line.equals("")) {
					if (packet.size() > 0) {
						// finish packet and put it in array
						packets.add(ByteBuffer.wrap(packet.toByteArray()));
						packet = new ByteArrayOutputStream();
					}
					continue;
				}
				String[] sBytes = line.split(" ");
				for (String sByte : sBytes) {
					packet.write(Utils.byteFromHex(sByte));
				}
			}
			if (packet.size() > 0) {
				packets.add(ByteBuffer.wrap(packet.toByteArray()));
			}
			
			this.buffers = packets;
		} finally {
			reader.close();
		}
	}

	@Override
	public Iterator<ByteBuffer> iterator() {
		return Collections.unmodifiableList(this.buffers).iterator();
	}
	
	@Override
	public String toString() {
		return "\"" + this.f.getAbsolutePath() + "\"";
	}
}
