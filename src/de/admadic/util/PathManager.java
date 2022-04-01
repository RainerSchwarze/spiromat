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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;

/**
 * The PathManager class provides a manager for certain application 
 * directories in system scope and user scope.
 * 
 * The supported directories are:
 * <table>
 * <th><td>id</td><td>SYSTEM</td><td>sample</td><td>USER</td><td>sample</td></th>
 * <tr><td>BASE</td><td>yes</td><td>/opt</td><td>yes</td><td>/home/rsc</td></tr>
 * <tr><td>VEN</td><td>yes</td><td>/opt/admadic</td><td>yes</td><td>/home/rsc/.admadic</td></tr>
 * </table>
 * 
 * @author Rainer Schwarze
 */
public class PathManager {
	static boolean DBG = false;

	/** Base directory in system space */
	public final static int SYS_BASE_DIR = 0;
	/** Vendor directory in system space */
	public final static int SYS_VEN_DIR = 1;
	/** Application directory in system space */
	public final static int SYS_APPGRP_DIR = 2;
	/** Application version directory in system space */
	public final static int SYS_APP_DIR = 3;
	/** Binray directory in system space */
	public final static int SYS_BIN_DIR = 4;
	/** Config directory in system space */
	public final static int SYS_CFG_DIR = 5;
	/** Library directory in system space */
	public final static int SYS_LIB_DIR = 6;
	/** Look-and-Feel directory in system space */
	public final static int SYS_LAF_DIR = 7;
	/** Log directory in system space */
	public final static int SYS_LOG_DIR = 8;
	/** Module directory in system space */
	public final static int SYS_MOD_DIR = 17;
	/** Doc directory in system space */
	public final static int SYS_DOC_DIR = 20;

	/** Base directory in user space */
	public final static int USR_BASE_DIR = 9;
	/** Vendor directory in user space */
	public final static int USR_VEN_DIR = 10;
	/** Application directory in user space */
	public final static int USR_APPGRP_DIR = 11;
	/** Application version directory in user space */
	public final static int USR_APP_DIR = 12;
	/** Config dir in user space */
	public final static int USR_CFG_DIR = 13;
	/** Library dir in user space */
	public final static int USR_LIB_DIR = 14;
	/** Look-and-Feel dir in user space */
	public final static int USR_LAF_DIR = 15;
	/** Log dir in user space */
	public final static int USR_LOG_DIR = 16;
	/** Module directory in user space */
	public final static int USR_MOD_DIR = 18;
	/** Doc directory in user space */
	public final static int USR_DOC_DIR = 21;
	/**
	 * Temp directory in user space
	 * @since 1.0.1
	 */
	public final static int USR_TMP_DIR = 19;

	final static int pathCount = 22;

	// FIXME: turn that into a Hashtable?
	ArrayList<File> paths;

	boolean canCreateDirs = false;

	
	/**
	 * FIXME: improve doc:
	 * %h = users home directory (user.home)
	 * %w = users working directory (user.dir)
	 * %t = default temp dir (java.io.tmpdir)
	 * %j = java installation directory (java.home)
	 * %% = percent sign
	 * slash ('/') = file separator
	 * 
	 * @param pattern
	 * @return	Returns the filename with variables expanded.
	 */
	public static File expandFilename(String pattern) {
		if (DBG) System.out.print("PathManager: expanding " + pattern + " -> ");
		File f = null;
		String fname = "";
		int idx, len;
		char c1, c2;

		idx = 0;
		len = pattern.length();
		while (idx<len) {
			c1 = pattern.charAt(idx);
			idx++;
			if (c1=='%') { // Percent sign:
				c2 = (idx<len) ? pattern.charAt(idx) : '\0';
				idx++;
				switch (c2) {
				case '\0':
				case '%':
					fname += '%';
					break;
				case 'h':
					fname = "";
					f = new File(System.getProperty("user.home"));
					break;
				case 'w':
					fname = "";
					f = new File(System.getProperty("user.dir"));
					break;
				case 'j':
					fname = "";
					f = new File(System.getProperty("java.home"));
					break;
				case 't':
					fname = "";
					f = new File(System.getProperty("java.io.tmpdir"));
					break;
				default:
					// invalid character?!
					// nothing.
					break;
				}
			} else if (c1=='/') { // File separator:
				if (fname.equals("")) {
					// nothing
					f = new File(f, "/");
				} else {
					f = new File(f, fname);
					fname = "";
				}
			} else { // not % and not / :
				fname += c1;
			}
		}

		if (!fname.equals("")) {
			f = new File(f, fname);
		}

		if (DBG) System.out.println(f.toString());

		return f;
	}

