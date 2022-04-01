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
import java.awt.Component;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

import de.admadic.spiromat.log.Logger;
import de.admadic.spiromat.model.types.AbstractProperty;
import de.admadic.spiromat.model.types.BooleanActionProperty;
import de.admadic.spiromat.model.types.BooleanProperty;
import de.admadic.spiromat.model.types.ColorProperty;
import de.admadic.spiromat.model.types.DoubleProperty;
import de.admadic.spiromat.model.types.GenericProperty;
import de.admadic.spiromat.model.types.IProperty;
import de.admadic.spiromat.model.types.IPropertyChangeManager;
import de.admadic.spiromat.model.types.IntegerProperty;

/**
 * The data model behind the Spiromat module.
 * 
 * The Model is thread safe - all public functions which require it are 
 * synchronized or use similar protection.
 * 
 * The individual fields of the Model are subclasses of Property. A Property
 * manages a list of components serving it. A Property can be enabled or 
 * disabled and forwards that state to all components registered for it.
 * 
 * The Model provides convenience methods for enabling/disabling Properties
 * for a given name.
 * 
 * @author Rainer Schwarze
 */
public final class AppModel implements IPropertyChangeManager {
	private final static Logger logger = Logger.getLogger(AppModel.class);

	private static AppModel singletonInstance = null;

	/**
	 * @return	Returns the singleton instance of the AppModel class.
	 */
	public static AppModel getInstance() {
		if (singletonInstance==null) {
			singletonInstance = new AppModel();
		}
		return singletonInstance;
	}
	
	
	/*
	 * Note:
	 * 
	 * fields marked transient, are only runtime properties and should not 
	 * be stored to some kind of settings persistence.
	 * (The transient keyword is right now primarily used as documentation.
	 * Should restoration of execution state be implemented some time, the 
	 * use of the keyword should be changed.)
	 */

	// FIXME: remove unused / superfluous properties.
	
	private BooleanProperty _animated;			// animate the display
	private BooleanProperty _mouseControlled;	// use mouse control
	private BooleanProperty _instantUpdate;		// instantly update display when parameters change

	private BooleanProperty _showGears;			// show gears in display
	private BooleanProperty _showFigure;		// show figure in display
	private BooleanProperty _autoFill;			// automatically fills figures
	private BooleanProperty _showPicture;		// show picture (the result) in display

	transient private BooleanProperty _showFigureTemporarily;	// show figure temporarily (overrides showFigure)
	transient private BooleanProperty _holdFigure;		// keeps the current figure
	transient private BooleanActionProperty _fillFigure;	// fills the current figure
	transient private BooleanActionProperty _clearFigure;	// clears the current figure
	transient private GenericProperty<DocModel>	_docModel;	// keeps the current docmodel

	private BooleanProperty _antialiasing;		// draw with antialiasing?

	private IntegerProperty _timeRound;			// [ms]	time for one round of inner gear
	private DoubleProperty _figureAlpha;		// the alpha value (transparency) for the figure

	// the colors of the drawing elements:
	private ColorProperty _colorFigure;
	private ColorProperty _colorOuterGear;
	private ColorProperty _colorInnerGear;
	private ColorProperty _colorMouseGuide;
	private ColorProperty _colorCanvas;

	private IntegerProperty _outerRadius;		// the radius of the outer gear
	private IntegerProperty _innerRadius;		// the radius of the inner gear
	private DoubleProperty _lambda;				// the relative location of the hole in the inner gear

	private BooleanProperty _started;			// handles start/stop
	private BooleanProperty _pause;				// handles pause/continue

