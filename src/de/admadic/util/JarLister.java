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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * Provides mechanisms of listing class and path names in Jar files.
 * FIXME: add support for non-path and non-class entries (such as resources...)
 * 
 * @author Rainer Schwarze
 */
public class JarLister {
	ArrayList<String> classList;
	ArrayList<String> pathList;

	/**
	 * Create an empty instance of a JarLister.
	 */
	public JarLister() {
		super();
		classList = new ArrayList<String>();
		pathList = new ArrayList<String>();
	}

	/**
	 * Clears the internal lists (classes and paths).
	 */
	public void clearList() {
		classList.clear();
		pathList.clear();
	}
	
	/**
	 * Appends the entries from the given jar to the internal lists.
	 * Filters can be specified for class names and path names. If no
	 * filters shall be used, <code>null</code> can be specified.
	 * The filters are regex which are tested by using the 
	 * (String).matches method.
	 * Note: by calling this method for several jars one after another
	 * the lists are filled with the contents of all jars. 
	 * 
	 * @param fileName	A String giving the name of the Jar file.
	 * @param pathFilter	A String giving a Reg. Expr. for a path filter,
	 * 						or null if no filter shall be used. 
	 * @param classFilter 	A String giving a Reg. Expr. for a class filter,
	 * 						or null if no filter shall be used.
	 * @throws IOException	Thrown if the file could not be read. 
	 * @throws FileNotFoundException 	Thrown if the file could not be found.
	 */
	public void appendList(
			String fileName, String pathFilter, String classFilter) 
	throws FileNotFoundException, IOException {
		JarInputStream jarFile;
		JarEntry jarEntry;
		String entry;

		jarFile = new JarInputStream(
					new FileInputStream(fileName));

		while (true) {
			jarEntry = jarFile.getNextJarEntry();
			if (jarEntry == null) {
				break;
			}
			entry = jarEntry.getName();

			if (entry.endsWith(".class")) {
				if (classFilter!=null) {
					if (!entry.matches(classFilter))
						continue;
				}
				classList.add(entry);
			}
			if (entry.endsWith("/")) {
				if (pathFilter!=null) {
					if (!entry.matches(pathFilter))
						continue;
				}
				pathList.add(entry);
			}
		}
	}

	/**
	 * Returns an array representation of the class name list (String[]). The 
	 * method always returns a non-null value. The array may have zero (no) 
	 * elements.
	 * A class name has the format Package/Class.class .
	 * 
	 * @return	Returns the list of class names (slashed representation).
	 */
	public String[]  getClassList() {
		return classList.toArray(new String[classList.size()]);
	}

	/**
	 * Returns an array representation of the class name list (String[]). The 
	 * method always returns a non-null value. The array may have zero (no) 
	 * elements.
	 * A class name has the format Package.Class.
	 * 
	 * @return	Returns the list of class names (dotted representation).
	 */
	public String[] getClassListDotted() {
		String [] res = new String[classList.size()];
		for (int i = 0; i < res.length; i++) {
			String cl = classList.get(i);
			String cld = cl.replace('/', '.').replace('$', '.');
			// remove ".class" (6 chars)
			cld = cld.substring(0, cld.length()-6);
			res[i] = cld;
		}
		return res;
	}

	/**
	 * Returns an array representation of the path name list (String[]). The 
	 * method always returns a non-null value. The array may have zero (no) 
	 * elements.
	 * 
	 * @return	Returns the list of path/package names
	 */
	public String[]  getPathList() {
		return pathList.toArray(new String[pathList.size()]);
	}
}
