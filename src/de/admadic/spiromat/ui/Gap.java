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

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * Provides a gap component to be used for layouting.
 * 
 * @author Rainer Schwarze
 */
public class Gap extends JPanel {
	/** */
	private static final long serialVersionUID = 1L;

	private boolean noPaint = false;
	
	/**
	 * Creates an instance of the gap component.
	 * With the parameter <code>noPaint</code> the gap instance
	 * can be defined to ignore any paint calls.
	 * 
	 * Note that the Dimension used for preferred size is also set to be the
	 * minimum size. 
	 * 
	 * @param preferredSize A Dimension containing the desired preferred size.
	 * @param noPaint 
	 */
	public Gap(Dimension preferredSize, boolean noPaint) {
		super();
		this.noPaint = noPaint;
		setPreferredSize(preferredSize);
		setMinimumSize(preferredSize);
	}

	/**
	 * Creates an instance of the gap component.
	 * With the parameter <code>noPaint</code> the gap instance
	 * can be defined to ignore any paint calls.
	 * 
	 * Note that the width and height used for preferred size are also set to be the
	 * minimum size.
	 *  
	 * @param prefWidth 
	 * @param prefHeight 
	 * @param noPaint 
	 */
	public Gap(int prefWidth, int prefHeight, boolean noPaint) {
		super();
		this.noPaint = noPaint;
		setPreferredSize(new Dimension(prefWidth, prefHeight));
		setMinimumSize(new Dimension(prefWidth, prefHeight));
	}

	/**
	 * @param g
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		if (noPaint) return;
		super.paintComponent(g);
	}
}