	/** name of the showGears property */
	public final static String SHOW_GEARS = "showGears";  //$NON-NLS-1$
	/** name of the showFigure property */
	public final static String SHOW_FIGURE = "showFigure";  //$NON-NLS-1$
	/** name of the showFigureTemporarily property */
	public final static String SHOW_FIGURE_TEMPORARILY = "showFigureTemporarily";  //$NON-NLS-1$
	/** name of the showPicture property */
	public final static String SHOW_PICTURE = "showPicture";  //$NON-NLS-1$
	/** name of the autoFill property */
	public final static String AUTO_FILL = "autoFill";  //$NON-NLS-1$
	/** name of the holdFigure property */
	public final static String HOLD_FIGURE = "holdFigure";  //$NON-NLS-1$
	/** name of the clearFigure action property */
	public final static String CLEAR_FIGURE = "clearFigure";  //$NON-NLS-1$
	/** name of the fillFigure action property */
	public final static String FILL_FIGURE = "fillFigure";  //$NON-NLS-1$
	/** name of the animated property */
	public final static String ANIMATED = "animated";  //$NON-NLS-1$
	/** name of the mouseControlled property */
	public final static String MOUSE_CONTROLLED = "mouseControlled";  //$NON-NLS-1$
	/** name of the instantUpdate property */
	public final static String INSTANT_UPDATE = "instantUpdate";  //$NON-NLS-1$
	/** name of the antiAliasing property */
	public final static String ANTIALIASING = "antialiasing";  //$NON-NLS-1$
	/** name of the timeRound property */
	public final static String TIME_ROUND = "timeRound";  //$NON-NLS-1$
	/** name of the figureAlpha property */
	public final static String FIGURE_ALPHA = "figureAlpha";  //$NON-NLS-1$
	/** name of the colorFigure property */
	public final static String COLOR_FIGURE = "colorFigure";  //$NON-NLS-1$
	/** name of the colorInnerGear property */
	public final static String COLOR_INNER_GEAR = "colorInnerGear";  //$NON-NLS-1$
	/** name of the colorOuterGear property */
	public final static String COLOR_OUTER_GEAR = "colorOuterGear";  //$NON-NLS-1$
	/** name of the colorMouseGuide property */
	public final static String COLOR_MOUSE_GUIDE = "colorMouseGuide";  //$NON-NLS-1$
	/** name of the colorCanvas property */
	public final static String COLOR_CANVAS = "colorCanvas";  //$NON-NLS-1$

	/** name of the outerRadius property */
	public final static String OUTER_RADIUS = "outerRadius";  //$NON-NLS-1$
	/** name of the innerRadius property */
	public final static String INNER_RADIUS = "innerRadius";  //$NON-NLS-1$
	/** name of the lambda property */
	public final static String LAMBDA = "lambda";  //$NON-NLS-1$

	/** name of the started property */
	public final static String STARTED = "started";  //$NON-NLS-1$
	/** name of the pause property */
	public final static String PAUSE = "pause";  //$NON-NLS-1$

	/** name of the docmodel property */
	public final static String DOC_MODEL = "docModel";  //$NON-NLS-1$

	private HashMap<String,IProperty> propertyHash;
	private PropertyChangeSupport propertyChangeSupport;

	/**
	 * Creates an instance of the Model and sets the default values.
	 */
	private AppModel() {
		super();
		propertyChangeSupport = new PropertyChangeSupport(this);
		setDefaults();
		initPropertySpecs();
	}

	/**
	 * Registers the given component to the Property with the given name.
	 * 
	 * @param propName	A String specifying the name of the Property to 
	 * 					register the Component with.
	 * @param comp		The Component to register to the Property.
	 */
	public synchronized void addComponent(String propName, Component comp) {
		AbstractProperty prop = (AbstractProperty) propertyHash.get(propName);
		prop.addComponent(comp);
	}

	/**
	 * Un-Registers the given component from the Property with the given name.
	 * 
	 * @param propName	A String specifying the name of the Property to 
	 * 					un-register the Component from.
	 * @param comp		The Component to unregister from the Property.
	 */
	public synchronized void removeComponent(String propName, Component comp) {
		AbstractProperty prop = (AbstractProperty) propertyHash.get(propName);
		prop.removeComponent(comp);
	}

