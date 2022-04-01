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

import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToggleButton;

import de.admadic.spiromat.DontObfuscate;

/**
 * @author Rainer Schwarze
 */
public class Util implements DontObfuscate {
	/**
	 * @param name
	 * @return	Returns the image for the given name.
	 */
	static public ImageIcon loadImage(String name) {
		return new ImageIcon(Toolkit.getDefaultToolkit().createImage(
				Util.class.getResource("res0/" + name))); //$NON-NLS-1$
	}

	/**
	 * @param name
	 * @return	Returns the image for the given name.
	 */
	static public ImageIcon loadButtonImage(String name) {
		return new ImageIcon(Toolkit.getDefaultToolkit().createImage(
				Util.class.getResource("res1/" + name))); //$NON-NLS-1$
	}

	/**
	 * @return	Returns the array of the logo images.
	 */
	static public List<? extends Image> loadLogoImages() {
		ArrayList<Image> images = new ArrayList<Image>();
		images.add(loadImage("logo-6-16.png").getImage()); //$NON-NLS-1$
		images.add(loadImage("logo-6-24.png").getImage()); //$NON-NLS-1$
		images.add(loadImage("logo-6-32.png").getImage()); //$NON-NLS-1$
		images.add(loadImage("logo-6-48.png").getImage()); //$NON-NLS-1$
		return images;
	}
	
	/**
	 * @param action
	 * @return	Returns the JButton created for the action.
	 */
	static public JButton createJButton(Action action) {
		JButton btn = new JButton(action);
		if (action != null
				&& (action.getValue(Action.SMALL_ICON) != null || action
						.getValue(Action.LARGE_ICON_KEY) != null)) {
			btn.setHideActionText(true);
		}
		btn.setHorizontalTextPosition(JButton.CENTER);
		btn.setVerticalTextPosition(JButton.BOTTOM);
		Insets insets = btn.getMargin();
		insets.left = insets.right = insets.top;
		btn.setMargin(insets);
		return btn;
	}

	/**
	 * @param action
	 * @return	Returns the JToggleButton created for the action.
	 */
	static public JToggleButton createJToggleButton(Action action) {
		JToggleButton btn = new JToggleButton(action);
		if (action != null
				&& (action.getValue(Action.SMALL_ICON) != null || action
						.getValue(Action.LARGE_ICON_KEY) != null)) {
			btn.setHideActionText(true);
		}
		btn.setHorizontalTextPosition(JButton.CENTER);
		btn.setVerticalTextPosition(JButton.BOTTOM);
		Insets insets = btn.getMargin();
		insets.left = insets.right = insets.top;
		btn.setMargin(insets);
		return btn;
	}
}
