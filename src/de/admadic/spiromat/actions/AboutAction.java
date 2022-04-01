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
import java.text.MessageFormat;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import de.admadic.spiromat.SpiromatApp;
import de.admadic.spiromat.Version;
import de.admadic.spiromat.ui.CfgSpi;
import de.admadic.spiromat.ui.Util;
import de.admadic.ui.util.AboutDialog;

/**
 * @author Rainer Schwarze
 *
 */
public class AboutAction extends AbstractAction {
	/** */
	private static final long serialVersionUID = 1L;

	/**
	 * The application string for the about dialog 
	 */
	final static String 
	UI_ABOUT_APP = 
		Messages.getString("AboutAction.aboutApp") + Version.version; //$NON-NLS-1$
	/**
	 * Copyright string for about dialog 
	 */
	final static String 
	UI_ABOUT_COPY = 
		Messages.getString("AboutAction.copyright"); //$NON-NLS-1$
	/**
	 * Further information for about dialog 
	 */
	final static String 
	UI_ABOUT_INF = 
		Messages.getString("AboutAction.info"); //$NON-NLS-1$

	/**
	 * External Software Libraries information for about dialog 
	 */
	final static String 
	UI_ABOUT_SOFTLIBS = 
		Messages.getString("AboutAction.softLibs1")+ //$NON-NLS-1$
		"\n"+ //$NON-NLS-1$
		Messages.getString("AboutAction.softLibs2") + //$NON-NLS-1$
		"\n" + //$NON-NLS-1$
		Messages.getString("AboutAction.softLibs3"); //$NON-NLS-1$

	/**
	 * Title of about dialog 
	 */
	final static String 
	UI_ABOUT_TITLE = Messages.getString("AboutAction.title"); //$NON-NLS-1$

	/**
	 * 
	 */
	public AboutAction() {
		super();
		putValue(Action.SHORT_DESCRIPTION, Messages.getString("AboutAction.shortDesc")); //$NON-NLS-1$
		putValue(Action.NAME, Messages.getString("AboutAction.name")); //$NON-NLS-1$
		putValue(Action.SMALL_ICON, Util.loadButtonImage("about.png")); //$NON-NLS-1$
	}

	/**
	 * @param e
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		JFrame parent = null;
		if (e.getSource() instanceof Component) {
			Component c = SwingUtilities.getRoot((Component) e.getSource());
			if (c instanceof JFrame) {
				parent = (JFrame) c;
			}
		}
//		AboutDialog1 dlg = new AboutDialog1(parent);
//		dlg.setVisible(true);
		{
			CfgSpi cfg = SpiromatApp.cfg;
			AboutDialog dialog = new AboutDialog(parent);

			dialog.setAboutInfo(
					UI_ABOUT_APP,
					UI_ABOUT_COPY,
					UI_ABOUT_INF,
					"",
					UI_ABOUT_SOFTLIBS);
			dialog.setTitle(UI_ABOUT_TITLE);
			ImageIcon ii = Util.loadImage("logo-6-48.png"); //$NON-NLS-1$
			dialog.setLogo(ii.getImage());

			dialog.pack();
			dialog.setLocationRelativeTo(parent);
			
			dialog.setVisible(true);
		}
	}

}
