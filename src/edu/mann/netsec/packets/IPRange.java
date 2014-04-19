package edu.mann.netsec.packets;

public class IPRange {

	private IPAddress addr;
	private IPAddress mask;

	public IPRange(String s) {
		// currently accepts:
		// ip address
		// address/cidr
		if (s.contains("/")) {
			String[] pair = s.split("/");
			String addr = pair[0];
			String cidr = pair[1];
			
			this.addr = new IPAddress(addr);
			this.mask = maskFromCidr(cidr);
		} else {
			this.addr = new IPAddress(s);
			this.mask = new IPAddress("255.255.255.255");
		}
	}
	
	public IPRange(IPAddress ip) {
		this.addr = ip;
		this.mask = new IPAddress("255.255.255.255");
	}

	public boolean contains(IPAddress ip) {
		return (this.addr.logicalAnd(this.mask)).equals(ip.logicalAnd(this.mask));
	}

	protected static IPAddress maskFromCidr(String cidr) {
		int bits = Integer.parseInt(cidr);
		
		byte[] bytes = new byte[4];
		
		for (int i = 0; i < bits / 8; i++) {
			bytes[i] = (byte) 255;
		}
		bytes[bits / 8] = (byte) (255 - (255 >> (bits % 8)));
		for (int i = (bits / 8) + 1; i < bytes.length; i++) {
			bytes[i] = 0;
		}
		
		return new IPAddress(bytes);
	}
}
