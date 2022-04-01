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
 * Available since admadiclib v1.0.1
 */
public class VersionRecord implements Comparable<VersionRecord> {
	Integer major;
	Integer minor;
	Integer micro;
	Integer revision;

	/** Mark version item as not specified. */
	public static final int NONE = -1;

	/** marks that version info up to major has to be evaluated. */
	public static final int MAJOR = 1;
	/** marks that version info up to minor has to be evaluated. */
	public static final int MINOR = 2;
	/** marks that version info up to micro has to be evaluated. */
	public static final int MICRO = 3;
	/** marks that version info up to revision has to be evaluated. */
	public static final int REVISION = 4;
	/** marks that all version info has to be evaluated. (same as REVISION) */
	public static final int ALL = REVISION;

	/**
	 * 
	 */
	public VersionRecord() {
		this(null, null, null, null);
	}
	
	/**
	 * @param major
	 * @param minor
	 * @param micro
	 * @param revis
	 */
	public VersionRecord(Integer major, Integer minor, Integer micro, Integer revis) {
		super();
		setMajor(major);
		setMinor(minor);
		setMicro(micro);
		setRevision(revis);
	}

	/**
	 * @param major
	 * @param minor
	 * @param micro
	 */
	public VersionRecord(Integer major, Integer minor, Integer micro) {
		this(major, minor, micro, null);
	}

	/**
	 * @param major
	 * @param minor
	 * @param micro
	 * @param revis
	 */
	public VersionRecord(int major, int minor, int micro, int revis) {
		setMajor(major);
		setMinor(minor);
		setMicro(micro);
		setRevision(revis);
	}
	
	/**
	 * @return Returns the major.
	 */
	public Integer getMajor() {
		return major;
	}

	/**
	 * @param major The major to set.
	 */
	public void setMajor(Integer major) {
		this.major = major;
	}

	/**
	 * @param major The major to set.
	 */
	public void setMajor(int major) {
		this.major = (major==NONE) ? null : Integer.valueOf(major);
	}

	/**
	 * @return Returns the micro.
	 */
	public Integer getMicro() {
		return micro;
	}

	/**
	 * @param micro The micro to set.
	 */
	public void setMicro(Integer micro) {
		this.micro = micro;
	}

	/**
	 * @param micro The micro to set.
	 */
	public void setMicro(int micro) {
		this.micro = (micro==NONE) ? null : Integer.valueOf(micro);
	}

	/**
	 * @return Returns the minor.
	 */
	public Integer getMinor() {
		return minor;
	}

	/**
	 * @param minor The minor to set.
	 */
	public void setMinor(Integer minor) {
		this.minor = minor;
	}

	/**
	 * @param minor The minor to set.
	 */
	public void setMinor(int minor) {
		this.minor = (minor==NONE) ? null : Integer.valueOf(minor);
	}

	/**
	 * @return Returns the revis.
	 */
	public Integer getRevision() {
		return revision;
	}

	/**
	 * @param revis The revis to set.
	 */
	public void setRevision(Integer revis) {
		this.revision = revis;
	}

	/**
	 * @param revision The revision to set.
	 */
	public void setRevision(int revision) {
		this.revision = (revision==NONE) ? null : Integer.valueOf(revision);
	}

	/**
	 * @param upto
	 * @param vsep
	 * @param rsep
	 * @return	Returns a String representation of the version.
	 */
	public String getVersionString(int upto, String vsep, String rsep) {
		String s = "";

		if (major==null) return s;
		s += major.toString();
		if (upto==MAJOR) return s;

		if (minor==null) return s;
		s += vsep + minor.toString();
		if (upto==MINOR) return s;

		if (micro==null) return s;
		s += vsep + micro.toString();
		if (upto==MICRO) return s;

		if (revision==null) return s;
		s += rsep + "r" + revision.toString();
		if (upto==REVISION) return s;

		// nothing else?
		return s;
	}

	/**
	 * @return	Returns a version in the form 1.2.3
	 */
	public String getMjMnMcVersionString() {
		return getVersionString(MICRO, ".", "-");
	}

	/**
	 * @return	Returns a version in the form 1.2.3-r17
	 */
	public String getMjMnMcRvVersionString() {
		return getVersionString(REVISION, ".", "-");
	}

