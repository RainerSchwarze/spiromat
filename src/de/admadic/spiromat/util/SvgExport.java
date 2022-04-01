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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import de.admadic.spiromat.Globals;
import de.admadic.spiromat.model.DocModel;
import de.admadic.spiromat.model.FigureSpec;
import de.admadic.spiromat.shapes.FigureView;

/**
 * @author Rainer Schwarze
 *
 */
public class SvgExport {
	DocModel docModel;
	Color backgroundColor = Color.YELLOW;

	/**
	 * @param docModel
	 */
	public SvgExport(DocModel docModel) {
		super();
		this.docModel = docModel;
	}

	/**
	 * @param file 
	 * @throws FileNotFoundException 
	 * @throws UnsupportedEncodingException 
	 * @throws SVGGraphics2DIOException 
	 */
	public void export(File file) throws UnsupportedEncodingException, FileNotFoundException, SVGGraphics2DIOException {
		ArrayList<FigureView> figureViews = new ArrayList<FigureView>();

		figureViews.clear();
		int count = docModel.getFigureSpecCount();
		for (int i=0; i<count; i++) {
			FigureSpec fs = docModel.getFigureSpec(i);
			FigureView fv = new FigureView(fs);
			fv.setVisible(true);
			fv.setIgnoreActiveStatus(true);
			figureViews.add(fv);
		}

//		buffer = new BufferedImage(
//				Globals.MODEL_WIDTH, Globals.MODEL_HEIGHT,
//				(backgroundColor==null) ? 
//						BufferedImage.TYPE_INT_ARGB :
//						BufferedImage.TYPE_INT_RGB);
//		Graphics2D g = (Graphics2D) buffer.getGraphics();
//
//		g.setRenderingHint(
//				RenderingHints.KEY_ANTIALIASING, 
//				RenderingHints.VALUE_ANTIALIAS_ON);

        // Get a DOMImplementation.
        DOMImplementation domImpl =
            GenericDOMImplementation.getDOMImplementation();

        // Create an instance of org.w3c.dom.Document.
        String svgNS = "http://www.w3.org/2000/svg"; //$NON-NLS-1$
        Document document = domImpl.createDocument(svgNS, "svg", null); //$NON-NLS-1$

        // Create an instance of the SVG Generator.
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

        svgGenerator.translate(Globals.MODEL_WIDTH/2, Globals.MODEL_HEIGHT/2);

        // Ask the test to render into the SVG Graphics2D implementation.
        SVGGraphics2D g = svgGenerator;
//		if (backgroundColor!=null) {
//			g.setColor(backgroundColor);
//			g.fillRect(0, 0, Globals.MODEL_WIDTH, Globals.MODEL_HEIGHT);
//		}

		for (FigureView fv : figureViews) {
			fv.drawStayingParts(g);
		}
		for (FigureView fv : figureViews) {
			fv.drawVolatileParts(g);
		}

        // Finally, stream out SVG to the standard output using
        // UTF-8 encoding.
        boolean useCSS = true; // we want to use CSS style attributes
        Writer out = new OutputStreamWriter(new FileOutputStream(file), "UTF-8"); //$NON-NLS-1$
        svgGenerator.stream(out, useCSS);
	}
}
