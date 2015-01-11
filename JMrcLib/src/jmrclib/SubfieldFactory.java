package jmrclib;

/**
 * Фабрика подполей
 * @author malinin
 *
 */
public interface SubfieldFactory {
	/**
	 * Создаёт подполе
	 * @param code - код подполя ({@code '\0'} для контрольного поля)
	 * @param data - данные подполя
	 * @return созданное подполе 
	 */
	Subfield createSubfield(char code, String data);
}
