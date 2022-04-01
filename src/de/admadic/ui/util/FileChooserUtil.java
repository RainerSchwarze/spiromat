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
package de.admadic.ui.util;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

/**
 * @author Rainer Schwarze
 *
 */
public class FileChooserUtil {
	/** open mode */
	public final static int OPEN = 1<<0;
	/** save mode */
	public final static int SAVE = 1<<1;
	/** ask overwrite */
	public final static int ASKOVERWRITE = 1<<2;

	/**
	 * 
	 */
	public FileChooserUtil() {
		super();
	}

	/**
	 * Selects a module file which can be a JAR or a ZIP.
	 * @param parent
	 * @param extension 
	 * @param flags 
	 * @return	Returns the selected file, or null, if none is found.
	 */
	public static File getFileWithExt(Component parent, final String extension, int flags) {
		class GenExtFilter extends FileFilter {
		    @Override
			public boolean accept(File f) {
		        if (f.isDirectory()) {
		            return true;
		        }
		        String ext = null;
		        String s = f.getName();
		        int i = s.lastIndexOf('.');
		        if (i > 0 &&  i < s.length() - 1) {
		            ext = s.substring(i+1).toLowerCase();
		        }
		        if (ext != null) {
		        	boolean retcode = false;
		        	if (ext.equals(extension)) return true;
		            return retcode;
		        }
		        return false;
		    }

		    //The description of this filter
		    @Override
			public String getDescription() {
		        return extension.toUpperCase() + " Files (*." + extension + ")";
		    }
		}

		return getFile(parent, new GenExtFilter(), flags);
	}
	
	/**
	 * @param parent 
	 * @param ff
	 * @param flags 
	 * @return	Returns a file selected by the user.
	 */
	public static File getFile(Component parent, FileFilter ff, int flags) {
		File file = null; 
		JFileChooser fc;
		fc = new JFileChooser();
		fc.addChoosableFileFilter(ff);
		fc.setFileHidingEnabled(false);
		int result;
	repeat: 
		while (true) {
			if ((flags & SAVE)!=0) {
				result = fc.showSaveDialog(parent);
			} else {
				result = fc.showOpenDialog(parent);
			}
			switch (result) {
			case JFileChooser.APPROVE_OPTION: // open or save
				file = fc.getSelectedFile();
				if ((flags & (SAVE | ASKOVERWRITE))!=0) {
					if (file.exists()) {
						int rc = JOptionPane.showConfirmDialog(
								parent,
								"The selected file already exists."+
								" Do you want to overwrite it?\n"+
								"file = " + file.getPath(),
								"Confirm Overwrite",
								JOptionPane.YES_NO_CANCEL_OPTION);
						switch (rc) {
						case JOptionPane.YES_OPTION:
							return file;
						case JOptionPane.NO_OPTION:
							return null;
						case JOptionPane.CANCEL_OPTION: // fall through
						default:
							// retry
							continue repeat;
						}
					}
				}
				return file;
				// unreachable
				// break;
			case JFileChooser.CANCEL_OPTION:
				break;
			case JFileChooser.ERROR_OPTION:
				break;
			}
			return null;
		}
	}
}
