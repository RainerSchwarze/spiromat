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

import java.io.File;

import org.apache.log4j.PropertyConfigurator;

import de.admadic.spiromat.log.ILogger;

/**
 * Provides a project level encapsulation of logging facilities. This logger is 
 * using log4j as a backend.
 * 
 * @author Rainer Schwarze
 */
public class Log4jLogger implements ILogger {
	private Class loggerClass;
	private org.apache.log4j.Logger loggerDelegate;

	/**
	 * Initializes the Logger facilities.
	 */
	public static void initialize() {
		String cfg = "../log4j.properties";
		if (!(new File("../log4j.properties").exists())) {
			if ((new File("./log4j.properties").exists())) {
				cfg = "./log4j.properties";
			}
		}
			
		PropertyConfigurator.configure(cfg);
	}

	/**
	 * Returns a Logger instance for the specified class.
	 * 
	 * @param loggerClass
	 * @return	The Logger instance created for the given class.
	 */
	public static Log4jLogger getLogger(Class loggerClass) {
		return new Log4jLogger(loggerClass);
	}
	
	protected Log4jLogger(Class loggerClass) {
		super();
		this.loggerClass = loggerClass;
		this.loggerDelegate = org.apache.log4j.Logger.getLogger(this.loggerClass);
	}

	/**
	 * @param message
	 * @param t
	 * @see de.admadic.spiromat.log.ILogger#debug(java.lang.Object, java.lang.Throwable)
	 */
	public void debug(Object message, Throwable t) {
		loggerDelegate.debug(message, t);
	}

	/**
	 * @param message
	 * @see de.admadic.spiromat.log.ILogger#debug(java.lang.Object)
	 */
	public void debug(Object message) {
		loggerDelegate.debug(message);
	}

	/**
	 * @param message
	 * @param t
	 * @see de.admadic.spiromat.log.ILogger#error(java.lang.Object, java.lang.Throwable)
	 */
	public void error(Object message, Throwable t) {
		loggerDelegate.error(message, t);
	}

	/**
	 * @param message
	 * @see de.admadic.spiromat.log.ILogger#error(java.lang.Object)
	 */
	public void error(Object message) {
		loggerDelegate.error(message);
	}

	/**
	 * @param message
	 * @param t
	 * @see de.admadic.spiromat.log.ILogger#fatal(java.lang.Object, java.lang.Throwable)
	 */
	public void fatal(Object message, Throwable t) {
		loggerDelegate.fatal(message, t);
	}

	/**
	 * @param message
	 * @see de.admadic.spiromat.log.ILogger#fatal(java.lang.Object)
	 */
	public void fatal(Object message) {
		loggerDelegate.fatal(message);
	}

	/**
	 * @param message
	 * @param t
	 * @see de.admadic.spiromat.log.ILogger#info(java.lang.Object, java.lang.Throwable)
	 */
	public void info(Object message, Throwable t) {
		loggerDelegate.info(message, t);
	}

	/**
	 * @param message
	 * @see de.admadic.spiromat.log.ILogger#info(java.lang.Object)
	 */
	public void info(Object message) {
		loggerDelegate.info(message);
	}

	/**
	 * @return	Returns true, if the debug level is enabled.
	 * @see de.admadic.spiromat.log.ILogger#isDebugEnabled()
	 */
	public boolean isDebugEnabled() {
		return loggerDelegate.isDebugEnabled();
	}

	/**
	 * @return	Returns true, if the info level is enabled.
	 * @see de.admadic.spiromat.log.ILogger#isInfoEnabled()
	 */
	public boolean isInfoEnabled() {
		return loggerDelegate.isInfoEnabled();
	}

	/**
	 * @return	Returns true, if the trace level is enabled.
	 * @see de.admadic.spiromat.log.ILogger#isTraceEnabled()
	 */
	public boolean isTraceEnabled() {
		return loggerDelegate.isTraceEnabled();
	}

	/**
	 * @param message
	 * @param t
	 * @see de.admadic.spiromat.log.ILogger#trace(java.lang.Object, java.lang.Throwable)
	 */
	public void trace(Object message, Throwable t) {
		loggerDelegate.trace(message, t);
	}

	/**
	 * @param message
	 * @see de.admadic.spiromat.log.ILogger#trace(java.lang.Object)
	 */
	public void trace(Object message) {
		loggerDelegate.trace(message);
	}

	/**
	 * @param message
	 * @param t
	 * @see de.admadic.spiromat.log.ILogger#warn(java.lang.Object, java.lang.Throwable)
	 */
	public void warn(Object message, Throwable t) {
		loggerDelegate.warn(message, t);
	}

	/**
	 * @param message
	 * @see de.admadic.spiromat.log.ILogger#warn(java.lang.Object)
	 */
	public void warn(Object message) {
		loggerDelegate.warn(message);
	}
}
