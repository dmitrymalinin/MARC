package jmrclib;

/**
 * Фабрика записей Record
 * @author malinin
 *
 */
public interface RecordFactory {
	/**
	 * Создаёт запись Record из буфера 
	 * @param buf - буфер
	 * @param offset - смещение в буфере
	 * @param length - длина записи
	 * @return Созданная запись Record
	 * @throws Exception
	 */
	Record createRecord(byte[] buf, int offset, int length) throws Exception;
	
	/**
	 * Создаёт запись Record из буфера 
	 * @param buf - буфер
	 * @return Созданная запись Record
	 * @throws Exception
	 */
	default Record createRecord(byte[] buf) throws Exception
	{
		return createRecord(buf, 0, buf.length);
	}
}
