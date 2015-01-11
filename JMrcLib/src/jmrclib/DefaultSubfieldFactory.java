package jmrclib;

/**
 * Фабрика подполей<br/>
 * Возвращает {@code new Subfield(code, data);}
 * @author malinin
 *
 */
public class DefaultSubfieldFactory implements SubfieldFactory {

	@Override
	public Subfield createSubfield(char code, String data) {
		return new SubfieldImpl(code, data);
	}
}
