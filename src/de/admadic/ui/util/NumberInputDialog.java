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
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.admadic.ui.util.Dialog;

/**
 * @author Rainer Schwarze
 *
 */
public class NumberInputDialog extends Dialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JTextArea textMsg;
	JPanel panelInputs;

	NumberInputSet [] inputSets;
	JPanel panelButtons;
	JButton btnOK;
	JButton btnCancel;
	
	final static String CMD_OK = "nid.ok";
	final static String CMD_CANCEL = "nid.cancel";

	NumberInputValidator validator;
	
	/**
	 * @param msg 
	 * @param inputs 
	 * @throws HeadlessException
	 */
	public NumberInputDialog(String msg, NumberInputSet [] inputs) throws HeadlessException {
		super();
		initContents(msg, inputs);
	}

	/**
	 * @param arg0
	 * @param msg 
	 * @param inputs 
	 * @throws HeadlessException
	 */
	public NumberInputDialog(Frame arg0, String msg, NumberInputSet [] inputs) throws HeadlessException {
		super(arg0);
		initContents(msg, inputs);
	}

	/**
	 * @param arg0
	 * @param msg 
	 * @param inputs 
	 * @throws HeadlessException
	 */
	public NumberInputDialog(java.awt.Dialog arg0, String msg, NumberInputSet [] inputs) throws HeadlessException {
		super(arg0);
		initContents(msg, inputs);
	}

	private void initContents(String msg, NumberInputSet [] inputs) {
		if (inputs.length<1) {
			throw new Error("must have at least 1 input set.");
		}
		inputSets = inputs;

		FormLayout fl = new FormLayout(
				"12px, p:grow, 12px", 
				"12px, p, 5px, p:grow, 5px, p, 12px");
		CellConstraints cc = new CellConstraints();
		this.setLayout(fl);

		// (label, input, info)
		String colSpec = "0px, p, 5px, p:grow, 5px, p, 0px";
		String rowSpec = null;
		for (int i = 0; i < inputs.length; i++) {
			if (rowSpec==null) {
				rowSpec = "0px, p";
			} else {
				rowSpec += ", 5px, p";
			}
		}
		rowSpec += ", 0px";

		FormLayout fli = new FormLayout(colSpec, rowSpec);
		CellConstraints cci = new CellConstraints();
		panelInputs = new JPanel();
		panelInputs.setLayout(fli);
		this.add(panelInputs, cc.xy(2, 4));

		// create items:
		if (msg!=null) {
			textMsg = new JTextArea();
			textMsg.setEditable(false);
			textMsg.setOpaque(false);
			textMsg.setText(msg);
			this.add(textMsg, cc.xy(2, 2));
		}

		for (int i = 0; i < inputs.length; i++) {
			NumberInputSet iset = inputs[i];
			panelInputs.add(iset.label, cci.xy(2, 2 + i*2));
			panelInputs.add(iset.spinner, cci.xy(4, 2 + i*2));
			if (iset.info!=null) {
				panelInputs.add(iset.info, cci.xy(6, 2 + i*2));
			}
		}

		panelButtons = new JPanel();
		this.add(panelButtons, cc.xy(2, 6));
		btnOK = new JButton("OK");
		btnOK.setActionCommand(CMD_OK);
		btnOK.addActionListener(this);
		btnCancel = new JButton("Cancel");
		btnCancel.setActionCommand(CMD_CANCEL);
		btnCancel.addActionListener(this);
		panelButtons.add(btnOK);
		panelButtons.add(btnCancel);

		registerEnterAction(btnOK);
		
		setTitle("Number Input");
		setModal(true);
		pack();
		setLocationRelativeTo(getOwner());
	}

	/**
	 * @param e
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals(CMD_OK)) {
			// transfer data!
			for (NumberInputSet iset : inputSets) {
				iset.value = (Integer)iset.spinner.getValue();
			}
			if (validator!=null) {
				if (!validator.validate(inputSets)) {
					return; // invalid -> do not close
				}
			}
			setResultCode(RESULT_OK);
			setVisible(false);
		} else if (cmd.equals(CMD_CANCEL)) {
			setResultCode(RESULT_CANCEL);
			setVisible(false);
		}
	}

	/**
	 * @return Returns the validator.
	 */
	public NumberInputValidator getValidator() {
		return validator;
	}

	/**
	 * @param validator The validator to set.
	 */
	public void setValidator(NumberInputValidator validator) {
		this.validator = validator;
	}
}
