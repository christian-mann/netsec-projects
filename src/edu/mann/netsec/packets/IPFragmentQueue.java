package edu.mann.netsec.packets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class IPFragmentQueue {

	private LinkedList<IPPacket> fragments;
	
	private int size;
	private boolean seenFinalPacket;

	private boolean timedOut;
	
	public IPFragmentQueue() {
		this.fragments = new LinkedList<IPPacket>();
		this.size = 0;
		this.seenFinalPacket = false;
	}

	public void addFragment(IPPacket f) {
		fragments.add(f);
		
		if (f.moreFragments == false) {
			this.seenFinalPacket = true;
			this.size = f.fragmentOffset + f.totalLength;
		}
	}

	public IPPacket assemblePacket() {
		
		if (!this.isReady()) return null;
		

		int sid = this.calculateSID();
		
		// copy an ip header
		IPPacket samplePacket = this.fragments.get(0);
		ByteBuffer header = samplePacket.getData();
		header.limit(samplePacket.headerLength());
		
		ByteBuffer data = ByteBuffer.allocate(samplePacket.headerLength() + this.size);
		
		data.put(header);

		if (sid == 0 || sid == 1 || sid == 2) {
			// assemble the payload
			for (IPPacket frag : this.fragments) {
				data.position(samplePacket.headerLength() + frag.fragmentOffset);
				data.put(frag.getPayload());
			}
			data.position(0);
			
			// modify the data thing to be correct 
			// I really should just make a constructor for this instead
			data.position(2);
			data.putShort((short) this.size);
			data.position(0);

			// create that new IP packet
			IPPacket ret = new IPPacket(data.duplicate());
			ret.totalLength = this.size;
			ret.moreFragments = false;
			ret.fragmentOffset = 0;
			
			ret.fragments = this.fragments;
			ret.sid = sid;
			
			return ret;
		} else if (sid == 3 || sid == 4) {
			IPPacket ret = new IPPacket(this.fragments.get(0).getData().duplicate());
			ret.sid = sid;
			ret.fragments = this.fragments;
			return ret;
		} else {
			return null;
		}
	}

	private int calculateSID() {
		// is this an ARP packet?
		if (this.fragments.size() == 1) {
			try {
				if (this.fragments.get(0).childPacket().getType().equals("arp")) {
					return 0;
				}
			} catch (NullPointerException ex) {
				;
			}
		}
		
		// larger than 64K?
		if (this.size >= 65536) {
			return 3;
		}
		
		// timeout?
		if (this.timedOut) {
			return 4;
		}
		
		// did we overlap?
		boolean overlap = packetsOverlap();
		if (overlap) return 2;
		
		return 1;
	}

	private boolean packetsOverlap() {
		// sort packets by fragment offset
		List<IPPacket> sortedFragments = new ArrayList<IPPacket>(this.fragments);
		Collections.copy(sortedFragments, this.fragments);
		Collections.sort(sortedFragments, new Comparator<IPPacket>() {
			@Override
			public int compare(IPPacket o1, IPPacket o2) {
				return o2.fragmentOffset - o1.fragmentOffset;
			}
		});
		
		// check for overlap
		int offset = 0;
		for (IPPacket f : sortedFragments) {
			if (f.fragmentOffset < offset) {
				return true;
			}
			
			offset = f.fragmentOffset + f.totalLength;
		}
		return false;
	}
	
	private boolean isReady() {
		if (!this.seenFinalPacket) return false;
		
		// sort packets by fragment offset
		List<IPPacket> sortedFragments = new ArrayList<IPPacket>(this.fragments);
		Collections.copy(sortedFragments, this.fragments);
		Collections.sort(sortedFragments, new Comparator<IPPacket>() {
			@Override
			public int compare(IPPacket o1, IPPacket o2) {
				return o1.fragmentOffset - o2.fragmentOffset;
			}
		});
		
		// check for gaps
		int offset = 0;
		for (IPPacket f : sortedFragments) {
			if (f.fragmentOffset > offset) {
				return false;
			}
			
			offset = f.fragmentOffset + f.totalLength;
		}
		return true;
	}
}
