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
package de.admadic.spiromat.math;

/**
 * Provides calculations for spiromats.
 * 
 * @author Rainer Schwarze
 */
public class SpiroMath {
	// parameters: 
	final private double rBig;		// radius outer gear
	final private double rSmall;	// radius inner gear (with pen hole)
	final private double lambda;	// position of pen hole

	// results:
	private double smallGearCenterX;	// position of inner gear's center
	private double smallGearCenterY;
	private double smallGearDirection;	// direction of inner gear
	private double figureX;			// point for spiromat figure according...
	private double figureY;			// ...to given angle of inner gear's center.

	/**
	 * Creates an instance of SpiroMath with the given parameters.
	 * 
	 * @param big	A double representing the radius of the outer gear.
	 * @param small A double representing the radius of the inner gear.
	 * @param lambda	A double representing the position of the pen hole.
	 */
	public SpiroMath(double big, double small, double lambda) {
		super();
		this.rBig = big;
		this.rSmall = small;
		this.lambda = lambda;
	}

	/**
	 * Performs calculations for the given angle.
	 * 
	 * @param phi
	 */
	public void calculate(double phi) {
		double phi2 = -phi*rBig/rSmall;	// the angle of the inner gear
		double x, y;
		double xc, yc;

		// first calculate the center location of the inner gear:
		xc = (rBig - rSmall)*Math.cos(phi);
		yc = (rBig - rSmall)*Math.sin(phi);
		// the pen hole position on base of center location:
		x = xc + lambda*rSmall*Math.cos(phi+phi2);
		y = yc + lambda*rSmall*Math.sin(phi+phi2);

		// now set the output fields for external querying:
		smallGearCenterX = xc;
		smallGearCenterY = yc;
		smallGearDirection = phi + phi2;
		figureX = x;
		figureY = y;
	}

	/**
	 * @return the figureX
	 */
	public double getFigureX() {
		return figureX;
	}

	/**
	 * @return the figureY
	 */
	public double getFigureY() {
		return figureY;
	}

	/**
	 * @return the smallGearCenterX
	 */
	public double getSmallGearCenterX() {
		return smallGearCenterX;
	}

	/**
	 * @return the smallGearCenterY
	 */
	public double getSmallGearCenterY() {
		return smallGearCenterY;
	}

	/**
	 * @return the smallGearDirection
	 */
	public double getSmallGearDirection() {
		return smallGearDirection;
	}
}
