package edu.mann.netsec.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import jnr.netdb.Service;

public class Utils {

	/*
	 * half-open interval
	 * assumes big-endian
	 * java is only signed (which is retarded) so this can't be more than 31 bits long
	 */
	public static int intFromBits(byte[] a, int firstBit, int lastBit) {
		
		if (firstBit > lastBit) {
			throw new IllegalArgumentException("firstBit should be <= lastBit");
		}
		
		if (firstBit >= a.length * 8) {
			throw new IllegalArgumentException("firstBit not within array bounds");
		}
		
		if (lastBit > a.length * 8) {
			throw new IllegalArgumentException("lastBit not within array bounds");
		}
		
		if (lastBit - firstBit > 31) {
			throw new IllegalArgumentException("int can only be up to 31 bits long");
		}
		
		if (firstBit >= 8) {
			return intFromBits(Arrays.copyOfRange(a, 1, a.length), firstBit-8, lastBit-8);
		}
		
		if (0 <= firstBit && lastBit <= 8) {
			return intFromBits(a[0], firstBit, lastBit);
		}
		
		if (0 <= firstBit && 8 < lastBit) {
			int val = intFromBits(a[0], firstBit, 8);
			if (16 < lastBit) {
				val <<= 8;
			} else {
				val <<= (lastBit - 8);
			}
			val += intFromBits(Arrays.copyOfRange(a, 1, a.length), 0, lastBit-8);
			return val;
		}
		
		throw new IllegalStateException("intFromBits broken :(");
	}

	/*
	 * half-open interval
	 * assumes big-endian
	 */
	public static int intFromBits(byte a, int firstBit, int lastBit) {
		a = (byte) (a & ((0x01 << (8-firstBit)) - 1));
		a = (byte) ((a & 0xFF) >> (8 - lastBit));
		return a;
	}

	public static String centerPad(String s, int size, char pad) {
		int toPad = size - s.length();
		s = Utils.leftPad(s, size - (toPad + 1) / 2, pad);
		s = Utils.rightPad(s, size, pad);
		return s;
	}

	public static String leftPad(String s, int size, char pad) {
		while (s.length() < size) {
			s = pad + s;
		}
		return s;
	}

	public static String rightPad(String s, int size, char pad) {
		while (s.length() < size) {
			s = s + pad;
		}
		return s;
	}
	
	public static byte byteFromHex(String hex) {
		if (hex.length() != 2) {
			throw new IllegalArgumentException("byteFromHex(\""+hex+"\") is invalid");
		}
		
		return (byte) (
				(Character.digit(hex.charAt(0), 16) << 4) + 
				Character.digit(hex.charAt(1), 16));
	}
	
	public static void eatNetDBWarning() {
		PrintStream err = System.err;
		System.setErr(new PrintStream(new OutputStream() {
			@Override public void write(int b) throws IOException {}
		}));
		try {
			Service.getServiceByPort(80, "tcp");
		} catch (NoClassDefFoundError e) {
			// well... whatever then
			
		} finally {
			System.setErr(err);
		}
	}
}
