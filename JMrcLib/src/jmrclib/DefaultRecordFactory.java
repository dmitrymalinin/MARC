package jmrclib;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class DefaultRecordFactory implements RecordFactory {
	private final String idFieldCode;
	private final FieldFactory fieldFactory;
	private final Charset charset;

	/**
	 * ctor with {@code null} idFieldCode, {@code StandardCharsets.UTF_8} charset and {@code DefaultFieldFactory}
	 */
	public DefaultRecordFactory() {
		this(null, StandardCharsets.UTF_8, new DefaultFieldFactory());
	}
	
	/**
	 * ctor with {@code StandardCharsets.UTF_8}, {@code DefaultFieldFactory}
	 * @param idFieldCode - код поля, содержащего ID записи. Если ID в записи нет, то {@code idFieldCode} может быть {@code null}.
	 */
	public DefaultRecordFactory(String idFieldCode) {
		this(idFieldCode, StandardCharsets.UTF_8, new DefaultFieldFactory());
	}
	
	/**
	 * ctor with {@code DefaultFieldFactory}
	 * @param idFieldCode - код поля, содержащего ID записи. Если ID в записи нет, то {@code idFieldCode} может быть {@code null}.
	 * @param charset - кодировка данных записи
	 */
	public DefaultRecordFactory(String idFieldCode, Charset charset) {
		this(idFieldCode, charset, new DefaultFieldFactory());
	}
	
	/**
	 * ctor
	 * @param idFieldCode - код поля, содержащего ID записи. Если ID в записи нет, то {@code idFieldCode} может быть {@code null}.
	 * @param charset - кодировка данных записи
	 * @param fieldFactory - фабрика полей
	 */
	public DefaultRecordFactory(String idFieldCode, Charset charset, FieldFactory fieldFactory) {
		this.idFieldCode = idFieldCode;
		this.charset = charset;
		this.fieldFactory = fieldFactory;
	}

	@Override
	public Record createRecord(byte[] buf, int offset, int length) throws Exception 
	{
		return new RecordImpl(buf, offset, length, idFieldCode, fieldFactory, charset);
	}

}
