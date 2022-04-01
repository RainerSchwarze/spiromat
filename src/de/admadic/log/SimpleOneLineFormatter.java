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
package de.admadic.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * @author Rainer Schwarze
 *
 */
public class SimpleOneLineFormatter extends Formatter {
	Date dat = new Date();
	private final static String format = "{0,date} {0,time}";
	private MessageFormat formatter;
	
	private Object args[] = new Object[1];
	
	// Line separator string.  This is the value of the line.separator
	// property at the moment that the SimpleFormatter was created.
	private String lineSeparator = "\n";

	/**
	 * 
	 */
	public SimpleOneLineFormatter() {
		super();
		if (formatter == null) {
			formatter = new MessageFormat(format);
		}
//		lineSeparator = (String)java.security.AccessController.doPrivileged(
//				new sun.security.action.GetPropertyAction("line.separator"));
		lineSeparator = System.lineSeparator();
	} 

	/**
	 * Format the given LogRecord.
	 * Date Time LEVEL: message (details)
	 * @param record the log record to be formatted.
	 * @return a formatted log record
	 */
	@Override
	public synchronized String format(LogRecord record) {
		StringBuffer sb = new StringBuffer();
		dat.setTime(record.getMillis());
		args[0] = dat;
		StringBuffer text = new StringBuffer();
		formatter.format(args, text, null);
		sb.append(text);
		sb.append(": ");

		String message = formatMessage(record);
		sb.append(record.getLevel().getName()); // not localized
		sb.append(": ");
		sb.append(message);

		sb.append(" (");
		if (record.getSourceClassName() != null) {	
			sb.append(" c=");
			sb.append(record.getSourceClassName());
		} else {
			sb.append(" l=");
			sb.append(record.getLoggerName());
		}
		if (record.getSourceMethodName() != null) {	
			sb.append(" m=");
			sb.append(record.getSourceMethodName());
		}
		if (record.getThrown() != null) {
			try {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				record.getThrown().printStackTrace(pw);
				pw.close();
				sb.append(sw.toString());
			} catch (Exception e) {
				// nothing
			}
		}
		sb.append(")");
		sb.append(lineSeparator);
		return sb.toString();
	} 
}
