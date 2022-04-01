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

/**
 * @author Rainer Schwarze
 *
 */
public class InnerGearSpec extends AbstractGearSpec {
	double penHolePos;

	double angle;
	double centerX;
	double centerY;

	/** property name for position change */
	public final static String PROP_POSITION = "position"; //$NON-NLS-1$
	/** property name for pen hole pos */
	public final static String PROP_PEN_HOLE_POS = "penHolePos"; //$NON-NLS-1$

	/**
	 * @param penHolePos 
	 * @param radius
	 * @param color
	 */
	public InnerGearSpec(double penHolePos, int radius, Color color) {
		super(radius, color);
		this.penHolePos = penHolePos;
	}


	/**
	 * @param angle
	 * @param centerX
	 * @param centerY
	 */
	public void setPosition(double angle, double centerX, double centerY) {
		this.angle = angle;
		this.centerX = centerX;
		this.centerY = centerY;
		pcs.firePropertyChange(PROP_POSITION, null, this);
	}
	
	/**
	 * @return the angle
	 */
	public double getAngle() {
		return angle;
	}


	/**
	 * @return the centerX
	 */
	public double getCenterX() {
		return centerX;
	}


	/**
	 * @return the centerY
	 */
	public double getCenterY() {
		return centerY;
	}



	/**
	 * @return the penHolePos
	 */
	public double getPenHolePos() {
		return penHolePos;
	}

	/**
	 * @param penHolePos the penHolePos to set
	 */
	public void setPenHolePos(double penHolePos) {
		double oldValue = this.penHolePos;
		this.penHolePos = penHolePos;
		double newValue = this.penHolePos;
		pcs.firePropertyChange(PROP_PEN_HOLE_POS, oldValue, newValue);
	}



	/**
	 * @param fs
	 */
	public void updateFromFigureSpec(FigureSpec fs) {
		setRadius(fs.getInnerRadius());
		setPenHolePos(fs.getPenHolePos());
	}
}
