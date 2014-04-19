package edu.mann.netsec.packets.filter;

import edu.mann.netsec.packets.IPAddress;
import edu.mann.netsec.packets.IPPacket;
import edu.mann.netsec.packets.IPRange;
import edu.mann.netsec.packets.Packet;

public class SrcAddressPacketFilter implements PacketFilter {

	private IPRange srcIP;

	public SrcAddressPacketFilter(String s) {
		this.srcIP = new IPRange(s);
	}
	
	public SrcAddressPacketFilter(IPAddress ip) {
		this.srcIP = new IPRange(ip);
	}

	@Override
	public String toString() {
		return "SrcAddressPacketFilter[" + srcIP + "]";
	}

	@Override
	public boolean allowPacket(Packet p) {
		IPPacket ip = (IPPacket) p.ancestorByType("ip");
		return ip != null && this.srcIP.contains(ip.srcAddress);
	}

}
