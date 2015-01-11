package jmrclib;

import java.nio.charset.Charset;

/**
 * Фабрика полей Field.
 * @author malinin
 *
 */
public interface FieldFactory {
	/**
	 * Creates MARC field
	 * @param buf - record's buffer
	 * @param offset - data offset
	 * @param length - length of record
	 * @param dirEntry - directory entry
	 * @param baseOffset - base address of data (characters 12-16). Адрес, относительно которого указаны позиции в Directory
	 * @param charset - data encoding
	 * @return new {@link jmrclib.Field}
	 * @throws Exception
	 */
	Field createField(byte buf[], int offset, int length, Directory.Entry dirEntry, int baseOffset, Charset charset) throws Exception;
	
	default Field createField(byte buf[], Directory.Entry dirEntry, int baseOffset, Charset charset) throws Exception
	{
		return createField(buf, 0, buf.length, dirEntry, baseOffset, charset);
	}
}
