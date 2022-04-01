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
public class VersionName {
	String prefix;
	String suffix;
	VersionRecord versionRecord;

	/**
	 * @param prefix
	 * @param versionRecord
	 * @param suffix
	 */
	public VersionName(String prefix, VersionRecord versionRecord, String suffix) {
		super();
		this.prefix = (prefix!=null) ? prefix : "";
		this.suffix = (suffix!=null) ? suffix : "";
		this.versionRecord = versionRecord;
	}

	/**
	 * @return	Returns a combined name.
	 */
	public String getCombinedName() {
		String s = "";
		if (prefix!=null) s += prefix;
		if (versionRecord!=null) s += versionRecord.getMjMnMcRvVersionString();
		if (suffix!=null) s += suffix;
		return s;
	}

	/**
	 * @param othervn
	 * @return	Returns true, if the filenames are identical when ignoring
	 * the version information.
	 */
	public boolean equalsIgnoreVersion(VersionName othervn) {
		if (this.getPrefix()==null && othervn.getPrefix()!=null) return false; 
		if (this.getSuffix()==null && othervn.getSuffix()!=null) return false;
		if (this.getPrefix()!=null && 
				!this.getPrefix().equals(othervn.getPrefix())) return false;
		if (this.getSuffix()!=null && 
				!this.getSuffix().equals(othervn.getSuffix())) return false;
		return true;
	}
	
	/**
	 * @return Returns the prefix.
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @param prefix The prefix to set.
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * @return Returns the suffix.
	 */
	public String getSuffix() {
		return suffix;
	}

	/**
	 * @param suffix The suffix to set.
	 */
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	/**
	 * @return Returns the versionRecord.
	 */
	public VersionRecord getVersionRecord() {
		return versionRecord;
	}

	/**
	 * @param versionRecord The versionRecord to set.
	 */
	public void setVersionRecord(VersionRecord versionRecord) {
		this.versionRecord = versionRecord;
	}
}
