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
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.text.MessageFormat;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.UIManager;

import de.admadic.spiromat.actions.ActionFactory;
import de.admadic.spiromat.log.Logger;
import de.admadic.spiromat.machines.AbstractMachine;
import de.admadic.spiromat.machines.Machine;
import de.admadic.spiromat.model.AppModel;
import de.admadic.spiromat.model.DocModel;
import de.admadic.spiromat.model.ModelPropertyChangeListener;
import de.admadic.spiromat.model.ModelPropertyChangeSupport;

/**
 * Provides a view of a Spiromat main window (application focused).
 * 
 * @author Rainer Schwarze
 */
public class MainFrame extends JFrame implements ModelPropertyChangeListener {
	/** */
	private static final long serialVersionUID = 1L;

	final static Logger logger = Logger.getLogger(MainFrame.class);

	protected AppModel appModel;		// the model used.
	protected AbstractMachine machine;	// the machine which is active.

	private JSplitPane splitMain;
	
	private JPanel borderNorth;
	private JPanel borderSouth;
	private JToolBar toolBar;

	private ControlPanel controlPanel;
	private FramedPanel canvasFrame;
	private SpiromatCanvas canvas;
	private DocPanel docPanel;

	private BackgroundStatus backgroundStatus;
	private StatusLine statusLine;
	
	private boolean lastPauseState;

	JLabel headlineLabel;
	ImageIcon imgHeadline;

	MessageLabel messageLabel;

	ModelPropertyChangeSupport modelPropertyChangeSupport;
	
	/**
	 * @param appModel 
	 * 
	 */
	public MainFrame(AppModel appModel) {
		super();
		this.appModel = appModel;
	}


	/**
	 * @param e
	 * @see de.admadic.spiromat.model.ModelPropertyChangeListener#appPropertyChange(java.beans.PropertyChangeEvent)
	 */
	public void appPropertyChange(PropertyChangeEvent e) {
		/* nothing */
	}

	/**
	 * @param e
	 * @see de.admadic.spiromat.model.ModelPropertyChangeListener#docPropertyChange(java.beans.PropertyChangeEvent)
	 */
	public void docPropertyChange(PropertyChangeEvent e) {
		String propName = e.getPropertyName();
		if (propName!=null) {
			if (propName.equals(DocModel.PROPERTY_DOC_MODEL_DIRTY)) {
				logger.debug("dirty change detected");  //$NON-NLS-1$
				updateTitle();
			}
		}
	}

	/**
	 * @param e
	 * @see de.admadic.spiromat.model.ModelPropertyChangeListener#figPropertyChange(java.beans.PropertyChangeEvent)
	 */
	public void figPropertyChange(PropertyChangeEvent e) {
		/* nothing */
	}

	/**
	 * 
	 */
	public void initContents() {
		modelPropertyChangeSupport = new ModelPropertyChangeSupport(
				ModelPropertyChangeSupport.MASK_APP |
				ModelPropertyChangeSupport.MASK_DOC, 
				this, 
				true);
		// lets register the initial document:
		ActionFactory.get(ActionFactory.NEW_DOC_ACTION).actionPerformed(
				new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "init"));  //$NON-NLS-1$
		updateTitle();
		this.setIconImages(Util.loadLogoImages());

