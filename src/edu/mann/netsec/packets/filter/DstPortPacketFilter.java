package edu.mann.netsec.packets.filter;

import edu.mann.netsec.packets.Packet;
import edu.mann.netsec.packets.PortRange;
import edu.mann.netsec.packets.TCPPacket;
import edu.mann.netsec.packets.UDPPacket;

public class DstPortPacketFilter implements PacketFilter {

	@Override
	public String toString() {
		return "DstPortPacketFilter[" + this.portRange.toString() + "]";
	}

	private PortRange portRange;
	private boolean any;
	
	public DstPortPacketFilter(String s) {
		if (s.equals("any")) this.any = true;
		this.portRange = new PortRange(s);
	}
	
	public DstPortPacketFilter(int lowPort, int highPort) {
		this.portRange = new PortRange(lowPort, highPort);
	}

	@Override
	public boolean allowPacket(Packet p) {
		TCPPacket tcp = (TCPPacket) p.ancestorByType("tcp");
		UDPPacket udp = (UDPPacket) p.ancestorByType("udp");
		
		if (tcp != null && this.portRange.contains(tcp.dstPort)) return true;
		if (udp != null && this.portRange.contains(udp.dstPort)) return true;
		
		if (tcp == null && udp == null && this.any) return true;
		
		return false;
	}

}