	/**
	 * Enables or disables the Property with the given name. If there are any
	 * components registered for that Property, they will get enabled or 
	 * disabled too.
	 * 
	 * @param propName	A String with the name of the Property which shall be 
	 * 					enabled or disabled.
	 * @param b			A boolean specifying whether to enable (true) or 
	 * 					disable (false).
	 */
	public synchronized void setEnabled(String propName, boolean b) {
		AbstractProperty prop = (AbstractProperty) propertyHash.get(propName);
		prop.setEnabled(b);
	}

	// ///////////////////////////////////////////
	// property change handling:

	/**
	 * @param listener
	 * @see java.beans.PropertyChangeSupport#addPropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	/**
	 * @param propertyName
	 * @param listener
	 * @see java.beans.PropertyChangeSupport#addPropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
	 */
	public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * @param propertyName
	 * @param oldValue
	 * @param newValue
	 * @see java.beans.PropertyChangeSupport#firePropertyChange(java.lang.String, boolean, boolean)
	 */
	public synchronized void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
		propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	/**
	 * @param propertyName
	 * @param oldValue
	 * @param newValue
	 * @see java.beans.PropertyChangeSupport#firePropertyChange(java.lang.String, int, int)
	 */
	public synchronized void firePropertyChange(String propertyName, int oldValue, int newValue) {
		propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	/**
	 * @param propertyName
	 * @param oldValue
	 * @param newValue
	 * @see java.beans.PropertyChangeSupport#firePropertyChange(java.lang.String, java.lang.Object, java.lang.Object)
	 */
	public synchronized void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		logger.debug("propchange: " + propertyName + ": " + oldValue + " -> " + newValue); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	/**
	 * @param listener
	 * @see java.beans.PropertyChangeSupport#removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	/**
	 * @param propertyName
	 * @param listener
	 * @see java.beans.PropertyChangeSupport#removePropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
	 */
	public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}


	// ////////////////////////////////////////////////
	// access functions for the properties:

	/**
	 * @return the animated
	 */
	public synchronized boolean getAnimated() {
		return _animated.booleanValue();
	}

	/**
	 * @param animated the animated to set
	 */
	public synchronized void setAnimated(boolean animated) {
		if (animated) {
			setMouseControlled(false);
			setInstantUpdate(false);
		}
		this._animated.setBooleanValue(animated);
	}

	/**
	 * @return the antialiasing
	 */
	public synchronized boolean getAntialiasing() {
		return _antialiasing.booleanValue();
	}

	/**
	 * @param antialiasing the antialiasing to set
	 */
	public synchronized void setAntialiasing(boolean antialiasing) {
		this._antialiasing.setBooleanValue(antialiasing);
	}

	/**
	 * @return the colorFigure
	 */
	public synchronized Color getColorFigure() {
		return _colorFigure.getValue();
	}

	/**
	 * @param colorFigure the colorFigure to set
	 */
	public synchronized void setColorFigure(Color colorFigure) {
		this._colorFigure.setValue(colorFigure);
	}

	/**
	 * @return the colorInnerGear
	 */
	public synchronized Color getColorInnerGear() {
		return _colorInnerGear.getValue();
	}

	/**
	 * @param colorInnerGear the colorInnerGear to set
	 */
	public synchronized void setColorInnerGear(Color colorInnerGear) {
		this._colorInnerGear.setValue(colorInnerGear);
	}

	/**
	 * @return the colorMouseGuide
	 */
	public synchronized Color getColorMouseGuide() {
		return _colorMouseGuide.getValue();
	}

	/**
	 * @param colorMouseGuide the colorMouseGuide to set
	 */
	public synchronized void setColorMouseGuide(Color colorMouseGuide) {
		this._colorMouseGuide.setValue(colorMouseGuide);
	}

