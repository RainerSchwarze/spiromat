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
 * Provides the interface of a Property.
 * 
 * @see AbstractProperty
 * 
 * @author Rainer Schwarze
 * @param <T> 
 */
public interface IProperty<T> {

	/**
	 * @return the enabled
	 */
	public abstract boolean isEnabled();

	/**
	 * @param enabled the enabled to set
	 */
	public abstract void setEnabled(boolean enabled);

	/**
	 * @return the visible
	 */
	public abstract boolean isVisible();

	/**
	 * @param visible the visible to set
	 */
	public abstract void setVisible(boolean visible);

	/**
	 * @return the name
	 */
	public abstract String getName();

	/**
	 * @return	Returns the value of the property.
	 */
	abstract public T getValue();

	/**
	 * @param value
	 */
	abstract public void setValue(T value);

	/**
	 * @param string
	 */
	abstract public void parseValue(String string);
}
