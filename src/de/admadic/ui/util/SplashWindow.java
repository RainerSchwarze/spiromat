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
package de.admadic.ui.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

/**
 * 
 * @author Rainer Schwarze
 */
public class SplashWindow extends JWindow implements ISplashWindow {
	/*
	 * Features to support:
	 * 
	 * <pre>
	 * | close | timeout | click | progress | wait100% |
	 * +-------+---------+-------+----------+----------+-----------------
	 * |   x   |         |       |          |          | program close
	 * |       |    x    |       |          |          | timeout after 10 sec
	 * |       |         |   x   |          |          | user must click
	 * |       |         |       |    x     |          | wait until app is 100%
	 * |       |         |       |          |   (x)    | (needs progress!)
	 * |       |         |   x   |    x     |    x     | click, but wait until 100% 
	 * |       |    x    |   x   |    x     |    x     | close with timeout, or click,
	 * |       |         |       |          |          | but wait until 100%
	 * </pre>
	 * 
	 * So we have the following sets:
	 * - simple program close
	 * - simple timeout close
	 * - simple user close
	 * - progress info
	 * - timeout + wait for 100%
	 * - user + wait for 100%
	 */

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected final static boolean DBGforce = false;

	JLayeredPane layeredPane;
	JPanel lowerPanel;
	JPanel upperPanel;
	JLabel pictureLabel;

	JProgressBar progress;
	JLabel labelTitle;
	JTextArea textDetails;

	// defaults all false:
	boolean useTimeout;
	boolean useUserclick; // mouse + space?
	boolean useProgress;
	boolean waitForCompletion;

	boolean stopAllThreads;
	
	/*
	 * user click:
	 */
	MouseAdapter mouseClickListener;
	
	/*
	 * update progress fields:
	 */
	String lastMessage;
	int lastPercent;

	/*
	 * thread safe operations:
	 */
	Runnable waitRunner;
	Runnable closeRunner;
	Runnable updateRunner;

	/*
	 * timeout fields:
	 */
	int splashDelay;
	Thread delayThread; 

	/**
	 * @param picture The splash image - should always be there
	 * @param initialMessage A message to show in progress for 0%
	 * @param title	A title for the splash
	 * @param details	Detail information like "registered to..." 
	 * @param detailsPos	Where to put the details
	 */ 
	public SplashWindow(
			final Icon picture,
			final String initialMessage, 
			final String title,
			final String details,
			final int detailsPos) {
		super();
		if (DBGforce) System.out.println("SplashWindow: <init>");
		if (initialMessage!=null) {
			useProgress = true;
		}
		initRunners();
		initVisuals(picture, initialMessage, title, details, detailsPos);
	}

	/**
	 * Sets the waitForCompletion flag.
	 * This flag is used in conjunction with progress handling.
	 * If the progress is not at 100% and the user clicks in the window, or
	 * the timeout has elapsed, the window is not yet closed.
	 * With the user click, the click is ignored.
	 * With the delayed operation, the delay thread waits until the progress
	 * indicates completion and closes then.
	 *  
	 * @param flag
	 */
	public void setWaitForCompletion(boolean flag) {
		waitForCompletion = flag;
	}

	/**
	 * Activates or deactivates the user click close operation. 
	 * 
	 * @param flag
	 */
	public void setUserClose(boolean flag) {
		useUserclick = flag;
		// should we remove the old listener?
		if (!useUserclick && mouseClickListener!=null) {
			removeMouseListener(mouseClickListener);
			mouseClickListener = null;
		}
		// should we create a new one?
		if (useUserclick && mouseClickListener==null) {
			mouseClickListener = new MouseAdapter() {
				/**
				 * @param e
				 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
				 */
				@Override
				public void mousePressed(MouseEvent e) {
					if (DBGforce) System.out.println("SplashWindow: mouse pressed");
					if (delayThread==null) {
						if (waitForCompletion && useProgress) {
							if (lastPercent<100) {
								// ignore
								return;
							}
						}
						if (DBGforce) System.out.println("SplashWindow: mouse pressed: no thread, closing");
						setVisible(false);
						dispose();
					} else {
						if (DBGforce) System.out.println("SplashWindow: mouse pressed: has thread, interrupting");
						// that will lead the thread to stop the sleep:
						delayThread.interrupt();
						/*
						 * (there is an initial sleep for the delay time.
						 * when this sleep is interrupted, it is checked, whether
						 * the splash window can be closed. The handling of
						 * waitForCompletion is done there. When the initial
						 * sleep is over and the delayThread is in repeat sleep
						 * mode for waiting for completion, this interrupt
						 * causes the sleep loop to shorten and test for 
						 * completion.
						 * Summarizing: the primary goal of the user click
						 * is the shortening of the delay-sleep if we have 
						 * such a thread active.)
						 */
					} // (has a delay or not)
				} // (mousePressed)
			}; // (new MouseAdapter)
			addMouseListener(mouseClickListener);
		}
	}