	/**
	 * @return the colorCanvas
	 */
	public synchronized Color getColorCanvas() {
		return _colorCanvas.getValue();
	}

	/**
	 * @param colorCanvas the colorMouseGuide to set
	 */
	public synchronized void setColorCanvas(Color colorCanvas) {
		this._colorCanvas.setValue(colorCanvas);
	}

	/**
	 * @return the colorOuterGear
	 */
	public synchronized Color getColorOuterGear() {
		return _colorOuterGear.getValue();
	}

	/**
	 * @param colorOuterGear the colorOuterGear to set
	 */
	public synchronized void setColorOuterGear(Color colorOuterGear) {
		this._colorOuterGear.setValue(colorOuterGear);
	}

	/**
	 * @return the figureAlpha
	 */
	public synchronized double getFigureAlpha() {
		return _figureAlpha.doubleValue();
	}

	/**
	 * @param figureAlpha the figureAlpha to set
	 */
	public synchronized void setFigureAlpha(double figureAlpha) {
		this._figureAlpha.setDoubleValue(figureAlpha);
	}

	/**
	 * @return the showFigureTemporarily
	 */
	public synchronized boolean getShowFigureTemporarily() {
		return _showFigureTemporarily.booleanValue();
	}

	/**
	 * @param showFigureTemporarily the showFigureTemporarily to set
	 */
	public synchronized void setShowFigureTemporarily(boolean showFigureTemporarily) {
		this._showFigureTemporarily.setBooleanValue(showFigureTemporarily);
	}

	/**
	 * @return the innerRadius
	 */
	public synchronized int getInnerRadius() {
		return _innerRadius.intValue();
	}

	/**
	 * @param innerRadius the innerRadius to set
	 */
	public synchronized void setInnerRadius(int innerRadius) {
		this._innerRadius.setIntValue(innerRadius);
	}

	/**
	 * @return the instantUpdate
	 */
	public synchronized boolean getInstantUpdate() {
		return _instantUpdate.booleanValue();
	}

	/**
	 * @param instantUpdate the instantUpdate to set
	 */
	public synchronized void setInstantUpdate(boolean instantUpdate) {
		if (instantUpdate) {
			setMouseControlled(false);
			setAnimated(false);
		}
		this._instantUpdate.setBooleanValue(instantUpdate);
	}

	/**
	 * @return the lambda
	 */
	public synchronized double getLambda() {
		return _lambda.doubleValue();
	}

	/**
	 * @param lambda the lambda to set
	 */
	public synchronized void setLambda(double lambda) {
		this._lambda.setDoubleValue(lambda);
	}

	/**
	 * @return the mouseControlled
	 */
	public synchronized boolean getMouseControlled() {
		return _mouseControlled.booleanValue();
	}

	/**
	 * @param mouseControlled the mouseControlled to set
	 */
	public synchronized void setMouseControlled(boolean mouseControlled) {
		if (mouseControlled) {
			setAnimated(false);
			setInstantUpdate(false);
		}
		this._mouseControlled.setBooleanValue(mouseControlled);
	}

	/**
	 * @return the outerRadius
	 */
	public synchronized int getOuterRadius() {
		return _outerRadius.intValue();
	}

	/**
	 * @param outerRadius the outerRadius to set
	 */
	public synchronized void setOuterRadius(int outerRadius) {
		this._outerRadius.setIntValue(outerRadius);
	}

	/**
	 * @return the showGears
	 */
	public synchronized boolean getShowGears() {
		return _showGears.booleanValue();
	}

	/**
	 * @param showGears the showGears to set
	 */
	public synchronized void setShowGears(boolean showGears) {
		this._showGears.setBooleanValue(showGears);
	}

	/**
	 * Returns true, if showFigure or showFigureTemporarily are true.
	 * This method should be used to determine, whether the figure should be 
	 * displayed or not.
	 * 
	 * @return	Returns true, if showFigure or showFigureTemporarily are true.
	 */
	public synchronized boolean getShowFigureActually() {
		return _showFigure.booleanValue() || _showFigureTemporarily.booleanValue();
	}
	
