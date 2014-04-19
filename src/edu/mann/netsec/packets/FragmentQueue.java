package edu.mann.netsec.packets;

import java.util.ArrayList;

public abstract class FragmentQueue<T extends Packet> {
	private ArrayList<PacketHandler<T>> handlers;

	public abstract void addFragment(Fragment<T> f);
	
	public void registerHandler(PacketHandler<T> ph) {
		this.handlers.add(ph);
	}
	
	protected void handlePacket(T p) {
		for (PacketHandler<T> ph : this.handlers) {
			ph.handlePacket(p);
		}
	}
}
