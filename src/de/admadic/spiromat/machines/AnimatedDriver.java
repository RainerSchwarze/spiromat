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
package de.admadic.spiromat.machines;

import javax.swing.SwingUtilities;

import de.admadic.spiromat.log.Logger;
import de.admadic.spiromat.math.Util;
import de.admadic.spiromat.model.AppModel;
import de.admadic.spiromat.model.FigureSpec;

/**
 * @author Rainer Schwarze
 *
 */
public class AnimatedDriver extends AbstractDriver implements Runnable {
	private final static Logger logger = Logger.getLogger(AnimatedDriver.class);

	private volatile Thread tickerThread;

	private double lastPhi;	// curAngle to location of inner gear's center
	private double endPhi;	// how far the location of inner gear's center shall 
							// rotate around the canvas center (multiple of 2*pi!)

	private final static int TIME_SLICE = 50;
	

	/**
	 * @param machine
	 */
	public AnimatedDriver(Machine machine) {
		super(machine);
	}

	/**
	 * 
	 * @see de.admadic.spiromat.machines.IDriver#attach()
	 */
	public void attach() {
		logger.debug("attach..."); //$NON-NLS-1$
		lastPhi = 0.0;
		updateEndPhi();
		setToCursor();
		
		tickerThread = new Thread(this, "AnimatedDriver"); //$NON-NLS-1$
		logger.debug("starting thread..."); //$NON-NLS-1$
		tickerThread.start();
	}

	/**
	 * 
	 */
	private void setToCursor() {
		FigureSpec fs = AppModel.getInstance().getDocModel().getActiveFigureSpec();
		double cursor = fs.getCursorAngle();
		if (cursor<0.0) {
			endPhi = 0.0 - endPhi;
		}
		lastPhi = cursor;
	}

	/**
	 * 
	 * @see de.admadic.spiromat.machines.IDriver#detach()
	 */
	public void detach() {
		logger.debug("detach..."); //$NON-NLS-1$
		Thread t = tickerThread;
		tickerThread = null;
		logger.debug("interrupt..."); //$NON-NLS-1$
		t.interrupt();	// interrupt it...
	}


	/**
	 * 
	 */
	private void updateEndPhi() {
		int rounds = Util.calculateRounds(
				AppModel.getInstance().getOuterRadius(), 
				AppModel.getInstance().getInnerRadius());
		endPhi = rounds*Math.PI*2;
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		logger.debug("ticker thread started"); //$NON-NLS-1$
		long lastTime = System.currentTimeMillis();
		long curTime = -1;
		double deltaPhi;
		Thread curthr = Thread.currentThread();
		while (curthr==tickerThread) {
			curTime = System.currentTimeMillis();
			if (curTime-lastTime < TIME_SLICE) {
				try {
					logger.debug("sleep..."); //$NON-NLS-1$
					Thread.sleep(TIME_SLICE);
					logger.debug("sleep done..."); //$NON-NLS-1$
				} catch (InterruptedException e) {
					logger.debug("InterruptedException caught, breaking..."); //$NON-NLS-1$
					break;	// stop loop in case of exception
				} finally {
					logger.debug("sleep (finally)..."); //$NON-NLS-1$
				}
				continue;
			}
//			if (isPause()) {
//				lastTime = curTime;
//				continue;
//			}

			logger.debug("getTimeRound..."); //$NON-NLS-1$
			deltaPhi = (curTime - lastTime) * Math.PI*2 / AppModel.getInstance().getTimeRound();
			logger.debug("getTimeRound done..."); //$NON-NLS-1$
			lastTime = curTime;

			if (endPhi<0.0) deltaPhi = -deltaPhi;
			
			if ((endPhi>0.0 && lastPhi<endPhi) || (endPhi<0.0 && lastPhi>endPhi)) {
				// action here
				lastPhi += deltaPhi;
				// just check interrupted:
				if (Thread.interrupted()) {
					logger.debug("interrupted, breaking..."); //$NON-NLS-1$
					break;
				}
				logger.debug("renderStep..."); //$NON-NLS-1$
				// old version: 
				machine.renderStep(lastPhi, 0, 0);
				// AppModel.getInstance().getDocModel().getActiveFigureSpec().setCursorAngle(lastPhi);
				logger.debug("renderStep done..."); //$NON-NLS-1$
			} else {
				// signal stop
				logger.debug("signalling stop..."); //$NON-NLS-1$
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						AppModel.getInstance().setStarted(false);
					}
				});
			}
		}
		logger.debug("ticker thread stopped"); //$NON-NLS-1$
	}
}
