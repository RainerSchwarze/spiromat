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

import de.admadic.spiromat.util.Knob;
import junit.framework.TestCase;

/**
 * @author Rainer Schwarze
 *
 */
public class KnobTest extends TestCase {

	/**
	 * Test method for {@link de.admadic.spiromat.util.Knob#getOpenAngle(double, double)}.
	 */
	public void testGetOpenAngle() {
		final double pi = Math.PI;
		do_testGetOpenAngle(0, pi/2, pi/2);
		do_testGetOpenAngle(0, -3*pi/2, pi/2);

		do_testGetOpenAngle(0, -pi/2, -pi/2);
		do_testGetOpenAngle(0, 3*pi/2, -pi/2);

		do_testGetOpenAngle(pi/2, 2*pi/2, pi/2);
		do_testGetOpenAngle(-3*pi/2, 2*pi/2, pi/2);
		do_testGetOpenAngle(-3*pi/2, -2*pi/2, pi/2);
		do_testGetOpenAngle(pi/2, -2*pi/2, pi/2);

		do_testGetOpenAngle(pi/4, 3*pi/4, pi/2);
		do_testGetOpenAngle(pi/4, -5*pi/4, pi/2);
		do_testGetOpenAngle(-7*pi/4, -5*pi/4, pi/2);
		do_testGetOpenAngle(-7*pi/4, 3*pi/4, pi/2);

		do_testGetOpenAngle(3*pi/4, 1*pi/4, -pi/2);
		do_testGetOpenAngle(pi/4, -pi/4, -pi/2);
		do_testGetOpenAngle(-pi/4, +pi/4, +pi/2);
		do_testGetOpenAngle(pi/4, 7*pi/4, -pi/2);
	}

	/**
	 * Test method for {@link de.admadic.spiromat.util.Knob#updateKnobFromMousePos(int, int)}.
	 */
	public void testNotifyMousePos() {
		final double pi = Math.PI;
		final double pi2 = pi/2;
		final double pi4 = pi/4;
		Knob knob; 
		knob = new Knob();
		assertEquals(0.0, knob.getAngle(), 1e-6);
		assertEquals(0.0, knob.updateKnobFromMousePos(0, 0), 1e-6);	// no change
		assertEquals(pi4, knob.updateKnobFromMousePos(20, 20), 1e-6);
		assertEquals(-pi4, knob.updateKnobFromMousePos(20, -20), 1e-6);
		assertEquals(-pi2, knob.updateKnobFromMousePos(0, -20), 1e-6);
		assertEquals(-pi4, knob.updateKnobFromMousePos(20, -20), 1e-6);
		assertEquals(pi4, knob.updateKnobFromMousePos(20, 20), 1e-6);
		assertEquals(3*pi4, knob.updateKnobFromMousePos(-20, 20), 1e-6);
		assertEquals(5*pi4, knob.updateKnobFromMousePos(-20, -20), 1e-6);
		assertEquals(7*pi4, knob.updateKnobFromMousePos(20, -20), 1e-6);
		assertEquals(9*pi4, knob.updateKnobFromMousePos(20, 20), 1e-6);
		assertEquals(11*pi4, knob.updateKnobFromMousePos(-20, 20), 1e-6);
		assertEquals(13*pi4, knob.updateKnobFromMousePos(-20, -20), 1e-6);
		assertEquals(15*pi4, knob.updateKnobFromMousePos(20, -20), 1e-6);
		assertEquals(17*pi4, knob.updateKnobFromMousePos(20, 20), 1e-6);

		knob = new Knob();
		assertEquals(0.0, knob.getAngle(), 1e-6);
		assertEquals(0.0, knob.updateKnobFromMousePos(0, 0), 1e-6);	// no change
		assertEquals(-pi4, knob.updateKnobFromMousePos(20, -20), 1e-6);
		assertEquals(-3*pi4, knob.updateKnobFromMousePos(-20, -20), 1e-6);
		assertEquals(-5*pi4, knob.updateKnobFromMousePos(-20, 20), 1e-6);
		assertEquals(-7*pi4, knob.updateKnobFromMousePos(20, 20), 1e-6);
		assertEquals(-9*pi4, knob.updateKnobFromMousePos(20, -20), 1e-6);
		assertEquals(-11*pi4, knob.updateKnobFromMousePos(-20, -20), 1e-6);
		assertEquals(-13*pi4, knob.updateKnobFromMousePos(-20, 20), 1e-6);
		assertEquals(-15*pi4, knob.updateKnobFromMousePos(20, 20), 1e-6);
		assertEquals(-17*pi4, knob.updateKnobFromMousePos(20, -20), 1e-6);
	}
	
	/**
	 * @param a
	 * @param b
	 * @param oa
	 */
	private void do_testGetOpenAngle(double a, double b, double oa) {
		double tr = Knob.getOpenAngle(a, b);
		assertEquals(oa, tr, 1e-6);
	}

}
