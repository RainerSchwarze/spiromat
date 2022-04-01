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

import java.util.Vector;

import javax.swing.AbstractListModel;

/**
 * @author Rainer Schwarze
 *
 */
public class ListRefListModel extends AbstractListModel {	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Vector<?> data;
	ListElementAccessor elementAccessor;
	
	/**
	 * 
	 */
	public ListRefListModel() {
		super();
	}

	/**
	 * @param data
	 */
	public void setData(Vector<?> data) {
		this.data = data;
	}
	
	/**
	 * @return	Returns the size of the list.
	 * @see javax.swing.ListModel#getSize()
	 */
	public int getSize() {
		if (data==null) return 0;
		return data.size();
	}

	/**
	 * @param index
	 * @return	Returns the element at the given index.
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	public Object getElementAt(int index) {
		if (data==null) return null;
		if (getElementAccessor()!=null) {
			return getElementAccessor().getElement(data.elementAt(index));
		} else {
			return data.elementAt(index);
		}
	}

	/**
	 * 
	 */
	public void fireContentsChanged() {
		if (data==null) return;
		fireContentsChanged(this, 0, data.size()-1);
	}

	/**
	 * @return Returns the elementAccessor.
	 */
	public ListElementAccessor getElementAccessor() {
		return elementAccessor;
	}

	/**
	 * @param elementAccessor The elementAccessor to set.
	 */
	public void setElementAccessor(ListElementAccessor elementAccessor) {
		this.elementAccessor = elementAccessor;
	}
}
