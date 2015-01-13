package jmrclib.test;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.atomic.AtomicInteger;

import jmrclib.DefaultRecordFactory;
import jmrclib.Field;
import jmrclib.Record;
import jmrclib.RecordFactory;
import jmrclib.Subfield;

import org.junit.Test;

import static org.junit.Assert.*;

public class RecordTest {
	
	private static byte[] getRecBuf() throws Exception
	{
		URL url = RecordTest.class.getResource("/rec1.mrc");
		return Files.readAllBytes(Paths.get(url.toURI()));
	}
	
	private static Record getRecord(String idFieldCode) throws Exception
	{
		byte recBuf[] = getRecBuf();
		RecordFactory recordFactory = new DefaultRecordFactory(idFieldCode); 
		return recordFactory.createRecord(recBuf);
	}

	@Test
	public void testLeader() throws Exception
	{
		Record rec = getRecord(null);
		
		assertEquals("getLength failed", 1005, rec.getLength());
		assertEquals("getType failed", String.valueOf('a'), String.valueOf(rec.getType()));
		assertEquals("getBiblevel failed", String.valueOf('m'), String.valueOf(rec.getBiblevel()));
		
		assertEquals("getId failed", "", rec.getId());
	}
	
	@Test
	public void testID() throws Exception
	{
		Record rec = getRecord("001");
		
		assertEquals("getId failed", "000000001", rec.getId());
		
		assertNotNull("getDigest failed", rec.getDigest());
		
		Calendar cal = GregorianCalendar.getInstance();
		cal.clear();
		cal.set(2008, 5-1, 28, 12, 0, 0); // Месяцы начинаются с 0 (Январь==0)		
		assertEquals("getLatestTransactionDate failed", cal.getTime(), rec.getLatestTransactionDate());
	}
	
	@Test
	public void testFields() throws Exception
	{
		Record rec = getRecord("001");
		
		Field f003 = rec.getFirstField("003");
		assertSame("Directory field cache test failed", f003, rec.getFirstField("003"));
		assertTrue("isControl test failed", f003.isControl());
		assertEquals("003 data failed", "RuMoRGB", f003.getFirstSubfield('\0').getData());
		
		Field f260 = rec.getFirstField("260");
		assertEquals("Field 260 indicator 1 test failed.", String.valueOf(' '), String.valueOf(f260.getInd1()));
		assertEquals("Field 260 indicator 2 test failed.", String.valueOf(' '), String.valueOf(f260.getInd2()));
		Subfield sf260a = f260.getFirstSubfield('a');
		assertNotNull("Subfield 260a not found", sf260a);
		assertEquals("Subfield 260a data test failed.", "Санкт-Петербург", sf260a.getData());
		Subfield sf260c = f260.getFirstSubfield('c');
		assertNotNull("Subfield 260c not found", sf260c);
		assertEquals("Subfield 260c data test failed.", "1992", sf260c.getData());
		
		Field f856 = rec.getFirstField("856");
		assertNotNull("Field 856 not found", f856);
		assertEquals("Field 856 indicator 1 test failed.", String.valueOf('4'), String.valueOf(f856.getInd1()));
		assertEquals("Field 856 indicator 2 test failed.", String.valueOf('1'), String.valueOf(f856.getInd2()));
		
		AtomicInteger totalFields = new AtomicInteger(0);
		rec.forEach(field -> totalFields.incrementAndGet());
		assertEquals("Fields count test failed.", 19, totalFields.get());
	}
}
