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

import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

/**
 * @author Rainer Schwarze
 */
public class Dialog extends JDialog {
	final static boolean DbgDialog = false;
	/**
	 * Option code is undefined (neither OK nor Cancel was pressed)
	 */
	public final static int RESULT_NONE = 0;
	/**
	 * Option code for button OK was pressed
	 */
	public final static int RESULT_OK = 1;
	/**
	 * Option code for button Cancel was pressed.
	 * (When the window frame close button is pressed, the code CANCEL
	 * is generated)
	 */
	public final static int RESULT_CANCEL = 2;
	/**
	 * Option code for button Close was pressed
	 */
	public final static int RESULT_CLOSE = 3;

	int resultCode = RESULT_NONE;

	int windowCloseCode = RESULT_CANCEL;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @throws HeadlessException
	 */
	public Dialog() throws HeadlessException {
		super();
	}

	/**
	 * @param arg0
	 * @throws HeadlessException
	 */
	public Dialog(Frame arg0) throws HeadlessException {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws HeadlessException
	 */
	public Dialog(Frame arg0, boolean arg1) throws HeadlessException {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws HeadlessException
	 */
	public Dialog(Frame arg0, String arg1) throws HeadlessException {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws HeadlessException
	 */
	public Dialog(Frame arg0, String arg1, boolean arg2)
			throws HeadlessException {
		super(arg0, arg1, arg2);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public Dialog(Frame arg0, String arg1, boolean arg2,
			GraphicsConfiguration arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	/**
	 * @param arg0
	 * @throws HeadlessException
	 */
	public Dialog(java.awt.Dialog arg0) throws HeadlessException {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws HeadlessException
	 */
	public Dialog(java.awt.Dialog arg0, boolean arg1) throws HeadlessException {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws HeadlessException
	 */
	public Dialog(java.awt.Dialog arg0, String arg1) throws HeadlessException {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws HeadlessException
	 */
	public Dialog(java.awt.Dialog arg0, String arg1, boolean arg2)
			throws HeadlessException {
		super(arg0, arg1, arg2);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 * @throws HeadlessException
	 */
	public Dialog(java.awt.Dialog arg0, String arg1, boolean arg2,
			GraphicsConfiguration arg3) throws HeadlessException {
		super(arg0, arg1, arg2, arg3);
	}

	/**
	 * 
	 * @see javax.swing.JDialog#dialogInit()
	 */
	@Override
	protected void dialogInit() {
		if (DbgDialog) System.out.println("Dialog: dialogInit...");
		super.dialogInit();
		this.addWindowListener(new WindowAdapter() {

			/**
			 * @param arg0
			 * @see java.awt.event.WindowAdapter#windowClosed(java.awt.event.WindowEvent)
			 */
			@Override
			public void windowClosed(WindowEvent arg0) {
				if (DbgDialog) System.out.println("Dialog: windowClosed.");
				super.windowClosed(arg0);
			}

			/**
			 * @param arg0
			 * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
			 */
			@Override
			public void windowClosing(WindowEvent arg0) {
				if (DbgDialog) System.out.println("Dialog: windowClosing.");
				resultCode = windowCloseCode;
				closingDialog(resultCode);
				super.windowClosing(arg0);
			}
		});
		registerEscapeAction();
	}

	/**
	 * Register a close action when the ESC key is pressed.
	 */
	protected void registerEscapeAction() {
		JRootPane rootPane = this.getRootPane();
		InputMap iMap = rootPane.getInputMap(                                            
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		iMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escape");
		ActionMap aMap = rootPane.getActionMap();
		aMap.put("escape", new AbstractAction() {
			/** */
			private static final long serialVersionUID = 1L;
			/** */
			public void actionPerformed(ActionEvent e) {
				if (DbgDialog) System.out.println("Dialog: ESC-action: calling dispose().");
				dispose();
			}
		}); 
	}

	/**
	 * Register a "press default button" action when the ENTER key is pressed.
	 * @param btn 
	 */
	protected void registerEnterAction(JButton btn) {
		JRootPane rootPane = this.getRootPane();
		rootPane.setDefaultButton(btn);
	}

	/**
	 * @param windowCloseCode The windowCloseCode to set.
	 */
	public void setWindowCloseCode(int windowCloseCode) {
		this.windowCloseCode = windowCloseCode;
	}

	/**
	 * @param code
	 */
	public void closingDialog(int code) {
		if (DbgDialog) System.out.println("Dialog: closingDialog: code=" + code);
		// to be overwritten
	}

	/**
	 * @return Returns the optionCode.
	 */
	public int getResultCode() {
		return resultCode;
	}

	/**
	 * @param resultCode The optionCode to set.
	 */
	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}
}
