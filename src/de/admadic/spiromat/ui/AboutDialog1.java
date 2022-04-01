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

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.admadic.spiromat.Version;

/**
 * @author Rainer Schwarze
 *
 */
public class AboutDialog1 extends JDialog {
	/** */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused") //$NON-NLS-1$
	private JLabel imageLabel;
	private JLabel infoLabel;
	private JButton okButton;
	private JPanel buttonPanel;

	/**
	 * 
	 */
	public AboutDialog1() {
		super();
		init();
	}

	/**
	 * @param owner
	 */
	public AboutDialog1(Dialog owner) {
		super(owner);
		init();
	}

	/**
	 * @param owner
	 */
	public AboutDialog1(Frame owner) {
		super(owner);
		init();
	}

	/**
	 * @param owner
	 */
	public AboutDialog1(Window owner) {
		super(owner);
		init();
	}

	/**
	 * 
	 */
	private void init() {
		this.setLayout(new FormLayout(
				"12px, d, 12px", //$NON-NLS-1$
				"12px, d, 5px, d, 5px, d, 5px")); //$NON-NLS-1$
		CellConstraints cc = new CellConstraints();

		this.setTitle(Messages.getString("AboutDialog1.dialogTitle")); //$NON-NLS-1$
		this.setModal(true);

		this.add(
				imageLabel = new JLabel(
						Util.loadImage("spiromat-splash2.png")),  //$NON-NLS-1$
				cc.xy(2, 2));
		this.add(infoLabel = new JLabel(""), cc.xy(2, 4)); //$NON-NLS-1$
		this.add(buttonPanel = new JPanel(), cc.xy(2, 6));
		buttonPanel.add(okButton = new JButton(Messages.getString("AboutDialog1.btnLabelOK"))); //$NON-NLS-1$
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AboutDialog1.this.setVisible(false);
			}
		});

		String msg = MessageFormat.format(
				Messages.getString("AboutDialog1.aboutMessage"),  //$NON-NLS-1$
				new Object[]{
						Version.version,
				});
		infoLabel.setText(msg);

		this.setUndecorated(true);
		
		pack();
		this.setLocationRelativeTo(getOwner());
	}
}
