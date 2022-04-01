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
package de.admadic.spiromat.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.text.MessageFormat;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import de.admadic.spiromat.log.Logger;
import de.admadic.spiromat.model.AppModel;
import de.admadic.spiromat.model.DocModel;
import de.admadic.spiromat.ui.FileChooserProvider;
import de.admadic.spiromat.ui.Util;
import de.admadic.spiromat.util.FileUtil;

/**
 * @author Rainer Schwarze
 *
 */
public class SaveAsDocAction extends AbstractAction {
	static Logger logger = Logger.getLogger(SaveAsDocAction.class);

	/** */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public SaveAsDocAction() {
		super();
		putValue(Action.NAME, Messages.getString("SaveAsDocAction.name")); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION, Messages.getString("SaveAsDocAction.shortDesc")); //$NON-NLS-1$
		putValue(Action.SMALL_ICON, Util.loadButtonImage("saveas.png")); //$NON-NLS-1$
	}

	/**
	 * @param e
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		DocModel docModel = AppModel.getInstance().getDocModel();
		JFrame parent = null;
		if (e.getSource() instanceof Component) {
			Component c = SwingUtilities.getRoot((Component) e.getSource());
			if (c instanceof JFrame) {
				parent = (JFrame) c;
			}
		}
		File f = null;
		JFileChooser fc = FileChooserProvider.getFileChooser();
		FileChooserProvider.setSpmFilter(fc);
		fc.setSelectedFile(null);
		while (true) {
			int res = fc.showSaveDialog(parent);
			if (res!=JFileChooser.APPROVE_OPTION) {
				return;
			}
			f = fc.getSelectedFile();
			f = FileUtil.tryToAppendExtension(f, ".spm", true); //$NON-NLS-1$
			if (!f.exists()) 
				break;

			String [] options = {
					Messages.getString("SaveAsDocAction.optionLabelOverwrite"), Messages.getString("SaveAsDocAction.optionLabelChooseOther"), Messages.getString("SaveAsDocAction.optionLabelCancel") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			};
			String msg = MessageFormat.format(
					Messages.getString("SaveAsDocAction.fileExistsErrorMsg1") + //$NON-NLS-1$
					Messages.getString("SaveAsDocAction.fileExistsErrorMsg2"),  //$NON-NLS-1$
					new Object[]{
							f.getName(), 
							(f.getParent()==null ? Messages.getString("SaveAsDocAction.unknownDir") : f.getParent())  //$NON-NLS-1$
					} 
				);
			res = JOptionPane.showOptionDialog(
					parent, 
					msg,
					Messages.getString("SaveAsDocAction.fileExistsErrorTitle"), //$NON-NLS-1$
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options, 
					options[1]);
			switch (res) {
			case JOptionPane.YES_OPTION: break;
			case JOptionPane.NO_OPTION: continue;	// repeat, select another file
			case JOptionPane.CANCEL_OPTION: return;
			}
		}
		docModel.setFile(f);

		Action action = ActionFactory.get(ActionFactory.SAVE_DOC_ACTION);
		action.actionPerformed(e);
	}
}
