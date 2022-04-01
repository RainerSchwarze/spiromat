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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

import de.admadic.spiromat.model.AppModel;
import de.admadic.spiromat.model.DocModel;
import de.admadic.spiromat.ui.Util;

/**
 * @author Rainer Schwarze
 *
 */
public class NewDocAction extends AbstractAction {
	/** */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public NewDocAction() {
		super();
		putValue(Action.SHORT_DESCRIPTION, Messages.getString("NewDocAction.shortDesc")); //$NON-NLS-1$
		putValue(Action.NAME, Messages.getString("NewDocAction.name")); //$NON-NLS-1$
		putValue(Action.SMALL_ICON, Util.loadButtonImage("new2.png")); //$NON-NLS-1$
	}

	/**
	 * @param e
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		DocModel oldModel = AppModel.getInstance().getDocModel();
		if (oldModel!=null && oldModel.isDirty()) {
			String [] options = {
					Messages.getString("NewDocAction.optionLabelSave"), Messages.getString("NewDocAction.optionLabelNew"), Messages.getString("NewDocAction.optionLabelCancel") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			};
			int res = JOptionPane.showOptionDialog(
					null, 
					Messages.getString("NewDocAction.notSavedErrorMsg1")+ //$NON-NLS-1$
					Messages.getString("NewDocAction.notSavesErrorMsg2"),  //$NON-NLS-1$
					Messages.getString("NewDocAction.notSavedErrorTitle"),  //$NON-NLS-1$
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options, 
					options[2]);
			if (res==JOptionPane.CANCEL_OPTION) return;
			if (res==JOptionPane.YES_OPTION) {
				// do save!
				ActionFactory.get(ActionFactory.SAVE_DOC_ACTION).actionPerformed(e);
			}
		}
		DocModel docModel = new DocModel();
		docModel.setDefaults();
		// this is not dirty, since it will be recreated on every start
		// (so loosing it is not a problem)
		docModel.setDirty(false);
		AppModel.getInstance().setDocModel(docModel);		
	}
}
