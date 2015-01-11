package jmrclib;

public class SubfieldImpl implements Subfield {
	private final char code;
	private final String data;
	
	/**
	 * ctor
	 * @param code - код подполя ({@code '\0'} для контрольного поля)
	 * @param data - данные подполя
	 */
	public SubfieldImpl(char code, String data) {
		this.code = code;
		this.data = data;
	}

	@Override
	public char getCode() {
		return code;
	}

	@Override
	public String getData() {
		return data;
	}

}
