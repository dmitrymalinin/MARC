package jmrclib.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import jmrclib.DefaultRecordFactory;
import jmrclib.Field;
import jmrclib.InputSource;
import jmrclib.MrcException;
import jmrclib.Record;
import jmrclib.Subfield;

import org.junit.Test;

public class InputSourceTest {
	/**
	 * Тест нового варианта библиотеки
	 * @throws Exception 
	 */
	@Test
	public void test2() throws Exception
	{
		final DefaultRecordFactory recordFactory = new DefaultRecordFactory("001");
		InputStream in1 = getClass().getResourceAsStream("/rec1.mrc");
		assertNotNull("resource not found", in1);
		try (InputSource mrcSource = new InputSource(in1, recordFactory))
		{
			int processedRecords1 = 0;
			int i = 0;
			for (Record record :mrcSource) {
				i++;
				String id = record.getId();
				Field authorField = record.getFirstField("100");
				Subfield authorSf = authorField.getFirstSubfield('a');
				String author = authorSf.getData();
				
				switch (i) {
				case 1:
					assertEquals("000000001", id);
					assertEquals("'Абд Ал-'Азиз Джа'фар Бин 'Акид", author);
					break;
				default:
					throw new MrcException("Неверное значение i");
				}
				processedRecords1++;
			}
			assertEquals(1, processedRecords1);
		}
		/////////////////
		InputStream in2 = getClass().getResourceAsStream("/rec2.mrc");
		assertNotNull("resource not found", in2);		
		try (InputSource mrcSource = new InputSource(in2, recordFactory))
		{
			int processedRecords2 = 0;
			int i = 0;
			for (Record record :mrcSource) {
				i++;
				String id = record.getId();
				Field authorField = record.getFirstField("100");
				Subfield authorSf = authorField.getFirstSubfield('a');
				String author = authorSf.getData();
				
				switch (i) {
				case 1:
					assertEquals("000000001", id);
					assertEquals("'Абд Ал-'Азиз Джа'фар Бин 'Акид", author);
					break;
				case 2:
					assertEquals("000000002", id);
					assertEquals("Абдувахитов, Абдужабар Абдусаттарович", author);
					break;
				default:
					throw new MrcException("Неверное значение i");
				}
				processedRecords2++;
			}
			assertEquals(2, processedRecords2);
		}
	}
	
}
