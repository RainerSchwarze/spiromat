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

import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 * @author Rainer Schwarze
 *
 */
public class MessageLabel extends JLabel {
	/** */
	private static final long serialVersionUID = 1L;

	Color normalColor;
	Color warningColor;
	Color infoColor;

	String defaultText;

	private Timer timer;
	private TimerTask timerTask;
	Runnable runnable;
	
	/**
	 * @param defaultText 
	 */
	public MessageLabel(String defaultText) {
		super(defaultText);
		this.defaultText = defaultText;
		// this.setOpaque(true);
		normalColor = this.getForeground();
		warningColor = Color.RED;
		infoColor = Color.decode("0x008000"); //$NON-NLS-1$
		runnable = new Runnable() {
			public void run() {
				setText(MessageLabel.this.defaultText);
				setForeground(normalColor);
			}
		};
		timer = new Timer("message-timer", true); //$NON-NLS-1$
	}

	/**
	 * @param text
	 */
	public void warning(String text) {
		setText(text);
		setForeground(warningColor);

		if (timerTask!=null) timerTask.cancel();
		timer.purge();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				SwingUtilities.invokeLater(runnable);
			}
		};		
		timer.schedule(timerTask, 5000);
	}

	/**
	 * @param text
	 */
	public void info(String text) {
		setText(text);
		setForeground(infoColor);

		if (timerTask!=null) timerTask.cancel();
		timer.purge();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				SwingUtilities.invokeLater(runnable);
			}
		};		
		timer.schedule(timerTask, 5000);
	}
}
