/**
 *
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
 */
package de.admadic.ui.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;


/**
 * @author Rainer Schwarze
 *
 */
public class LicTextViewDialog extends de.admadic.ui.util.Dialog {
	/** */
	private static final long serialVersionUID = 1L;
	final static boolean LOG = true;
	Logger logger = (LOG) ? Logger.getLogger("de.admadic") : null;

	JScrollPane spLicText;
	JTextArea taLicText;
	JButton btnAccept;
	JButton btnClose;	// or decline
	JButton btnPrint;
	JPanel pnButtons;

	boolean accepted;

	/**
	 * @param frame
	 */
	public LicTextViewDialog(JFrame frame) {
		this(frame, false);
	}

	/**
	 * @param frame
	 * @param acceptMode
	 */
	public LicTextViewDialog(JFrame frame, boolean acceptMode) {
		super(frame);
		initGUI(acceptMode);
	}

	/**
	 * @param text
	 */
	public void setText(String text) {
		taLicText.setText(text);
		taLicText.setCaretPosition(0);
		spLicText.getVerticalScrollBar().setValue(0);
	}

	/**
	 * @param file
	 */
	public void setLicSrc(File file) {
		FileReader fr;
		String text = "";
		String line;
		BufferedReader br;

		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			while ((line = br.readLine())!=null) {
				text += line + '\n';
			}
		} catch (FileNotFoundException e) {
			// e.printStackTrace();
			if (logger!=null) logger.severe(
					"Could not find the license text file @" + 
					file.toString() + " error: " + e.getMessage());
			text = "Error: The license file could not be found.\n" +
					"It should be at " + file.toString();
		} catch (IOException e) {
			// e.printStackTrace();
			if (logger!=null) logger.severe(
					"Could read from the license text file @" + 
					file.toString() + " error: " + e.getMessage());
			text = "Error: Could not read the license file.\n" +
					"It is located at " + file.toString();
		}
		setText(text);
	}

	/**
	 * @param istream
	 */
	public void setLicSrc(InputStream istream) {
		InputStreamReader isr;
		BufferedReader br;
		String text = "";
		String line;

		try {
			isr = new InputStreamReader(istream);
			br = new BufferedReader(isr);
			while ((line = br.readLine())!=null) {
				text += line + '\n';
			}
		} catch (IOException e) {
			// e.printStackTrace();
			if (logger!=null) logger.severe(
					"Could read from the license text. " + 
					" error: " + e.getMessage());
			text = "Error: Could not read the license file.\n" + 
					"Please contact customer support.";
		}
		setText(text);
	}

	/**
	 * 
	 */
	public void doPrint() {
		StringPrinter sp = new StringPrinter(this, taLicText.getText());
		sp.setFontSize(10);
		sp.doPrint(false);
	}

	private void initGUI(boolean acceptMode) {
		try {
			this.setTitle("License Information - admaDIC Calculator");
			if (acceptMode) {
				setModal(true);
			}
			FormLayout thisLayout = new FormLayout(
					"5px, p, 5px", 
					"5px, p, 5px, p, 5px");
			this.getContentPane().setLayout(thisLayout);
			spLicText = new JScrollPane();
			this.getContentPane().add(
					spLicText,
					new CellConstraints("2, 2, 1, 1, default, default"));
			taLicText = new JTextArea();
			spLicText.setViewportView(taLicText);
			taLicText.setText("<license text>");
			taLicText.setColumns(80);
			taLicText.setRows(20);
			taLicText.setEditable(false);
			taLicText.setFont(new java.awt.Font("Monospaced",0,14));
			pnButtons = new JPanel();
			pnButtons.setOpaque(false);
			this.getContentPane().add(
					pnButtons,
					new CellConstraints("2, 4, 1, 1, default, default"));

			if (acceptMode) {
				btnAccept = new JButton();
				pnButtons.add(btnAccept);
				btnAccept.setText("Accept");
				btnAccept.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setAccepted(true);
						LicTextViewDialog.this.dispose();
					}
			});
			}
			
			btnClose = new JButton();
			pnButtons.add(btnClose);
			btnClose.setText((acceptMode) ? "Decline" : "Close");
			btnClose.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					LicTextViewDialog.this.dispose();
				}
			});
			btnPrint = new JButton();
			pnButtons.add(btnPrint);
			btnPrint.setText("Print...");
			btnPrint.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					doPrint();
				}
			});
			this.pack();
			this.setLocationRelativeTo(null);
		} catch (Exception e) {
			// e.printStackTrace();
			if (logger!=null) logger.severe(
					"Error creating the license view dialog: " + 
					e.getMessage());
		}
	}

	/**
	 * @return Returns the accepted.
	 */
	public boolean isAccepted() {
		return accepted;
	}

	/**
	 * @param accepted The accepted to set.
	 */
	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}	
}
