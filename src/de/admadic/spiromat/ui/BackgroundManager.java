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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashSet;

import javax.swing.SwingUtilities;

/**
 * @author Rainer Schwarze
 *
 */
public class BackgroundManager {
	private static BackgroundManager instance = null;

	private HashSet<Runnable> runnables = new HashSet<Runnable>();
	PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	/** Property name for the task list */
	public final static String PROP_TASK_LIST = "taskList"; //$NON-NLS-1$
	
	/**
	 * @return	Returns the singleton instance of BackgroundManager.
	 */
	public static BackgroundManager getInstance() {
		if (instance==null) {
			instance = new BackgroundManager();
		}
		return instance;
	}

	private BackgroundManager() {
		super();
	}

	/**
	 * @param r
	 */
	public synchronized void addTask(Runnable r) {
		final int oldSize = runnables.size();
		runnables.add(r);
		final int newSize = runnables.size();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				pcs.firePropertyChange(PROP_TASK_LIST, oldSize, newSize);
			}
		});
	}

	/**
	 * @param r
	 */
	public synchronized void markDone(Runnable r) {
		final int oldSize = runnables.size();
		runnables.remove(r);
		final int newSize = runnables.size();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				pcs.firePropertyChange(PROP_TASK_LIST, oldSize, newSize);
			}
		});
	}

	/**
	 * @return	Returns the number of tasks waiting to terminate.
	 */
	public synchronized int getTaskCount() {
		return runnables.size();
	}
	
	/**
	 * @param listener
	 * @see java.beans.PropertyChangeSupport#addPropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	/**
	 * @param listener
	 * @see java.beans.PropertyChangeSupport#removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}
}
