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
package de.admadic.spiromat.ui;

import de.admadic.spiromat.log.Logger;

/**
 * @author Rainer Schwarze
 *
 */
public class Messenger {
	final Logger logger = Logger.getLogger(Messenger.class);

	private static Messenger messenger = null;

	/**
	 * @return	Returns the Messenger instance (singleton).
	 */
	public static Messenger getInstance() {
		if (messenger==null) {
			messenger = new Messenger();
		}
		return messenger;
	}

	MessageLabel messageLabel;
	
	private Messenger() {
		super();
	}

	/**
	 * @param label
	 */
	public void registerMessageLabel(MessageLabel label) {
		messageLabel = label;
	}

	/**
	 * @param text
	 */
	public void warning(String text) {
		if (messageLabel==null) return;
		logger.debug("(warning:) " + text); //$NON-NLS-1$
		messageLabel.warning(text);
	}

	/**
	 * @param text
	 */
	public void info(String text) {
		if (messageLabel==null) return;
		logger.debug("(info:) " + text); //$NON-NLS-1$
		messageLabel.info(text);
	}
}
