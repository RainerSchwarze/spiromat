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
public class BooleanProperty extends AbstractProperty<Boolean> {

	/**
	 * @param name
	 */
	public BooleanProperty(String name) {
		super(name);
	}

	/**
	 * @param name
	 * @param value 
	 */
	public BooleanProperty(String name, Boolean value) {
		super(name, value);
	}


	/**
	 * @param name
	 * @param value
	 * @param manager
	 */
	public BooleanProperty(String name, Boolean value, IPropertyChangeManager manager) {
		super(name, value, manager);
	}

	/**
	 * @param value
	 */
	public void setBooleanValue(boolean value) {
		setValue(Boolean.valueOf(value));
	}

	/**
	 * @return	Returns the int value of the property.
	 * @throws	NullPointerException if the underlying property is <code>null</code>.
	 * @throws	IllegalCastException if the underlying property is not a Boolean.
	 */
	public boolean booleanValue() {
		return getValue().booleanValue();
	}


	/**
	 * @return	Returns the value of the property as a Boolean.
	 */
	public Boolean getBooleanValue() {
		return getValue();
	}


	/**
	 * @param string
	 * @see de.admadic.spiromat.model.types.IProperty#parseValue(java.lang.String)
	 */
	public void parseValue(String string) {
		setBooleanValue(Boolean.parseBoolean(string));
	}
}
