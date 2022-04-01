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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Rainer Schwarze
 *
 */
public class BackgroundWaitDialog extends JDialog {
	/** */
	private static final long serialVersionUID = 1L;

	JLabel msgLabel;
	JLabel tskLabel;
	JButton cancel;
	
	private PropertyChangeListener pcl = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			updateTaskCount(((Integer)(evt.getNewValue())).intValue());
		}
	};

	/**
	 * @param owner
	 */
	public BackgroundWaitDialog(Frame owner) {
		super(owner);
		init();
	}

	/**
	 * @param owner
	 */
	public BackgroundWaitDialog(Dialog owner) {
		super(owner);
		init();
	}

	/**
	 * @param enabled
	 */
	public void setCancelEnabled(boolean enabled) {
		cancel.setEnabled(enabled);
	}
	
	/**
	 * This method checks for background tasks and waits for them to 
	 * terminate.
	 */
	public void execute() {
		this.setModal(true);
		BackgroundManager.getInstance().addPropertyChangeListener(pcl);
		int taskCount = BackgroundManager.getInstance().getTaskCount();
		if (taskCount>0) {
			tskLabel.setText("" + taskCount); //$NON-NLS-1$
			this.setVisible(true);
			// its modal - we return, if the tasks are finished
		}
		BackgroundManager.getInstance().removePropertyChangeListener(pcl);
	}
	
	/**
	 * 
	 */
	private void init() {
		this.setLayout(new FormLayout(
				"12px, p, 12px",  //$NON-NLS-1$
				"12px, d, 5px, d, 12px, d, 12px")); //$NON-NLS-1$
		CellConstraints cc = new CellConstraints();

		this.setTitle(Messages.getString("BackgroundWaitDialog.dialogTitle")); //$NON-NLS-1$
		
		this.add(
				msgLabel = new JLabel(
						Messages.getString("BackgroundWaitDialog.waitMessage") //$NON-NLS-1$
						),
				cc.xy(2, 2, CellConstraints.FILL, CellConstraints.FILL));
		this.add(tskLabel = new JLabel("?"), cc.xy(2, 4, CellConstraints.FILL, CellConstraints.FILL)); //$NON-NLS-1$
		cancel = new JButton(Messages.getString("BackgroundWaitDialog.btnLabelCancel")); //$NON-NLS-1$
		JPanel tmp = new JPanel();
		tmp.add(cancel);
		this.add(tmp, cc.xy(2, 6));

		msgLabel.setHorizontalAlignment(SwingConstants.CENTER);
		tskLabel.setHorizontalAlignment(SwingConstants.CENTER);

		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// we don't want to wait any more.
				// FIXME: should some worker tasks be non-daemon, we need to add task cancellation here...
				setVisible(false);
			}
		});
		this.pack();
		this.setLocationRelativeTo(this.getOwner());
	}

	/**
	 * @param taskCount
	 */
	protected void updateTaskCount(int taskCount) {
		tskLabel.setText("" + taskCount); //$NON-NLS-1$
		if (taskCount<1) {
			this.setVisible(false);
			return;
		}
	}
}
