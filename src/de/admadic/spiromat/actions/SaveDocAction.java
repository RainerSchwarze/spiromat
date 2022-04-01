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
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import de.admadic.spiromat.log.Logger;
import de.admadic.spiromat.model.AppModel;
import de.admadic.spiromat.model.DocModel;
import de.admadic.spiromat.model.io.DocumentWriter;
import de.admadic.spiromat.model.io.SpiromatIOException;
import de.admadic.spiromat.ui.BackgroundManager;
import de.admadic.spiromat.ui.BackgroundWaitDialog;
import de.admadic.spiromat.ui.Util;

/**
 * @author Rainer Schwarze
 *
 */
public class SaveDocAction extends AbstractAction {
	static Logger logger = Logger.getLogger(SaveDocAction.class); 

	/** */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public SaveDocAction() {
		super();
		putValue(Action.NAME, Messages.getString("SaveDocAction.name")); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION, Messages.getString("SaveDocAction.shortDesc")); //$NON-NLS-1$
		putValue(Action.SMALL_ICON, Util.loadButtonImage("save.png")); //$NON-NLS-1$
	}

	/**
	 * @param e
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		DocModel docModel = AppModel.getInstance().getDocModel();
		if (docModel.getFile()==null) {
			// do save as?
			Action action = ActionFactory.get(ActionFactory.SAVE_AS_DOC_ACTION);
			action.actionPerformed(e);
			// ATN: if this succeeds it will go through to this method again!
		} else {
			JFrame parent = null;
			if (e.getSource() instanceof Component) {
				Component c = SwingUtilities.getRoot((Component) e.getSource());
				if (c instanceof JFrame) {
					parent = (JFrame) c;
				}
			}

			Worker worker = new Worker(docModel, docModel.getFile(), parent);
			worker.execute();

			BackgroundWaitDialog bwd = new BackgroundWaitDialog(parent);
			bwd.setCancelEnabled(false);
			bwd.execute();
		}
	}

	static class Worker implements Runnable {
		AppModel appModel;
		DocModel docModel;
		File file;
		JFrame parent;

		/**
		 * @param docModel
		 * @param file
		 * @param parent 
		 */
		public Worker(DocModel docModel, File file, JFrame parent) {
			super();
			this.docModel = docModel;
			this.file = file;
			this.parent = parent;
		}

		/**
		 * 
		 */
		public void execute() {
			Thread thread = new Thread(this, "save-thread"); //$NON-NLS-1$
			thread.setDaemon(true);
			BackgroundManager.getInstance().addTask(this);
			thread.start();
		}
		
		/**
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			try {
				DocumentWriter dw = new DocumentWriter(docModel);
				boolean ok = false;
				Exception e = null;
				try {
					dw.write();
					ok = true;
				} catch (SpiromatIOException ex) {
					logger.error("save error", ex); //$NON-NLS-1$
					e = ex;
					ok = false;
				}
				final boolean ok0 = ok;
				final Exception e0 = e;
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (ok0) {
							docModel.setDirty(false);
						} else {
							showSaveError(parent, docModel.getFile(), e0);
						}
					}
				});

			} finally {
				BackgroundManager.getInstance().markDone(this);
			}
		}
	}

	/**
	 * @param parent 
	 * @param f0
	 * @param e0
	 */
	static protected void showSaveError(final JFrame parent, final File f0, final Exception e0) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				String msg = MessageFormat.format(
						Messages.getString("SaveDocAction.saveErrorMsg"), //$NON-NLS-1$
						new Object[]{
								f0.getName(), 
								(f0.getParent()==null ? Messages.getString("SaveDocAction.unknownDir") : f0.getParent()), //$NON-NLS-1$
								e0.getLocalizedMessage(),
						}
				);
				JOptionPane.showMessageDialog(
						parent, 
						msg,
						Messages.getString("SaveDocAction.saveErrorTitle"), //$NON-NLS-1$
						JOptionPane.ERROR_MESSAGE);
			}
		});
	}
}
