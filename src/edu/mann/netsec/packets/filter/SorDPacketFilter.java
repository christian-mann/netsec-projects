package edu.mann.netsec.packets.filter;


public class SorDPacketFilter extends OrPacketFilter {
	
	public SorDPacketFilter(String s) {
		super(
			new SrcAddressPacketFilter(s.split(",")[0]), 
			new DstAddressPacketFilter(s.split(",")[1])
		);
	}
}
