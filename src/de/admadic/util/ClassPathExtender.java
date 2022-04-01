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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * 
 * Since admadiclib v1.0.1 the ClassPathExtender handles versioned filenames
 * so that older versions are not loaded. For instance if
 * lib-1.0.0.jar and lib-1.1.0.jar are added, only lib-1.1.0.jar is used
 * and the 1.0.0 version is dropped.
 * 
 * @author Rainer Schwarze
 */
public class ClassPathExtender {
	static boolean DBG = false;
	final static boolean LOG = true;
	final static Logger logger = (LOG) ? Logger.getLogger("de.admadic") : null;


	ArrayList<File> files;
	ArrayList<URL> urls;
	URLClassLoader urlClassLoader;

	/**
	 * 
	 */
	public ClassPathExtender() {
		super();
		{
			String tmp = System.getProperty("admadic.debug");
			if (tmp!=null && tmp.toLowerCase().equals("yes")) {
				DBG = true;
			}
		}
		files = new ArrayList<File>();
		urls = new ArrayList<URL>();
		if (logger!=null) logger.fine("ClassPathExtender started.");
	}

	/**
	 * Registers the URLClassLoader-member of this ClassPathExtender
	 * to the specified thread. If the given thread is null, the current thread
	 * is used.
	 *  
	 * @param thread
	 */
	public void registerToThread(Thread thread) {
		// FIXME: should we track double registration?
		if (urls==null) {
			if (logger!=null) logger.info("registerToThread: no urls there.");
			return;
		}
		if (urlClassLoader==null) {
			generateClassLoader();
		}
		if (thread==null) {
			thread = Thread.currentThread();
		}
		thread.setContextClassLoader(urlClassLoader);
	}

	/**
	 * 
	 */
	public void generateClassLoader() {
		convertFilesToURLs();

		URL [] urlary = new URL[urls.size()];
		urlary = urls.toArray(urlary);
		if (logger!=null) {
			for (URL url : urlary) {
				logger.finest("classpath entry -> " + url.toString());
			}
		}
		urlClassLoader = new URLClassLoader(urlary);
	}

	protected void convertFilesToURLs() {
		URL url;
		for (int i = 0; i < files.size(); i++) {
			if (files.get(i)==null) continue;
			try {
				url = files.get(i).toURL();
				if (logger!=null) logger.fine("setURLs: adding " + url.toString());
				if (DBG) System.out.println("CPE: setURLs: adding " + url.toString());
			} catch (MalformedURLException e) {
				//e.printStackTrace();
				if (logger!=null) logger.severe("could not get URL for " + 
						files.get(i).toString());
				if (DBG) System.out.println("CPE: could not get URL for " + 
						files.get(i).toString());
				url = null;
			}
			if (url!=null)
				urls.add(url);
		}
	}

	protected void checkFileVersions() {
		VersionUtil.removeOldVersions(files);
	}

	/**
	 * Set the URLs for the ClassPathExtender to use for the
	 * URLClassLoader.
	 * 
	 * @param fileNames
	 */
	public void setURLs(String [] fileNames) {
		File [] files = createFileArray(fileNames);
		setURLs(files);
	}

	/**
	 * Sets the URLs for the ClassPathExtender to use for the
	 * URLClassLoader.
	 * 
	 * @param flist
	 */
	public void setURLs(File [] flist) {
		for (int i = 0; i < flist.length; i++) {
			if (flist[i]==null) continue;
			files.add(flist[i]);
		}
	}

	/**
	 * Sets the URLs for the ClassPathExtender to use for the
	 * URLClassLoader by specifying a directory and a wildcard.
	 * 
	 * @param dir
	 * @param wildcard
	 */
	public void setURLs(File dir, String wildcard) {
		// to be done
		File [] flist = dir.listFiles();
//		System.out.println("cpe: dir = " + dir.toString());
//		System.out.println("cpe: flist = " + flist);
		// we want to ignore the filename case:
		// FIXME: maybe add another method to support passing ignoreCase
		flist = FileUtil.filterFilesByWildcard(flist, wildcard, true);
		setURLs(flist);
	}

	/**
	 * @return	Returns the classloader created in the ClassPathExtender
	 */
	public ClassLoader getClassLoader() {
		return urlClassLoader;
	}

	/**
	 * Compacts the given array by removing <code>null</code> elements.
	 * 
	 * @param urlary
	 * @param count	Specifies the number of non-<code>null</code> elements.
	 *  If count is &lt;0, the method determines the number of 
	 *  non-<code>null</code> elements.  
	 * @return	Returns an array without <code>null</code> elements
	 */
	protected URL[] compactURLArray(URL [] urlary, int count) {
		if (count<0) {
			count = 0;
			for (int i = 0; i < urlary.length; i++) {
				if (urlary[i]!=null)
					count++;
			}
		}
		URL [] out = new URL[count];
		int dragindex = 0;
		for (int i = 0; i < urlary.length; i++) {
			if (urlary[i]!=null) {
				out[dragindex++] = urlary[i];
			}
		}
		return out;
	}

	/**
	 * Creates an array of File objects from the given array of filenames.
	 * @param fnames	A String[] containing the filenames.
	 * @return	Returns a File[] from the given filenames.
	 */
	protected File[] createFileArray(String [] fnames) {
		File [] out = new File[fnames.length];
		for (int i = 0; i < out.length; i++) {
			out[i] = new File(fnames[i]);
		}
		return out;
	}

	/**
	 * 
	 */
	public void dumpUrls() {
//		for (URL url : urls) {
//			System.out.println("-> " + url.toString());
//		}
	}
}
