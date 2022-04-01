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

/**
 * @author Rainer Schwarze
 *
 */
public class IntegerProperty extends AbstractProperty<Integer> {

	/**
	 * @param name
	 */
	public IntegerProperty(String name) {
		super(name);
	}

	/**
	 * @param name
	 * @param value 
	 */
	public IntegerProperty(String name, Integer value) {
		super(name, value);
	}

	/**
	 * @param name
	 * @param value
	 * @param manager
	 */
	public IntegerProperty(String name, Integer value, IPropertyChangeManager manager) {
		super(name, value, manager);
	}


	/**
	 * @param value
	 */
	public void setIntValue(int value) {
		setValue(new Integer(value));
	}

	/**
	 * @return	Returns the int value of the property.
	 * @throws	NullPointerException if the underlying property is <code>null</code>.
	 * @throws	IllegalCastException if the underlying property is not an Integer.
	 */
	public int intValue() {
		return getValue().intValue();
	}

	
	/**
	 * @param string
	 * @see de.admadic.spiromat.model.types.IProperty#parseValue(java.lang.String)
	 */
	public void parseValue(String string) {
		setIntValue(Integer.parseInt(string));
	}
}
