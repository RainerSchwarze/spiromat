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
package de.admadic.spiromat.log;

/**
 * Provides a null logger which is activated if nothing else is specified.
 * A NullLogger ignores all logging data and all calls return immediately.
 * The isXxxEnabled functions return false.
 * 
 * @author Rainer Schwarze
 */
public class NullLogger implements ILogger {
	// we do not need full blown documentation of methods...
	
	/**
	 * Initializes the Logger facilities.
	 */
	public static void initialize() {
		// nothing
	}

	/**
	 * Returns a Logger instance for the specified class.
	 * 
	 * @param loggerClass
	 * @return	The Logger instance created for the given class.
	 */
	public static NullLogger getLogger(Class loggerClass) {
		return new NullLogger(loggerClass);
	}
	
	protected NullLogger(Class loggerClass) {
		super();
		// this.loggerClass = loggerClass;
	}

	/**
	 * @param message
	 * @param t
	 * @see de.admadic.spiromat.log.ILogger#debug(java.lang.Object, java.lang.Throwable)
	 */
	public void debug(Object message, Throwable t) {
		/* nothing */
	}

	/**
	 * @param message
	 * @see de.admadic.spiromat.log.ILogger#debug(java.lang.Object)
	 */
	public void debug(Object message) {
		/* nothing */
	}

	/**
	 * @param message
	 * @param t
	 * @see de.admadic.spiromat.log.ILogger#error(java.lang.Object, java.lang.Throwable)
	 */
	public void error(Object message, Throwable t) {
		/* nothing */
	}

	/**
	 * @param message
	 * @see de.admadic.spiromat.log.ILogger#error(java.lang.Object)
	 */
	public void error(Object message) {
		/* nothing */
	}

	/**
	 * @param message
	 * @param t
	 * @see de.admadic.spiromat.log.ILogger#fatal(java.lang.Object, java.lang.Throwable)
	 */
	public void fatal(Object message, Throwable t) {
		/* nothing */
	}

	/**
	 * @param message
	 * @see de.admadic.spiromat.log.ILogger#fatal(java.lang.Object)
	 */
	public void fatal(Object message) {
		/* nothing */
	}

	/**
	 * @param message
	 * @param t
	 * @see de.admadic.spiromat.log.ILogger#info(java.lang.Object, java.lang.Throwable)
	 */
	public void info(Object message, Throwable t) {
		/* nothing */
	}

	/**
	 * @param message
	 * @see de.admadic.spiromat.log.ILogger#info(java.lang.Object)
	 */
	public void info(Object message) {
		/* nothing */
	}

	/**
	 * @return	Returns true, if the debug level is enabled.
	 * @see de.admadic.spiromat.log.ILogger#isDebugEnabled()
	 */
	public boolean isDebugEnabled() {
		return false;
	}

	/**
	 * @return	Returns true, if the info level is enabled.
	 * @see de.admadic.spiromat.log.ILogger#isInfoEnabled()
	 */
	public boolean isInfoEnabled() {
		return false;
	}

	/**
	 * @return	Returns true, if the trace level is enabled.
	 * @see de.admadic.spiromat.log.ILogger#isTraceEnabled()
	 */
	public boolean isTraceEnabled() {
		return false;
	}

	/**
	 * @param message
	 * @param t
	 * @see de.admadic.spiromat.log.ILogger#trace(java.lang.Object, java.lang.Throwable)
	 */
	public void trace(Object message, Throwable t) {
		/* nothing */
	}

	/**
	 * @param message
	 * @see de.admadic.spiromat.log.ILogger#trace(java.lang.Object)
	 */
	public void trace(Object message) {
		/* nothing */
	}

	/**
	 * @param message
	 * @param t
	 * @see de.admadic.spiromat.log.ILogger#warn(java.lang.Object, java.lang.Throwable)
	 */
	public void warn(Object message, Throwable t) {
		/* nothing */
	}

	/**
	 * @param message
	 * @see de.admadic.spiromat.log.ILogger#warn(java.lang.Object)
	 */
	public void warn(Object message) {
		/* nothing */
	}
}
