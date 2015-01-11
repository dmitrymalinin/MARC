Библиотека JMrcLib

Библиотека предназначена для работы с записями в формате ISO-2709 (MARC21, UNIMARC, RUSMARC и т.п.)

Основные функции:
 - чтение MARC записей из потока
 - создание MARC записи из буфера с данными

Основные классы

InputSource реализует Iterable<Record>, Closeable

InputStream in;
...
RecordFactory recordFactory = new DefaultRecordFactory("001"); // "001" - поле с ID записи
...
try (InputSource mrcSource = new InputSource(in, recordFactory))
{
	for (Record record :mrcSource) {
	}
}