/**
 *
 * #license-begin#
 * MIT License
 *
 * Copyright (c) 2005 - 2022 admaDIC GbR - http://www.admadic.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * #license-end#
 *
 * $Id$ 
 */
package de.admadic.util;

/**
 * Provides basic bit operations on base of byte arrays and integers.
 * Most functions are static.
 * 
 * @author Rainer Schwarze
 */
public class BitOp {

	/**
	 * No instance
	 */
	protected BitOp() {
		super();
	}


	/**
	 * xor's the <code>data</code> with <code>xorpar</code>.
	 * (The input data is changed!)
	 * if xorpar is shorter than data, the method loops through xorpar.
	 * 
	 * @param data	the byte array to be xored
	 * @param xorpar	the byte array to xor the data with
	 */
	public static void xor(byte [] data, byte [] xorpar) {
		int xi = 0;
		for (int i=0; i<data.length; i++) {
			data[i] = (byte)(data[i] ^ xorpar[xi]);
			xi++;
			if (xi>=xorpar.length)
				xi = 0;
		}
	}

	/**
	 * Returns a byte array which is <code>data</code> xor'ed with 
	 * <code>xorpar</code>.
	 * (The input is preserved)
	 * if xorpar is shorter than data, the method loops through xorpar.
	 * 
	 * @param data		the byte array to be xored
	 * @param xorpar	the byte array to xor the data with
	 * @return 		Returns the xored output vector
	 */
	public static byte [] xorOut(byte [] data, byte [] xorpar) {
		byte [] out = new byte[data.length];
		System.arraycopy(data, 0, out, 0, data.length);
		xor(out, xorpar);
		return out;
	}

	/**
	 * Calculates the number of set bits of the specified int.
	 * (for instance b000000010111 has 4 bits set.)
	 * 
	 * @param mask	the int to calculate the number of set bits of. 
	 * @return		Returns the number of bits which are 1
	 */
	static int calculateBitCount(int mask) {
		int bitcount = 0;
		for (int i=0; i<32; i++) {
			if (((mask>>i)&1)!=0)
				bitcount++;
		}
		return bitcount;
	}

	/**
	 * 0x00000011 -> 1
	 * 0x00001111 -> 2
	 * 0x00111111 -> 3
	 * 0x11111111 -> 4
	 * @param mask 
	 * @return the number of bytes to represent this mask
	 */
	static int calculateByteBase(int mask) {
		if (mask==0) 
			return 0;
		for (int i=4-1; i>=0; i--) {
			if (((mask>>(i*8))&0x0ff)!=0)
				return i+1;
		}
		return 0;
	}

	/**
	 * Helper function for extract/insertBits
	 * @param mask
	 * @param table
	 */
	static void calculateBitIndexTable(int mask, int [][] table) {
		int tableidx = 0;
		for (int i=0; i<32; i++) {
			if (((mask>>i)&1)!=0) {
				table[tableidx][0] = i;		// bit index
				table[tableidx][1] = i / 8;	// byte index
				//System.out.println("tab = " + idxtable[idxtableidx][0] + " @ " + idxtable[idxtableidx][1]);
				tableidx++;
			}
		}
	}

	/**
	 * @param data
	 * @param mask
	 * @return	the bit vector extracted from the input data
	 */
	public static byte [] extractBits(byte [] data, int mask) {
		byte [] out = null;
		int outlen;

		int bitcount;
		int bytebase;
		bitcount = calculateBitCount(mask);
		bytebase = calculateByteBase(mask);
		if (bitcount==0 || bytebase==0) return null;
		outlen = data.length*bitcount / (bytebase*8);
		out = new byte[outlen];
		int [][] idxtable = new int[bitcount][2];
		calculateBitIndexTable(mask, idxtable);

		int bitidx = 0;
		int dataofs = 0;
		for (int i=0; i<out.length; i++) {
			out[i] = 0;
			for (int j=0; j<8; j++) {
				int bii, byi;
				bii = idxtable[bitidx][0];
				byi = idxtable[bitidx][1];
				bii = bii % 8;
				byte ib = data[dataofs + byi];
				ib = (byte)((ib >> bii) & 1);
				//System.out.print(ib);
				out[i] = (byte)(out[i] | (ib<<j));
				bitidx++;
				if (bitidx>=bitcount) {
					bitidx = 0;
					dataofs += bytebase;
				}
			}
		}

		return out;
	}

	/**
	 * @param data
	 * @param in
	 * @param mask
	 */
	public static void insertBits(byte [] data, byte [] in, int mask) {

		int bitcount;
		int bytebase;
		bitcount = calculateBitCount(mask);
		bytebase = calculateByteBase(mask);
		int [][] idxtable = new int[bitcount][2];
		calculateBitIndexTable(mask, idxtable);

		int bitidx = 0;
		int dataofs = 0;
		for (int i=0; i<in.length; i++) {
			for (int j=0; j<8; j++) {
				int bii, byi;
				bii = idxtable[bitidx][0];
				byi = idxtable[bitidx][1];
				bii = bii % 8;
				byte ob = data[dataofs + byi];
				byte ib = in[i];
				ib = (byte)((ib >> j) & 1);
				ob = (byte)(ob & ~(1 << bii));
				ob = (byte)(ob | (ib << bii));
				data[dataofs + byi] = ob;

				bitidx++;
				if (bitidx>=bitcount) {
					bitidx = 0;
					dataofs += bytebase;
				}
			}
		}
	}

	/**
	 * @param data
	 * @param mask
	 * @param state 1 - set to 1, 0 - set to 0
	 */
	public static void maskBits(byte [] data, int mask, int state) {
		int bytebase;
		bytebase = calculateByteBase(mask);
		if (bytebase==0) return;
		byte [] bm = new byte[4]; 
		int2Bytes(mask, bm, 0, 4);

		int byteidx = 0;
		for (int i=0; i<data.length; i++) {
			if (state==0) {
				data[i] = (byte)(data[i] & ~bm[byteidx]);
			} else {
				data[i] = (byte)(data[i] | bm[byteidx]);
			}
			byteidx++;
			if (byteidx>=bytebase)
				byteidx = 0;
		}
	}

	/**
	 * @param data
	 * @return	the hex string for the byte array
	 */
	public static String bytes2Hex(byte [] data) {
		String s = "";
		boolean flg = false;
		for (byte b : data) {
			if (flg) {
				s += "-";
			} else {
				flg = true;
			}
			s += String.format("%02x", Byte.valueOf(b));
		}
		return s;
	}

	/**
	 * @param code
	 * @param b
	 * @param ofs
	 * @param len
	 */
	public static void int2Bytes(int code, byte [] b, int ofs, int len) {
		for (int i=0; i<len; i++) {
			b[i+ofs] = (byte)(code % 256);
			code >>= 8;
		}
	}

	/**
	 * @param b
	 * @param ofs
	 * @param len
	 * @return	the integer representing the byte array
	 */
	public static int bytes2Int(byte [] b, int ofs, int len) {
		int v = 0;
		for (int i = len-1; i>=0; i--) {
			v <<= 8;
			v += b[i+ofs] & 0x0FF;
		}
		return v;
	}
}
