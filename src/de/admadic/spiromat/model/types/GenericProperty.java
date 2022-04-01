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
package de.admadic.spiromat.model.types;

import de.admadic.spiromat.SpiromatException;

/**
 * @author Rainer Schwarze
 * @param <T> 
 *
 */
public class GenericProperty<T> extends AbstractProperty<T> {

	/**
	 * @param name
	 */
	public GenericProperty(String name) {
		super(name);
	}

	/**
	 * @param name
	 * @param value
	 */
	public GenericProperty(String name, T value) {
		super(name, value);
	}

	/**
	 * @param name
	 * @param value
	 * @param manager
	 */
	public GenericProperty(String name, T value,
			IPropertyChangeManager manager) {
		super(name, value, manager);
	}

	/**
	 * @param string
	 * @see de.admadic.spiromat.model.types.IProperty#parseValue(java.lang.String)
	 */
	public void parseValue(String string) {
		throw new SpiromatException("GenericProperty does not support parsing"); //$NON-NLS-1$
	}
}
