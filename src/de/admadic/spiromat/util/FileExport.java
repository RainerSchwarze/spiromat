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
package de.admadic.spiromat.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;

import de.admadic.spiromat.SpiromatException;

/**
 * @author Rainer Schwarze
 *
 */
public class FileExport {
	/**
	 * Format enumeration for export file format.
	 * 
	 * @author Rainer Schwarze
	 */
	public static enum Format {
		/** automatic, detect by file extension */
		AUTO,
		/** JPeg Format */
		JPEG,
		/** Portable Network Graphics Format */
		PNG,
	}

	/**
	 * @param image
	 * @param destFile
	 * @param format
	 * @throws IOException 
	 */
	static public void export(BufferedImage image, File destFile, Format format) throws IOException {
		String formatName = null;
		switch (format) {
		case AUTO: formatName = getFormatFromFileName(destFile); break;
		case JPEG: formatName = "jpg"; break; //$NON-NLS-1$
		case PNG: formatName = "png"; break; //$NON-NLS-1$
		default:
			throw new SpiromatException("unsupported export format: " + format); //$NON-NLS-1$
		}
		ImageIO.write(image, formatName, destFile);
	}

	/**
	 * @param destFile
	 * @return	Return format string based on filename extension.
	 */
	static private String getFormatFromFileName(File destFile) {
		String ext = FilenameUtils.getExtension(destFile.getName());
		if (ext.toLowerCase().equals("jpg") || ext.toLowerCase().equals("jpeg")) { //$NON-NLS-1$ //$NON-NLS-2$
			return "jpg"; //$NON-NLS-1$
		} else if (ext.toLowerCase().equals("png")) { //$NON-NLS-1$
			return "png"; //$NON-NLS-1$
		}
		throw new SpiromatException("could not detect graphics format from file extension: file=" + destFile.toString());			 //$NON-NLS-1$
	}
}
