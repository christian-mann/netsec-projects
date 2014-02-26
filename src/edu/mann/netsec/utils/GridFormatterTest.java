package edu.mann.netsec.utils;


public class GridFormatterTest {
	public static void main(String[] args) {
		GridFormatter gf = new GridFormatter();

		gf.append(4, "Hello\nHello");
		gf.append(4, "World");
		gf.append(1, "ABC");
		gf.append(2, "AB");
		gf.append(5, "Moon");
		System.out.println(gf.format(8));
	}
}
