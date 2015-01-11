package jmrclib;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Источник MARC записей. Реализует Iterable по записям из потока.
 * @author malinin
 *
 */
public class InputSource implements Iterable<Record>, Closeable {
	private final InputStream stream;
	private final RecordFactory recordFactory;
	private final byte buf[] = new byte[Record.MaxRecordLength];
	
	/**
	 * ctor
	 * @param stream - поток с MARC записями
	 * @param recordFactory - фабрика классов Record
	 */
	public InputSource(InputStream stream, RecordFactory recordFactory) {
		this.stream = stream;
		this.recordFactory = recordFactory;
	}

	@Override
	public Iterator<Record> iterator() {
		return new MrcIterator();
	}

	private class MrcIterator implements Iterator<Record>
	{		
		@Override
		public boolean hasNext() {
			try {
				return stream.available() != 0;
			} catch (IOException e) {
				return false;
			}
		}
		
		@Override
		public Record next()
		{
			try {
				int size;
				
				if (Utils.readBuf(stream, buf, 0, Record.RecordSizeLength) != Record.RecordSizeLength)
					throw new MrcException("Unable to read record length");
				
				try {
					size = Utils.BytesToInt2(buf, 0, Record.RecordSizeLength);
				} catch (NumberFormatException e) {
					throw new MrcException("Wrong record size: " + new String(buf, 0, Record.RecordSizeLength));
				}
				
				if (size < 24)
					throw new MrcException("record size < 24");
				
				if (Utils.readBuf(stream, buf, Record.RecordSizeLength, size-Record.RecordSizeLength) != size-Record.RecordSizeLength)
					throw new MrcException("Unable to read record");
				
				return recordFactory.createRecord(buf, 0, size);
			} catch (Exception e) {
				throw new NoSuchElementException(e.toString());
			}
		}
	
		
	}

	@Override
	public void close() throws IOException {
		if (stream != null) 
			stream.close();
	}

}
