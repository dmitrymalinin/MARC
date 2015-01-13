package jmrclib;

/**
 * MARC-поле. Может быть либо контрольным полем, либо полем данных.
 * Поле данных имеет индикаторы и список подполей с данными (список может быть пустым).
 * Контрольное поле не имеет индикаторов и содержит единственное подполе с кодом {@code '\0'}, в котором содержатся данные поля.
 * @author malinin
 *
 */
public interface Field extends Iterable<Subfield>{
	/**
	 * Проверяет существование подполя с кодом {@code code} в данном поле
	 * @param code - код подполя
	 * @return {@code true}, если такое подполе есть, иначе - {@code false}
	 */
	boolean isSubfieldExists(char code);
	
	/**
	 * Возвращает первое по порядку следования в поле подполе с кодом {@code code}, или {@code null}, если такого поля нет
	 * @param code - код подполя
	 * @return Первое по списку подполе с кодом {@code code}
	 */
	Subfield getFirstSubfield(char code);
	
	/**
	 * 
	 * @return Метка поля ({@code "100", "001", "500"}, и т.п.)
	 */
	String getTag();
	
	/**
	 * 
	 * @return Первый индикатор
	 */
	char getInd1();
	
	/**
	 * 
	 * @return Второй индикатор
	 */
	char getInd2();
	
	/**
	 * 
	 * @return {@code true}, если это контрольное поле, иначе - {@code false}
	 */
	boolean isControl();

	/**
	 * 
	 * @param code - код подполя
	 * @return Подполя с кодом {@code code}
	 */
	Iterable<Subfield> getSubfields(char code);
}
