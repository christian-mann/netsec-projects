
public class GridFormatter {
	public void append(int windowSize, String s) {
		this.boxes.append(GridBox(windowSize, s));
	}

	public String format(int width) {
		assert width < 100;
		StringBuilder sb = new StringBuilder();
		// top row is 0...1...2..3. etc
		for(int i = 0; i < width; i++) {
			sb.append(' ');
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
			sb.append(' ');
			sb.append(i % 10);
		}
		sb.append(' ');
		sb.append('\n');

		// third row is composed of +-+-+- etc
		for(int i = 0; i < width; i++) {
			sb.append('+');
			sb.append('-');
		}
		sb.append('-');
		sb.append('\n');
	}
}

class GridBox {
	public final int width;
	public final String s;
	public final int minHeight;

	private String[] inLines;
	private String[] outLines;
	
	public GridBox(int width, String s) {
		this.width = width * 2 - 1; // two characters per bit, but one character for border
		this.s = s;

		this.inLines = StringUtils.split(s, '\n');
		this.minHeight = this.inLines.length;
	}

	public String getRow(int index) {
		this.layout();
		return this.outLines[index];
	}

	public void layout(int height) {
		assert height >= this.minHeight;
		
		// initialize output lines
		String[] out = new String[this.height];
		for (int i = 0; i < this.height; i++) {
			out[i] = "";
		}

		// fill up each line with input lines
		int iOut = 0;
		for (String line : this.inLines) {
			while ( ! line.equals("")) {
				int remaining = width - out.get(iOut).size();
				if (remaining <= line.size()) {
					out[iOut] += line;
					line = ""
				} else {
					// put all we can in there and then move to the next line
					out[iOut] += line.substring(0, remaining);
					line = line.substring(remaining);
					iOut += 1;
				}
			}
		}

		// possibly shift down
		if (out.length > 2) {
			while (out[out.length-1].equals("") && out[out.length-2].equals("")) {
				Collections.rotate(Arrays.asList(out), 1);
			}
		}

		// pad each line to the correct width
		for (int i = 0; i < out.length; i++) {
			out[i] = StringUtils.center(out[i], this.width, ' ');
		}

		this.outLines = out;
	}
}
