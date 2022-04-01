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
 * @since admadiclib v1.0.3
 */
public abstract class AbstractRendererEditorColumnAccessor implements
		RendererEditorColumnAccessor {

	/**
	 * 
	 */
	public AbstractRendererEditorColumnAccessor() {
		super();
	}

	/**
	 * @param rowItem
	 * @param value
	 * @param columnIndex
	 * @see de.admadic.ui.util.RendererEditorColumnAccessor#setRendererValueAt(java.lang.Object, java.lang.Object, int)
	 */
	public void setRendererValueAt(Object rowItem, Object value, int columnIndex) {
		setValueAt(rowItem, value, columnIndex);
	}

	/**
	 * @param rowItem
	 * @param columnIndex
	 * @return	Returns the renderer value.
	 * @see de.admadic.ui.util.RendererEditorColumnAccessor#getRendererValueAt(java.lang.Object, int)
	 */
	public Object getRendererValueAt(Object rowItem, int columnIndex) {
		return getValueAt(rowItem, columnIndex);
	}

	/**
	 * @param rowItem
	 * @param value
	 * @param columnIndex
	 * @see de.admadic.ui.util.RendererEditorColumnAccessor#setEditorValueAt(java.lang.Object, java.lang.Object, int)
	 */
	public void setEditorValueAt(Object rowItem, Object value, int columnIndex) {
		setRendererValueAt(rowItem, value, columnIndex);
	}

	/**
	 * @param rowItem
	 * @param columnIndex
	 * @return	Returns the editor value.
	 * @see de.admadic.ui.util.RendererEditorColumnAccessor#getEditorValueAt(java.lang.Object, int)
	 */
	public Object getEditorValueAt(Object rowItem, int columnIndex) {
		return getRendererValueAt(rowItem, columnIndex);
	}

	/**
	 * @param rowItem
	 * @param columnIndex
	 * @return	Returns always false.
	 * @see de.admadic.ui.util.ColumnAccessor#isCellEditable(java.lang.Object, int)
	 */
	public boolean isCellEditable(Object rowItem, int columnIndex) {
		return false;
	}
}
