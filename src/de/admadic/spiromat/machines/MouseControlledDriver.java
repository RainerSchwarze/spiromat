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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import de.admadic.spiromat.log.Logger;

/**
 * @author Rainer Schwarze
 */
public class MouseControlledDriver extends AbstractDriver {
	final static Logger logger = Logger.getLogger(MouseControlledDriver.class);

	private MouseMotionListener mouseMotionListener;	// for motions
	private MouseListener mouseListener;				// for initial press

	// this one debounces mouse events:
	private DebounceRunner mouseEventTrigger;

	// mouse event parameters:
	// (those are collected within a mouse event: write access in mouse event 
	// handler, read access in calculation handler)
	final private Object mouseEventLock = new Object();
	private int mouseEventX;
	private int mouseEventY;
	private double mouseEventPhi;
	private boolean hasNewEventData = false;

	// processing variables:
	// (those are handled in the calculation handler and rendering)
	final private Object calculationLock = new Object();
	private int mousex;
	private int mousey;
	private double targetPhi;

	/**
	 * @param machine
	 */
	public MouseControlledDriver(Machine machine) {
		super(machine);
	}

	/**
	 * Called from within a mouse event listener.
	 * 
	 * @param x
	 * @param y
	 * @param pressed
	 */
	protected void updateMousePos(int x, int y, boolean pressed) {
		if (!pressed) return;
		// logger.debug("mouse.");
		int [] a = {x, y};
		machine.getCanvas().transformToCanvasSpace(a);
		if (logger.isDebugEnabled())
			logger.debug("updM: x=" + x + " y=" + y + " a[0]=" + a[0] + " a[1]=" + a[1]); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

		synchronized (mouseEventLock) {
			mouseEventX = a[0];
			mouseEventY = a[1];
			mouseEventPhi = machine.getKnob().updateKnobFromMousePos(
					mouseEventX, mouseEventY);
			hasNewEventData = true;		// flip the flip-flop
			mouseEventTrigger.setHasValue(true);
		}
	}

	/**
	 * Handler called when new mouse data has been stored by the mouse event
	 * listener.
	 */
	protected void applyNewMouseData() {
		logger.debug("applyNewMouseData..."); //$NON-NLS-1$
		boolean doit = false;

		synchronized (mouseEventLock) {
			if (!hasNewEventData) return;

			synchronized (calculationLock) {
				mousex = mouseEventX;
				mousey = mouseEventY;
				targetPhi = mouseEventPhi;
			}
			
			doit = true;
			hasNewEventData = false;	// flop the flip-flop
		}

		// action here
		if (doit) {
			machine.renderStep(targetPhi, mousex, mousey);
		}
	}

	/**
	 * 
	 * @see de.admadic.spiromat.machines.IDriver#attach()
	 */
	public void attach() {
		mouseEventTrigger = new DebounceRunner(new Runnable() {
			public void run() {
				applyNewMouseData();
			}
		}, true);

		mouseMotionListener = new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
				updateMousePos(e.getX(), e.getY(), true);
			}
			public void mouseMoved(MouseEvent e) {
				updateMousePos(e.getX(), e.getY(), false);
			}
		};
		mouseListener = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				updateMousePos(e.getX(), e.getY(), true);
			}
		};

		machine.getCanvas().getCanvasComponent().addMouseMotionListener(mouseMotionListener);
		machine.getCanvas().getCanvasComponent().addMouseListener(mouseListener);
	}

	/**
	 * 
	 * @see de.admadic.spiromat.machines.IDriver#detach()
	 */
	public void detach() {
		machine.getCanvas().getCanvasComponent().removeMouseMotionListener(mouseMotionListener);
		machine.getCanvas().getCanvasComponent().removeMouseListener(mouseListener);

		mouseEventTrigger.stopThread();
	}
}