	/**
	 * 
	 */
	public PathManager() {
		super();
		{
			String tmp = System.getProperty("admadic.debug");
			if (tmp!=null && tmp.toLowerCase().equals("yes")) {
				DBG = true;
			}
		}
		if (DBG) System.out.println("PathManager: <init>");
		paths = new ArrayList<File>();
		for (int i=0; i<pathCount; i++) {
			paths.add(i, null);
		}
	}

	/**
	 * @param mainClass
	 * @return	Returns a String representing the code base.
	 */
	public static String getCodeBase(Class mainClass) {
		if (DBG) System.out.println("PathManager: getCodeBase");
		File f;
		URL sysbaseUrl;
		CodeSource source;
		String sysbaseStr;
		source = mainClass.getProtectionDomain().getCodeSource();
		sysbaseUrl = source.getLocation();
		// these contain %20 in case of space in path:
		if (DBG) System.out.println("PathManager: source = " + source.toString());
		if (DBG) System.out.println("PathManager: sourceUrl = " + sysbaseUrl.toString());

		String sysbaseProtocol = sysbaseUrl.getProtocol();
		if (DBG) System.out.println("PathManager: prot = " + sysbaseProtocol);
		if (!sysbaseProtocol.equals("file")) {
			throw new Error(
					"PathManager: cannot handle application store "+
					"protocols other than 'file://'");
		}

		{
			URI uri;
			try {
				uri = sysbaseUrl.toURI();
			} catch (URISyntaxException e) {
				// e.printStackTrace();
				// the url probably has invalid characters in it, so we need
				// to let URI escape them:
				try {
					uri = new URI(
							null, sysbaseUrl.getHost(), sysbaseUrl.getPath(), null);
				} catch (URISyntaxException e2) {
					// e2.printStackTrace();
					// ok. no idea. lets them bail out...
					System.err.println("PathManager: could not create a URI.");
					System.err.println("please contact customer support and send the next line to them:");
					System.err.println("url = " + sysbaseUrl);
					System.exit(1);
					// FIXME: make that a visible error
					return null; // helper for the warning below regarding 
									// uri potentially not initialized
				}
			}
			f = new File(uri.getPath());
			if (DBG) System.out.println("PathManager: unescaped = " + f.toString());
		}
		
		sysbaseStr = f.toString();
		if (sysbaseStr.toLowerCase().endsWith(".jar")) {
			if (DBG) System.out.println("PathManager: is jar! stripping");
			sysbaseStr = f.getParent();
		} else {
			// nothing
		}
		return sysbaseStr;
	}

	/**
	 * @param file
	 * @return	File without trailing "/."
	 */
	public static File stripTrailers(File file) {
		File f = file;
		while (f.getName().equals(".")) {
			if (f.getParentFile()==null) 
				return f;	// FIXME: not quite right!
			f = f.getParentFile();
		}
		return f;
	}
	
