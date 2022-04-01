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
package de.admadic.util;

/**
 * @author Rainer Schwarze
 *
 * @param <T1>
 * @param <T2>
 */
public class Pair<T1,T2> {
	T1 e1;
	T2 e2;

	/**
	 * @param e1
	 * @param e2
	 */
	public Pair(T1 e1, T2 e2) {
		super();
		this.e1 = e1;
		this.e2 = e2;
	}

	/**
	 * @param e1
	 */
	public void setFirst(T1 e1) {
		this.e1 = e1;
	}

	/**
	 * @param e2
	 */
	public void setSecond(T2 e2) {
		this.e2 = e2;
	}

	/**
	 * @return	Returns the first element of the pair.
	 */
	public T1 getFirst() {
		return e1;
	}

	/**
	 * @return	Returns the second element of the pair.
	 */
	public T2 getSecond() {
		return e2;
	}
}