	/**
	 * Turns on a delayed close of the splash window.
	 * If the delay amount is less than 1, the method returns
	 * immediately.
	 * 
	 * @param delay	The delay until the splash is closed automatically in ms.
	 */
	public void setDelayedClose(int delay) {
		if (delay<1) return;

		splashDelay = delay;

		waitRunner = new Runnable() {
			public void run() {
				if (DBGforce) System.out.println("SplashWindow: waitRunner hit");
				try { // (general exception catching)
					try { // (interrupt signal catching)
						if (DBGforce) System.out.println("SplashWindow: waitRunner start sleep");
						Thread.sleep(splashDelay);
					} catch (InterruptedException e) { /* nothing */ }
					if (DBGforce) System.out.println("SplashWindow: waitRunner woke up");
					if (waitForCompletion) {
						// FIXME: fix stop condition:
						while (lastPercent<100 && !stopAllThreads) {
							try {
								if (DBGforce) System.out.println("SplashWindow: waitRunner wait for complete start sleep");
								Thread.sleep(500);
							} catch (InterruptedException e) { /* nothing */ }
						}
					}
					try {
						if (DBGforce) System.out.println("SplashWindow: waitRunner invoking closeRunner");
						SwingUtilities.invokeAndWait(closeRunner);
					} catch (InterruptedException e) { /* nothing */ }
				} catch (Exception e) {
					// e.printStackTrace();
					if (DBGforce) System.out.println("SplashWindow: waitRunner caught bad exception");
				}
				delayThread = null;
			}
		};

		//setVisible (true);
		delayThread = new Thread(waitRunner, "SplashThread");
	}

	/**
	 * 
	 * @see de.admadic.ui.util.ISplashWindow#close()
	 */
	public void close() {
		if (isVisible()) {
			//SwingUtilities.invokeLater(new CloseSplashScreen());
			SwingUtilities.invokeLater(closeRunner);
		}
	}

	/**
	 * @param show
	 * @see java.awt.Component#setVisible(boolean)
	 */
	@Override
	public void setVisible(final boolean show) {
		if (show) {
			if (DBGforce) System.out.println("SplashWindow: setVisible(true)");
			//pack();
			//setLocationRelativeTo(null);
			if (delayThread!=null) {
				if (!delayThread.isAlive() && !stopAllThreads) {
					delayThread.start();
				}
			}
		}
		super.setVisible(show);
	}

	/**
	 * @param message
	 * @see de.admadic.ui.util.ISplashWindow#updateStatus(java.lang.String)
	 */
	public void updateStatus(final String message) {
		updateStatus(message, -1);
	}

	/**
	 * @param message
	 * @param percent
	 * @see de.admadic.ui.util.ISplashWindow#updateStatus(java.lang.String, int)
	 */
	public void updateStatus(final String message, final int percent) {
		lastMessage = message;
		if (percent>=0) 
			lastPercent = percent;
		if (useProgress && isVisible() && updateRunner!=null) {
			//SwingUtilities.invokeLater(new UpdateStatus(message, percent));
			SwingUtilities.invokeLater(updateRunner);
		}
	}

	/**
	 * 
	 * @see de.admadic.ui.util.ISplashWindow#notifyCompleted()
	 */
	public void notifyCompleted() {
		updateStatus(lastMessage, 100);
	}

	// //////////////////////////////////////////////////////////
	// Protected 
	// //////////////////////////////////////////////////////////

