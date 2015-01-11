package jmrclib;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

/**
 * Directory
 * @author malinin
 * Вариант с TreeMap
 */
public class Directory implements Iterable<Directory.Entry> {

	/** Field tag -> {@link Entry} */
	private final Map<String, List<Entry>> entryMap = new TreeMap<String, List<Entry>>();
	
	/**
	 * Creates Directory from record's buffer
	 * @param buf
	 */
	public Directory(byte[] buf)
	{
		this(buf, 0, buf.length);
	}
			
	/**
	 * Creates Directory from record's buffer
	 * @param buf
	 * @param offset
	 * @param length
	 */
	public Directory(byte[] buf, int offset, int length) {
		for (int i = offset+Record.DirectoryStartPosition; i < offset+length; i+=Record.DirectoryEntryLength) {
			if (buf[i] == Record.FT) break;
			addEntry(new Entry(Utils.stringFromByteTag(buf, i, 3), Utils.BytesToInt2(buf, i+3, 4), Utils.BytesToInt2(buf, i+3+4, 5)));
		}
		
//		entryMap.forEach((tag, entryList) -> System.out.println(tag+": "+entryList));
	}
	
	/** 
	 * Add {@link Entry} to entry map
	 */
	private void addEntry(Entry entry)
	{
		List<Entry> entries = entryMap.get(entry.tag);
		if (entries == null)
		{
			entries = new ArrayList<Entry>();
			entryMap.put(entry.tag, entries);
		}
		
		entries.add(entry);
	}
	
	/**
	 * Get entries, assosiated with tag
	 * @param tag - field tag
	 * @return List of entries if this tag exists in the map, else - {@code null}
	 */
	public List<Entry> getEntries(String tag)
	{
		return entryMap.get(tag);
	}
	
	public boolean isEntryExists(String tag)
	{
		return entryMap.containsKey(tag);
	}
	
	/** Directory entry */
	public static class Entry
	{
		/** 00-02 - Tag */
		final String tag;
		/** 03-06 - Field length. Length includes the indicators, subfield codes, data and field terminator associated with the field */
		final int len;
		/** 07-11 - Starting character position */
		final int pos;
		/** Созданное MARC поле, для кеширования */
		Field field = null;
		
		Entry(String tag, int len, int pos) {
			this.tag = tag;
			this.len = len;
			this.pos = pos;
		}

		@Override
		public String toString() {
			return "(tag=" + tag + ", len=" + len + ", pos=" + pos + ")";
		}	
		
	}

	/**
	 * Iterator over all entries
	 */
	@Override
	public Iterator<Entry> iterator() {
		return new EntriesIterator();
	}
	
	class EntriesIterator implements Iterator<Entry>	
	{

		Iterator<List<Entry>> entryMapValuesIterator = entryMap.values().iterator();
		Iterator<Entry> entryListIterator = null; 
		
		@Override
		public boolean hasNext() {
			if ((entryListIterator == null || !entryListIterator.hasNext()) && entryMapValuesIterator.hasNext())
				// Если entryListIterator не инициализирован или закончился
			{
				entryListIterator = entryMapValuesIterator.next().iterator();
			}
			return entryListIterator != null && entryListIterator.hasNext();
		}

		@Override
		public Entry next() {
			if (entryListIterator == null || !entryListIterator.hasNext()) throw new NoSuchElementException();
			return entryListIterator.next();
		}
		
	}
}
