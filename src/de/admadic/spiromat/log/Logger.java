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

import java.lang.reflect.Method;
import java.security.AccessControlException;

/**
 * Provides a project level encapsulation of logging facilities.
 * 
 * The actual logging handler is set by defining 
 * <code>de.admadic.spiromat.log.loggerClass</code> as an option to the VM. If nothing
 * has been specified or the given logger causes errors during initialization,
 * the logger is set to an instance of <code>de.admadic.spiromat.log.NullLogger</code>.
 * 
 * To choose the log4j logging, specify the following option for the VM:
 * <code>-Dde.admadic.spiromat.log.loggerClass=de.admadic.spiromat.log.Log4jLogger</code> .
 * 
 * @author Rainer Schwarze
 */
public class Logger implements ILogger {
	// fields for identifying the "global" Logger class:
	private static String loggerClassName = null;
	private static Class<?> loggerClass = null;
	private static final String LOGGER_PROPNAME = "de.admadic.spiromat.log.loggerClass"; //$NON-NLS-1$
	private static final String NULL_LOGGER_CLASSNAME = "de.admadic.spiromat.log.NullLogger"; //$NON-NLS-1$

	// instance fields:
	private Class<?> loggedClass = null;
	private ILogger loggerDelegate = null;

	
	/**
	 * Initializes the Logger facilities.
	 */
	public static void initialize() {
		try {
			loggerClassName = System.getProperty(LOGGER_PROPNAME, NULL_LOGGER_CLASSNAME);
			loggerClass = Class.forName(loggerClassName);
		} catch (AccessControlException e) {
			loggerClass = NullLogger.class;
		} catch (ClassNotFoundException e) {
			loggerClass = NullLogger.class;
		}

		try {
			Method m = loggerClass.getMethod("initialize", new Class[]{}); //$NON-NLS-1$
			m.invoke(null, new Object[]{});
		} catch (Throwable e) {
			// no initialization - we fall back to nulllogger
			loggerClassName = NULL_LOGGER_CLASSNAME;
			loggerClass = NullLogger.class;
		}
	}

	/**
	 * Returns a Logger instance for the specified class.
	 * 
	 * @param loggedClass
	 * @return	The Logger instance created for the given class.
	 */
	public static Logger getLogger(Class loggedClass) {
		return new Logger(loggedClass);
	}

	/**
	 * Constructs an instance and creates a delegate logger instance on base 
	 * of the registered logger class.
	 * 
	 * @param loggedClass
	 */
	protected Logger(Class<?> loggedClass) {
		super();
		this.loggedClass = loggedClass;
		try {
			Method m;
			m = loggerClass.getMethod("getLogger", new Class[]{Class.class}); //$NON-NLS-1$
			// this.loggerDelegate = org.apache.log4j.Logger.getLogger(this.loggerClass);
			this.loggerDelegate = (ILogger) m.invoke(null, new Object[]{this.loggedClass});
		} catch (Throwable e) {
			// in case of any error, revert to the NullLogger
			this.loggerDelegate = NullLogger.getLogger(loggedClass);
		}
		if (this.loggerDelegate==null) {
			// if for any reason, no instance has been set, use the NullLogger
			this.loggerDelegate = NullLogger.getLogger(loggedClass);
		}
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
	 * @return	Returns true, if debug level is enabled.
	 * @see de.admadic.spiromat.log.ILogger#isDebugEnabled()
	 */
	public boolean isDebugEnabled() {
		return loggerDelegate.isDebugEnabled();
	}

	/**
	 * @return	Returns true, if info level is enabled.
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
