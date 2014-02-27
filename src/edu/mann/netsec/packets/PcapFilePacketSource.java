package edu.mann.netsec.packets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PcapFilePacketSource extends FilePacketSource {

	private File file;
	private List<ByteBuffer> packets;

	public PcapFilePacketSource(String filename) throws IOException {
		this(new File(filename));
	}

	public PcapFilePacketSource(File file) throws FileNotFoundException, IOException {
		this.file = file;
		
		parseFile(file);
	}
	
	@Override
	public String getFileName() {
		if (this.file != null) {
			return this.file.getAbsolutePath();
		} else {
			return "<no filename>";
		}
	}

	private void parseFile(File file) throws FileNotFoundException, IOException {
		this.packets = new ArrayList<ByteBuffer>();
		
		byte fileData[] = new byte[(int) file.length()];
		new FileInputStream(file).read(fileData);
		
		ByteBuffer fileBuffer = ByteBuffer.wrap(fileData);
		
		// read header
		int magic_number = fileBuffer.getInt();
		if (magic_number == 0xA1B2C3D4) {
			// order is good
		} else if (magic_number == 0xD4C3B2A1) {
			fileBuffer.order(ByteOrder.LITTLE_ENDIAN);
		} else {
			throw new IllegalArgumentException("Not a pcap file");
		}
		int version_major = fileBuffer.getShort();
		int version_minor = fileBuffer.getShort();
		int thiszone = fileBuffer.getInt();
		int sigfigs = fileBuffer.getInt();
		int snaplen = fileBuffer.getInt();
		int network = fileBuffer.getInt();
		
		while (fileBuffer.hasRemaining()) {
			int ts_sec = fileBuffer.getInt();
			int ts_usec = fileBuffer.getInt();
			int incl_len = fileBuffer.getInt();
			int orig_len = fileBuffer.getInt();
			
			byte data[] = new byte[incl_len];
			fileBuffer.get(data);
			
			this.packets.add(ByteBuffer.wrap(data));
		}
	}

	@Override
	public Iterator<ByteBuffer> iterator() {
		return this.packets.iterator();
	}

}
