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

import static org.junit.Assert.*;

import org.junit.Test;

import de.admadic.spiromat.math.Util;

/**
 * @author Rainer Schwarze
 *
 */
public class UtilTest {

	/**
	 * Test method for {@link de.admadic.spiromat.math.Util#lcm(int, int)}.
	 */
	@Test
	public void testLcm() {
		assertEquals("test item 0", 0, Util.lcm(0, 1));
		assertEquals("test item 1", 0, Util.lcm(0, 0));
		assertEquals("test item 2", 0, Util.lcm(0, 0));
		assertEquals("test item 3", 1, Util.lcm(1, 1));
		assertEquals("test item 4", 2, Util.lcm(2, 1));
		assertEquals("test item 5", 2, Util.lcm(1, 2));
		assertEquals("test item 6", 10, Util.lcm(2, 5));
		assertEquals("test item 7", 10, Util.lcm(5, 2));
		assertEquals("test item 8", 20, Util.lcm(10, 4));
		assertEquals("test item 9", 20, Util.lcm(4, 10));
		assertEquals("test item 9", 20, Util.lcm(5, 4));
	}

}
