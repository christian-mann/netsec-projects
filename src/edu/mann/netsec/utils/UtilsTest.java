package edu.mann.netsec.utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class UtilsTest {

	@Test
	public void testIntFromBitsByteArrayIntInt() {
		assertEquals(Utils.intFromBits(new byte[]{0x34, 0x56}, 2, 12), 0x345);
	}

	@Test
	public void testIntFromBitsByteIntInt() {
		assertEquals(Utils.intFromBits((byte)0xFE, 0, 4), (byte)0x0F);
		assertEquals(Utils.intFromBits((byte)0xFE, 4, 8), (byte)0x0E);
		assertEquals(Utils.intFromBits((byte)0x34, 2, 8), (byte)0x34);
	}

	@Test
	public void testCenterPad() {
		assertEquals(Utils.centerPad("hello", 9, ' '), "  hello  ");
	}

	@Test
	public void testLeftPad() {
		assertEquals(Utils.leftPad("hello", 10, ' '), "     hello");
	}

	@Test
	public void testRightPad() {
		assertEquals(Utils.rightPad("hello", 10, ' '), "hello     ");
	}

	@Test
	public void testByteFromHex() {
		assertEquals(Utils.byteFromHex("ab"), (byte)0xab);
	}

}
