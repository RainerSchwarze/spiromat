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
 * Provides a Logger interface compatible with log4j serving as a facade to
 * apache log4j. The purpose of working through this interface is to decouple 
 * completely from log4j jars.
 * 
 * @author Rainer Schwarze
 */
public interface ILogger {
	// we do not need full blown documentation of methods...

	/**
	 * @param message
	 * @param t
	 * @see org.apache.log4j.Category#debug(java.lang.Object, java.lang.Throwable)
	 */
	public abstract void debug(Object message, Throwable t);

	/**
	 * @param message
	 * @see org.apache.log4j.Category#debug(java.lang.Object)
	 */
	public abstract void debug(Object message);

	/**
	 * @param message
	 * @param t
	 * @see org.apache.log4j.Category#error(java.lang.Object, java.lang.Throwable)
	 */
	public abstract void error(Object message, Throwable t);

	/**
	 * @param message
	 * @see org.apache.log4j.Category#error(java.lang.Object)
	 */
	public abstract void error(Object message);

	/**
	 * @param message
	 * @param t
	 * @see org.apache.log4j.Category#fatal(java.lang.Object, java.lang.Throwable)
	 */
	public abstract void fatal(Object message, Throwable t);

	/**
	 * @param message
	 * @see org.apache.log4j.Category#fatal(java.lang.Object)
	 */
	public abstract void fatal(Object message);

	/**
	 * @param message
	 * @param t
	 * @see org.apache.log4j.Category#info(java.lang.Object, java.lang.Throwable)
	 */
	public abstract void info(Object message, Throwable t);

	/**
	 * @param message
	 * @see org.apache.log4j.Category#info(java.lang.Object)
	 */
	public abstract void info(Object message);

	/**
	 * @return	Returns true, if the debug level is enabled.
	 * @see org.apache.log4j.Category#isDebugEnabled()
	 */
	public abstract boolean isDebugEnabled();

	/**
	 * @return	Returns true, if the info level is enabled.
	 * @see org.apache.log4j.Category#isInfoEnabled()
	 */
	public abstract boolean isInfoEnabled();

	/**
	 * @return	Returns true, if the trace level is enabled.
	 * @see org.apache.log4j.Logger#isTraceEnabled()
	 */
	public abstract boolean isTraceEnabled();

	/**
	 * @param message
	 * @param t
	 * @see org.apache.log4j.Logger#trace(java.lang.Object, java.lang.Throwable)
	 */
	public abstract void trace(Object message, Throwable t);

	/**
	 * @param message
	 * @see org.apache.log4j.Logger#trace(java.lang.Object)
	 */
	public abstract void trace(Object message);

	/**
	 * @param message
	 * @param t
	 * @see org.apache.log4j.Category#warn(java.lang.Object, java.lang.Throwable)
	 */
	public abstract void warn(Object message, Throwable t);

	/**
	 * @param message
	 * @see org.apache.log4j.Category#warn(java.lang.Object)
	 */
	public abstract void warn(Object message);

}
