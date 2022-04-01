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

import de.admadic.spiromat.SpiromatException;

/**
 * Provides a timing monitor which measures times between invocations
 * of start and stop calls. Use it like this:
 * 
 * Initialization:
 * </code>TimingMonitor tm = new TimingMonitor();</code>
 * ...
 * 
 * Timing loop:
 * <code>tm.start();</code>
 * ...
 * <code>tm.stop();</code>
 * 
 * Dump statistics:
 * <code>tm.dump();</code>
 * 
 * The output will include time betwen repeated invocations of start(),
 * time between start() and stop() and the ratio (called load). The load 
 * will indicate how much time the watched code takes relative to the
 * total time in the loop (time between invocation of start()). A load 
 * value close to zero means, nearly no load, a value close to 1 means,
 * the loop takes up all CPU time.
 * 
 * The time will be measured in ns.
 * 
 * The call sequence must consist of repeated calls to start() and stop() 
 * in that order. If the order is wrong, a RuntimeException is thrown.
 * 
 * @author Rainer Schwarze
 */
public class TimeMonitor {
	String name;
	
	// statistics
	long startIntervalSum;
	long startIntervalMin;
	long startIntervalMax;
	long startIntervalCount;

	long runDurationSum;
	long runDurationMin;
	long runDurationMax;
	long runDurationCount;

	// calculation
	long lastStart;

	// status
	final static int STATUS_NONE = 0;
	final static int STATUS_START = 1;
	final static int STATUS_STOP = 2;
	int lastStatus = STATUS_NONE;

	boolean autoReset = true;
	
	/**
	 * Creates an instance of the TimingMonitor and initializes all 
	 * fields.
	 * @param name 
	 */
	public TimeMonitor(String name) {
		super();
		this.name = name;
		reset();
	}

	/**
	 * Creates an instance of the TimingMonitor and initializes all 
	 * fields.
	 * @param name 
	 * @param autoReset 
	 */
	public TimeMonitor(String name, boolean autoReset) {
		super();
		this.autoReset = autoReset;
		this.name = name;
		reset();
	}

	/**
	 * 
	 */
	public void reset() {
		startIntervalSum = 0;
		startIntervalMin = 0;
		startIntervalMax = 0;
		startIntervalCount = 0;

		runDurationSum = 0;
		runDurationMin = 0;
		runDurationMax = 0;
		runDurationCount = 0;

		lastStatus = STATUS_NONE;
	}

	/**
	 * Marks a start event for the statistics calculation.
	 */
	public void start() {
		if (lastStatus==STATUS_NONE) {
			lastStart = System.nanoTime();
			lastStatus = STATUS_START;
			return;
		}
		if (lastStatus!=STATUS_STOP) {
			throw new SpiromatException("invalid call sequence (no stop before start)"); //$NON-NLS-1$
		}
		lastStatus = STATUS_START;

		long delta = System.nanoTime() - lastStart;
		startIntervalSum += delta;
		if (delta>startIntervalMax) startIntervalMax = delta;
		if (delta<startIntervalMin || startIntervalMin==0) startIntervalMin = delta;
		startIntervalCount++;

		lastStart = System.nanoTime();
	}

	/**
	 * Marks a stop event for the statistics calculations.
	 */
	public void stop() {
		if (lastStatus==STATUS_NONE) {
			throw new RuntimeException("stop called without calling start first"); //$NON-NLS-1$
		}
		if (lastStatus!=STATUS_START) {
			throw new RuntimeException("invalid call sequence (no start before stop)"); //$NON-NLS-1$
		}
		lastStatus = STATUS_STOP;

		long delta = System.nanoTime() - lastStart;
		runDurationSum += delta;
		if (delta>runDurationMax) runDurationMax = delta;
		if (delta<runDurationMin || runDurationMin==0) runDurationMin = delta;
		runDurationCount++;

		if ((runDurationCount % 50)==0) {
			dump();
			if (autoReset) reset();
		}
	}

	/**
	 * Right now, dump is wired to do nothing.
	 */
	public void dump() {
		/* nothing */
	}

	/**
	 * @return	Returns the start average
	 */
	public double getStartAvg() {
		return startIntervalSum * 1.0 / startIntervalCount;
	}

	/**
	 * @return	Returns the run average
	 */
	public double getRunAvg() {
		return runDurationSum * 1.0 / runDurationCount;
	}
	
	/**
	 * Dumps the statistics to System.out.
	 */
	public void dumpLoad() {
		if (startIntervalCount==0 || runDurationCount==0) {
			System.out.println("not enough data"); //$NON-NLS-1$
			return;
		}
		double startAvg = startIntervalSum * 1.0 / startIntervalCount;
		double runAvg = runDurationSum * 1.0 / runDurationCount;
		double load = runAvg / startAvg;

		double startJitter = (startIntervalMax - startIntervalMin) / startAvg;
		double runJitter = (runDurationMax - runDurationMin) / startAvg;

		startJitter = Math.round(startJitter * 1000.0) / 1000.0;
		runJitter = Math.round(runJitter * 1000.0) / 1000.0;
		load = Math.round(load * 1000.0) / 1000.0;
		
//		System.out.println(
//				"timing stats: [" + name + "]\n" +
//				"\tstart=" + startAvg + " / " + startIntervalMin + " / " + startIntervalMax + " c=" + startIntervalCount + "\n" + 
//				"\trun=" + runAvg + " / " + runDurationMin + " / " + runDurationMax + " c=" + runDurationCount + "\n" +
//				"\tload=" + load);
		System.out.println(
				"timing stats [" + name + "]: " +  //$NON-NLS-1$ //$NON-NLS-2$
				"load=" + load + " (sj=" + startJitter + " rj=" + runJitter + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}

	/**
	 * Dumps the statistics to System.out.
	 */
	public void dumpRunTime() {
		if (startIntervalCount==0 || runDurationCount==0) {
			System.out.println("not enough data"); //$NON-NLS-1$
			return;
		}
		double startAvg = startIntervalSum * 1.0 / startIntervalCount;
		double runAvg = runDurationSum * 1.0 / runDurationCount;
		double load = runAvg / startAvg;
		double runDurMax = runDurationMax;
		double runDurMin = runDurationMin;

		double startJitter = (startIntervalMax - startIntervalMin) / startAvg;
		double runJitter = (runDurationMax - runDurationMin) / startAvg;

		startJitter = Math.round(startJitter * 1000.0) / 1000.0;
		runJitter = Math.round(runJitter * 1000.0) / 1000.0;
		load = Math.round(load * 1000.0) / 1000.0;

		runAvg /= 1e6; // ms
		runDurMax /= 1e6; // ms
		runDurMin /= 1e6; // ms
		
		System.out.println(
				"timing stats [ms]: [" + name + "] " + //$NON-NLS-1$ //$NON-NLS-2$
				" run=" + runAvg + " / " + runDurMin + " / " + runDurMax + " c=" + runDurationCount); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}
}