	/**
	 * Returns a VersionRecord parsed from the given String.
	 * The String must follow the format "X.Y.Z-rR" where the items
	 * can be missing starting from the end. (X.Y.Z is valid, but X-rR 
	 * is not)
	 * If the format is invalid, null is returned.
	 * 
	 * @param s
	 * @return	Returns a VersionRecord parsed from the given String.
	 */
	public static VersionRecord valueOf(String s) {
		return valueOf(s, ".", "-");
	}

	/**
	 * Returns a VersionRecord parsed from the given String.
	 * The String must follow the format "X.Y.Z-rR" where the items
	 * can be missing starting from the end. (X.Y.Z is valid, but X-rR 
	 * is not)
	 * If the format is invalid, null is returned.
	 * 
	 * @param s
	 * @param vsep 
	 * @param rsep 
	 * @return	Returns a VersionRecord parsed from the given String.
	 */
	public static VersionRecord valueOf(String s, String vsep, String rsep) {
		// FIXME: add exception raising in case of error
		VersionRecord vr = new VersionRecord();
		// check whether we have a revision:
		int rpos = s.indexOf(rsep);
		String vpart = null;
		String rpart = null;
		if (rpos<0) { // no revision
			vpart = s;
		} else { // has revision
			vpart = s.substring(0, rpos);
			if (s.charAt(rpos+1)!='r') {
				// must have an 'r'!
				return null;
			}
			rpart = s.substring(rpos+2);
		}

		if (vpart!=null) {
			String [] va = vpart.split("\\" + vsep);
			if (va.length<1 || va.length>3) {
				// must have 1..3 entries.
				return null;
			}
			if (va.length>0) vr.setMajor(Integer.valueOf(va[0]));
			if (va.length>1) vr.setMinor(Integer.valueOf(va[1]));
			if (va.length>2) vr.setMicro(Integer.valueOf(va[2]));
		}
		if (rpart!=null) {
			vr.setRevision(Integer.valueOf(rpart));
		}
		
		return vr;
	}

	/**
	 * @param o
	 * @return	Returns -1 if this VersionRecord is smaller than the given one,
	 * 0 if it is equal, +1 if this is larger than the given one.
	 * @see java.lang.Comparable#compareTo(Object)
	 */
	public int compareTo(VersionRecord o) {
		return compareTo(o, ALL);
	}

	/**
	 * Returns -1 if this VersionRecord is smaller than the given one,
	 * 0 if it is equal, +1 if this is larger than the given one.
	 * The comparison is stopped at the level given by <code>upto</code>.
	 * For instance giving upto MINOR, the micro version and revision 
	 * information will be ignored, but major and minor version will be checked.
	 * 
	 * @param o
	 * @param upto 
	 * @return	Returns -1 if this VersionRecord is smaller than the given one,
	 * 0 if it is equal, +1 if this is larger than the given one.
	 */
	public int compareTo(VersionRecord o, int upto) {
		// this <-> other
		// 1.0.0    1.0.1    smaller
		// 1.0      1.0.1    equal?
		// 1        1.0.1    equal?
		// ?        ?        equal

		return compareTo(o, upto, false);

// This code is a little simpler than the actually used one. It is kept
// in the sources for a while until we definitely don't need it anymore.
// The actually used code is quite more complex and may cause trouble for 
// some enhancements.
//
//		int rc;
//		// first start with major:
//		if (this.getMajor()==null || o.getMajor()==null) return 0;
//		rc = this.getMajor().compareTo(o.getMajor());
//		if (rc!=0) return rc;
//		if (upto==MAJOR) return rc;
//
//		if (this.getMinor()==null || o.getMinor()==null) return 0;
//		rc = this.getMinor().compareTo(o.getMinor());
//		if (rc!=0) return rc;
//		if (upto==MINOR) return rc;
//
//		if (this.getMicro()==null || o.getMicro()==null) return 0;
//		rc = this.getMicro().compareTo(o.getMicro());
//		if (rc!=0) return rc;
//		if (upto==MICRO) return rc;
//
//		if (this.getRevision()==null || o.getRevision()==null) return 0;
//		rc = this.getRevision().compareTo(o.getRevision());
//		if (rc!=0) return rc;
//		if (upto==REVISION) return rc;
//
//		return 0;
	}

