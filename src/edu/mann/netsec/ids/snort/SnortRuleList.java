package edu.mann.netsec.ids.snort;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import edu.mann.netsec.packets.Packet;

@SuppressWarnings("serial")
public class SnortRuleList extends LinkedList<SnortRule> {
	public void handlePacket(Packet p) {
		for (SnortRule rule : this) {
			if (rule.matches(p)) {
				rule.handlePacket(p);
			}
		}
	}

	public static SnortRuleList parseFile(File file) throws IOException, SnortInvalidRuleException, SnortInvalidOptionException {
		SnortRuleList rules = new SnortRuleList();
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while ((line = br.readLine()) != null) {
			SnortRule r = SnortRule.parse(line);
			rules.add(r);
		}
		
		br.close();
		
		return rules;
	}
}
