package edu.mann.netsec.packets.filter;


public class SandDPacketFilter extends AndPacketFilter {
	
	public SandDPacketFilter(String s) {
		super(new SrcAddressPacketFilter(s.split(",")[0]),
				new DstAddressPacketFilter(s.split(",")[1]));
	}
}
