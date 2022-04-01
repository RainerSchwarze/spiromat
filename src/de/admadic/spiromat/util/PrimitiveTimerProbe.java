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

import de.admadic.spiromat.log.Logger;

/**
 * @author Rainer Schwarze
 *
 */
public class PrimitiveTimerProbe {
	Logger logger;

	long startTime;

	/** use ns units */
	public final static int UNIT_NS = 0;
	/** use ms units */
	public final static int UNIT_MS = 1;
	/** use us units */
	public final static int UNIT_US = 2;

	String [] units = {"ns", "ms", "us"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	double [] factors = { 1.0, 1e6, 1e3 };

	/**
	 * @param logger 
	 * 
	 */
	public PrimitiveTimerProbe(Logger logger) {
		super();
		this.logger = logger;
		startTime = System.nanoTime();
	}

	/**
	 * @param msg
	 * @param timeUnit
	 */
	public void probe(String msg, int timeUnit) {
		long delta = System.nanoTime() - startTime;
		if (logger!=null && logger.isDebugEnabled()) 
			logger.debug("probe[" + units[timeUnit] + "] for [" + msg + "] = " + delta/factors[timeUnit]); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * @param msg
	 * @param timeUnit
	 */
	public void probeAndReset(String msg, int timeUnit) {
		long delta = System.nanoTime() - startTime;
		if (logger!=null && logger.isDebugEnabled()) 
			logger.debug("probe[" + units[timeUnit] + "] for [" + msg + "] = " + delta/factors[timeUnit]); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		startTime += delta;
	}
}
