package jmrclib.test;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import jmrclib.Utils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class UtilsTest {
	@Rule
    public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void testBytesToInt2() 
	{
		String testVals[] =     {"0", "1", "9", "11", "99", "1636", "64899"};
		int expectedResults[] = { 0,   1,   9,   11,   99,   1636,   64899 };
		
		for (int i = 0; i < testVals.length; ++i)			
		{
			String val = testVals[i];
			int actualResult = Utils.BytesToInt2(val.getBytes(StandardCharsets.ISO_8859_1));
			assertEquals("BytesToInt2 failed:", expectedResults[i], actualResult);
		}
	}

	@Test
	public void testBytesToInt2Exception() 
	{
		thrown.expect(NumberFormatException.class);
		Utils.BytesToInt2("qwerty".getBytes(StandardCharsets.ISO_8859_1));
	}
	
	@Test
	public void testStringFromByteTag()
	{
		String testVals[] = {"001", "SYS", "880", "ÃWE", "ÄÅÃ"};
		String expectedResults[] = testVals;
		String actualResults[] = new String[testVals.length];
		
		for (int i = 0; i < testVals.length; ++i)
		{
			String val = testVals[i];
			actualResults[i] = Utils.stringFromByteTag(val.getBytes(StandardCharsets.ISO_8859_1), 0, 3);
		}
		
		assertArrayEquals("stringFromByteTag failed", expectedResults, actualResults);
	}
	
	@Test 
	public void testReadBuf() throws IOException
	{
		byte testVal[] = {1, 2, 3, 4, 5, 6, 7, 8, 9};
		byte expectedResult[] = testVal;
		byte actualResult[] = new byte[testVal.length];
		InputStream in = new ByteArrayInputStream(testVal);
		
		int countRead = Utils.readBuf(in, actualResult, 0, actualResult.length-5);
		countRead += Utils.readBuf(in, actualResult, actualResult.length-5, 5);
		
		assertEquals("readBuf failed", expectedResult.length, countRead);
		assertArrayEquals("readBuf failed", expectedResult, actualResult);
	}
}
