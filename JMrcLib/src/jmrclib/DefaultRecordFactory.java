package jmrclib;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class DefaultRecordFactory implements RecordFactory {
	private final String idFieldCode;
	private final FieldFactory fieldFactory;
	private final Charset charset;

	/**
	 * ctor with StandardCharsets.UTF_8, DefaultFieldFactory
	 * @param idFieldCode
	 */
	public DefaultRecordFactory(String idFieldCode) {
		this(idFieldCode, StandardCharsets.UTF_8, new DefaultFieldFactory());
	}
	
	/**
	 * ctor with DefaultFieldFactory
	 * @param idFieldCode
	 * @param charset
	 */
	public DefaultRecordFactory(String idFieldCode, Charset charset) {
		this(idFieldCode, charset, new DefaultFieldFactory());
	}
	
	/**
	 * ctor
	 * @param idFieldCode
	 * @param charset
	 * @param fieldFactory
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
