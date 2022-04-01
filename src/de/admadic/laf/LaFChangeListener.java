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

import java.util.EventListener;

/**
 * @author Rainer Schwarze
 *
 */
public interface LaFChangeListener extends EventListener {
	/**
	 * LaF has successfully changed.
	 * @param e
	 */
	public abstract void lafChanged(LaFChangeEvent e);
	/**
	 * LaF will change now.
	 * @param e
	 */
	public abstract void lafChangeBegin(LaFChangeEvent e);
	/**
	 * LaF could not be changed, restore anything done in lafChangeBegin.
	 * @param e
	 */
	public abstract void lafChangeFailed(LaFChangeEvent e);

	/**
	 * LaF changed - updateUI finished.
	 * @param e
	 */
	public abstract void lafChangedUI(LaFChangeEvent e);
}
