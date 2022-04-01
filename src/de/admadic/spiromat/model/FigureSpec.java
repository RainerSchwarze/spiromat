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
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import de.admadic.spiromat.DontObfuscate;
import de.admadic.spiromat.Globals;
import de.admadic.spiromat.math.Util;

/**
 * Provides a figure specification for the trochoid curves.
 * Almost all fields are suported with associated property change handling, 
 * except the active field which is merely an optimization for visualizing.
 * 
 * Note that this class only contains the specification. The actual curve 
 * data (list of points) is covered in another class.
 * 
 * The individual fields have the following purpose:
 * <ul>
 * <li>offsetAngle: an angle by which the whole figure is rotated (0..2pi)</li>
 * <li>startAngle: the start angle of the interval which is already drawn/calculated</li>
 * <li>endAngle: the end angle of the interval which is already drawn/calculated</li>
 * <li>cursorAngle: the angular cursor position - this is the location where the inner gear attaches to</li>
 * </ul>
 * 
 * @author Rainer Schwarze
 */
public class FigureSpec implements Serializable, DontObfuscate {
	/** */
	private static final long serialVersionUID = 1L;

	// these are primary parameters and are always needed
	private int outerRadius;
	private int innerRadius;
	private double penHolePos;	// sometimes named "lambda"

	private double maxAngleInterval;	// only depends on the radii

	// these parameters may have a default value (they need to be set in the
	// constructor based on the primary parameters)
	private double offsetAngle;
	private double startAngle;
	private double endAngle;
	private double cursorAngle;
	private Color color;

	
	private boolean active;

	transient private FigureModel cachedFigureModel;
	
	transient private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	/** property name for outer radius */
	public final static String PROP_OUTER_RADIUS = "outerRadius"; //$NON-NLS-1$
	/** property name for inner radius */
	public final static String PROP_INNER_RADIUS = "innerRadius"; //$NON-NLS-1$
	/** property name for pen hole pos */
	public final static String PROP_PEN_HOLE_POS = "penHolePos"; //$NON-NLS-1$
	/** property name for offset angle */
	public final static String PROP_OFFSET_ANGLE = "offsetAngle"; //$NON-NLS-1$
	/** property name for start angle */
	public final static String PROP_START_ANGLE = "startAngle"; //$NON-NLS-1$
	/** property name for end angle */
	public final static String PROP_END_ANGLE = "endAngle"; //$NON-NLS-1$
	/** property name for cursor angle */
	public final static String PROP_CURSOR_ANGLE = "cursorAngle"; //$NON-NLS-1$
	/** property name for color */
	public final static String PROP_COLOR = "color"; //$NON-NLS-1$
	/** property name for clear event */
	public final static String PROP_CLEARED = "cleared"; //$NON-NLS-1$

	/**
	 * Creates a FigureSpec with its angle interval set to [0,0].
	 * To set the interval to the full figures ranges, call 
	 * {@link #initFullInterval}.
	 * 
	 * @param outerRadius
	 * @param innerRadius
	 * @param penHolePos
	 * @param color TODO
	 */
	public FigureSpec(int outerRadius, int innerRadius, double penHolePos, Color color) {
		super();
		this.outerRadius = outerRadius;
		this.innerRadius = innerRadius;
		this.penHolePos = penHolePos;
		updateMaxAngleInterval();

		// default settings:
		this.offsetAngle = 0.0;
		this.startAngle = 0.0;
		this.endAngle = 0.0;
		this.cursorAngle = 0.0;
		this.color = color;
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		pcs = new PropertyChangeSupport(this);
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
	}

	/**
	 * Note: this method should be used by IO modules only.
	 * 
	 * @param startAngle
	 * @param endAngle
	 * @param cursorAngle
	 */
	public void setAngles(double startAngle, double endAngle, double cursorAngle) {
		this.startAngle = startAngle;
		this.endAngle = endAngle;
		updateCachedFigureModel(this.startAngle);
		updateCachedFigureModel(this.endAngle);
		setCursorAngle(cursorAngle);
	}

	/**
	 * Initializes the angle interval of this FigureSpec to the full range.
	 * The interval is set to [0,max] and the cursor is set to the end of the
	 * interval.
	 */
	public void initFullInterval() {
		startAngle = 0.0;
		endAngle = maxAngleInterval;
		cursorAngle = endAngle;
		updateCachedFigureModel(this.cursorAngle);
		pcs.firePropertyChange(PROP_CURSOR_ANGLE, 
				-1 - this.cursorAngle,	// a dummy old value 
				this.cursorAngle);
	}

	/**
	 * Clears the figure - among other things the interval is set to [0,0].
	 */
	public void clear() {
		this.offsetAngle = 0.0;
		this.startAngle = 0.0;
		this.endAngle = 0.0;
		this.cursorAngle = 0.0;
		clearCachedFigureModel();
		pcs.firePropertyChange(PROP_CLEARED, false, true);
	}

