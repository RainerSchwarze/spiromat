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
 * @author Rainer Schwarze
 *
 */
public class StringUtil {

	/**
	 * 
	 */
	protected StringUtil() {
		super();
	}

	/**
	 * Create a string consisting of the elements of the given
	 * String array joined with the given String between the
	 * elements. 
	 * 
	 * @param sa	the String[] to join to a single String
	 * @param s		the String used between the elements
	 * @return		the joined String
	 */
	public static String join(String [] sa, String s) {
		StringBuffer sb = new StringBuffer();
		String out = "";
		if (sa==null) return out;
		for (int i = 0; i < sa.length; i++) {
			if (i!=0 && s!=null) {
				sb.append(s);
			}
			sb.append(sa[i]);
		}
		out = sb.toString();
		return out;
	}

	/**
	 * @param ch
	 * @param len
	 * @return	Returns a string consisting of len times ch.
	 */
	public static String fill(char ch, int len) {
		StringBuffer sb = new StringBuffer(len);
		for (int i = 0; i < len; i++) {
			sb.append(ch);
		}
		return sb.toString();
	}
}
