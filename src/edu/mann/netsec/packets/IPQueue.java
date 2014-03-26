package edu.mann.netsec.packets;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class IPQueue {

	private IPFragmentQueue fragments;
	
	private IPAddress saddr;
	private IPAddress daddr;
	private int identification;
	private int protocol;
	
	private static Collection<IPQueue> queues = new LinkedList<IPQueue>();
	
	
	protected IPQueue(IPPacket firstFragment) {
		this.fragments = new IPFragmentQueue();
		this.fragments.addFragment(firstFragment);
		
		this.saddr = firstFragment.srcAddress;
		this.daddr = firstFragment.dstAddress;
		this.identification = firstFragment.identification;
		this.protocol = firstFragment.protocol;
	}
	
	public boolean matches(IPPacket p) {
		return (p.srcAddress.equals(saddr)
			&&  p.dstAddress.equals(daddr)
			&&  p.identification == identification
			&&  p.protocol == protocol);
	}
	
	public static void addFragment(IPPacket p) {
		for (IPQueue q : IPQueue.queues) {
			if (q.matches(p)) {
				q.fragments.addFragment(p);
				return;
			}
		}
		// no matches
		IPQueue q = new IPQueue(p);
		IPQueue.queues.add(q);
	}
	
	public static IPPacket getPacket() {
		for (Iterator<IPQueue> it = IPQueue.queues.iterator(); it.hasNext(); ) {
			IPQueue q = it.next();
			IPPacket p = q.fragments.assemblePacket();
			if (p != null) {
				it.remove();
				return p;
			}
		}
		return null;
	}

}
