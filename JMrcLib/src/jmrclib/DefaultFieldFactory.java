package jmrclib;

import java.nio.charset.Charset;

public class DefaultFieldFactory implements FieldFactory {
	private final SubfieldFactory subfieldFactory;
	
	public DefaultFieldFactory() {
		this.subfieldFactory = new DefaultSubfieldFactory();
	}
	
	public DefaultFieldFactory(SubfieldFactory subfieldFactory) {
		this.subfieldFactory = subfieldFactory;
	}

	@Override
	public Field createField(byte[] buf, int offset, int length, Directory.Entry dirEntry, int baseOffset, Charset charset) throws Exception
	{
		return new FieldImpl(buf, offset, length, dirEntry, baseOffset, subfieldFactory, charset);
	}

}
