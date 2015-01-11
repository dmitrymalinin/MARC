package jmrclib;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;

public class Utils {	

	/**
	 * Converts {@code bytes} to {@code int}
	 * @param bytes
	 * @return
	 * @throws NumberFormatException
	 */
	public static int BytesToInt2(byte bytes[]) throws NumberFormatException
	{
		return BytesToInt2(bytes, 0, bytes.length);
	}
	
	/**
	 * Converts {@code bytes} to {@code int}
	 * @param bytes
	 * @param offset
	 * @param len
	 * @return
	 * @throws NumberFormatException
	 */
	public static int BytesToInt2(byte bytes[], int offset, int len) throws NumberFormatException
	{
		int res = 0;
		int d;
		final int maxi = offset + len;
		for (int i = offset; i < maxi; ++i)
		{
			d = bytes[i]-'0';
			if (d < 0 || d > 9) 
			{
				throw new NumberFormatException("BytesToInt Error '"+new String(bytes, offset, len)+"'");
			}
			res = res*10 + d;
		}
		
		return res;
	}
	
	/**
	 * Reads {@code length} bytes from {@code source} into {@code buf} starting from {@code offset}
	 * @param source - input
	 * @param buf - result buffer
	 * @param offset - offset in result buffer
	 * @param length - count bytes to read
	 * @return count bytes actually read
	 * @throws IOException
	 */
	public static int readBuf(InputStream source, byte[] buf, int offset, int length) throws IOException
	{
        int nread = 0;
        int n;
		while ((n = source.read(buf, offset+nread, length - nread)) > 0)
            nread += n;
		
		return nread;
	}
	
	/**
	 * Creates String from three bytes field's tag
	 * @param buf
	 * @param offset
	 * @param length
	 * @return
	 */
	public static String stringFromByteTag(byte[] buf, int offset, int length)
	{
		if (length < 3) throw new InvalidParameterException("Utils.stringFromByteTag(): length < 3"); 
		final char chars[] = {(char)(buf[offset]&0xFF), (char)(buf[offset+1]&0xFF), (char)(buf[offset+2]&0xFF)};
		return new String(chars);
	}

//	public static String decodeBuf(byte[] buf, int offset, int length, CharsetDecoder decoder) throws CharacterCodingException
//	{
//		decoder.reset();
//		final ByteBuffer bb = ByteBuffer.wrap(buf, offset, length);
////		final CharBuffer cb = decoder.decode(bb);
//		final int charsLen = (int) (length * decoder.maxCharsPerByte());
//        final char[] ca = new char[charsLen];
//        final CharBuffer cb = CharBuffer.wrap(ca);
//        CoderResult cr = decoder.decode(bb, cb, true);
//        if (!cr.isUnderflow())
//            cr.throwException();
//        cr = decoder.flush(cb);
//        if (!cr.isUnderflow())
//            cr.throwException();
//        
//		return new String(ca, 0, cb.position());
//	}
	
	/*public static void main(String args[]) throws Exception
	{
		int vals[] = {0, 1, 2, 5,
				10, 11, 12, 19, 99,
				100, 101, 156, 589,
				1000, 3458, 9999,
				10000, 35688, 99999};
		
		for (int val: vals)
		{
			byte byte_val[] = String.valueOf(val).getBytes();
//			int res_val = BytesToInt(byte_val, 0, byte_val.length);
			int res_val = BytesToInt2(byte_val);
			System.out.printf("%6d  %6d\n", val, res_val);
			if (val != res_val) throw new Exception("error!");
		}
		
		final java.nio.charset.Charset charset = java.nio.charset.StandardCharsets.UTF_8;
		final CharsetDecoder decoder = charset.newDecoder();
		final byte buf[][] = {
				"qwerty".getBytes(charset),
				"йцукен".getBytes(charset),
				"йцукен_123".getBytes(charset)
				};
		
		for (byte buf_i[]:buf)
		{
			System.out.printf("[%10s] : [%s]\n",
					new String(buf_i, 0, buf_i.length, charset), decodeBuf(buf_i, 0, buf_i.length, decoder));
		}
	}*/
}
