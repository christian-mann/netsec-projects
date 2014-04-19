package edu.mann.netsec.packets;

public class PortRange {

	private int lowPort;
	private int highPort;

	/**
	 * Currently accepts: "exact", "low:high", "low:", ":high", "any"
	 * Also accepts "-" as delimiter
	 * @param s
	 */
	public PortRange(String s) {

		if (s.equals("any")) {
			this.lowPort = 0;
			this.highPort = 65535;
			return;
		}
		
		String delim;
		if (s.contains("-")) {
			delim = "-";
		} else if (s.contains(":")) {
			delim = ":";
		} else {
			delim = null;
		}

		if (delim != null) {
			String[] ports = s.split("-");
			this.lowPort = ports[0].isEmpty() ? 0 : Integer.parseInt(ports[0]);
			this.highPort = ports[1].isEmpty() ? 65535 : Integer.parseInt(ports[1]);	
		} else {
			this.lowPort = this.highPort = Integer.parseInt(s);
		}
	}

	public PortRange(Integer port) {
		this.lowPort = this.highPort = port;
	}

	public PortRange(Integer low, Integer high) {
		this.lowPort = low;
		this.highPort = high;
	}

	public boolean contains(Integer port) {
		return this.lowPort <= port && port <= this.highPort;
	}

	@Override
	public String toString() {
		return "PortRange[" + lowPort + "-" + highPort + "]";
	}
	
}
