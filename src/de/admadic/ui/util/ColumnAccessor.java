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

/**
 * @author Rainer Schwarze
 *
 */
public interface ColumnAccessor {
	/**
	 * @param rowItem 
	 * @param value
	 * @param columnIndex
	 */
	public void setValueAt(Object rowItem, Object value, int columnIndex);

	/**
	 * @param rowItem 
	 * @param columnIndex
	 * @return	Returns the value for the given column
	 */
	public Object getValueAt(Object rowItem, int columnIndex);

	/**
	 * @param rowItem 
	 * @param columnIndex
	 * @return	Returns true, if the cell is editable.
	 */
	public boolean isCellEditable(Object rowItem, int columnIndex);

	/**
	 * @param columnIndex
	 * @return	Returns the class of the given column.
	 */
	public Class getColumnClass(int columnIndex);
}
