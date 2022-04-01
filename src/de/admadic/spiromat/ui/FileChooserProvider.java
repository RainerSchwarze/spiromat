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

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.filefilter.SuffixFileFilter;

import de.admadic.spiromat.log.Logger;

/**
 * @author Rainer Schwarze
 *
 */
public class FileChooserProvider {
	static Logger logger = Logger.getLogger(FileChooserProvider.class);

	static Object fcLock = new Object();
	static JFileChooser theFileChooser = null;
	static Thread thread;
	static boolean prepareStarted = false;

	/**
	 * 
	 */
	public static void prepareInBackground() {
		logger.trace("prepare started"); //$NON-NLS-1$
		synchronized (fcLock) {
			if (prepareStarted) return;
			prepareStarted = true;
			logger.trace("prepare started for sure"); //$NON-NLS-1$
		}

		thread = new Thread(new Runnable() {
			public void run() {
				try {
					logger.trace("thread: creating new JFileChooser"); //$NON-NLS-1$
					JFileChooser fc = new JFileChooser();
					logger.trace("thread: done."); //$NON-NLS-1$
					if (Thread.currentThread().isInterrupted()) {
						logger.trace("thread: interrupted!"); //$NON-NLS-1$
						return;
					}
					synchronized (fcLock) {
						logger.trace("thread: storing the instance and notifying"); //$NON-NLS-1$
						theFileChooser = fc;
						fcLock.notifyAll();
					}
				} finally {
					logger.trace("thread: cleaning thread field"); //$NON-NLS-1$
					thread = null;
				}
			}
		}, "JFileChooser loader"); //$NON-NLS-1$
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * @return	Returns the file chooser instance.
	 */
	public static JFileChooser getFileChooser() {
		if (theFileChooser==null) {
			logger.trace("auto creating..."); //$NON-NLS-1$
			createFileChooser();
		}
		return theFileChooser;
	}

	/**
	 * 
	 */
	private static void createFileChooser() {
		logger.trace("creating new JFileChooser"); //$NON-NLS-1$
		if (theFileChooser!=null) {
			logger.trace("an instance is already there - bailing out..."); //$NON-NLS-1$
			return;
		}

		synchronized (fcLock) {
			if (!prepareStarted) {
				logger.trace("not yet prepared, calling prepare..."); //$NON-NLS-1$
				prepareInBackground();
			}
			if (theFileChooser!=null) {
				logger.trace("filechooser already there, don't need waiting..."); //$NON-NLS-1$
				return;
			}

			logger.trace("waiting for the instance."); //$NON-NLS-1$
			while (theFileChooser==null) {
				try {
					logger.trace("wait()"); //$NON-NLS-1$
					fcLock.wait();
				} catch (InterruptedException e) {
					// FIXME: Auto-generated catch block
					logger.trace("error waiting", e); //$NON-NLS-1$
					e.printStackTrace();
					return;
				}
			}
			logger.trace("ok. we have an instance now..."); //$NON-NLS-1$
		}
	}

	/**
	 * @param fc 
	 * 
	 */
	public static void setSpmFilter(JFileChooser fc) {
		FileFilter[] ff = fc.getChoosableFileFilters();
		for (FileFilter f : ff) {
			fc.removeChoosableFileFilter(f);
		}
		fc.setAcceptAllFileFilterUsed(false);
		fc.addChoosableFileFilter(new GenericFilter(
				".spm", Messages.getString("FileChooserProvider.fileDescSpm"))); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * @param fc
	 */
	public static void setExportsFilter(JFileChooser fc) {
		FileFilter[] ff = fc.getChoosableFileFilters();
		for (FileFilter f : ff) {
			fc.removeChoosableFileFilter(f);
		}
		fc.setAcceptAllFileFilterUsed(false);
		fc.addChoosableFileFilter(new GenericFilter(
				".png", Messages.getString("FileChooserProvider.fileDescPng"))); //$NON-NLS-1$ //$NON-NLS-2$
		fc.addChoosableFileFilter(new GenericFilter(
				".jpg", Messages.getString("FileChooserProvider.fileDescJpg"))); //$NON-NLS-1$ //$NON-NLS-2$
		fc.addChoosableFileFilter(new GenericFilter(
				".svg", Messages.getString("FileChooserProvider.fileDescSvg"))); //$NON-NLS-1$ //$NON-NLS-2$
	}

	static class GenericFilter extends FileFilter {
		SuffixFileFilter ff;

		String extension;
		String description;

		/**
		 * @param extension
		 * @param description
		 */
		public GenericFilter(String extension, String description) {
			super();
			this.extension = extension;
			this.description = description;
			ff = new SuffixFileFilter(this.extension);
		}


		/**
		 * @param f
		 * @return	Returns true, if this filter accepts this file.
		 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
		 */
		@Override
		public boolean accept(File f) {
			if (f.isDirectory()) return true;
			return ff.accept(f);
		}

		/**
		 * @return	Returns a String containing the description of this file filter.
		 * @see javax.swing.filechooser.FileFilter#getDescription()
		 */
		@Override
		public String getDescription() {
			return description;
		}

		/**
		 * @return the extension
		 */
		public String getExtension() {
			return extension;
		}
	}

	/**
	 * Returns a File instance with corrected extension. If the filename was
	 * entered in quotes, the file is returned as it is.
	 * If it has no extension which matches the selected filefilter, that
	 * selected extension is appended.
	 * 
	 * @param fc
	 * @param f
	 * @return	Returns a File instance with corrected extension.
	 */
	public static File fixupExtension(JFileChooser fc, File f) {
		String fname = f.getName();
		if (fname.startsWith("\"") && fname.endsWith("\"")) { //$NON-NLS-1$ //$NON-NLS-2$
			return f;
		}
		FileFilter ff = fc.getFileFilter();
		if (!(ff instanceof GenericFilter)) {
			return f;
		}
		GenericFilter gff = (GenericFilter) ff;
		String ext = gff.getExtension().toLowerCase();
		if (f.getName().endsWith(ext)) {
			return f;
		}
		return new File(f.getParentFile(), f.getName() + ext);
	}

}
