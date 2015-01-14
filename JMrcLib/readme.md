# Библиотека JMrcLib

Библиотека предназначена для работы с записями в формате ISO-2709 (MARC21, UNIMARC, RUSMARC и т.п.) См. [MARC 21 Format](http://www.loc.gov/marc/bibliographic/)

Основные функции:

 - чтение MARC записей из потока
 - создание MARC записи из буфера с данными

## Основные классы

### Класс `InputSource`  
Предназначен для чтения MARC записей из потока.  
Реализует `Iterable<Record>`, `Closeable`

Пример.
```java
InputStream in;
...
RecordFactory recordFactory = new DefaultRecordFactory("001"); // "001" - поле с ID записи
...
try (InputSource mrcSource = new InputSource(in, recordFactory))
{
	for (Record record :mrcSource) 
	{
		...
	}
} // здесь вызывается mrcSource.close(), который закрывает in
```

### Интерфейс `Record`
Наследует `Iterable<Field>`  
Реализован в классе `RecordImpl`  
Предназначен для работы с MARC записью

Основные функции:

 - ID записи
 - хеш записи
 - итератор по всем полям
 - итератор по полям с заданным тегом
 - первое поле с заданным тегом
 
Пример.
```java
Record record;
...
for (Field field100: record.getFields("100"))
{
	...
}
```

### Интерфейс `Field`
Наследует `Iterable<Subfield>`  
Реализован в классе `FieldImpl`  
Предназначен для работы с MARC полями  
Может быть либо контрольным полем, либо полем данных. Поле данных имеет индикаторы и список подполей с данными (список может быть пустым). Контрольное поле не имеет индикаторов и его данные записываются в единственное подполе с кодом `'\0'`, в котором содержатся данные поля.

Основные функции:

 - итератор по всем подполям
 - итератор по подполям с заданным кодом
 - первое подполе с заданным тегом
  
Пример.
```java
Field field;
...
Subfield sfA = field.getFirstSubfield('a');
System.out.println(sfA.getData());
for (Subfield sf: field.getSubfields('j'))
{
...
}
```
  
### Интерфейс `Subfield`
Реализован в классе `SubfieldImpl`  
Предназначен для работы с подполями. Содержит код и данные подполя.

 