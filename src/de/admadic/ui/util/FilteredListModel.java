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
package de.admadic.ui.util;

import java.util.List;
import java.util.Vector;

import javax.swing.AbstractListModel;

/**
 * @author Rainer Schwarze
 *
 */
public class FilteredListModel extends AbstractListModel {
	/** */
	private static final long serialVersionUID = 1L;

	/** Filter by checking anywhere in the list entries */ 
	final static public int FLT_INSIDE = 0;
	/** Filter by checking the beginning of the list entries */ 
	final static public int FLT_BEGIN = 1;
	/** Filter by checking the end of the list entries */ 
	final static public int FLT_END = 2;
	// FIXME: maybe add regex support

	private String filter;

	private Vector list;
	private Vector filteredList;
	int filterType;

	/**
	 * 
	 */
	public FilteredListModel() {
		super();
		list = new Vector();
		filteredList = new Vector();
		filterType = FLT_INSIDE;
	}

	/**
	 * @param list
	 */
	public void addList(List list) {
		this.list.addAll(list);
		updateFilterImpl();
	}
	
	/**
	 * @param filter
	 */
	public void updateFilter(String filter) {
		this.filter = filter;
		updateFilterImpl();
	}

	/**
	 *
	 */
	public void updateFilter() {
		updateFilterImpl();
	}

	protected void updateFilterImpl() {
		if ((filter==null || filter.equals("")) && 
				filteredList.size()==list.size()) {
			// equal -> no filtering
			return;
		}
		if (filter==null || filter.equals("")) {
			// copy everything
			filteredList.clear();
			for (Object o : list) {
				filteredList.add(o);
			}
			fireContentsChanged(this, 0, filteredList.size()-1);
			return;
		}

		// we have a filter:
		filteredList.clear();
		for (Object o : list) {
			if (matches(o)) {
				filteredList.add(o);
			}
		}
		fireContentsChanged(this, 0, filteredList.size()-1);
	}

	protected boolean matches(Object o) {
		// FIXME: that filter function should be externalised
		String s = (String)o;
		s = s.toLowerCase();
		String tmpF = filter.toLowerCase();

		switch (filterType) {
		case FLT_INSIDE:
			if (s.indexOf(tmpF)>=0) return true;
			break;
		case FLT_BEGIN:
			if (s.startsWith(tmpF)) return true;
			break;
		case FLT_END:
			if (s.endsWith(tmpF)) return true;
			break;
		default:
			break;
		}
		return false;
	}
	
	/**
	 * @return	Returns the size of the list
	 * @see javax.swing.ListModel#getSize()
	 */
	public int getSize() {
		return filteredList.size();
	}

	/**
	 * @param index
	 * @return	Returns the element at the given index.
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	public Object getElementAt(int index) {
		return filteredList.elementAt(index);
	}

	/**
	 * 
	 */
	public void clear() {
		list.clear();
		filteredList.clear();
	}

	/**
	 * @return Returns the filterType.
	 */
	public int getFilterType() {
		return filterType;
	}

	/**
	 * @param filterType The filterType to set.
	 */
	public void setFilterType(int filterType) {
		this.filterType = filterType;
	}
}
