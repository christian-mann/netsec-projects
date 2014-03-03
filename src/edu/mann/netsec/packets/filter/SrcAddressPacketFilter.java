package edu.mann.netsec.packets.filter;

import edu.mann.netsec.packets.IPAddress;
import edu.mann.netsec.packets.IPPacket;
import edu.mann.netsec.packets.Packet;

public class SrcAddressPacketFilter implements PacketFilter {

	private IPAddress srcIP;

	public SrcAddressPacketFilter(String s) {
		this(new IPAddress(s));
	}
	
	public SrcAddressPacketFilter(IPAddress ip) {
		this.srcIP = ip;
	}

	@Override
	public String toString() {
		return "SrcAddressPacketFilter[" + srcIP + "]";
	}

	@Override
	public boolean allowPacket(Packet p) {
		// look for the ip packet
		while (p.getType() != "ip") {
			p = p.childPacket();
			if (p == null) return false;
		}
		if (p instanceof IPPacket) {
			IPPacket ipp = (IPPacket)p;
			if (ipp.srcAddress.equals(this.srcIP)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

}
