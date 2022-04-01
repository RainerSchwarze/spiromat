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
package de.admadic.spiromat.shapes;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import de.admadic.spiromat.log.Logger;
import de.admadic.spiromat.model.AppModel;
import de.admadic.spiromat.model.FigureModel;
import de.admadic.spiromat.model.FigureSpec;

/**
 * 
 * @author Rainer Schwarze
 */
public class FigureView implements Drawable, PropertyChangeListener {
	final static Logger logger = Logger.getLogger(FigureView.class);

	FigureSpec figureSpec;
	FigureModel model;
	
	private int [] x = new int[0];
	private int [] y = new int[0];
	int count = 0;

	private boolean visible = true;
	private boolean ignoreActiveStatus = false;

	final private Stroke stroke = new BasicStroke(3.0f);
	final private Stroke strokeActive = new BasicStroke(5.0f);
	
	/**
	 * @param figureSpec 
	 */
	public FigureView(FigureSpec figureSpec) {
		super();
		this.figureSpec = figureSpec;
		this.model = this.figureSpec.getFigureModel();
		this.figureSpec.addPropertyChangeListener(this);
		ensureCapacity(11);

		updatePointsFromModel();
	}

	/**
	 * @param capacity
	 */
	private void ensureCapacity(int capacity) {
		if (x.length<capacity) {
			int [] tmpx = new int[capacity]; 
			int [] tmpy = new int[capacity]; 
			System.arraycopy(x, 0, tmpx, 0, x.length);
			System.arraycopy(y, 0, tmpy, 0, y.length);

			x = tmpx;
			y = tmpy;
		}
	}

	/**
	 * @param p
	 */
	public void addPoint(Point p) {
		addPoint(p.x, p.y);
	}

	/**
	 * @param xpos 
	 * @param ypos 
	 */
	public void addPoint(int xpos, int ypos) {
		if (count>=x.length) {
			ensureCapacity(2*count+1);
		}
		synchronized (this) {
			x[count] = xpos;
			y[count] = ypos;
			count++;
		}
	}

	/**
	 * @param pointsX
	 * @param pointsY
	 */
	public void setPoints(int[] pointsX, int[] pointsY) {
		x = pointsX;
		y = pointsY;

		count = x.length;
	}

	/**
	 * 
	 */
	public void clear() {
		synchronized (this) {
			logger.debug("clearing"); //$NON-NLS-1$
			count = 0;
		}
	}

	/**
	 * @see de.admadic.spiromat.shapes.Drawable#drawReset()
	 */
	public void drawReset() {
		logger.debug("resetting"); //$NON-NLS-1$
		model.clearDrawn();
	}

	/**
	 * @param g
	 * @see de.admadic.spiromat.shapes.Drawable#drawFull(java.awt.Graphics2D)
	 */
	public void drawFull(Graphics2D g) {
		logger.debug("draw: " + //$NON-NLS-1$
				" cur=" + count); //$NON-NLS-1$

		g.setStroke(stroke);
		g.setColor(figureSpec.getColor());
		g.drawRect(-100, -100, 200, 200);
	}

	/**
	 * @param g
	 * @see de.admadic.spiromat.shapes.Drawable#drawErase(java.awt.Graphics2D)
	 */
	public void drawErase(Graphics2D g) {
		/* nothing */
	}

	/**
	 * @param g
	 * @see de.admadic.spiromat.shapes.Drawable#drawStayingParts(java.awt.Graphics2D)
	 */
	public void drawStayingParts(Graphics2D g) {
		logger.debug("drawStaying: " + //$NON-NLS-1$
				" v=" + this.isVisible() + //$NON-NLS-1$
				" Ro=" + figureSpec.getOuterRadius() +  //$NON-NLS-1$
				" Ri=" + figureSpec.getInnerRadius() +  //$NON-NLS-1$
				" L=" + figureSpec.getPenHolePos()); //$NON-NLS-1$

		if (!isVisible()) return;
		
		int [] tmpxs;
		int [] tmpys;
		int [] tmpxe;
		int [] tmpye;
		synchronized (model) {
			tmpxs = model.getDirtyAtStartPointsX();
			tmpys = model.getDirtyAtStartPointsY();
			tmpxe = model.getDirtyAtEndPointsX();
			tmpye = model.getDirtyAtEndPointsY();
			model.markDrawn();
		}
		Graphics2D gb = g;
		try {
			gb.setPaintMode();
			Stroke s = null;
			if (isIgnoreActiveStatus() || AppModel.getInstance().getShowPicture()) {
				s = stroke;
			} else {
				s = figureSpec.isActive() ? strokeActive : stroke;
			}
			gb.setStroke(s);
			gb.setColor(figureSpec.getColor());

			logger.debug("drawing: start=" + tmpxs.length + " end=" + tmpxe.length); //$NON-NLS-1$ //$NON-NLS-2$
			gb.drawPolyline(tmpxs, tmpys, tmpxs.length);
			gb.drawPolyline(tmpxe, tmpye, tmpxe.length);
		} finally {
			// gb.dispose();
		}
	}

	/**
	 * @param g
	 * @see de.admadic.spiromat.shapes.Drawable#drawVolatileParts(java.awt.Graphics2D)
	 */
	public void drawVolatileParts(Graphics2D g) {
		/* everything with figures is staying - so we don't draw volatile parts */
		logger.debug("drawVolatile: " + //$NON-NLS-1$
				" v=" + this.isVisible() + //$NON-NLS-1$
				" Ro=" + figureSpec.getOuterRadius() +  //$NON-NLS-1$
				" Ri=" + figureSpec.getInnerRadius() +  //$NON-NLS-1$
				" L=" + figureSpec.getPenHolePos()); //$NON-NLS-1$
		if (!isVisible()) return;
	}

	/**
	 * 
	 */
	void updatePointsFromModel() {
		synchronized (model) {
			model.clearDrawn();
			this.setPoints(
					model.getPointsX(), 
					model.getPointsY());
		}
	}

	/**
	 * @param evt
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		String propName = evt.getPropertyName();
		if (propName!=null) {
			logger.debug("propchange: " + propName); //$NON-NLS-1$
			if (propName.equals(FigureSpec.PROP_CLEARED)) {
//				setParameters(
//						figureSpec.getOuterRadius(), 
//						figureSpec.getInnerRadius(), 
//						figureSpec.getPenHolePos());
				this.clear();
			} else if (
					propName.equals(FigureSpec.PROP_OUTER_RADIUS) ||
					propName.equals(FigureSpec.PROP_INNER_RADIUS) ||
					propName.equals(FigureSpec.PROP_PEN_HOLE_POS)
					) {
				this.clear();	 // redundant...
				this.updatePointsFromModel();
			} else if (propName.equals(FigureSpec.PROP_CURSOR_ANGLE)) {
				// FIXME: is the cursor angle handling ok here? (especially with the locking?)
				// ? if (model.addPoints(phi)) {
//				this.updatePointsFromModel();
			}
		}
	}


	/**
	 * @return the visible
	 */
	public boolean isVisible() {
		return visible;
	}


	/**
	 * @param visible the visible to set
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}


	/**
	 * @return the ignoreActiveStatus
	 */
	public boolean isIgnoreActiveStatus() {
		return ignoreActiveStatus;
	}


	/**
	 * @param ignoreActiveStatus the ignoreActiveStatus to set
	 */
	public void setIgnoreActiveStatus(boolean ignoreActiveStatus) {
		this.ignoreActiveStatus = ignoreActiveStatus;
	}
}