	/**
	 * Initialize the visual components
	 * @param picture 
	 * @param initialMessage 
	 * @param title 
	 * @param details 
	 * @param detailsPos 
	 */
	protected void initVisuals(
			final Icon picture, 
			final String initialMessage, 
			final String title,
			final String details,
			final int detailsPos) {
		if (DBGforce) System.out.println("SplashWindow: initContents");

		layeredPane = new JLayeredPane();
		getContentPane().setLayout(new BorderLayout());
	
		lowerPanel = new JPanel();
		Color bgColor = new Color(240, 240, 240);
		Color fgColor = new Color(0, 0, 0);

		lowerPanel.setBackground(bgColor);
		lowerPanel.setLayout(new BorderLayout());

		upperPanel = new JPanel();
		upperPanel.setLayout(null);
		upperPanel.setOpaque(false);

		pictureLabel = new JLabel(picture);
		lowerPanel.add(pictureLabel, BorderLayout.CENTER);

		if (title!=null) { 
			if (DBGforce) System.out.println("SplashWindow: initContents: adding title");
			labelTitle = new JLabel(title, SwingConstants.CENTER);
			labelTitle.setForeground(fgColor);
			lowerPanel.add(labelTitle, BorderLayout.NORTH);
		}

		if (useProgress) {
			if (DBGforce) System.out.println("SplashWindow: initContents: adding progress");
			progress = new JProgressBar(0, 100);
			progress.setForeground(fgColor);
			progress.setBorderPainted(false);
			progress.setBackground(bgColor);
			progress.setStringPainted(true);
			progress.setString(initialMessage);
			lowerPanel.add(progress, BorderLayout.SOUTH);
		}

		lowerPanel.setBorder(new LineBorder(fgColor, 1));

		upperPanel.setPreferredSize(lowerPanel.getPreferredSize());
		if (DBGforce) System.out.println("lowerPanel: prefSize = " + lowerPanel.getPreferredSize());
		if (DBGforce) System.out.println("upperPanel: prefSize = " + lowerPanel.getPreferredSize());
		layeredPane.setPreferredSize(lowerPanel.getPreferredSize());
		if (DBGforce) System.out.println("layeredPane: prefSsize = " + lowerPanel.getPreferredSize());

		if (details!=null) {
			if (DBGforce) System.out.println("SplashWindow: initContents: adding details");
			textDetails = new JTextArea();
			textDetails.setEditable(false);
			textDetails.setOpaque(false);
			textDetails.setText(details);
			textDetails.setMargin(new Insets(5, 5, 5, 5));
			//textDetails.setFont(textDetails.getFont().deriveFont(10.0f));
			textDetails.setFont(
					textDetails.getFont()
					.deriveFont(Font.PLAIN, 9.0f));
			if (detailsPos>0) {
				//if (DBGforce) System.out.println("layeredPane: prefSsize = " + lowerPanel.getPreferredSize());
				Dimension d = upperPanel.getPreferredSize();
				upperPanel.add(textDetails);
				textDetails.setBounds(
						0, detailsPos,
						d.width, d.height - detailsPos);
			} else {
				upperPanel.setLayout(new BorderLayout());
				upperPanel.add(textDetails, BorderLayout.SOUTH);
			}
		}

		//layeredPane.setLayout(new BorderLayout());
		layeredPane.add(lowerPanel, new Integer(1));
		layeredPane.add(upperPanel, new Integer(2));
		//getContentPane().add(lowerPanel, BorderLayout.CENTER);
		getContentPane().add(layeredPane, BorderLayout.CENTER);
		//setContentPane(layeredPane);
		//layeredPane.setOpaque(false);

		pack();

		// fixup the bounds?
		Dimension d = lowerPanel.getPreferredSize();
		Rectangle r = lowerPanel.getBounds();
		if (DBGforce) System.out.println("lowerPanel: bounds = " + r);
		if (r.width==0) {
			// fix it!
			r = new Rectangle(0, 0, d.width, d.height);
		}
		lowerPanel.setBounds(r);
		if (details!=null) {
			Rectangle ur = new Rectangle(r);
			ur.x = 0;
			if (labelTitle!=null) {
				ur.y += labelTitle.getPreferredSize().height;
				ur.height -= labelTitle.getPreferredSize().height;
			}
			if (progress!=null) {
				ur.height -= progress.getPreferredSize().height;
			}
			upperPanel.setBounds(ur);
		} else {
			upperPanel.setBounds(r);
		}
		//layeredPane.setB
		

		setLocationRelativeTo(null);
		this.setAlwaysOnTop(true);
	}

	/**
	 * Used to turn on the flag stopAllThreads which threads use to stop
	 * themself. The threads are interrupted, so that they can handle 
	 * the new situation immediately.
	 * 
	 * @param flag
	 */
	protected void setStopAllThreads(boolean flag) {
		if (!flag) return;

		stopAllThreads = true;
		if (delayThread!=null) {
			delayThread.interrupt();
		}
	}

	/**
	 * Initialize the Runnables.
	 * (actually the closeRunner and if necessary the updateRunner)
	 */
	protected void initRunners() {
		// we always need a close:
		closeRunner = new Runnable() {
			public void run() {
				if (DBGforce) System.out.println("SplashWindow: closeRunner hit");
				if (stopAllThreads) return;
				setStopAllThreads(true);
				setVisible(false);
				dispose();
			}
		};
		if (useProgress) {
			updateRunner = new Runnable() {
				public void run() {
					if (DBGforce) System.out.println("SplashWindow: updateRunner tick");
					if (progress!=null) {
						progress.setValue(lastPercent);
						progress.setString(lastMessage);
					}
				}
			};
		}
	}
}
