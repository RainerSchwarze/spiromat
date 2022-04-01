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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

/**
 * FileUtil provides a set of helper functions for file management.
 * 
 * @author Rainer Schwarze
 */
public class FileUtil {

	/**
	 * Creates an instance of FileUtil. Not useful right now, because all 
	 * methods are static.
	 */
	protected FileUtil() {
		super();
	}

	/**
	 * Converts a wildcard into a regex representation.
	 * Attn: This method is not fully complete for standard unix wildcards.
	 * 
	 * @param wildcard
	 * @return	Returns a regex which matches the wildcard
	 */
	static public String convertWildcardToRegex(String wildcard) {
		// FIXME: finish implementation
		String tmp;
		tmp = wildcard;
		tmp = tmp.replaceAll("\\.", "\\\\.");
		tmp = tmp.replaceAll("\\?", ".");
		tmp = tmp.replaceAll("\\*", ".*");
		//tmp = "^" + tmp + "$";
		return tmp;
	}

	/**
	 * Converts a wildcard into a regex representation (Windows based).
	 * Note: this method does not take care of case insensitivity.
	 * To be case insensitive which is common in Windows environments,
	 * use toLower for the regex and the file name representation when
	 * testing.
	 *  
	 * @param wildcard
	 * @return	Returns the regex representation of the wildcard.
	 */
	public static String convertWindowsWildcardToRegex(String wildcard) {
		if (wildcard == null) return null;
		StringBuffer buffer = new StringBuffer();
		char [] chars = wildcard.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == '*')
				buffer.append(".*");
			else if (chars[i] == '?')
				buffer.append(".");
			else if ("+()^$.{}[]|\\".indexOf(chars[i])>=0)
				// prefix all metacharacters with backslash
				buffer.append('\\').append(chars[i]); 
			else
				buffer.append(chars[i]);
		}

		return buffer.toString();
	}

	/**
	 * @param files
	 * @param wildcard
	 * @param ignoreCase 
	 * @return	Returns the filtered File array
	 */
	public static File[] filterFilesByWildcard(
			File [] files, 
			String wildcard, 
			boolean ignoreCase) {
		String regex = convertWildcardToRegex(wildcard);
		return filterFilesByRegex(files, regex, ignoreCase);
	}
	

	/**
	 * @param files
	 * @param regex 
	 * @param ignoreCase 
	 * @return	Returns the filtered File array
	 */
	public static File[] filterFilesByRegex(
			File [] files, 
			String regex,
			boolean ignoreCase) {
		File [] tmp = new File[files.length];
		String tmps;
		if (ignoreCase) regex = regex.toLowerCase();
		int count = 0;
		for (int i = 0; i < files.length; i++) {
			if (files[i]==null) {
				tmp[i] = null;
				continue;
			}
			tmps = files[i].getName().toString();
			if (ignoreCase) tmps = tmps.toLowerCase();
			if (tmps.matches(regex)) {
//				System.out.println("match: " + tmps);
				tmp[i] = files[i];
				count++;
			} else {
//				System.out.println("no match: " + tmps);
				tmp[i] = null;
			}
		}

		if (count<files.length) {
			File [] tmp2 = new File[count];
			int dragindex = 0;
			for (int i = 0; i < tmp.length; i++) {
				if (tmp[i]!=null) {
					tmp2[dragindex++] = tmp[i];
				}
			}
			tmp = tmp2;
		}

		return tmp;
	}


	/**
	 * @param src
	 * @param dst
	 * @return Returns true, if success
	 */
	public static boolean copyFile(String src, String dst) {
		File srcfile = null;
		File dstfile = null;
		FileInputStream fin = null;
		FileOutputStream fout = null;
		boolean flag = true;
		int bufsize = 1024;
		byte [] buffer = new byte[bufsize];
		int readsize;
		// System.out.println("copy: " + src + " -> " + dst);
		try {
			srcfile = new File(src);
			dstfile = new File(dst);
			fin = new FileInputStream(srcfile);
			fout = new FileOutputStream(dstfile);
			while (true) {
				readsize = fin.read(buffer);
				if (readsize<0) break;
				fout.write(buffer, 0, readsize);
			}
			fout.close();
			fin.close();
			// System.out.println("copy: ok");
		} catch (FileNotFoundException e) {
			// e.printStackTrace();
			flag = false;
		} catch (IOException e) {
			// e.printStackTrace();
			flag = false;
		} finally {
			buffer = null;
			srcfile = null;
			dstfile = null;
			fin = null;
			fout = null;
		}
		return flag;
	}

	/**
	 * @param src
	 * @param dst
	 * @return Returns true, if success
	 */
	public static boolean copyFile(InputStream src, String dst) {
		File dstfile = null;
		BufferedInputStream fin = null;
		FileOutputStream fout = null;
		boolean flag = true;
		int bufsize = 1024;
		byte [] buffer = new byte[bufsize];
		int readsize;
		// System.out.println("copy: " + src + " -> " + dst);
		try {
			dstfile = new File(dst);
			fin = new BufferedInputStream(src);
			fout = new FileOutputStream(dstfile);
			while (true) {
				readsize = fin.read(buffer);
				if (readsize<0) break;
				fout.write(buffer, 0, readsize);
			}
			fout.close();
			fin.close();
			// System.out.println("copy: ok");
		} catch (FileNotFoundException e) {
			// e.printStackTrace();
			flag = false;
		} catch (IOException e) {
			// e.printStackTrace();
			flag = false;
		} finally {
			buffer = null;
			dstfile = null;
			fin = null;
			fout = null;
		}
		return flag;
	}

	/**
	 * @param src
	 * @param dst
	 * @return Returns true, if success
	 */
	public static boolean copyTextFile(String src, String dst) {
		File srcfile = null;
		File dstfile = null;
		FileReader fin = null;
		BufferedReader bin = null;
		FileWriter fout = null;
		BufferedWriter bout = null;
		boolean flag = true;
		// System.out.println("copy: " + src + " -> " + dst);
		try {
			srcfile = new File(src);
			dstfile = new File(dst);
			fin = new FileReader(srcfile);
			bin = new BufferedReader(fin);
			fout = new FileWriter(dstfile);
			bout = new BufferedWriter(fout);
			String line;
			while ((line = bin.readLine())!=null) {
				bout.write(line + "\n");
			}
			bout.close();
			bin.close();
			// System.out.println("copy: ok");
		} catch (FileNotFoundException e) {
			// e.printStackTrace();
			flag = false;
		} catch (IOException e) {
			// e.printStackTrace();
			flag = false;
		} finally {
			srcfile = null;
			dstfile = null;
			fin = null;
			bin = null;
			fout = null;
			bout = null;
		}
		return flag;
	}

	/**
	 * @param fileName
	 * @return	Returns a fixed filename
	 */
	public static String fixFileName1(String fileName) {
		String fixed = fileName;
		File t = new File(fileName);
		if (t.isAbsolute()) {
			if (Character.isLetter(fileName.charAt(0))) {
				// probably a windows drive
				fixed = '/' + fixed;
			}
		}
		return fixed;
	}
}