	private void updateMaxAngleInterval() {
		int rounds = Util.calculateRounds(outerRadius, innerRadius);
		maxAngleInterval = rounds * 2*Math.PI;
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

	/**
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(Color color) {
		Color oldValue = this.color;
		this.color = color;
		pcs.firePropertyChange(PROP_COLOR, oldValue, this.color);
	}

	/**
	 * @return the innerRadius
	 */
	public int getInnerRadius() {
		return innerRadius;
	}

	/**
	 * @return the outerRadius
	 */
	public int getOuterRadius() {
		return outerRadius;
	}

	/**
	 * @return the penHolePos
	 */
	public double getPenHolePos() {
		return penHolePos;
	}

	/**
	 * @return the endAngle
	 */
	public double getEndAngle() {
		return endAngle;
	}

	/**
	 * @return the offsetAngle
	 */
	public double getOffsetAngle() {
		return offsetAngle;
	}

	/**
	 * @return the cursorAngle
	 */
	public double getCursorAngle() {
		return cursorAngle;
	}

	/**
	 * @return the startAngle
	 */
	public double getStartAngle() {
		return startAngle;
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @param innerRadius the innerRadius to set
	 */
	public void setInnerRadius(int innerRadius) {
		int oldValue = this.innerRadius;
		this.innerRadius = innerRadius;
		updateMaxAngleInterval();
		updateCachedFigureModel(this.outerRadius, this.innerRadius, this.penHolePos);
		pcs.firePropertyChange(PROP_INNER_RADIUS, oldValue, this.innerRadius);
	}

	/**
	 * @param offsetAngle the offsetAngle to set
	 */
	public void setOffsetAngle(double offsetAngle) {
		double oldValue = this.offsetAngle;
		this.offsetAngle = offsetAngle;
		pcs.firePropertyChange(PROP_OFFSET_ANGLE, oldValue, this.offsetAngle);
	}

	/**
	 * @param cursorAngle the cursorAngle to set
	 */
	public void setCursorAngle(double cursorAngle) {
		double oldValue = this.cursorAngle;
		this.cursorAngle = cursorAngle;
		if (this.cursorAngle<this.startAngle) {
			setStartAngle(this.cursorAngle);
		} else if (this.cursorAngle>this.endAngle) {
			setEndAngle(this.cursorAngle);
		} else {
			// inside old interval
		}
		updateCachedFigureModel(this.cursorAngle);
		pcs.firePropertyChange(PROP_CURSOR_ANGLE, oldValue, this.cursorAngle);
	}

	/**
	 * @param outerRadius the outerRadius to set
	 */
	public void setOuterRadius(int outerRadius) {
		int oldValue = this.outerRadius;
		this.outerRadius = outerRadius;
		updateMaxAngleInterval();
		updateCachedFigureModel(this.outerRadius, this.innerRadius, this.penHolePos);
		pcs.firePropertyChange(PROP_OUTER_RADIUS, oldValue, this.outerRadius);
	}

	/**
	 * @param penHolePos the penHolePos to set
	 */
	public void setPenHolePos(double penHolePos) {
		double oldValue = this.penHolePos;
		this.penHolePos = penHolePos;
		updateCachedFigureModel(this.outerRadius, this.innerRadius, this.penHolePos);
		pcs.firePropertyChange(PROP_PEN_HOLE_POS, oldValue, this.penHolePos);
	}

	/**
	 * @param startAngle the startAngle to set
	 */
	private void setStartAngle(double startAngle) {
		double oldValue = this.startAngle;
		this.startAngle = startAngle;
		pcs.firePropertyChange(PROP_START_ANGLE, oldValue, this.startAngle);
	}

	/**
	 * @param endAngle the endAngle to set
	 */
	private void setEndAngle(double endAngle) {
		double oldValue = this.endAngle;
		this.endAngle = endAngle;
		pcs.firePropertyChange(PROP_END_ANGLE, oldValue, this.endAngle);
	}

	/**
	 * If the FigureSpec instance once has created a FigureModel instance
	 * it is never replaced. FigureView now keeps a reference to the
	 * FigureModel which is acceptable. However, if the interface changes, 
	 * a FigureModel needs to be retrieved from FigureSpec everytime one
	 * is needed/used.
	 * 
	 * @return the (cached) FigureModel or creates one
	 */
	public FigureModel getFigureModel() {
		if (cachedFigureModel==null) {
			cachedFigureModel = createFigureModel(this);
		}
		return cachedFigureModel;
	}

	/**
	 * @param outerRadiusArg
	 * @param innerRadiusArg
	 * @param penHolePosArg
	 */
	private void updateCachedFigureModel(
			int outerRadiusArg, int innerRadiusArg, double penHolePosArg) {
		if (cachedFigureModel==null) return;

		cachedFigureModel.setParameters(
				outerRadiusArg * Globals.MAX_RADIUS / 100, 
				innerRadiusArg * Globals.MAX_RADIUS / 100, 
				penHolePosArg, 
				0.0);
	}

	/**
	 * @param cursorAngleArg
	 */
	private void updateCachedFigureModel(double cursorAngleArg) {
		if (cachedFigureModel==null) return;

		cachedFigureModel.addPoints(cursorAngleArg);
	}

	/**
	 * 
	 */
	private void clearCachedFigureModel() {
		if (cachedFigureModel==null) return;

		cachedFigureModel.clear();
	}

	/**
	 * @param figureSpec
	 * @return	Returns the FigureModel created from the given FigureSpec.
	 */
	private static FigureModel createFigureModel(FigureSpec figureSpec) {
		FigureModel fm = new FigureModel(
				figureSpec.getOuterRadius() * Globals.MAX_RADIUS / 100,
				figureSpec.getInnerRadius() * Globals.MAX_RADIUS / 100,
				figureSpec.getPenHolePos());
		fm.addPoints(figureSpec.getStartAngle());
		fm.addPoints(figureSpec.getEndAngle());
		return fm;
	}


}
