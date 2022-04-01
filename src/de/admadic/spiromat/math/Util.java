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
 * Provides some utility functions currently focused on the least common
 * multiple. 
 * 
 * @author Rainer Schwarze
 */
public class Util {
	/**
	 * @param a0
	 * @param b0
	 * @return	Returns the least common multiple
	 */
	public static int lcm(int a0, int b0) {
		if (a0==0 || b0==0) return 0;
		if (a0==1 || b0==1) return a0*b0;

		int a = a0;
		int b = b0;
		int t;
		while (b!=0) {
			t = b;
			b = a % b;
			a = t;
		}
		int lcm = a0*b0 / a;
		return lcm;
	}

	/**
	 * @param rBig
	 * @param rSmall
	 * @return	Returns the number of rounds for the given gear radii.
	 */
	public static int calculateRounds(int rBig, int rSmall) {
		return lcm(rBig, rSmall) / rBig;
	}
}
