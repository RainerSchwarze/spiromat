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
package de.admadic.spiromat.util;

/**
 * Provides a knob feature which allows to push a sequence of mouse positions
 * into the knob instance and retrieve the curAngle by which the knob has been 
 * turned.
 * 
 * @author Rainer Schwarze
 */
public class Knob {
	/*
	 * implmentation note:
	 * (in drawings o = old, * = new)
	 * 
	 * different cases:
	 * 
	 * 1) no overlap, increase:
	 * 
	 *      * | o
	 *       \|/
	 * -------+--------
	 *        |
	 *        |
	 * The new curAngle is slightly larger than the old one.
	 * 
	 * 2) no overlap, decrease:
	 * 
	 *      o | *
	 *       \|/
	 * -------+--------
	 *        |
	 *        |
	 * The new curAngle is slightly smaller than the old one.
	 * 
	 * 3) overlap, increase:
	 * 
	 *        | *
	 *        |/
	 * -------+--------
	 *        |\
	 *        | o
	 *
	 * 4) overlap, decrease:
	 * 
	 *        | o
	 *        |/
	 * -------+--------
	 *        |\
	 *        | *
	 * 
	 */

	// the angle includes potential overlaps (multiples of 2*PI)
	private double angle = 0.0;

	// the direction is similar to angle, only having no overlaps
	// (values within 0...2*PI)
	private double direction = 0.0;

	// number of rounds gone so far
	private int rounds = 0;

	/**
	 * Creates an instance of the Knob.
	 */
	public Knob() {
		super();
	}

	/**
	 * Resets the status of the knob instance.
	 * This is identical to calling setAngle(0.0).
	 */
	public void reset() {
		setAngle(0.0);
	}
	
	/**
	 * Notifies the knob instance about a new mouse position.
	 * The knob instance will update its angle and direction accordingly
	 * (the rounds too, if overlaps occured).
	 * 
	 * @param x	An int specifying the mouse position relative to the knob center.
	 * @param y An int specifying the mouse position relative to the knob center.
	 * @return Returns the new angle of the knob (includes overlaps).
	 */
	public double updateKnobFromMousePos(int x, int y) {
		double tempAngle = Math.atan2(y, x);
		if (tempAngle<0) tempAngle += Math.PI*2;

		// openAngle is the angle between old knob angle and the "mouse angle"
		double openAngle = getOpenAngle(direction, tempAngle);
		if (openAngle==0.0) {
			// no change
			return angle;
		}
		// We just add the angle to the direction and correct the overflows 
		// afterwards. The overflow is kept in the field rounds. Finally
		// the angle is constructed by combining the rounds with the direction.
		direction += openAngle;
		if (openAngle>0.0) {
			// an increase
			if (direction>Math.PI*2) {
				direction -= Math.PI*2;
				rounds++;
			}
		} else {
			// a decrease
			if (direction<0) {
				direction += Math.PI*2;
				rounds--;
			}
		}
		angle = rounds*Math.PI*2 + direction;
		return angle;
	}

	/**
	 * @return the angle of the knob.
	 */
	public double getAngle() {
		return angle;
	}

	/**
	 * @param angle	The angle to set. 
	 */
	public void setAngle(double angle) {
		this.angle = angle;
		// now update direction and rounds:
		this.rounds = (int) Math.floor(angle / Math.PI / 2.0);
		this.direction = this.angle - (rounds * Math.PI * 2.0);
	}

	// /////////////////////////////////////////////////
	// protected helper functions

	/**
	 * Returns the smaller of the two angles between a and b.
	 * 
	 * In mathematically positive direction (ccw) the openAngle is positive, 
	 * otherwise negative.
	 * Say, a is PI and b is 0. Then the result is -PI.
	 * 
	 * <pre>
	 *          |
	 *          ^
	 *         (a)- . . . . .angle (a => b) = -PI
	 *          |   \        angle (b => a) = +PI
	 * ---------+---(b)=>--
	 *          |
	 * </pre>
	 * 
	 * @return	Returns the smaller of the two angles between a and b.
	 */
	protected static double getOpenAngle(double a, double b) {
		double o = b - a;
		if (o>Math.PI) {
			o -= Math.PI*2;
		} else if (o<-Math.PI) {
			o += Math.PI*2;
		}
		if (o==-Math.PI) {
			o = Math.PI;
		}
		return o;
	}
}
