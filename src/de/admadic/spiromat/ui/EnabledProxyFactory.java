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
package de.admadic.spiromat.ui;

import javax.swing.Action;
import javax.swing.JComponent;

import de.admadic.spiromat.actions.ActionFactory;

/**
 * @author Rainer Schwarze
 *
 */
public class EnabledProxyFactory {
	/**
	 * @param target
	 * @return	Returns the IEnabledProxy implementation instance wrapping this 
	 * 			JComponent instance.
	 */
	static public IEnabledProxy wrap(JComponent target) {
		return new JComponentEnabledProxy(target);
	}

	/**
	 * @param target
	 * @return	Returns the IEnabledProxy implementation instance wrapping this 
	 * 			Action instance.
	 */
	static public IEnabledProxy wrap(Action target) {
		return new ActionEnabledProxy(target);
	}

	/**
	 * @param actionName 
	 * @return	Returns the IEnabledProxy implementation instance wrapping this 
	 * 			Action instance.
	 */
	static public IEnabledProxy wrap(String actionName) {
		return new ActionEnabledProxy(
			ActionFactory.get(actionName));
	}
}
