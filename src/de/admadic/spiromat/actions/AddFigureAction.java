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

import de.admadic.spiromat.model.AppModel;
import de.admadic.spiromat.model.DocModel;
import de.admadic.spiromat.model.FigureSpec;
import de.admadic.spiromat.model.ModelUtil;
import de.admadic.spiromat.ui.Util;

/**
 * Adds a new figure to the list of figures.
 * 
 * @author Rainer Schwarze
 */
public class AddFigureAction extends AbstractAction {
	/** */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public AddFigureAction() {
		super();
		putValue(Action.NAME, Messages.getString("AddFigureAction.name")); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION, Messages.getString("AddFigureAction.shortDesc")); //$NON-NLS-1$
		putValue(Action.SMALL_ICON, Util.loadButtonImage("addfig.png")); //$NON-NLS-1$
	}

	/**
	 * @param e
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		DocModel docModel = AppModel.getInstance().getDocModel();
		FigureSpec fs = ModelUtil.createStandardFigureSpec(docModel);
		if (AppModel.getInstance().getAutoFill()) {
			fs.initFullInterval();
		}
		docModel.addFigureSpec(fs);
		docModel.setActiveFigureIndex(docModel.getFigureSpecCount()-1);
	}
}
