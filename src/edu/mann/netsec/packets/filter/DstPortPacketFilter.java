package edu.mann.netsec.packets.filter;

import edu.mann.netsec.packets.Packet;
import edu.mann.netsec.packets.TCPPacket;
import edu.mann.netsec.packets.UDPPacket;

public class DstPortPacketFilter implements PacketFilter {

	@Override
	public String toString() {
		return "DstPortPacketFilter[" + lowPort + "-"
				+ highPort + "]";
	}

	private int lowPort;
	private int highPort;
	
	public DstPortPacketFilter(String s) {
		String delim;
		if (s.contains("-")) {
			delim = "-";
		} else if (s.contains(":")) {
			delim = ":";
		} else {
			delim = null;
		}
		if (delim != null) {
			String[] ports = s.split("-");
			this.lowPort = Integer.parseInt(ports[0]);
			this.highPort = Integer.parseInt(ports[1]);	
		} else {
			this.lowPort = this.highPort = Integer.parseInt(s);
		}
	}
	
	public DstPortPacketFilter(int lowPort, int highPort) {
		this.lowPort = lowPort;
		this.highPort = highPort;
	}

	@Override
	public boolean allowPacket(Packet p) {
		if (p == null) return false;
		do {
			if (p.getType() == "tcp") {
				if (p instanceof TCPPacket) {
					int dstPort = ((TCPPacket)p).dstPort;
					if (this.lowPort <= dstPort && dstPort <= this.highPort) {
						return true;
					}
				}
			} else if (p.getType() == "udp") {
				if (p instanceof UDPPacket) {
					int dstPort = ((UDPPacket)p).dstPort;
					if (this.lowPort <= dstPort && dstPort <= this.highPort) {
						return true;
					}
				}
			}
			p = p.childPacket();
		} while (p != null);
		return false;
	}

}
