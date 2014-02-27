package edu.mann.netsec.utils;

import java.lang.StringBuilder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

public class GridFormatter {
	private ArrayList<GridBox> boxes;
	private ArrayList<ArrayList<GridBox>> rows;
	private boolean compressed = false;

	public boolean isCompressed() {
		return compressed;
	}

	public void setCompressed(boolean compressed) {
		this.compressed = compressed;
	}

	public GridFormatter() {
		this.boxes = new ArrayList<GridBox>();
		this.rows = new ArrayList<ArrayList<GridBox>>();
	}

	public void append(int windowSize, String s) {
		this.boxes.add(new GridBox(windowSize, s, this.compressed));
	}

	public String format(int width) {
		assert width < 100;
		StringBuilder sb = new StringBuilder();
		// top row is 0...1...2..3. etc
		for(int i = 0; i < width; i++) {
			if (!this.compressed) sb.append(' ');
			if(i % 10 == 0) {
				sb.append(i / 10);
			} else {
				sb.append(' ');
			}
		}
		sb.append(' ');
		sb.append('\n');

		// second row is 012345...
		for(int i = 0; i < width; i++) {
			if (!this.compressed) sb.append(' ');
			sb.append(i % 10);
		}
		sb.append(' ');
		sb.append('\n');

		sb.append(delimiterRow(width));

		this.layout(width);

		for (List<GridBox> row : this.rows) {
			for (int r = 0; r < row.get(0).height(); r++) {
				sb.append('|');
				for (GridBox box : row) {
					sb.append(box.getRow(r));
					if (!this.compressed || box.bits > 1) sb.append('|');
				}
				sb.append('\n');
			}
			sb.append(delimiterRow(width));
		}

		return sb.toString();
	}

	private String delimiterRow(int width) {
		StringBuilder sb = new StringBuilder();

		for(int i = 0; i < width; i++) {
			if (this.compressed) {
				if (i == 0) sb.append('+');
				else sb.append('-');
			} else {
				sb.append('+');
				sb.append('-');
			}
		}
		sb.append('+');
		sb.append('\n');

		return sb.toString();
	}

	private void layout(int width) {
		assert width > 0;

		LinkedList<GridBox> boxes = new LinkedList<GridBox>(this.boxes);

		while ( ! boxes.isEmpty()) {
			// we need to work out how tall this row should be
			ArrayList<GridBox> row = new ArrayList<GridBox>();
			int curWidth = 0;
			int minHeight = 1;

			do {
				GridBox box = boxes.pop();
				curWidth += box.bits;
				minHeight = Math.max(minHeight, box.minHeight());
				row.add(box);
			} while (curWidth < width);

			// for now, must be exactly the right width.
			if (curWidth != width) {
				throw new RuntimeException("Layout failure.");
			}

			for (GridBox box : row) {
				box.layout(minHeight);
			}

			// add row to list
			this.rows.add(row);
		}
	}
}

class GridBox {
	public final int width;
	public final String s;

	private String[] inLines;
	private String[] unsizedOutLines;
	private String[] outLines;
	public int bits;
	
	public GridBox(int bits, String s) {
		this(bits, s, false);
	}
	
	public GridBox(int bits, String s, boolean compressed) {
		this.bits = bits;
		this.width = compressed ? Math.max(1, bits-1) : 2*bits-1;
		this.s = s;

		this.inLines = s.split("\n");

		this.layout();
	}

	public String getRow(int index) {
		this.layout();
		return this.outLines[index];
	}

	public int height() {
		return this.outLines.length;
	}

	public String[] layout() {
		ArrayList<String> out = new ArrayList<String>();

		// fill up each line with input lines
		int iOut = 0;
		for (String line : this.inLines) {
			while ( ! line.equals("")) {
				if (out.size() <= iOut) { out.add(""); }
				int remaining = this.width - out.get(iOut).length();
				if (remaining >= line.length()) {
					out.set(iOut, out.get(iOut) + line);
					line = "";
				} else {
					// put all we can in there and then move to the next line
					out.set(iOut, out.get(iOut) + line.substring(0, remaining));
					line = line.substring(remaining);
				}
				iOut += 1;
			}
		}

		this.unsizedOutLines = out.toArray(new String[out.size()]);
		return this.unsizedOutLines;
	}

	public String[] layout(int height) {
		String[] out = this.layout();

		if (height < this.minHeight()) {
			System.out.printf("height = %d, minHeight = %d", height, this.minHeight());
			throw new RuntimeException("Layout failure");
		}

		// grow out array to correct size
		out = Arrays.copyOf(out, height);

		// change nulls to empty strings
		for (int i = 0; i < out.length; i++) {
			if (out[i] == null) {
				out[i] = "";
			}
		}

		// possibly shift down
		boolean anyLinesNonEmpty = false;
		for (String line : out) {
			if (!line.equals("")) anyLinesNonEmpty = true;
		}
		if (out.length > 2 && anyLinesNonEmpty) {
			while (out[out.length-1].equals("") && out[out.length-2].equals("")) {
				Collections.rotate(Arrays.asList(out), 1);
			}
		}

		// pad each line to the correct width
		for (int i = 0; i < out.length; i++) {
			out[i] = Utils.centerPad(out[i], this.width, ' ');
		}

		this.outLines = out;
		return out;
	}

	public int minHeight() {
		return this.unsizedOutLines.length;
	}

}
