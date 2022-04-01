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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import de.admadic.spiromat.Globals;
import de.admadic.spiromat.model.DocModel;
import de.admadic.spiromat.model.FigureSpec;
import de.admadic.spiromat.shapes.FigureView;

/**
 * @author Rainer Schwarze
 *
 */
public class BitmapExport {
	DocModel docModel;
	Color backgroundColor = null;	// null==transparent

	/**
	 * @param docModel
	 */
	public BitmapExport(DocModel docModel) {
		this(docModel, null);
	}

	/**
	 * @param docModel
	 * @param backgroundColor 
	 */
	public BitmapExport(DocModel docModel, Color backgroundColor) {
		super();
		this.docModel = docModel;
		this.backgroundColor = backgroundColor;
	}

	/**
	 * @return	Returns the image which has been created.
	 */
	public BufferedImage export() {
		ArrayList<FigureView> figureViews = new ArrayList<FigureView>();
		BufferedImage buffer;

		figureViews.clear();
		int count = docModel.getFigureSpecCount();
		for (int i=0; i<count; i++) {
			FigureSpec fs = docModel.getFigureSpec(i);
			FigureView fv = new FigureView(fs);
			fv.setVisible(true);
			fv.setIgnoreActiveStatus(true);
			figureViews.add(fv);
		}

		buffer = new BufferedImage(
				Globals.MODEL_WIDTH, Globals.MODEL_HEIGHT,
				(backgroundColor==null) ? 
						BufferedImage.TYPE_INT_ARGB :
						BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) buffer.getGraphics();
		if (backgroundColor!=null) {
			g.setColor(backgroundColor);
			g.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());
		}

		g.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING, 
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.translate(Globals.MODEL_WIDTH/2, Globals.MODEL_HEIGHT/2);

		for (FigureView fv : figureViews) {
			fv.drawStayingParts(g);
		}
		for (FigureView fv : figureViews) {
			fv.drawVolatileParts(g);
		}
		return buffer;
	}
}
