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

import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;

import javax.swing.JToggleButton;

/**
 * The VetoableToggleButtonModel supports a veto when the selected state
 * of the underlying button model changes.
 * To use it, call addVetoableChangeListener and handle the VetoableChangeEvent.
 * If the selected state shall not be changed, throw a PropertyVetoException.
 * Note:
 * The setSelected method of the underlying button model will still be called,
 * but with the state reset to the old value.
 * 
 * @author Rainer Schwarze
 */
public class VetoableToggleButtonModel extends 
	JToggleButton.ToggleButtonModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * We simply use the VetoableChangeSupport as the "worker" for the 
	 * handling of the listener management.
	 */
	VetoableChangeSupport vetoableChangeSupport;
	/** 
	 * constant to be used for vetoable change listeners which 
	 * compare the property name.
	 */
	public final static String SELECTED_PROPERTY = "selected";
	
	/**
	 * Creates a VetoableToggleButtonModel.
	 */
	public VetoableToggleButtonModel() {
		super();
		vetoableChangeSupport = new VetoableChangeSupport(this);
	}

	/**
	 * Changes the selected state of the button model, but first asks any
	 * VetoableChangeListeners whether thats allowed or not.
	 * The setSelected method of the underlying button model is called in any 
	 * case.
	 * 
	 * @param b	True if selected, false if not selected.
	 * @see javax.swing.JToggleButton.ToggleButtonModel#setSelected(boolean)
	 */
	@Override
	public void setSelected(boolean b) {
		boolean oldB = isSelected();
		boolean newB = b;
		try {
			vetoableChangeSupport.fireVetoableChange(
					SELECTED_PROPERTY, oldB, newB);
		} catch (PropertyVetoException e) {
			// we have a veto:
			newB = oldB;
		}
		b = newB;
		super.setSelected(b);
	}

	/**
	 * Adds a VetoableChangeListener.
	 * 
	 * @param listener	The VetoableChangeListener to add.
	 * @see java.beans.VetoableChangeSupport#addVetoableChangeListener(java.beans.VetoableChangeListener)
	 */
	public synchronized void addVetoableChangeListener(VetoableChangeListener listener) {
		vetoableChangeSupport.addVetoableChangeListener(listener);
	}

	/**
	 * Removes a VetoableChangeListener.
	 * 
	 * @param listener	The VetoableChangeListener to remove.
	 * @see java.beans.VetoableChangeSupport#removeVetoableChangeListener(java.beans.VetoableChangeListener)
	 */
	public synchronized void removeVetoableChangeListener(VetoableChangeListener listener) {
		vetoableChangeSupport.removeVetoableChangeListener(listener);
	}	
}
