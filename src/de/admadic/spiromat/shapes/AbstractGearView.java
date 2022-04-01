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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import de.admadic.spiromat.Globals;
import de.admadic.spiromat.log.Logger;
import de.admadic.spiromat.model.AbstractGearSpec;

/**
 * Provides drawing of a gear.
 * 
 * @author Rainer Schwarze
 *
 */
abstract public class AbstractGearView implements Drawable, PropertyChangeListener {
	final static Logger logger = Logger.getLogger(AbstractGearView.class);

	AbstractGearSpec gearSpec;
	
	protected int teeth;			// number of teeth
	protected int teethHeight;	// height of teeth (min<->max)

	int [] x;
	int [] y;

	double angle;
	double xc;
	double yc;

	boolean visible = true;
	final static float strokeWidth = 2.0f;

	
	/**
	 * @param gearSpec 
	 */
	public AbstractGearView(AbstractGearSpec gearSpec) {
		super();
		this.gearSpec = gearSpec;
		gearSpec.addPropertyChangeListener(this);
		updateParameters();
	}

	/**
	 * @param evt
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		String propName = evt.getPropertyName();
		if (propName!=null) {
			if (propName.equals(AbstractGearSpec.PROP_RADIUS)) {
				updateParameters();
			}
		}
	}

	/**
	 * 
	 */
	void updateParameters() {
		double radius = gearSpec.getRadius() * Globals.MAX_RADIUS / 100.0;
		this.teeth = gearSpec.getRadius();
		this.teethHeight = (int) (radius * Math.PI * 2 / (this.teeth*1.5 ));
		initData();
	}
	
	/**
	 * 
	 */
	abstract protected void initData();

	/**
	 * @param angle 
	 * @param xc
	 * @param yc
	 */
	public void setState(double angle, double xc, double yc) {
		logger.debug("setState: " + " angle=" + angle); //$NON-NLS-1$ //$NON-NLS-2$
		synchronized (this) {
			this.angle = angle;
			this.xc = xc;
			this.yc = yc;
		}
	}

	/**
	 * @param g
	 */
	public void drawImpl(Graphics2D g) {
		Graphics2D g2 = (Graphics2D) g.create();
		try {
			AffineTransform aftr = null;
	
			g2.setPaintMode();
			g2.setColor(gearSpec.getColor());
			g2.setPaint(gearSpec.getColor());
	
			g2.setStroke(new BasicStroke(2.0f));
	
			aftr = g2.getTransform();
	    	if (aftr==null) {
	    		aftr = AffineTransform.getTranslateInstance(0, 0);
	    	}
			aftr.concatenate(AffineTransform.getTranslateInstance(xc, yc));
			aftr.concatenate(AffineTransform.getRotateInstance(angle));
			g2.setTransform(aftr);

			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
	
			drawShape(g2);
		} finally {
			g2.dispose();
		}
	}

	/**
	 * @param g
	 */
	protected void drawShape(Graphics2D g) {
		if (isVisible()) 
			g.fillPolygon(x, y, x.length);
	}

	/**
	 * @return the teeth
	 */
	int getTeeth() {
		return teeth;
	}

	/**
	 * @return the teethHeight
	 */
	int getTeethHeight() {
		return teethHeight;
	}

	/**
	 * 
	 * @see de.admadic.spiromat.shapes.Drawable#drawReset()
	 */
	public void drawReset() {
		logger.debug("resetting: " + this); //$NON-NLS-1$
		// nothing?
	}

	/**
	 * @param g
	 * @see de.admadic.spiromat.shapes.Drawable#drawFull(java.awt.Graphics2D)
	 */
	public void drawFull(Graphics2D g) {
		logger.debug("draw: " + this); //$NON-NLS-1$
		drawImpl(g);
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
	 * @param g
	 * @see de.admadic.spiromat.shapes.Drawable#drawStayingParts(java.awt.Graphics2D)
	 */
	public void drawStayingParts(Graphics2D g) {
		/* for now there are no staying parts - don't draw anything */
		logger.debug("drawStaying: " + //$NON-NLS-1$
				" R=" + gearSpec.getRadius()); //$NON-NLS-1$
	}

	/**
	 * @param g
	 * @see de.admadic.spiromat.shapes.Drawable#drawVolatileParts(java.awt.Graphics2D)
	 */
	public void drawVolatileParts(Graphics2D g) {
		logger.debug("drawVolatile: " + //$NON-NLS-1$
				" R=" + gearSpec.getRadius()); //$NON-NLS-1$
		drawImpl(g);
	}
}