		// we don't want the user to make it really big for now...
		this.setResizable(false);
		
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				ActionFactory.get(ActionFactory.CLOSE_ACTION).actionPerformed(
						ActionFactory.createEvent(MainFrame.this, "quit"));  //$NON-NLS-1$
			}
		});
		
		BorderLayout borderLayout = new BorderLayout();
		this.setLayout(borderLayout);

		imgHeadline = Util.loadImage("headline.png");  //$NON-NLS-1$
		headlineLabel = new JLabel(imgHeadline);
		headlineLabel.setAlignmentX(0.5f);

		messageLabel = new MessageLabel(Messages.getString("MainFrame.labelCopyright"));  //$NON-NLS-1$
		messageLabel.setBorder(
				BorderFactory.createEmptyBorder(3, 12, 3, 12));
		messageLabel.setFont(messageLabel.getFont().deriveFont(12.0f));

		Messenger.getInstance().registerMessageLabel(messageLabel);

		toolBar = new JToolBar(JToolBar.VERTICAL);
		toolBar.setBorderPainted(true);
		toolBar.setBackground(UIManager.getColor("Panel.background")); //$NON-NLS-1$
		toolBar.setBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createEmptyBorder(3, 1, 3, 3), 
						BorderFactory.createMatteBorder(0, 1, 0, 0, Color.decode("0x339933"))));  //$NON-NLS-1$
		
		
		borderNorth = new JPanel();
		borderNorth.setLayout(new BorderLayout());
		borderNorth.add(headlineLabel, BorderLayout.LINE_END);
		borderNorth.setBackground(Color.WHITE);
		borderNorth.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.decode("0x339933")));  //$NON-NLS-1$
		
		backgroundStatus = new BackgroundStatus();
		
		statusLine = new StatusLine();
		statusLine.addComponent(messageLabel, null);
		statusLine.addComponent(backgroundStatus, "20dlu");  //$NON-NLS-1$
		
		borderSouth = new JPanel();
		borderSouth.setLayout(new BorderLayout());
		borderSouth.add(statusLine, BorderLayout.CENTER);
		borderSouth.setBackground(Color.WHITE);
		borderSouth.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, Color.decode("0x0066cc")));  //$NON-NLS-1$

		toolBar.addSeparator();
		toolBar.add(new JToggleButton(ActionFactory.get(ActionFactory.SHOW_PICTURE_ACTION)));
		toolBar.addSeparator();
		toolBar.add(ActionFactory.get(ActionFactory.NEW_DOC_ACTION));
		toolBar.addSeparator();
		toolBar.add(ActionFactory.get(ActionFactory.OPEN_DOC_ACTION));
		toolBar.addSeparator();
		toolBar.add(ActionFactory.get(ActionFactory.SAVE_DOC_ACTION));
		toolBar.addSeparator();
		toolBar.add(ActionFactory.get(ActionFactory.SAVE_AS_DOC_ACTION));
		toolBar.addSeparator();
		toolBar.add(ActionFactory.get(ActionFactory.EXPORT_DOC_ACTION));
		toolBar.addSeparator();
		toolBar.add(ActionFactory.get(ActionFactory.ABOUT_ACTION));
		toolBar.addSeparator();
		toolBar.add(ActionFactory.get(ActionFactory.CLOSE_ACTION));

		toolBar.setFloatable(false);

		this.add(borderNorth, BorderLayout.NORTH);
		this.add(borderSouth, BorderLayout.SOUTH);
		this.add(toolBar, BorderLayout.EAST);

		for (int i=0; i<toolBar.getComponentCount(); i++) {
			Component c = toolBar.getComponent(i);
			if (c instanceof AbstractButton) {
				AbstractButton btn = (AbstractButton) c;
				btn.setHideActionText(true);
			}
		}
		
		splitMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false);
		splitMain.setBorder(null);
		this.add(splitMain, BorderLayout.CENTER);

		canvasFrame = new FramedPanel();
		canvasFrame.setFrameImage(
				Util.loadImage("canvas-frame2-exp.png").getImage(),  //$NON-NLS-1$
				20, 100);

		canvas = new SpiromatCanvas();
		canvasFrame.add(canvas);

		splitMain.setRightComponent(canvasFrame);

		docPanel = new DocPanel();
		controlPanel = new ControlPanel(appModel, docPanel);
		splitMain.setLeftComponent(controlPanel);

		replaceMachine(new Machine(canvas));

		// retrieve sizes from preferences?
		this.setSize(700, 550);
		this.setLocationRelativeTo(null);

		FileChooserProvider.prepareInBackground();
	}

	/**
	 * 
	 */
	protected void updateTitle() {
		boolean dirty = false;
		Integer dirtyi = null;
		String fname = "<untitled>";  //$NON-NLS-1$
		String fpath = "";  //$NON-NLS-1$
		if (
				AppModel.getInstance()!=null &&
				AppModel.getInstance().getDocModel()!=null
			) {
			DocModel docModel = AppModel.getInstance().getDocModel();
			dirty = docModel.isDirty();
			File f = docModel.getFile();
			if (f!=null) {
				fname = f.getName();
				if (f.getParent()!=null) {
					fpath = f.getParent();
				}
			}
		}
		dirtyi = Integer.valueOf(dirty ? 1 : 0);
		String msg = MessageFormat.format(
				Messages.getString("MainFrame.titleFormat"),  //$NON-NLS-1$
				// "admaDIC Spiromat :{0,choice,0# |1# *} {1} {2,choice,0# |0<@} {3}", 
				new Object[]{
						dirtyi,
						fname,
						Integer.valueOf(fpath.length()),
						fpath,
				});
		setTitle(msg);
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
		appModel.setPause(lastPauseState);
	}

	/**
	 * (Method to forward the stop call of an applet.)
	 */
	public void stop() {
		lastPauseState = appModel.getPause();
		appModel.setPause(true);
	}
}
