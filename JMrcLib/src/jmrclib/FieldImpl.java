package jmrclib;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class FieldImpl implements Field {

	// internal states
	/** subfield code */
	private static final int ST_SF_CODE 		= 0;
	/** subfield data begin */
	private static final int ST_SF_DATA_BEGIN	= 1;
	/** subfield data in process */
	private static final int ST_SF_DATA 		= 2;
	/** delimiter */
	private static final int ST_DELIMITER		= 3;
	/** end of field */
	private static final int ST_F_END			= 4;
				
	private final String tag;
	private final char ind1;
	private final char ind2;
	/** {@code true}, если это контрольное поле */
	private final boolean control; 
	
	private final List<Subfield> subfields = new ArrayList<Subfield>();
	
	public FieldImpl(byte[] buf, int offset, int length, Directory.Entry dirEntry, int baseOffset, SubfieldFactory subfieldFactory, Charset charset) throws Exception {
		// check pos and len for out of buffer's boundary
		// if all right here, then all must be OK
		if (baseOffset + dirEntry.pos + dirEntry.len > length - 1/*except RT*/)
		{
			throw new MrcException("Field out of buffer.");
		}
		
		this.tag = dirEntry.tag;
		
		final int absFieldPos = offset+baseOffset+dirEntry.pos; // Абсолютная позиция начала поля (относительно начала buf)

		if (dirEntry.len > 3 && buf[absFieldPos+2] == Record.US && buf[absFieldPos+dirEntry.len-1] == Record.FT)
		{
			// data field
			// Structure of a Variable Data Field in MARC 21 Records
			//
			//	INDICATOR_1  INDICATOR_2  DELIMITER  DATA_ELEMENT_IDENTIFIER_1  DATA_ELEMENT_1  DELIMITER  ...  DATA_ELEMENT_IDENTIFIER_n  DATA_ELEMENT_n  FT

			this.control = false;
			
			this.ind1 = (char) (buf[absFieldPos+0]&0xFF); // на случай, если byte < 0
			this.ind2 = (char) (buf[absFieldPos+1]&0xFF);
			
			// parse field data			
			int state = ST_DELIMITER;
			
			int sfdataoffset = 0, sfdatalen = 0;
			char sfcode = '\0';

			for (int i = absFieldPos+2; i < absFieldPos+dirEntry.len; ++i) // start on the first delimiter
			{
				if (buf[i] == Record.US) 
					state = ST_DELIMITER;
				else if (buf[i] == Record.FT) 
					state = ST_F_END;

				switch (state)
				{
				case ST_SF_CODE:
					state = ST_SF_DATA_BEGIN;
					sfcode = (char) (buf[i]&0xFF);
					sfdatalen = 0;
					break;
				case ST_SF_DATA_BEGIN:
					sfdataoffset = i;
					sfdatalen = 1;
					state = ST_SF_DATA;
					break;
				case ST_SF_DATA:
					sfdatalen++;
					break;
				case ST_DELIMITER:
					state = ST_SF_CODE;				
				case ST_F_END:
					// insert subfield into map
					if (sfcode != '\0')
					{
						// by way sfdatalen mustn't go out of the field's boundary
//						String sfdata = Utils.decodeBuf(buf, sfdataoffset, sfdatalen, charsetDecoder);
						String sfdata = new String(buf, sfdataoffset, sfdatalen, charset);
						subfields.add(subfieldFactory.createSubfield(sfcode, sfdata));
						sfdataoffset = 0;
						sfdatalen = 0;
						sfcode = '\0';
					}
					break;
				default:
					break;
				}

				if (state == ST_F_END) break;			
			}

		} else if (dirEntry.len >= 1 && buf[absFieldPos+dirEntry.len-1] == Record.FT)
		{
			// control field
			// DATA_ELEMENT  FT
			this.control = true;
			this.ind1 = '\0';
			this.ind2 = '\0';
//			String data = Utils.decodeBuf(buf, offset+baseOffset+dirEntry.pos, dirEntry.len-1, charsetDecoder);
			String data = new String(buf, absFieldPos, dirEntry.len-1, charset);
			subfields.add(subfieldFactory.createSubfield('\0', data));
		} else
			throw new MrcException("Bad MARC record format. Directory entry: "+dirEntry.toString()+"  BaseOffset="+baseOffset);

	}

	@Override
	public boolean isSubfieldExists(char code) 
	{
		for (Subfield mrcSubfield : subfields) 
		{
			if (mrcSubfield.getCode() == code)
				return true;
		}
		return false;
	}

//	@Override
//	public List<Subfield> getSubfields() {
//		return subfields;
//	}
//		
//	@Override
//	public List<Subfield> getSubfields(char code) {
//		List<Subfield> res = new ArrayList<Subfield>();
//		for (Subfield mrcSubfield : subfields) {
//			if (mrcSubfield.getCode() == code)
//				res.add(mrcSubfield);
//		}
//		return res;
//	}

	@Override
	public Subfield getFirstSubfield(char code) 
	{
		for (Subfield mrcSubfield : subfields) 
		{
			if (mrcSubfield.getCode() == code)
				return mrcSubfield;
		}
		return null;
		
//		SubfieldsIterator iterator = new SubfieldsIterator(code);
//		if (iterator.hasNext())
//			return iterator.next();
//		else
//			return null;
	}

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public char getInd1() {
		return ind1;
	}

	@Override
	public char getInd2() {
		return ind2;
	}

	@Override
	public boolean isControl() {
		return control;
	}

	@Override
	public Iterator<Subfield> iterator() {
		return new SubfieldsIterator('\0');
	}
	
//	@Override
//	public Iterator<Subfield> iterator(char code) {
//		return new SubfieldsIterator(code);
//	}
	
	@Override
	public Iterable<Subfield> getSubfields(char code) {
		return new Iterable<Subfield>() {			
			@Override
			public Iterator<Subfield> iterator() {
				return new SubfieldsIterator(code);
			}
		};
	}


	class SubfieldsIterator implements Iterator<Subfield>
	{
		private final Iterator<Subfield> iterator = subfields.iterator();
		private final char code;
		private Subfield next = null;
		
		SubfieldsIterator(char code)
		{
			this.code = code;
		}
		
		@Override
		public boolean hasNext() {
			if (code == '\0')
			{
				next = iterator.hasNext() ? iterator.next() : null; 
			} else
			{
				Subfield sf;
				next = null;
				while (iterator.hasNext())
				{
					sf = iterator.next();
					if (sf.getCode() == code)
					{
						next = sf;
						break;
					}
				}
			}
			return next != null;
		}

		@Override
		public Subfield next() {
			if (next == null) throw new NoSuchElementException();
			return next;
		}
		
	}
}
