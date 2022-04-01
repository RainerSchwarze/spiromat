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
package de.admadic.spiromat.model;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import de.admadic.spiromat.DontObfuscate;
import de.admadic.spiromat.log.Logger;

/**
 * Provides a document model containing the data for a spiromat drawing.
 * 
 * CHECKME: Should we rename this to a Document? Or should it be kept a DocModel in relation to the AppModel?
 * 
 * @author Rainer Schwarze
 */
public class DocModel implements Serializable, DontObfuscate {
	/** */
	private static final long serialVersionUID = 1L;

	static Logger logger = Logger.getLogger(DocModel.class);

	// the model data:
	
	// the list of figures:
	private ArrayList<FigureSpec> figureSpecs = new ArrayList<FigureSpec>();
	// the index to the active figure:
	private int activeFigureIndex = -1;
	// the file for saving/loading:
	private File file;
	// marks the data as dirty, which causes a recommendation to save:
	private boolean dirty;

	transient private ArrayList<ListDataListener> listDataListeners = new ArrayList<ListDataListener>();

	// secondary data not being part of the actual data model:
	transient private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	/** property name of the list of FigureSpecs */
	final static public String PROPERTY_FIGURE_LIST = "figureList"; //$NON-NLS-1$
	/** property name for signalling list content changes */
	final static public String PROPERTY_FIGURE_LIST_CONTENT_CHANGED = "figureListContent"; //$NON-NLS-1$
	/** property name of the dirty field */
	final static public String PROPERTY_DOC_MODEL_DIRTY = "docModelDirty"; //$NON-NLS-1$
	/** property name of the active figure field */
	final static public String PROPERTY_ACTIVE_FIGURE = "activeFigure"; //$NON-NLS-1$

	transient private PropertyChangeListener figureChangeListener;
	
	// the DocModel delivers default colors for newly created FigureSpecs.
	// these are the helper fields for that purpose. (they are instance fields,
	// because if two DocModels exist, we want each instance to walk through
	// the list of default colors without being "disturbed".
	private int defaultColorIndex = 0;
	private Color[] defaultColors = new Color[] {
			Color.decode("0x339933"),	// admadic green //$NON-NLS-1$
			Color.ORANGE,
			Color.decode("0x0066cc"),	// admadic blue //$NON-NLS-1$
			Color.RED,
	};

	transient private boolean activeFigureSpecLocked = false;
	
	/**
	 * Creates an instance of the DocModel. Note that setDefaults is not called
	 * in this constructor.
	 */
	public DocModel() {
		super();
		initFigureChangeListener();
	}

