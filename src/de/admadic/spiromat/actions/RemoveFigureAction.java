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
import java.lang.ref.WeakReference;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ListSelectionModel;

import de.admadic.spiromat.model.AppModel;
import de.admadic.spiromat.ui.Util;

/**
 * Removes a figure from the document model.
 * 
 * @author Rainer Schwarze
 */
public class RemoveFigureAction extends AbstractAction {
	/** */
	private static final long serialVersionUID = 1L;

	WeakReference<ListSelectionModel> refListSelectionModel = 
		new WeakReference<ListSelectionModel>(null);
	
	/**
	 * 
	 */
	public RemoveFigureAction() {
		super();
		putValue(Action.NAME, Messages.getString("RemoveFigureAction.name")); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION, Messages.getString("RemoveFigureAction.shortDesc")); //$NON-NLS-1$
		putValue(Action.SMALL_ICON, Util.loadButtonImage("remfig.png")); //$NON-NLS-1$
	}

	/**
	 * @param e
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (refListSelectionModel.get()==null) {
			throw new Error("no ListSelectionModel registered"); //$NON-NLS-1$
		}
		ListSelectionModel lsm = refListSelectionModel.get();
		int sel = lsm.getMinSelectionIndex();
		if (sel<0) {
			// FIXME: make error if there is no selection
			return;
		}
		if (sel>=AppModel.getInstance().getDocModel().getFigureSpecCount()) {
			return;
		}
		AppModel.getInstance().getDocModel().removeFigureSpec(sel);
	}

	/**
	 * @param listSelectionModel
	 */
	public void setListModelRef(ListSelectionModel listSelectionModel) {
		refListSelectionModel = new WeakReference<ListSelectionModel>(listSelectionModel);
	}
}
