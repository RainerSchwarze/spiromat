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

import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Provides a container for a list of components for which the enabled status 
 * shall be managed. This class is used as a delegate in Property
 * class and its subclasses.
 * <p>
 * Putting UI component handling into a Property is not very nice for 
 * encapsulation but it shall suffice for now.
 * 
 * @author Rainer Schwarze
 */
public class ComponentUpdater {
	private final static ArrayList<Component> EMPTY_LIST = new ArrayList<Component>();
	private ArrayList<Component> components = EMPTY_LIST;

	/**
	 * Creates an instance with an empty list of components.
	 */
	public ComponentUpdater() {
		super();
	}

	/**
	 * Adds the given component to the list of components.
	 * 
	 * @param comp
	 */
	public void addComponent(Component comp) {
		if (components==EMPTY_LIST) {
			components = new ArrayList<Component>();
		}
		components.add(comp);
	}

	/**
	 * Removes the given component from the list of components.
	 * 
	 * @param comp
	 */
	public void removeComponent(Component comp) {
		components.remove(comp);
	}

	/**
	 * Calls setEnabled on all Components in the list of components.
	 * 
	 * @param b
	 */
	public void setEnabled(boolean b) {
		Iterator<Component> it = components.iterator();
		while (it.hasNext()) {
			Component comp = it.next();
			comp.setEnabled(b);
		}
	}
}
