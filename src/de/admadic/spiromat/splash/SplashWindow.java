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
package de.admadic.spiromat.splash;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import de.admadic.spiromat.ui.Util;

/**
 * @author Rainer Schwarze
 *
 */
public class SplashWindow extends JFrame {
	/** */
	private static final long serialVersionUID = 1L;

	static private SplashWindow instance;

	ImageIcon splashImage = null;
	JLabel splashLabel = null;
	ImagePanel splashPanel = null;
	Timer timer;
	TimerTask timerTask;

	/**
	 * @return	Returns the single instance of the SplashWindow.
	 */
	public static SplashWindow getInstance() {
		if (instance==null) {
			instance = new SplashWindow();
		}
		return instance;
	}

	/**
	 * 
	 */
	private SplashWindow() {
		super();
		this.setIconImages(Util.loadLogoImages());
		splashImage = Util.loadImage("spiromat-splash3.png"); //$NON-NLS-1$
		this.setContentPane(splashPanel = new ImagePanel(splashImage));
		splashPanel.setLayout(null);
		this.getContentPane().add(splashLabel = new JLabel("")); //$NON-NLS-1$
		splashLabel.setBounds(5, 255, 250, 40);
		// splashLabel.setBorder(BorderFactory.createLineBorder(Color.RED));
		splashLabel.setFont(
				splashLabel.getFont()
				.deriveFont(Font.PLAIN)
				.deriveFont(10.0f));
		splashLabel.setForeground(Color.decode("0x0066cc")); //$NON-NLS-1$
		// splashLabel.setOpaque(true);
		this.setUndecorated(true);
		this.setSize(400, 300);
		this.validate();
		this.setLocationRelativeTo(null);
		timer = new Timer("splash-timer", true); //$NON-NLS-1$
		timerTask = new TimerTask() {
			@Override
			public void run() {
				timerRing();
			}
		};
	}

	/**
	 * 
	 */
	protected void timerRing() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SplashWindow.this.setVisible(false);
				SplashWindow.this.dispose();
			}
		});
	}

	/**
	 * Shows the splash with a hide-timeout of 10 sec.
	 */
	public void showSplash() {
		timer.schedule(timerTask, 10000);
		this.setAlwaysOnTop(true);
		this.setVisible(true);
		this.paintAll(this.getGraphics());
	}
	
	/**
	 * Hides the splash if it is still visible.
	 */
	public void hideSplash() {
		if (!this.isVisible()) return;

		timerTask.cancel();
		this.setVisible(false);
		this.dispose();
	}

	/**
	 * @param text
	 */
	public void setMessage(String text) {
		splashLabel.setText(text);
		splashLabel.paintAll(splashLabel.getGraphics());
	}

	static class ImagePanel extends JPanel {
		/** */
		private static final long serialVersionUID = 1L;
		ImageIcon image;

		/**
		 * @param image
		 */
		public ImagePanel(ImageIcon image) {
			super();
			this.setBackground(Color.WHITE);
			this.image = image;
		}

		/**
		 * @param g
		 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
		 */
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (image!=null) {
				g.drawImage(image.getImage(), 0, 0, this);
			}
		}

		
	}
}
