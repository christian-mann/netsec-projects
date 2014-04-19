package edu.mann.netsec.packets;

import static org.junit.Assert.*;

import org.junit.Test;

public class IPRangeTest {

	@Test
	public void testMaskFromCidr() {
		//assertEquals((new IPAddress("255.255.255.0")), (IPRange.maskFromCidr("24")));
		assertTrue((new IPAddress("255.255.255.0")).equals(IPRange.maskFromCidr("24")));
		
		//assertEquals((new IPAddress("255.192.0.0")), (IPRange.maskFromCidr("10")));	
		assertTrue((new IPAddress("255.192.0.0")).equals(IPRange.maskFromCidr("10")));
	}

	@Test
	public void testContains() {
		IPRange rng = new IPRange("192.168.1.0/24");
		
		assertTrue(rng.contains(new IPAddress("192.168.1.101")));
		assertFalse(rng.contains(new IPAddress("192.168.2.101")));
	}
}
