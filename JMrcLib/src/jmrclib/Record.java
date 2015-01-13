package jmrclib;

import java.util.Date;

/**
 *  MARC запись без привязки к конкретному формату. Наследует {@code Iterable}
 * @author malinin
 *
 */
public interface Record extends Iterable<Field>{
	/** Max record length */
	final int MaxRecordLength = 99999;
	/** Length of record size field */
	final int RecordSizeLength = 5;
	/** Leader length */
	final int LeaderLength = 24;
	/** Directory start position */
	final int DirectoryStartPosition = 24;
	/** Directory entry length */
	final int DirectoryEntryLength = 12;
	/** Field terminator (ASCII 1E hex) */
	final char FT = 0x1E;
	/** Delimiter (unit separator) (ASCII 1F hex) */
	final char US = 0x1F;
	/** Record terminator (ASCII 1D hex) */
	final char RT = 0x1D;
	
	/**
	 * Буфер с данными записи, прочитанными из потока 
	 * @return Record buffer */
	byte[] getBuf();
	
	/** 
	 * @return Record (buffer) length 
	 */
	int getLength();
	
	/** 
	 * Хеш записи
	 * @return Digest
	 * @throws Exception 
	 */
	byte[] getDigest() throws Exception;
	
	/**
	 * Type of record (position 6)
	 * @return caracter in position 6
	 */
	char getType();
	
	/**
	 *  Bibliographic level (position 7)
	 * @return caracter in position 7
	 */
	char getBiblevel();

	/**
	 * Leader
	 * @return leader as String
	 * @throws Exception 
	 */
	String getLeader() throws Exception;
	
	/**
	 * Возвращает список полей, или пустой список, если такого поля нет.
	 * @param tag - код поля, например "245"
	 * @return 
	 */
	Iterable<Field> getFields(String tag);
	
//	/**
//	 * Возвращает список значений поля данных, или пустой список, если такого поля нет.
//	 * @param fieldCode - код поля, например "245a"
//	 * @return список значений поля данных
//	 * @throws Exception
//	 */
//	Iterable<String> getFieldVals(String fieldCode) throws Exception;
	
	/**
	 * Возвращает первое по порядку следования в MARC записи поле с кодом {@code tag}, или {@code null}, если такого поля нет.
	 * @param tag - код поля, например "245"
	 * @throws Exception
	 * @return 
	 */
	Field getFirstField(String tag) throws Exception;
	
	/**
	 * Проверяет наличие поля в записи
	 * @param tag - тег поля
	 * @return {@code true}, если это поле есть в этой записи, иначе - {@code false}
	 */
	boolean isFieldExists(String tag);
	
	/**
	 * ID записи 
	 * @return Record ID
	 * @throws Exception 
	 */
	String getId() throws Exception;
	
	/**
	 * Returns Date and Time of Latest Transaction up to seconds
	 * from field 005
	 * @return Date and Time of Latest Transaction
	 * @throws Exception 
	 */
	public Date getLatestTransactionDate() throws Exception;
}