	/**
	 * @param vendor
	 * @param app
	 * @param version
	 * @param mainClass 
	 */
	public void init(String vendor, String app, String version, Class mainClass) {
		if (DBG) System.out.println("PathManager: init: " + 
				vendor + ", " + app + ", " + version + ", " + mainClass.getName());

		// get system base path:
		String sysbaseStr;
		sysbaseStr = PathManager.getCodeBase(mainClass);

		if (DBG) System.out.println("PathManager: sysbase = " + sysbaseStr);

		// sysbaseStr = sysbaseUrl.getPath();
		if (sysbaseStr.endsWith(File.separator + "bin" + File.separator)) {
			if (DBG) System.out.println("PathManager: stripping /bin/");
			sysbaseStr = sysbaseStr.substring(
					0,
					sysbaseStr.length() - 4);
		} else if (sysbaseStr.endsWith(File.separator + "bin")) {
			if (DBG) System.out.println("PathManager: stripping /bin");
			sysbaseStr = sysbaseStr.substring(
					0,
					sysbaseStr.length() - 3);
		} else if (sysbaseStr.endsWith(File.separator + "lib" + File.separator)) {
			if (DBG) System.out.println("PathManager: stripping /lib/");
			sysbaseStr = sysbaseStr.substring(
					0,
					sysbaseStr.length() - 4);
		} else if (sysbaseStr.endsWith(File.separator + "lib")) {
			if (DBG) System.out.println("PathManager: stripping /lib");
			sysbaseStr = sysbaseStr.substring(
					0,
					sysbaseStr.length() - 3);
		}
		

		File base = new File(sysbaseStr);
		if (DBG) System.out.println("PathManager: base = " + base.toString());

		paths.set(SYS_APP_DIR, createPath(base, ""));
		paths.set(SYS_BIN_DIR, createPath(base, "bin"));
		paths.set(SYS_CFG_DIR, createPath(base, "cfg"));
		paths.set(SYS_LIB_DIR, createPath(base, "lib"));
		paths.set(SYS_LAF_DIR, createPath(base, "laf"));
		paths.set(SYS_LOG_DIR, createPath(base, "log"));
		paths.set(SYS_MOD_DIR, createPath(base, "mod"));
		paths.set(SYS_DOC_DIR, createPath(base, "doc"));

		// if above app(ver) = app -> appgrp e-> appgrp = app
		// if above appgrp = ven -> ven e-> ven = appgrp
		{
			File f0 = base;
			f0 = stripTrailers(f0);
			File f  = f0.getParentFile();
			if (f==null) {
				if (DBG) System.out.println(
						"PathManager: parent of app failed: " + base.toString());
				paths.set(SYS_APPGRP_DIR, paths.get(SYS_APP_DIR));
			} else {
				String pn = f.getName();
				if (pn.equals(app)) {
					// we got it
					paths.set(SYS_APPGRP_DIR, createPath(f, ""));
				} else if (pn.equalsIgnoreCase(app)) {
					// should we?
					paths.set(SYS_APPGRP_DIR, createPath(f, ""));
				} else {
					if (DBG) System.out.println(
							"PathManager: appgrp not detected.");
					// did not work out. choose the toplevel app as appgrp
					paths.set(SYS_APPGRP_DIR, paths.get(SYS_APP_DIR));
				}
			}
		}
		{
			File f0 = paths.get(SYS_APPGRP_DIR);
			f0 = stripTrailers(f0);
			File f = f0.getParentFile();
			if (f==null) {
				if (DBG) System.out.println(
						"PathManager: parent of appgrp failed: " + f0.toString());
				paths.set(SYS_VEN_DIR, paths.get(SYS_APPGRP_DIR));
			} else {
				String pn = f.getName();
				if (pn.equals(vendor)) {
					// we got it
					paths.set(SYS_VEN_DIR, createPath(f, ""));
				} else if (pn.equalsIgnoreCase(vendor)) {
					// should we?
					paths.set(SYS_VEN_DIR, createPath(f, ""));
				} else {
					if (DBG) System.out.println(
							"PathManager: ven not detected.");
					// did not work out. choose the appgrp as ven
					paths.set(SYS_VEN_DIR, paths.get(SYS_APPGRP_DIR));
				}
			}
		}
		// FIXME: add sys base!
		
		// returned in File representation, not in URL or URI:
		String homedir = System.getProperty("user.home");
		if (homedir==null) {
			homedir = sysbaseStr;
		}
		File ubase = new File(homedir);
		paths.set(USR_BASE_DIR, createPath(ubase, "", false));
		String apppath = "";
		if (vendor!=null) {
			if (!apppath.equals(""))
				apppath += File.separator;
			apppath += "." + vendor;
		}
		paths.set(USR_VEN_DIR, createPath(ubase, apppath, true));
		if (app!=null) {
			if (!apppath.equals(""))
				apppath += File.separator;
			apppath += app;
		}
		paths.set(USR_APPGRP_DIR, createPath(ubase, apppath, true));
		if (version!=null) {
			if (!apppath.equals(""))
				apppath += File.separator;
			apppath += version;
		}
		paths.set(USR_APP_DIR, createPath(ubase, apppath, true));
		if (!apppath.equals(""))
			apppath += File.separator;

		paths.set(USR_CFG_DIR, createPath(ubase, apppath + "cfg", true));
		paths.set(USR_LIB_DIR, createPath(ubase, apppath + "lib", true));
		paths.set(USR_LAF_DIR, createPath(ubase, apppath + "laf", true));
		paths.set(USR_LOG_DIR, createPath(ubase, apppath + "log", true));
		paths.set(USR_MOD_DIR, createPath(ubase, apppath + "mod", true));
		paths.set(USR_DOC_DIR, createPath(ubase, apppath + "doc", true));
		paths.set(USR_TMP_DIR, createPath(ubase, apppath + "tmp", true));

		if (DBG) {
			dump();
		}
	}

	
	/**
	 * @param id
	 * @return	Returns the path string for the given id, null, if not defined.
	 */
	public String getPathString(int id) {
		File f = paths.get(id);
		if (f!=null) {
			return f.toString();
		}
		return null;
	}