	/**
	 * @return the _showFigure
	 */
	public synchronized boolean getShowFigure() {
		return _showFigure.booleanValue();
	}

	/**
	 * @param showFigure the _showFigure to set
	 */
	public synchronized void setShowFigure(boolean showFigure) {
		_showFigure.setBooleanValue(showFigure);
	}

	/**
	 * @return the _showPicture
	 */
	public synchronized boolean getShowPicture() {
		return _showPicture.booleanValue();
	}

	/**
	 * @param showPicture the _showPicture to set
	 */
	public synchronized void setShowPicture(boolean showPicture) {
		_showPicture.setBooleanValue(showPicture);
	}

	/**
	 * @return the _autoFill
	 */
	public synchronized boolean getAutoFill() {
		return _autoFill.booleanValue();
	}

	/**
	 * @param autoFill the _showFigure to set
	 */
	public synchronized void setAutoFill(boolean autoFill) {
		_autoFill.setBooleanValue(autoFill);
	}

	/**
	 * @return the timeRound
	 */
	public synchronized int getTimeRound() {
		return _timeRound.intValue();
	}

	/**
	 * @param timeRound the timeStep to set
	 */
	public synchronized void setTimeRound(int timeRound) {
		this._timeRound.setIntValue(timeRound);
	}

	/**
	 * @return the started
	 */
	public synchronized boolean getStarted() {
		return _started.booleanValue();
	}

	/**
	 * @param started the started to set
	 */
	public synchronized void setStarted(boolean started) {
		this._started.setBooleanValue(started);
	}

	/**
	 * @return the pause
	 */
	public synchronized boolean getPause() {
		return _pause.booleanValue();
	}

	/**
	 * @param pause the reset to set
	 */
	public synchronized void setPause(boolean pause) {
		this._pause.setBooleanValue(pause);
	}

	/**
	 * @return the holdFigure
	 */
	public synchronized boolean getHoldFigure() {
		return _holdFigure.booleanValue();
	}

	/**
	 * @param holdFigure the holdFigure to set
	 */
	public synchronized void setHoldFigure(boolean holdFigure) {
		_holdFigure.setBooleanValue(holdFigure);
	}

	/**
	 * Note: there is no getter, since this is backed by an BooleanActionProperty
	 * whose value is always FALSE.
	 * 
	 * @param clearFigure the _clearFigure to set
	 */
	public synchronized void setClearFigure(boolean clearFigure) {
		_clearFigure.setBooleanValue(clearFigure);
	}

	/**
	 * Note: there is no getter, since this is backed by an BooleanActionProperty
	 * whose value is always FALSE.
	 * 
	 * @param fillFigure the _fillFigure to set
	 */
	public synchronized void setFillFigure(boolean fillFigure) {
		_fillFigure.setBooleanValue(fillFigure);
	}

	
	/**
	 * @return the docModel
	 */
	public synchronized DocModel getDocModel() {
		return _docModel.getValue();
	}

	/**
	 * @param docModel the DocModel to set
	 */
	public synchronized void setDocModel(DocModel docModel) {
		this._docModel.setValue(docModel);
	}

	// ////////////////////////////////////////////////////
	// private worker functions:

	private void addPropertySpec(IProperty prop) {
		propertyHash.put(prop.getName(), prop);
	}
	
