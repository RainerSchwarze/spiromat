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

import java.util.ArrayList;

/**
 * 
 * @author Rainer Schwarze
 *
 */
public class CharCodeOp {
	// FIXME: finish documentation
	String codeMap;

	/**
	 * @param codeMap 
	 */
	public CharCodeOp(String codeMap) {
		super();
		this.codeMap = codeMap;
	}

	/**
	 * @param in
	 * @return	the number of the given String in the CodeOp's base
	 * @throws CharCodeOpException 
	 */
	public int string2Num(String in) throws CharCodeOpException {
		// FIXME: check overflow protection for potentially turning into neg number
		long v;
		int tmp;
		v = 0;
		if (in==null) return 0;
		for (int i=0; i<in.length(); i++) {
			char c = in.charAt(i);
			v *= codeMap.length();
			tmp = codeMap.indexOf(c);
			if (tmp<0) {
				throw new CharCodeOpException("Invalid character found: " + c);
				//return -1;
			}
			if (v > 2*(long)Integer.MAX_VALUE) {
				throw new CharCodeOpException("Overflow (" + in + " too large)");
			}
			v += tmp;
		}
		return (int)v;
	}

	/**
	 * @param v
	 * @return the String in codeOp's base representing the given number
	 */
	public String num2String(int v) {
		String s;
		int base = codeMap.length();
		int t;
		long lv = v;
		if (v==0) {
			s = codeMap.substring(0, 1);
		} else {
			s = "";
			while (lv!=0) {
				t = (int)((lv & 0xffffffffl) % base); // strip sign
				s = codeMap.charAt(t) + s;
				lv = (lv & 0xffffffffl) / base;
			}
		}
		return s;
	}

	/**
	 * @param d
	 * @param bytelen
	 * @param padsize
	 * @return	the string
	 */
	public String bytes2String(byte [] d, int bytelen, int padsize) {
		String s = "";
		int code = BitOp.bytes2Int(d, 0, bytelen);
		s = num2String(code);
		while (s.length()<padsize) {
			s = codeMap.substring(0, 1) + s;
		}
		return s;
	}

	/**
	 * @param s
	 * @param bytelen
	 * @return	the byte array
	 * @throws CharCodeOpException 
	 */
	public byte[] string2Bytes(String s, int bytelen) throws CharCodeOpException {
		int code = string2Num(s);
		byte [] data = new byte[bytelen];
		for (int i = 0; i < data.length; i++) {
			data[i] = 0;
		}
		BitOp.int2Bytes(code, data, 0, bytelen);
		return data;
	}

	/**
	 * @param data
	 * @param bytegrp
	 * @param stringgrp
	 * @return	the String array
	 */
	public String[] encodeBytes2Strings(byte [] data, int bytegrp, int stringgrp) {
		String s = "";
		ArrayList<String> outs = new ArrayList<String>();
		byte [] d;
		int clen;
		for (int i=0; i<data.length; i += bytegrp) {
			d = new byte[bytegrp];
			for (int j = 0; j < d.length; j++) {
				d[j] = 0;
			}
			clen = bytegrp;
			if (data.length-i<bytegrp)
				clen = data.length-i;
			System.arraycopy(data, i, d, 0, clen);
			s = bytes2String(d, bytegrp, stringgrp);
			outs.add(s);
		}
		String [] out = new String[outs.size()];
		for (int i = 0; i < out.length; i++) {
			out[i] = outs.get(i);
		}
		outs = null;
		return out;
	}

	/**
	 * @param sa
	 * @param bytegrp
	 * @param bytelen
	 * @return	the byte array
	 * @throws CharCodeOpException 
	 */
	public byte [] decodeStrings2Bytes(
			String [] sa, 
			int bytegrp, 
			int bytelen) throws CharCodeOpException {
		byte [] data = new byte[bytelen];

//		if (sa.length!=5) 
//			return null;

		int index = 0;
		for (String s : sa) {
			byte [] d;// = new byte[bytegrp];
			d = string2Bytes(s, bytegrp);
			for (int i = 0; i < d.length; i++) {
				if ((index+i)<data.length) data[index+i] = d[i];
			}
			index += bytegrp;
		}

		return data;
	}
}
