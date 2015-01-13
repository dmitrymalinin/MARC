package jmrclib;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class RecordImpl implements Record {
	/** Record's buffer */
	private final byte[] buf;
	private byte[] digest = null;
	private String id = null;
	/** Base address of data (characters 12-16). Адрес, относительно которого указаны позиции в Directory */
	private final int baseOffset;
	/** Type of record */
	private final char type;
	/** Bibliographic level */
	private final char biblevel;
	private final String idFieldCode; 
	private final FieldFactory fieldFactory;
	/** Data encoding */
	private final Charset charset;
	
	/** Directory */
	private final Directory dir;
	
	/** yyyyMMddHHmmss */
	private static final SimpleDateFormat latestTransactionDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	
	public RecordImpl(byte[] buf, int offset, int length, String idFieldCode, FieldFactory fieldFactory, Charset charset) throws MrcException, NoSuchAlgorithmException {

		if (buf == null) throw new MrcException("RecordImpl: wrong buf parameter");
		
		if (length < 25 || length > 99999) throw new MrcException("RecordImpl: Invalid record length");

		// check record terminator (ASCII 1D hex)
		if (buf[length-1] != RT)
		{
			throw new MrcException(String.format("RecordImpl: Bad record format: record terminator (ASCII 1D hex) check failed. Actially the record ended with 0x%X", buf[length-1]));
		}
		
		if (idFieldCode != null && idFieldCode.length() != 3 && idFieldCode.length() != 4) throw new MrcException("idFieldCode must be 3 or 4 chars");

		this.buf = new byte[length];
		System.arraycopy(buf, offset, this.buf, 0, length);
		this.idFieldCode = idFieldCode;
		this.fieldFactory = fieldFactory;
		this.charset = charset;

        this.type = (char) (this.buf[6]&0xFF);
        this.biblevel = (char) (this.buf[7]&0xFF);
        
        this.baseOffset = Utils.BytesToInt2(this.buf, 12, 5);        
    	if (this.baseOffset == 0 || this.baseOffset >= length - 1/*except RT*/)
    	{
    		throw new MrcException("Invalid base offset");
    	}

    	this.dir = new Directory(this.buf);
	}

	@Override
	public byte[] getBuf() {
		return buf;
	}

	@Override
	public int getLength() {
		return buf.length;
	}

	@Override
	public byte[] getDigest() throws Exception {
		if (digest == null)
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(buf);
			this.digest = md.digest();
		}
		return digest;
	}
	
	@Override
	public char getType() {
		return type;
	}
	
	@Override
	public char getBiblevel() {
		return biblevel;
	}
	
	@Override
	public String getLeader() throws Exception
	{
		return new String(buf, 0, LeaderLength, charset);
//		return Utils.decodeBuf(buf, 0, LeaderLength, charsetDecoder);
	}
	
	@Override
	public String getId() throws Exception {
		if (id == null) readId();
		return id;
	}

	private void readId() throws Exception
	{
		if (idFieldCode == null) 
		{
			id = "";
			return;
		}
			
		final String tag = idFieldCode.substring(0, 3);
		final char code;
		
		if (idFieldCode.length() > 3)
		{
			code = idFieldCode.charAt(3);
		} else
		{
			code = '\0';
		}
		
		Field idField = getFirstField(tag);
		if (idField != null)
		{
			final Subfield sf = idField.getFirstSubfield(code);
			final String sfData = sf.getData();
			if (sfData != null)
				id = sfData.trim();
		}
		
		if (id == null)	id = "";
	}

	public boolean isFieldExists(String tag)
	{
		return dir.isEntryExists(tag);	
	}
	
	/** Iterator over all fields */
	@Override
	public Iterator<Field> iterator() {
		return new AllFieldsIterator();
	}
	
	@Override
	public Iterable<Field> getFields(String tag) {		
		return new Iterable<Field>() {			
			@Override
			public Iterator<Field> iterator() {
				return new FieldsWithTagIterator(tag);
			}
		};
	}

	@Override
	public Field getFirstField(String tag) throws Exception {
		final List<Directory.Entry> entries = dir.getEntries(tag);
		if (entries != null && entries.size() > 0)
		{
			final Directory.Entry dirEntry = entries.get(0);
			if (dirEntry.field == null)
				dirEntry.field = fieldFactory.createField(buf, dirEntry, baseOffset, charset);
			return dirEntry.field;
		}
		return null;
	}

	private class FieldsWithTagIterator implements Iterator<Field>
	{
		final Iterator<Directory.Entry> entriesIterator;
		FieldsWithTagIterator(String tag)
		{
			List<Directory.Entry> entries = dir.getEntries(tag); 
			entriesIterator = entries != null ? entries.iterator() : null;
		}

		@Override
		public boolean hasNext() {
			return entriesIterator != null && entriesIterator.hasNext();
		}

		@Override
		public Field next() {
			if (entriesIterator == null || !entriesIterator.hasNext()) throw new NoSuchElementException();
			Directory.Entry dirEntry = entriesIterator.next();
			
			try {
				if (dirEntry.field == null)
					dirEntry.field = fieldFactory.createField(buf, dirEntry, baseOffset, charset);
				return dirEntry.field;
			} catch (Exception e) {
				throw new NoSuchElementException(e.toString());
			}
		}		
	}


	private class AllFieldsIterator implements Iterator<Field>
	{
		Iterator<Directory.Entry> dirIterator = dir.iterator();
		
		@Override
		public boolean hasNext() {
			return dirIterator.hasNext();
		}

		@Override
		public Field next() {
			if (!dirIterator.hasNext()) throw new NoSuchElementException();
			Directory.Entry dirEntry = dirIterator.next();
			
			try {
				if (dirEntry.field == null)
					dirEntry.field = fieldFactory.createField(buf, dirEntry, baseOffset, charset);
				return dirEntry.field;
			} catch (Exception e) {
				throw new NoSuchElementException(e.toString());
			}
		}		
	}


	@Override
	public Date getLatestTransactionDate() throws Exception {
		Date res = null;
		final Field f005 = getFirstField("005");
		if (f005 != null)
		{
			final Subfield sf = f005.getFirstSubfield('\0');
			if (sf != null)
			{
				final String datestr = sf.getData();
				if (datestr != null && !datestr.isEmpty())
				{
//					final SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
					res = latestTransactionDateFormat.parse(datestr);
				}
			}
		}		
			
		return res;
	}
}
