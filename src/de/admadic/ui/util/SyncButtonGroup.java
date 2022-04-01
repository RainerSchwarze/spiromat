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

import java.awt.ItemSelectable;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.event.EventListenerList;

/**
 * 
 * @author Rainer Schwarze
 *
 */
public class SyncButtonGroup extends ButtonGroup implements ItemSelectable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	boolean working;	// FIXME: <- used for sync/locking
	boolean selected;
	EventListenerList listenerList;

	/**
	 * Creates an empty instance of a SyncButtonGroup.
	 */
	public SyncButtonGroup() {
		super();
		listenerList = new EventListenerList();
	}

	/**
	 * @return	Returns the button model which is selected.
	 * @see javax.swing.ButtonGroup#getSelection()
	 */
	@Override
	public ButtonModel getSelection() {
		return super.getSelection();
	}

	/**
	 * Note: the given ButtonModel is not used for evaluation of isSelected.
	 * @param m
	 * @return	Returns true, if the SyncButtonGroup instance is selected.
	 * @see javax.swing.ButtonGroup#isSelected(javax.swing.ButtonModel)
	 */
	@Override
	public boolean isSelected(ButtonModel m) {
		boolean tmp;
		tmp = selected;
		//tmp = m.isSelected();
		//tmp = super.isSelected(m);
		return tmp;
	}

	/**
	 * @return	Returns true, if this SyncButtonGroup is selected.
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param m
	 * @param b
	 * @see javax.swing.ButtonGroup#setSelected(javax.swing.ButtonModel, boolean)
	 */
	@Override
	public void setSelected(ButtonModel m, boolean b) {
		boolean DBG = false;
		if (working) {
			// FIXME: fix locking of this code
			if (DBG) System.out.println("i am working");
			return;
		}
		//super.setSelected(m, b);
		working = true;
		selected = b;
		if (DBG) System.out.println("sbg: setSelected: " + b + " (working)");
		for (AbstractButton ab : buttons) {
			if (ab!=null && m!=null && ab.getModel().equals(m)) {
				// its me: nothing
				if (DBG) System.out.println("its me!");
			} else {
				// all others: set to this state
				if (DBG) System.out.println("setting " + ab.getActionCommand() + " to " + b);
				ab.setSelected(b);
			}
		}
        fireItemStateChanged(new ItemEvent(
        		this,
				ItemEvent.ITEM_STATE_CHANGED, 
				this,
				this.isSelected() ? 
						ItemEvent.SELECTED
						: ItemEvent.DESELECTED)); 
		if (DBG) System.out.println("sbg: (stopped working)");
		working = false;
	}

	/**
	 * Sets the selected state of this SyncButtonGroup.
	 * @param b
	 */
	public void setSelected(boolean b) {
		setSelected(null, b);
	}
	
	/**
	 * @param l
	 * @see java.awt.ItemSelectable#addItemListener(java.awt.event.ItemListener)
	 */
	public void addItemListener(ItemListener l) {
		listenerList.add(ItemListener.class, l);
	}

	/**
	 * @return	See doc.
	 * @see java.awt.ItemSelectable#getSelectedObjects()
	 */
	public Object[] getSelectedObjects() {
		// ok that way:
		return null;
	}

	/**
	 * @param l
	 * @see java.awt.ItemSelectable#removeItemListener(java.awt.event.ItemListener)
	 */
	public void removeItemListener(ItemListener l) {
		listenerList.remove(ItemListener.class, l);
	}

	protected void fireItemStateChanged(ItemEvent e) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==ItemListener.class) {
                ((ItemListener)listeners[i+1]).itemStateChanged(e);
            }          
        }
    }   
}