package edu.mann.netsec.packets.filter;

import edu.mann.netsec.packets.IPAddress;
import edu.mann.netsec.packets.IPPacket;
import edu.mann.netsec.packets.IPRange;
import edu.mann.netsec.packets.Packet;

public class DstAddressPacketFilter implements PacketFilter {

	@Override
	public String toString() {
		return "DstAddressPacketFilter[" + dstIP + "]";
	}

	private IPRange dstIP;

	public DstAddressPacketFilter(String s) {
		this.dstIP = new IPRange(s);
	}
	
	public DstAddressPacketFilter(IPAddress ip) {
		this.dstIP = new IPRange(ip);
	}

	@Override
	public boolean allowPacket(Packet p) {
		IPPacket ip = (IPPacket)p.ancestorByType("ip");
		return ip != null && this.dstIP.contains(ip.dstAddress);
	}

}
