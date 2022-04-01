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
package de.admadic.laf;

import java.util.EventObject;

/**
 * @author Rainer Schwarze
 *
 */
public class LaFChangeEvent extends EventObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String lafName;
	String skinName;
	/**
	 * @param source
	 */
	public LaFChangeEvent(Object source) {
		this(source, null, null);
	}
	/**
	 * @param source
	 * @param name
	 * @param name2
	 */
	public LaFChangeEvent(Object source, String name, String name2) {
		super(source);
		lafName = name;
		skinName = name2;
	}
	/**
	 * @return Returns the lafName.
	 */
	public String getLafName() {
		return lafName;
	}
	/**
	 * @return Returns the skinName.
	 */
	public String getSkinName() {
		return skinName;
	}
	/**
	 * @param lafName The lafName to set.
	 */
	public void setLafName(String lafName) {
		this.lafName = lafName;
	}
	/**
	 * @param skinName The skinName to set.
	 */
	public void setSkinName(String skinName) {
		this.skinName = skinName;
	}
}
