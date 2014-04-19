package edu.mann.netsec.ids.snort;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.javatuples.Pair;
import org.junit.Test;

import edu.mann.netsec.ids.snort.options.SnortConditionIPID;
import edu.mann.netsec.ids.snort.options.SnortConditionIPTOS;

public class SnortOptionTest {

	@Test
	public void testParseRuleOptions() {
		try {
			List<SnortOption> ops;
			
			ops = SnortOption.parseRuleOptions("(id:123; tos:123;)");
			assertEquals(SnortConditionIPID.class, ops.get(0).getClass());
			
			assertEquals(SnortConditionIPTOS.class, ops.get(1).getClass());
		} catch (SnortInvalidOptionException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testExtractWithQuotes() {
		// raw strings would be really helpful here...
		try {
			Pair<String, String> pair;
			
			pair = SnortOption.extractWithQuotes("foo;");
			assertEquals("foo", pair.getValue0());
			assertEquals(";", pair.getValue1());
			
			pair = SnortOption.extractWithQuotes("\"f;b\";");
			assertEquals("f;b", pair.getValue0());
			assertEquals(";", pair.getValue1());
			
			pair = SnortOption.extractWithQuotes("\" \\\\ \""); // this is two backslashes in a row
			assertEquals(" \\ ", pair.getValue0());
			assertEquals("", pair.getValue1());
		} catch (SnortInvalidOptionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}
}
