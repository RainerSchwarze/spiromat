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
package de.admadic.spiromat.util;

import java.io.File;

/**
 * @author Rainer Schwarze
 *
 */
public class FileUtil {

	/**
	 * @param f
	 * @param ext
	 * @param ignoreCase 
	 * @return	Returns a new File instance if the extension was added or the 
	 * 			File instance passed to this method.
	 */
	public static File tryToAppendExtension(File f, String ext, boolean ignoreCase) {
		boolean append = false;
		String name = f.getName();
		if (ignoreCase) {
			if (name.startsWith("\"") && name.endsWith("\"")) { //$NON-NLS-1$ //$NON-NLS-2$
				// keep it that way, but strip the quotes
				return new File(f.getParent(), name.substring(1, name.length()-2));
			} else if (name.toLowerCase().endsWith(ext.toLowerCase())) {
				// keep it that way (extension is there)
			} else {
				// extension not there and not quoted:
				append = true;
			}
		} else {
			if (name.startsWith("\"") && name.endsWith("\"")) { //$NON-NLS-1$ //$NON-NLS-2$
				// keep it that way, but strip the quotes
				return new File(f.getParent(), name.substring(1, name.length()-2));
			} else if (name.endsWith(ext)) {
				// keep it that way (extension is there)
			} else {
				// extension not there and not quoted:
				append = true;
			}
		}
		if (append) {
			return new File(f.getParent(), name + ext);
		}
		return f;
	}

	/**
	 * @param f
	 * @return	Returns the file without quotes, if there were any.
	 */
	public static File stripQuotes(File f) {
		String name = f.getName();
		if (name.startsWith("\"") && name.endsWith("\"")) { //$NON-NLS-1$ //$NON-NLS-2$
			return new File(f.getParent(), name.substring(1, name.length()-2));
		}
		return f;
	}

	/**
	 * @param f 
	 * @param extensions
	 * @param ignoreCase
	 * @return	Returns true, if the file has one of the given extensions.
	 */
	public static boolean hasExtension(File f, String[] extensions, boolean ignoreCase) {
		String name = f.getName();
		if (ignoreCase) {
			name = name.toLowerCase();
		}
		for (String ext : extensions) {
			if (ignoreCase) {
				ext = ext.toLowerCase();
			}
			if (name.endsWith(ext)) {
				return true;
			}
		}
		return false;
	}

}
