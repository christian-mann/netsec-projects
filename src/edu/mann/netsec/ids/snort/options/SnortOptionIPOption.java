package edu.mann.netsec.ids.snort.options;

import edu.mann.netsec.ids.snort.SnortOption;
import edu.mann.netsec.packets.Packet;

public class SnortOptionIPOption extends SnortOption {

	public SnortOptionIPOption(String key, String value) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean allowPacket(Packet p) {
		// TODO Auto-generated method stub
		return false;
	}

}