	/**
	 * Returns -1 if this VersionRecord is smaller than the given one,
	 * 0 if it is equal, +1 if this is larger than the given one.
	 * The comparison is stopped at the level given by <code>upto</code>.
	 * For instance giving upto MINOR, the micro version and revision 
	 * information will be ignored, but major and minor version will be checked.
	 * 
	 * @param o
	 * @param upto 
	 * @param dontIgnoreNull 
	 * @return	Returns -1 if this VersionRecord is smaller than the given one,
	 * 0 if it is equal, +1 if this is larger than the given one.
	 */
	public int compareTo(VersionRecord o, int upto, boolean dontIgnoreNull) {
		// this <-> other
		// 1.0.0    1.0.1    smaller
		// 1.0      1.0.1    equal?
		// 1        1.0.1    equal?
		// ?        ?        equal

		int rc;
		// compare Major:
		if (this.getMajor()==null && o.getMajor()==null) return 0;
		if (dontIgnoreNull) {
			if (this.getMajor()==null) return -1;
			if (o.getMajor()==null) return +1;
		} else {
			if (this.getMajor()==null) return 0;
			if (o.getMajor()==null) return 0;
		}
		rc = this.getMajor().compareTo(o.getMajor());
		if (rc!=0) return rc;
		if (upto==MAJOR) return rc;

		// compare Minor:
		if (this.getMinor()==null && o.getMinor()==null) return 0;
		if (dontIgnoreNull) {
			if (this.getMinor()==null) return -1;
			if (o.getMinor()==null) return +1;
		} else {
			if (this.getMinor()==null) return 0;
			if (o.getMinor()==null) return 0;
		}
		rc = this.getMinor().compareTo(o.getMinor());
		if (rc!=0) return rc;
		if (upto==MINOR) return rc;

		// compare Micro:
		if (this.getMicro()==null && o.getMicro()==null) return 0;
		if (dontIgnoreNull) {
			if (this.getMicro()==null) return -1;
			if (o.getMicro()==null) return +1;
		} else {
			if (this.getMicro()==null) return 0;
			if (o.getMicro()==null) return 0;
		}
		rc = this.getMicro().compareTo(o.getMicro());
		if (rc!=0) return rc;
		if (upto==MICRO) return rc;

		// compare Revision:
		if (this.getRevision()==null && o.getRevision()==null) return 0;
		if (dontIgnoreNull) {
			if (this.getRevision()==null) return -1;
			if (o.getRevision()==null) return +1;
		} else {
			if (this.getRevision()==null) return 0;
			if (o.getRevision()==null) return 0;
		}
		rc = this.getRevision().compareTo(o.getRevision());
		if (rc!=0) return rc;
		if (upto==REVISION) return rc;

		return 0;
	}

	/**
	 * @param obj
	 * @return	Returns true, if the given VersionRecord is equal to this one.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj==null) throw new NullPointerException();
		if (!(obj instanceof VersionRecord)) return false;
		VersionRecord vr = (VersionRecord)obj;
		return equalsUpto(vr, ALL);
	}


	/**
	 * @param vr
	 * @param upto
	 * @return	Returns true, if this VersionRecord is equal to the given
	 * one, but evaluating only the items which are given by the 
	 * <code>upto</code> parameter.
	 */
	public boolean equalsUpto(VersionRecord vr, int upto) {
		if (vr==null) throw new NullPointerException();
		if (this.major==null && vr.major!=null) return false;
		if (this.major!=null && !this.major.equals(vr.major)) return false;
		if (upto==MAJOR) return true;
		if (this.minor==null && vr.minor!=null) return false;
		if (this.minor!=null && !this.minor.equals(vr.minor)) return false;
		if (upto==MINOR) return true;
		if (this.micro==null && vr.micro!=null) return false;
		if (this.micro!=null && !this.micro.equals(vr.micro)) return false;
		if (upto==MICRO) return true;
		if (this.revision==null && vr.revision!=null) return false;
		if (this.revision!=null && !this.revision.equals(vr.revision)) return false;
		if (upto==REVISION) return true;
		return true;
	}
	
	/**
	 * @return	Returns a String representation of the version record.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String s = super.toString();
		s += "version=" + getVersionString(ALL, ".", "-");
		return s;
	}

	
}