	protected File createPath(File base, String subpath, boolean create) {
		File f = null;
		if (subpath.equals("")) {
			return new File(base.toString());
		}
//		String fn = base.toString() + File.separator + subpath;
//		f = new File(fn);
		f = new File(base, subpath);
		if (f.exists()) {
			return f;
		}
		if (create) {
			if (f.mkdirs()) {
				return f;
			} else {
				// could not create!?
			}
		}
		return new File(base.toString());
	}

	protected File createPath(File base, String subpath) {
		return createPath(base, subpath, false);
	}

	protected void dumpEntry(int id, String name) {
		System.out.println(name + ": " + paths.get(id));
	}

	/**
	 * 
	 */
	public void dump() {
		dumpEntry(SYS_BASE_DIR, "sys base");
		dumpEntry(SYS_VEN_DIR, "sys ven");
		dumpEntry(SYS_APPGRP_DIR, "sys appgrp");
		dumpEntry(SYS_APP_DIR, "sys app");
		dumpEntry(SYS_BIN_DIR, "sys bin");
		dumpEntry(SYS_CFG_DIR, "sys cfg");
		dumpEntry(SYS_LIB_DIR, "sys lib");
		dumpEntry(SYS_LAF_DIR, "sys laf");
		dumpEntry(SYS_LOG_DIR, "sys log");
		dumpEntry(SYS_MOD_DIR, "sys mod");
		dumpEntry(SYS_DOC_DIR, "sys doc");

		dumpEntry(USR_BASE_DIR, "usr base");
		dumpEntry(USR_VEN_DIR, "usr ven");
		dumpEntry(USR_APPGRP_DIR, "usr appgrp");
		dumpEntry(USR_APP_DIR, "usr app");
		dumpEntry(USR_CFG_DIR, "usr cfg");
		dumpEntry(USR_LIB_DIR, "usr lib");
		dumpEntry(USR_LAF_DIR, "usr laf");
		dumpEntry(USR_LOG_DIR, "usr log");
		dumpEntry(USR_MOD_DIR, "usr mod");
		dumpEntry(USR_DOC_DIR, "usr doc");
	}
}
