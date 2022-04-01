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

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.admadic.spiromat.log.Logger;
import de.admadic.spiromat.machines.AbstractMachine;
import de.admadic.spiromat.machines.Machine;
import de.admadic.spiromat.model.AppModel;

/**
 * Provides a view of a Spiromat main window (applet focused?).
 * 
 * @author Rainer Schwarze
 */
public class MainView extends FramedPanel {
	/** */
	private static final long serialVersionUID = 1L;

	final static Logger logger = Logger.getLogger(MainView.class);

	protected AppModel model;		// the model used.
	protected AbstractMachine machine;	// the machine which is active.

	private JPanel borderNorth;
	private JPanel borderEast;
	private JPanel borderSouth;

	private ControlPanel controlPanel;
	private FramedPanel canvasFrame;
	private SpiromatCanvas canvas;

	private boolean lastPauseState;

	JLabel headlineLabel;
	ImageIcon imgHeadline;

	MessageLabel messageLabel;

	/**
	 * @param model 
	 * 
	 */
	public MainView(AppModel model) {
		super();
		this.model = model;

		this.setFrameImage(Util.loadImage("spiromat-frame.png").getImage(), 5, 50); //$NON-NLS-1$

		BorderLayout borderLayout = new BorderLayout();
		this.setLayout(borderLayout);

		imgHeadline = Util.loadImage("headline.png"); //$NON-NLS-1$
		headlineLabel = new JLabel(""); //$NON-NLS-1$
		headlineLabel.setIcon(imgHeadline);
		headlineLabel.setAlignmentX(0.5f);

		messageLabel = new MessageLabel(de.admadic.spiromat.ui.Messages.getString("MainView.labelCoopyright")); //$NON-NLS-1$
		messageLabel.setBorder(
				BorderFactory.createEmptyBorder(3, 12, 3, 12));
		messageLabel.setFont(messageLabel.getFont().deriveFont(12.0f));

		Messenger.getInstance().registerMessageLabel(messageLabel);
		
		borderNorth = new JPanel();
		borderEast = new JPanel();
		borderSouth = new JPanel();

		borderNorth.setLayout(new BorderLayout());
		borderNorth.add(headlineLabel, BorderLayout.LINE_END);
		
		borderSouth.setLayout(new BorderLayout());
		borderSouth.add(messageLabel, BorderLayout.CENTER);
		
		this.setBackground(Color.LIGHT_GRAY);
		
		int borderWidth = 20;
		
		borderNorth.setSize(borderWidth, borderWidth);
		borderEast.setSize(borderWidth, borderWidth);
		borderSouth.setSize(borderWidth, borderWidth);

		borderNorth.setBackground(Color.WHITE);
		borderNorth.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.decode("0x339933"))); //$NON-NLS-1$
		borderSouth.setBackground(Color.WHITE);
		borderSouth.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, Color.decode("0x0066cc"))); //$NON-NLS-1$
//		borderSouth.setBackground(Color.RED);
		
		this.add(borderNorth, BorderLayout.NORTH);
		this.add(borderEast, BorderLayout.EAST);
		this.add(borderSouth, BorderLayout.SOUTH);

		canvasFrame = new FramedPanel();
		canvasFrame.setFrameImage(Util.loadImage("canvas-frame2-exp.png").getImage(), 20, 100); //$NON-NLS-1$

		canvas = new SpiromatCanvas();
		canvasFrame.add(canvas);

		this.add(canvasFrame, BorderLayout.CENTER);
		controlPanel = new ControlPanel(model, null);
		this.add(controlPanel, BorderLayout.WEST);

		replaceMachine(new Machine(canvas));
	}

	/**
	 * Replaces the existing machine with the new machine.
	 * The old machine is properly shut down and the new machine is 
	 * attached to the model.
	 * Note that the newmachine can be null, in which case the old machine
	 * is simply thrown away.
	 * 
	 * @param newmachine
	 */
	protected void replaceMachine(AbstractMachine newmachine) {
		if (machine!=null) {
			machine.stopMachine();
			machine.detachFromModel();
			machine.destroyMachine();
		}
		machine = newmachine;
		if (machine!=null) {
			machine.attachToModel();
		}
	}

	/**
	 * @return	Returns the SpiromatCanvas used for actual drawing.
	 */
	public SpiromatCanvas getSpiromatCanvas() {
		return canvas;
	}

	/**
	 * (Method to forward the destroy call of an applet.)
	 */
	public void destroy() {
		machine.destroyMachine();
	}

	/**
	 * (Method to forward the init call of an applet.)
	 */
	public void init() {
		/* nothing */
	}

	/**
	 * (Method to forward the start call of an applet.)
	 */
	public void start() {
		model.setPause(lastPauseState);
	}

	/**
	 * (Method to forward the stop call of an applet.)
	 */
	public void stop() {
		lastPauseState = model.getPause();
		model.setPause(true);
	}
}