	/**
	 * 
	 */
	private void initFigureChangeListener() {
		figureChangeListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				// FIXME: logic for deriving dirty from figure event ok?
				// a change took place in a figure. We must be dirty now.
				logger.debug("figure changed: setting dirty flag"); //$NON-NLS-1$
				setDirty(true);
			}
		};
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		listDataListeners = new ArrayList<ListDataListener>();
		pcs = new PropertyChangeSupport(this);
		initFigureChangeListener();

		for (FigureSpec fs : figureSpecs) {
			fs.addPropertyChangeListener(figureChangeListener);
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
	}

	/**
	 * Sets the default data for a fresh document model.
	 */
	public void setDefaults() {
		figureSpecs.clear();
		addFigureSpec(ModelUtil.createStandardFigureSpec(this));
	}

	/**
	 * @param figureSpec
	 */
	public void addFigureSpec(FigureSpec figureSpec) {
		int oldSize = figureSpecs.size();
		figureSpecs.add(figureSpec);
		setDirty(true);
		fireIntervalAdded(oldSize, oldSize);
		firePropertyChange(oldSize, figureSpecs.size());
		if (oldSize==0) {
			setActiveFigureIndex(0);
		}
		figureSpec.addPropertyChangeListener(figureChangeListener);
	}

	/**
	 * @return	Returns the number of FigureSpecs which exist in the DocModel.
	 */
	public int getFigureSpecCount() {
		return figureSpecs.size();
	}

	/**
	 * @return	Returns the currently active FigureSpec instance.
	 */
	public FigureSpec getActiveFigureSpec() {
		return activeFigureIndex>=0 ? figureSpecs.get(activeFigureIndex) : null;
	}
	
	/**
	 * @param index
	 * @return	Returns the FigureSpec instance for the given index.
	 */
	public FigureSpec getFigureSpec(int index) {
		return figureSpecs.get(index);
	}
	
	/**
	 * @param figureSpec
	 */
	public void removeFigureSpec(FigureSpec figureSpec) {
		int oldSize = figureSpecs.size();
		if (oldSize<=1) {
			logger.warn("cannot remove last FigureSpec"); //$NON-NLS-1$
			return;
		}
		int pos = figureSpecs.indexOf(figureSpec);
		if (pos<0) return;
		removeFigureSpec(pos);
		// this is done in the (int)-parameter version
//		figureSpecs.remove(figureSpec);
//		setDirty(true);
//		fireIntervalRemoved(pos, pos+1);
//		firePropertyChange(oldSize, figureSpecs.size());
	}

	/**
	 * @param index
	 */
	public void removeFigureSpec(int index) {
		int oldSize = figureSpecs.size();
		if (oldSize<=1) {
			logger.warn("cannot remove last FigureSpec"); //$NON-NLS-1$
			return;
		}
		FigureSpec fs = figureSpecs.get(index);
		fs.removePropertyChangeListener(figureChangeListener);
		int lastActive = getActiveFigureIndex();
		if (lastActive==index) {
			setActiveFigureIndex(-1);
		}
		figureSpecs.remove(index);
		if (lastActive!=-1) {
			if (lastActive>=figureSpecs.size()) {
				lastActive = figureSpecs.size()-1;
			}
		}
		setDirty(true);
		fireIntervalRemoved(index, index+1);
		firePropertyChange(oldSize, figureSpecs.size());
		if (lastActive!=-1) {
			setActiveFigureIndex(lastActive);
		}
	}

	/**
	 * @param idx1
	 * @param idx2
	 */
	public void exchangeFigureSpecsAt(int idx1, int idx2) {
		logger.debug("exchanging " + idx1 + " with " + idx2); //$NON-NLS-1$ //$NON-NLS-2$
		if (idx1<0 || idx2<0) {
			throw new IllegalArgumentException("indexes must be non-negative integers"); //$NON-NLS-1$
		}
		int tmp = figureSpecs.size();
		if (idx1>=tmp || idx2>=tmp) {
			throw new IllegalArgumentException("indexes must be less than the number of FigureSpecs"); //$NON-NLS-1$
		}
		if (idx1==idx2) return;	// just ignore that

		FigureSpec f1 = figureSpecs.get(idx1);
		FigureSpec f2 = figureSpecs.get(idx2);
		figureSpecs.set(idx1, f2);
		figureSpecs.set(idx2, f1);

		setDirty(true);

		fireContentsChanged(idx1, idx2);
	}

	/**
	 * @param listener
	 * @see java.beans.PropertyChangeSupport#addPropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	/**
	 * @param oldValue
	 * @param newValue
	 */
	protected void firePropertyChange(int oldValue, int newValue) {
		pcs.firePropertyChange(PROPERTY_FIGURE_LIST, oldValue, newValue);
	}

	/**
	 * @param listener
	 * @see java.beans.PropertyChangeSupport#removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	/**
	 * @return	Returns a reference to the list of FigureSpecs.
	 */
	public WeakReference<ArrayList<FigureSpec>> getFigureListReference() {
		return new WeakReference<ArrayList<FigureSpec>>(figureSpecs);
	}

	/**
	 * @return the dirty
	 */
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * @param dirty the dirty to set
	 */
	public void setDirty(boolean dirty) {
		// this method is called frequently: bail out soon if nothing's changed:
		if (this.dirty==dirty) 
			return;

		boolean oldDirty = this.dirty;
		this.dirty = dirty;
		pcs.firePropertyChange(PROPERTY_DOC_MODEL_DIRTY, oldDirty, this.dirty);
	}

	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(File file) {
		this.file = file;
		setDirty(true);
	}

	/**
	 * @return	Returns the next default color.
	 */
	public Color getNextDefaultColor() {
		Color c = defaultColors[defaultColorIndex++];
		if (defaultColorIndex>=defaultColors.length) {
			defaultColorIndex = 0;
		}
		return c;
	}

	/**
	 * ATN: This method may change, it is only intended for serialization.
	 * 
	 * @return the defaultColorIndex
	 */
	public int getDefaultColorIndex() {
		return defaultColorIndex;
	}


	/**
	 * ATN: This method may change, it is only intended for serialization.
	 * 
	 * @param defaultColorIndex the defaultColorIndex to set
	 */
	public void setDefaultColorIndex(int defaultColorIndex) {
		this.defaultColorIndex = defaultColorIndex;
	}


	/**
	 * @return the activeFigureIndex
	 */
	public int getActiveFigureIndex() {
		return activeFigureIndex;
	}

	/**
	 * @param activeFigureIndex the activeFigureIndex to set
	 */
	public void setActiveFigureIndex(int activeFigureIndex) {
		if (activeFigureIndex>=figureSpecs.size()) {
			logger.warn("tried to set active figure index out of range"); //$NON-NLS-1$
			return;
		}
		FigureSpec oldValue = null;
		if (this.activeFigureIndex>=0) {
			oldValue = figureSpecs.get(this.activeFigureIndex);
			oldValue.setActive(false);
		}
		setDirty(true);
		pcs.firePropertyChange(PROPERTY_ACTIVE_FIGURE, oldValue, -1);
		this.activeFigureIndex = activeFigureIndex;
		FigureSpec newValue = null;
		if (this.activeFigureIndex>=0) {
			newValue = figureSpecs.get(this.activeFigureIndex);
			newValue.setActive(true);
		}
		pcs.firePropertyChange(PROPERTY_ACTIVE_FIGURE, -1, newValue);
	}

	protected void fireIntervalAdded(int start, int end) {
		Iterator<ListDataListener> it = listDataListeners.iterator();
		ListDataEvent ev = null;
		while (it.hasNext()) {
			ListDataListener l = it.next();
			if (ev==null) {
				ev = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, start, end);
			}
			l.intervalAdded(ev);
		}
	}

	protected void fireIntervalRemoved(int start, int end) {
		Iterator<ListDataListener> it = listDataListeners.iterator();
		ListDataEvent ev = null;
		while (it.hasNext()) {
			ListDataListener l = it.next();
			if (ev==null) {
				ev = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, start, end);
			}
			l.intervalRemoved(ev);
		}
	}

	protected void fireContentsChanged(int start, int end) {
		pcs.firePropertyChange(PROPERTY_FIGURE_LIST_CONTENT_CHANGED, false, true);
		Iterator<ListDataListener> it = listDataListeners.iterator();
		ListDataEvent ev = null;
		while (it.hasNext()) {
			ListDataListener l = it.next();
			if (ev==null) {
				ev = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, start, end);
			}
			l.contentsChanged(ev);
		}
	}

	/**
	 * @param o
	 */
	public void addListDataListener(ListDataListener o) {
		listDataListeners.add(o);
	}


	/**
	 * @param o
	 */
	public void removeListDataListener(ListDataListener o) {
		listDataListeners.remove(o);
	}


	/**
	 * @return the figureSpecLocked
	 */
	public boolean isActiveFigureSpecLocked() {
		return activeFigureSpecLocked;
	}


	/**
	 * @param figureSpecLocked the figureSpecLocked to set
	 */
	public void setActiveFigureSpecLocked(boolean figureSpecLocked) {
		this.activeFigureSpecLocked = figureSpecLocked;
	}

}
