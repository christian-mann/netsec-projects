public class Utils {

	/*
	 * closed interval
	 * assumes big-endian
	 */
	public static int intFromBits(byte[] a, int firstBit, int lastBit) {
		int ret = 0;
		int iByte = firstBit;

		while (iByte + 8 < a.length * 8 && iByte + 8 < lastBit) {
			ret *= 8;
			ret += a[iByte];
		}

		ret += a[iByte] >> (lastBit - iByte*8)
	}

	public static int intFromBits(byte a, int firstBit, int lastBit) {
		int ret = 0;
		int iByte = firstBit;

		ret += a[iByte] >> (lastBit - iByte*8)
	}
}
