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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Rainer Schwarze
 *
 */
public class VersionUtil {
	final static boolean DBG = false;
	final static boolean LOG = true;
	final static Logger logger = (LOG) ? Logger.getLogger("de.admadic") : null;


	/**
	 * No instance.
	 */
	private VersionUtil() {
		super();
	}

	/**
	 * @param filename
	 * @param alwaysCreateResult If true, always a VersionName is returned,
	 * but if no version could be detected, the prefix in the VersionName
	 * contains the plain filename.
	 * @return	Returns a VersionRecord created from information in the 
	 * filename, or null, if none could be found.
	 */
	public static VersionName getVersionNameFromFileName(
			String filename, boolean alwaysCreateResult) {
		VersionName vn = null;
		VersionRecord vr = null;
		if (alwaysCreateResult) {
			vn = new VersionName(filename, null, "");
		}
		/*
		 * Algorithm:
		 * - check for filename-X.Y.Z-rR.?
		 */
		Pattern pt = Pattern.compile(
				"(.*[^0-9]+)?"+
				"([0-9]++)\\."+
				"([0-9]+)\\."+
				"([0-9]+)"+
				"(?:-r([0-9]+))?"+
				"(.*)");
		Matcher mt = pt.matcher(filename);
		if (!mt.matches()) {
			return vn;
		}
		String f1 = mt.group(1);
		String g1 = mt.group(2);
		String g2 = mt.group(3);
		String g3 = mt.group(4);
		String g4 = mt.group(5);
		String f2 = mt.group(6);
		int mj = -1;
		int mn = -1;
		int mc = -1;
		int rv = -1;

		if (g1!=null && !g1.equals("")) mj = Integer.parseInt(g1);
		if (g2!=null && !g2.equals("")) mn = Integer.parseInt(g2);
		if (g3!=null && !g3.equals("")) mc = Integer.parseInt(g3);
		if (g4!=null && !g4.equals("")) rv = Integer.parseInt(g4);

		vr = new VersionRecord(mj, mn, mc, rv);
		vn = new VersionName(f1, vr, f2);

		return vn;
	}

	/**
	 * @param filename
	 * @return	Returns a VersionRecord created from information in the 
	 * filename, or null, if none could be found.
	 */
	public static VersionName getVersionNameFromFileName(String filename) {
		return getVersionNameFromFileName(filename, false);
	}

	/**
	 * This method checks the given list of files for version information 
	 * in the file names and removes all older versions, so that only
	 * the newest versions remain. The removed entries are set to 
	 * <code>null</code>.
	 * 
	 * @param files
	 */
	public static void removeOldVersions(List<File> files) {
		// note: the array uses the same indices as the files array.
		// files which are not versioned, appear as a null entry in the vvn
		// array.
		ArrayList<VersionName> vvn = new ArrayList<VersionName>();
		for (int i = 0; i < files.size(); i++) {
			File f = files.get(i);
			VersionName vn = null;
			if (f!=null) {
				vn = VersionUtil.getVersionNameFromFileName(f.getName());
				if (vn!=null) {
					if (DBG) System.out.println(
							"this has a version: " + f.getPath() + 
							" : " + vn.getVersionRecord().getMjMnMcRvVersionString());
				}
			}
			vvn.add(vn);
		}

		Vector<VersionName> othervns = new Vector<VersionName>();
		for (int i = 0; i < vvn.size(); i++) {
			VersionName vn = vvn.get(i);
			if (vn==null) continue;
			othervns.clear();
			if (DBG) System.out.println("checking entry: " + vn.getCombinedName());

			for (int j=0; j<vvn.size(); j++) {
				VersionName vnt = vvn.get(j);
				if (vnt==null) continue;
				if (vnt==vn) continue;	// should not happen, but anyway
				if (vn.equalsIgnoreVersion(vnt)) {
					othervns.add(vnt);
					if (DBG) System.out.println("  same type: " + vnt.getCombinedName());
				}
			}

			if (othervns.size()>0) {
				// we have different versions!
				// so add the primary one:
				othervns.add(vn);
				if (DBG) System.out.println("  == more than 1 entry!");
				VersionName maxvn = Collections.max(
						othervns, 
						new Comparator<VersionName>() {
					public int compare(VersionName o1, VersionName o2) {
						return o1.getVersionRecord().compareTo(
								o2.getVersionRecord(), VersionRecord.ALL, true);
					}
				});
				if (DBG) System.out.println("  => max : " + maxvn.getCombinedName());
				if (logger!=null) logger.config("detected max version: " + 
						maxvn.getCombinedName());
				// now set all items to null, which are not equal to the max:
				for (VersionName vnclr : othervns) {
					if (vnclr==null || vnclr==maxvn) continue;
					int idx = vvn.indexOf(vnclr);
					if (DBG) System.out.println("    => clr : " + vnclr.getCombinedName());
					if (logger!=null) logger.config("clearing old version: " + 
							vnclr.getCombinedName());
					vvn.set(idx, null);
					files.set(idx, null);
				}
			}
		}
		othervns.clear();
		
		// shutdown
		vvn.clear();
	}
}
