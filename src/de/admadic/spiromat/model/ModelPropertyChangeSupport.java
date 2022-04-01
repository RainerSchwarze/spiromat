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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import de.admadic.spiromat.log.Logger;

/**
 * @author Rainer Schwarze
 *
 */
public class ModelPropertyChangeSupport implements ModelPropertyChangeListener {
	static Logger logger = Logger.getLogger(ModelPropertyChangeSupport.class);

	final private int mask;
	/** mask for handling app events */
	final static public int MASK_APP = 1<<0;
	/** mask for handling doc events */
	final static public int MASK_DOC = 1<<1;
	/** mask for handling fig events */
	final static public int MASK_FIG = 1<<2;
	/** mask for handling all events */
	final static public int MASK_ALL = 0xffffffff;

	private AppModel appModel;
	private DocModel docModel;
	private FigureSpec activeFigureSpec;
	
	private PropertyChangeListener appListener;
	private PropertyChangeListener docListener;
	private PropertyChangeListener figListener;

	private ModelPropertyChangeListener externalListener;

	/**
	 * 
	 */
	public ModelPropertyChangeSupport() {
		this(MASK_ALL, null, true);
	}

	/**
	 * @param externalListener 
	 * 
	 */
	public ModelPropertyChangeSupport(ModelPropertyChangeListener externalListener) {
		this(MASK_ALL, externalListener, true);
	}

	/**
	 * @param mask 
	 * @param externalListener 
	 * @param attachInitially 
	 * 
	 */
	public ModelPropertyChangeSupport(
			int mask, 
			ModelPropertyChangeListener externalListener, 
			boolean attachInitially) {
		super();
		this.mask = mask;
		this.externalListener = (externalListener==null) ? this : externalListener;

		if (isEnabled(MASK_APP)) {
			appListener = new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					coreAppPropertyChange(evt);
				}
			};
		}
		if (isEnabled(MASK_DOC)) {
			docListener = new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					coreDocPropertyChange(evt);
				}
			};
		}
		if (isEnabled(MASK_FIG)) {
			figListener = new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					coreFigPropertyChange(evt);
				}
			};
		}
		if (attachInitially) {
			attachToAppModel();
		}
	}

	/**
	 * 
	 */
	public void detachFromAppModel() {
		logger.debug("detaching from AppModel"); //$NON-NLS-1$
		setAppModel(null);
	}

	/**
	 * 
	 */
	public void attachToAppModel() {
		logger.debug("attaching to AppModel"); //$NON-NLS-1$
		if (isEnabled(MASK_APP)) {
			setAppModel(AppModel.getInstance());
		}
	}

	/**
	 * 
	 * @param bits
	 * @return	Returns true, if any of the specified bits is enabled.
	 */
	protected boolean isEnabled(int bits) {
		return (mask & bits)!=0;
	}

	protected void setAppModel(AppModel appModel) {
		logger.debug("setting AppModel = " + appModel); //$NON-NLS-1$
		if (this.appModel!=null) {
			this.appModel.removePropertyChangeListener(appListener);
			this.appModel = null;
		}
		this.appModel = appModel;
		DocModel newDocModel = null;
		if (this.appModel!=null) {
			this.appModel.addPropertyChangeListener(appListener);
			newDocModel = this.appModel.getDocModel();
		}
		if (isEnabled(MASK_DOC)) {
			setDocModel(newDocModel);
		}
	}
	
	/**
	 * @param docModel
	 */
	protected void setDocModel(DocModel docModel) {
		logger.debug("setting DocModel = " + docModel); //$NON-NLS-1$
		if (this.docModel!=null) {
			this.docModel.removePropertyChangeListener(docListener);
			this.docModel = null;
		}
		this.docModel = docModel;
		FigureSpec newFigureSpec = null;
		if (this.docModel!=null) {
			this.docModel.addPropertyChangeListener(docListener);
			newFigureSpec = this.docModel.getActiveFigureSpec();
		}
		if (isEnabled(MASK_FIG)) {
			setFigureSpec(newFigureSpec);
		}
	}	

	/**
	 * @param newFigureSpec
	 */
	protected void setFigureSpec(FigureSpec figureSpec) {
		logger.debug("setting FigureSpec = " + figureSpec); //$NON-NLS-1$
		if (activeFigureSpec!=null) {
			activeFigureSpec.removePropertyChangeListener(figListener);
			activeFigureSpec = null;
		}
		activeFigureSpec = figureSpec;
		if (activeFigureSpec!=null) {
			activeFigureSpec.addPropertyChangeListener(figListener);
		}
	}

	/**
	 * @param evt
	 */
	protected void coreAppPropertyChange(PropertyChangeEvent evt) {
		logger.debug("coreAppPropertyChange: " + evt.getPropertyName()); //$NON-NLS-1$
		externalListener.appPropertyChange(evt);
		String propName = evt.getPropertyName();
		if (propName!=null) {
			if (propName.equals(AppModel.DOC_MODEL)) {
				setDocModel(AppModel.getInstance().getDocModel());
			}
		}
	}

	/**
	 * @param evt
	 */
	protected void coreDocPropertyChange(PropertyChangeEvent evt) {
		logger.debug("coreDocPropertyChange: " + evt.getPropertyName()); //$NON-NLS-1$
		externalListener.docPropertyChange(evt);
		String propName = evt.getPropertyName();
		if (propName!=null) {
			if (propName.equals(DocModel.PROPERTY_ACTIVE_FIGURE)) {
				setFigureSpec(AppModel.getInstance().getDocModel().getActiveFigureSpec());
			}
		}
	}

	/**
	 * @param evt
	 */
	protected void coreFigPropertyChange(PropertyChangeEvent evt) {
		logger.debug("coreFigPropertyChange: " + evt.getPropertyName()); //$NON-NLS-1$
		externalListener.figPropertyChange(evt);
	}

	/**
	 * @param e
	 * @see de.admadic.spiromat.model.ModelPropertyChangeListener#appPropertyChange(java.beans.PropertyChangeEvent)
	 */
	public void appPropertyChange(PropertyChangeEvent e) {
		/* to override */
	}

	/**
	 * @param e
	 * @see de.admadic.spiromat.model.ModelPropertyChangeListener#docPropertyChange(java.beans.PropertyChangeEvent)
	 */
	public void docPropertyChange(PropertyChangeEvent e) {
		/* to override */
	}

	/**
	 * @param e
	 * @see de.admadic.spiromat.model.ModelPropertyChangeListener#figPropertyChange(java.beans.PropertyChangeEvent)
	 */
	public void figPropertyChange(PropertyChangeEvent e) {
		/* to override */
	}

}
