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

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.SerializationUtils;

import de.admadic.spiromat.SpiromatException;
import de.admadic.spiromat.log.Logger;
import de.admadic.spiromat.model.AppModel;
import de.admadic.spiromat.model.DocModel;
import de.admadic.spiromat.model.FigureSpec;
import de.admadic.spiromat.ui.BackgroundManager;
import de.admadic.spiromat.ui.FileChooserProvider;
import de.admadic.spiromat.ui.Util;
import de.admadic.spiromat.util.BitmapExport;
import de.admadic.spiromat.util.FileExport;
import de.admadic.spiromat.util.FileUtil;
import de.admadic.spiromat.util.SvgExport;

/**
 * @author Rainer Schwarze
 *
 */
public class ExportDocAction extends AbstractAction {
	static Logger logger = Logger.getLogger(ExportDocAction.class);

	/** */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public ExportDocAction() {
		super();
		putValue(Action.NAME, Messages.getString("ExportDocAction.name")); //$NON-NLS-1$
		putValue(Action.SHORT_DESCRIPTION, Messages.getString("ExportDocAction.shortDesc")); //$NON-NLS-1$
		putValue(Action.SMALL_ICON, Util.loadButtonImage("export.png")); //$NON-NLS-1$
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
		FileChooserProvider.setExportsFilter(fc);
		if (docModel.getFile()!=null) {
			// this case is active, if the file has been saved already
			fc.setSelectedFile(
					new File(
							FilenameUtils.removeExtension(
									docModel.getFile().getName()
							)
					)
			);
		} else if (fc.getSelectedFile()!=null) {
			// this case is active, if the file has not been saved already
			fc.setSelectedFile(
					new File(
							FilenameUtils.removeExtension(
									fc.getSelectedFile().getName()
							)
					)
			);
		}
	existLoop:
		while (true) {
			int res = fc.showSaveDialog(parent);
			if (res!=JFileChooser.APPROVE_OPTION) {
				return;
			}
			f = fc.getSelectedFile();
			f = FileChooserProvider.fixupExtension(fc, f);
			f = FileUtil.stripQuotes(f);
			if (!FileUtil.hasExtension(f, new String[]{".png", ".jpg", ".svg"}, true)) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				JOptionPane.showMessageDialog(
						parent, 
						Messages.getString("ExportDocAction.missingExtensionError") +  //$NON-NLS-1$
						f.toString() + "\n" + //$NON-NLS-1$
						Messages.getString("ExportDocAction.missingExtensionInfo"), //$NON-NLS-1$
						Messages.getString("ExportDocAction.missingExtensionTitle"), //$NON-NLS-1$
						JOptionPane.WARNING_MESSAGE);
				continue;	// repeat, select another file
			}
			if (!f.exists()) 
				break;

			String [] options = {
					Messages.getString("ExportDocAction.optionLabelOverwrite"), Messages.getString("ExportDocAction.optionLabelChooseOther"), Messages.getString("ExportDocAction.optionLabelCancel") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			};
			String msg = MessageFormat.format(
					Messages.getString("ExportDocAction.fileExistsError"),  //$NON-NLS-1$
					new Object[]{
							f.getName(), 
							(f.getParent()==null ? Messages.getString("ExportDocAction.unknownDirectory") : f.getParent())  //$NON-NLS-1$
					}
			);
			res = JOptionPane.showOptionDialog(
					parent, 
					msg,
					Messages.getString("ExportDocAction.fileExistsTitle"), //$NON-NLS-1$
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					options[1]);
			switch (res) {
			case JOptionPane.YES_OPTION: break existLoop;
			case JOptionPane.NO_OPTION: continue;	// repeat, select another file
			case JOptionPane.CANCEL_OPTION: return;
			}
		}

		FigureSpec tmpfs = (FigureSpec)SerializationUtils.clone(docModel.getActiveFigureSpec());
		
		byte [] cloneData = SerializationUtils.serialize(docModel);
		DocModel copy = (DocModel) SerializationUtils.deserialize(cloneData);
		Worker worker = new Worker(copy, f, parent);
		worker.execute();

	}

	static class Worker implements Runnable {
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
			Thread thread = new Thread(this, "export-thread"); //$NON-NLS-1$
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
				File f = file;
				String fname = f.getName();
				fname = fname.toLowerCase();
				if (fname.endsWith(".png")) { //$NON-NLS-1$
					BitmapExport be = new BitmapExport(docModel);
					BufferedImage bi = be.export();
					try {
						FileExport.export(bi, f, FileExport.Format.PNG);
					} catch (IOException e1) {
						logger.error("export error", e1); //$NON-NLS-1$
						showSaveError(parent, f, e1);
					}
				} else if (fname.endsWith(".jpg")) { //$NON-NLS-1$
					BitmapExport be = new BitmapExport(docModel, Color.WHITE);
					BufferedImage bi = be.export();
					try {
						FileExport.export(bi, f, FileExport.Format.JPEG);
					} catch (IOException e1) {
						logger.error("export error", e1); //$NON-NLS-1$
						showSaveError(parent, f, e1);
					}
				} else if (fname.endsWith(".svg")) { //$NON-NLS-1$
					SvgExport se = new SvgExport(docModel);
					try {
						se.export(f);
					} catch (IOException e1) {
						logger.error("export error", e1); //$NON-NLS-1$
						showSaveError(parent, f, e1);
					} catch (Exception e1) {
						logger.error("export error", e1); //$NON-NLS-1$
						showSaveError(parent, f, e1);
					}
				} else {
					throw new SpiromatException("The extension should have been blocked earlier"); //$NON-NLS-1$
				}
			} finally {
				BackgroundManager.getInstance().markDone(this);
			}
		}
	}

	/**
	 * @param parent 
	 * @param f0
	 * @param e1
	 */
	static protected void showSaveError(final JFrame parent, final File f0, final Exception e1) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				String msg = MessageFormat.format(
						Messages.getString("ExportDocAction.exportErrorMessage"),  //$NON-NLS-1$
						new Object[]{
								f0.getName(), 
								(f0.getParent()==null ? Messages.getString("ExportDocAction.exportErrorUnknownDir") : f0.getParent()), //$NON-NLS-1$
								e1.getLocalizedMessage(),
						}
				);
				JOptionPane.showMessageDialog(
						parent, 
						msg,
						Messages.getString("ExportDocAction.exportErrorTitle"), //$NON-NLS-1$
						JOptionPane.WARNING_MESSAGE);
			}
		});
	}
}