	/**
	 * 
	 */
	private void initPropertySpecs() {
		if (propertyHash==null) {
			propertyHash = new HashMap<String,IProperty>();
		}
		propertyHash.clear();

		addPropertySpec(_showGears);
		addPropertySpec(_showFigure);
		addPropertySpec(_showPicture);
		addPropertySpec(_autoFill);
		addPropertySpec(_animated);
		addPropertySpec(_mouseControlled);
		addPropertySpec(_instantUpdate);
		addPropertySpec(_showFigureTemporarily);
		addPropertySpec(_holdFigure);
		addPropertySpec(_clearFigure);
		addPropertySpec(_antialiasing);
		addPropertySpec(_timeRound);
		addPropertySpec(_figureAlpha);
		addPropertySpec(_colorFigure);
		addPropertySpec(_colorInnerGear);
		addPropertySpec(_colorOuterGear);
		addPropertySpec(_colorMouseGuide);
		addPropertySpec(_colorCanvas);
		addPropertySpec(_outerRadius);
		addPropertySpec(_innerRadius);
		addPropertySpec(_lambda);
		addPropertySpec(_started);
		addPropertySpec(_pause);

		addPropertySpec(_docModel);
	}

	private void setDefaults() {
		_animated = new BooleanProperty(ANIMATED, Boolean.FALSE, this);
		_mouseControlled = new BooleanProperty(MOUSE_CONTROLLED, Boolean.FALSE, this);
		_instantUpdate = new BooleanProperty(INSTANT_UPDATE, Boolean.TRUE,this);

		_showFigure = new BooleanProperty(SHOW_FIGURE, Boolean.TRUE, this);
		_showGears = new BooleanProperty(SHOW_GEARS, Boolean.TRUE, this);
		_showPicture = new BooleanProperty(SHOW_PICTURE, Boolean.FALSE, this);
		_showFigureTemporarily = new BooleanProperty(SHOW_FIGURE_TEMPORARILY, Boolean.FALSE, this);
		_autoFill = new BooleanProperty(AUTO_FILL, Boolean.FALSE, this);
		_holdFigure = new BooleanProperty(HOLD_FIGURE, Boolean.FALSE, this);
		_clearFigure = new BooleanActionProperty(CLEAR_FIGURE, Boolean.FALSE, this);
		_fillFigure = new BooleanActionProperty(FILL_FIGURE, Boolean.FALSE, this);
		_antialiasing = new BooleanProperty(ANTIALIASING, Boolean.TRUE, this);
		_timeRound = new IntegerProperty(TIME_ROUND, new Integer(2000), this);
		_figureAlpha = new DoubleProperty(FIGURE_ALPHA, new Double(1.0), this);
		_colorFigure = new ColorProperty(COLOR_FIGURE, Color.decode("0x339933"), this); //$NON-NLS-1$
		_colorOuterGear = new ColorProperty(COLOR_OUTER_GEAR, Color.decode("0x339933"), this); //$NON-NLS-1$
		_colorInnerGear = new ColorProperty(COLOR_INNER_GEAR, Color.decode("0x0066cc"), this); //$NON-NLS-1$
		_colorMouseGuide = new ColorProperty(COLOR_MOUSE_GUIDE, Color.decode("0xffc0c0"), this);  //$NON-NLS-1$
		_colorCanvas = new ColorProperty(COLOR_CANVAS, Color.WHITE, this);  //$NON-NLS-1$

		_outerRadius = new IntegerProperty(OUTER_RADIUS, new Integer(60), this);
		_innerRadius = new IntegerProperty(INNER_RADIUS, new Integer(35), this);
		_lambda = new DoubleProperty(LAMBDA, new Double(0.85), this);

		_started = new BooleanProperty(STARTED, Boolean.FALSE, this);
		_pause = new BooleanProperty(PAUSE, Boolean.FALSE, this);
		_docModel = new GenericProperty<DocModel>(DOC_MODEL, null, this);
	}


	/**
	 * @param parameters
	 */
	public void setParameters(Map<String,String> parameters) {
		for (String key : parameters.keySet()) {
			String value = parameters.get(key);
			IProperty<?> prop = propertyHash.get(key);
			try {
				prop.parseValue(value);
			} catch (Throwable t) {
				// ignore
			}
		}
	}
}
