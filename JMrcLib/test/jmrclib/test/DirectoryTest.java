package jmrclib.test;

import static org.junit.Assert.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import jmrclib.Directory;

public class DirectoryTest {

	@Test
	public void testDirectory() {
		String src = 
				"01005"
				+ "nam a2200253 i 4500"
				+ "FMT000300000"
				+ "001001000003"
				+ "100005800155"
				+ "245028000213"
				+ "245015000493"
				+ "SYS001000741"
				+ "\u001E";
		byte buf[] = src.getBytes(StandardCharsets.ISO_8859_1);
		Directory dir = new Directory(buf);
		
		AtomicInteger totalEntries = new AtomicInteger(0);
		dir.forEach(entry -> totalEntries.incrementAndGet());
		assertEquals(6, totalEntries.get());
		
		List<Directory.Entry> entryFMT = dir.getEntries("FMT");
		assertEquals(1, entryFMT.size());
		assertEquals("(tag=FMT, len=3, pos=0)", entryFMT.get(0).toString());
		
		List<Directory.Entry> entry001 = dir.getEntries("001");
		assertEquals(1, entry001.size());
		assertEquals("(tag=001, len=10, pos=3)", entry001.get(0).toString());
		
		List<Directory.Entry> entry100 = dir.getEntries("100");
		assertEquals(1, entry100.size());
		assertEquals("(tag=100, len=58, pos=155)", entry100.get(0).toString());
		
		List<Directory.Entry> entry245 = dir.getEntries("245");
		assertEquals(2, entry245.size());
		assertEquals("(tag=245, len=280, pos=213)", entry245.get(0).toString());
		assertEquals("(tag=245, len=150, pos=493)", entry245.get(1).toString());
		
		List<Directory.Entry> entrySYS = dir.getEntries("SYS");
		assertEquals(1, entrySYS.size());
		assertEquals("(tag=SYS, len=10, pos=741)", entrySYS.get(0).toString());
	}

}
