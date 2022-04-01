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

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import de.admadic.spiromat.log.Logger;

/**
 * @author Rainer Schwarze
 *
 */
public class FramedPanel extends JPanel {
	final private Logger logger = Logger.getLogger(FramedPanel.class);

	/** */
	private static final long serialVersionUID = 1L;

	protected Image frameImage = null;
	int grid = 20;
	int fsz = 100;

	/**
	 * 
	 */
	public FramedPanel() {
		super();
		this.setLayout(new BorderLayout());
	}

	/**
	 * @param frameImage 
	 * @param grid
	 * @param fsz 
	 */
	public void setFrameImage(Image frameImage, int grid, int fsz) {
		this.frameImage = frameImage;
		this.grid = grid;
		this.fsz = fsz;
		this.setBorder(BorderFactory.createEmptyBorder(grid, grid, grid, grid));
	}

	/**
	 * @param g
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (frameImage==null) return;

		int width = this.getWidth();
		int height = this.getHeight();
		logger.debug("paint(): width=" + width + " height=" + height); //$NON-NLS-1$ //$NON-NLS-2$
		paintFrame((Graphics2D) g, width, height);
	}

	/**
	 * @param g
	 * @param height 
	 * @param width 
	 */
	protected void paintFrame(Graphics2D g, int width, int height) {
		/*
		 * image is:
		 * +----------+
		 * |          |
		 * |          |
		 * +----------+
		 * 
		 * We need the corners and the sides.
		 */
		Graphics2D g2 = g;
		// draw top left, top edge and top right segment:
		drawTile(
				g2, 
				0, 0, grid, grid, 
				0, 0, grid, grid);
		drawTile(
				g2, 
				grid, 0, width-grid, grid, 
				grid, 0, fsz-grid, grid);
		drawTile(
				g2, 
				width-grid, 0, width, grid, 
				fsz-grid, 0, fsz, grid);
	
		// draw left edge and right edge segment:
		drawTile(
				g2, 
				0, grid, grid, height-grid, 
				0, grid, grid, fsz-grid);
		drawTile(
				g2, 
				width-grid, grid, width, height-grid, 
				fsz-grid, grid, fsz, fsz-grid);
	
		// draw bottom left, bottom edge and bottom right segment:
		drawTile(
				g2, 
				0, height-grid, grid, height, 
				0, fsz-grid, grid, fsz);
		drawTile(
				g2, 
				grid, height-grid, width-grid, height, 
				grid, fsz-grid, fsz-grid, fsz);
		drawTile(
				g2, 
				width-grid, height-grid, width, height, 
				fsz-grid, fsz-grid, fsz, fsz);
	}

	/**
	 * Draws a "tile" of the frame image.
	 * 
	 * @param g
	 * @param xs
	 * @param ys
	 * @param xs2
	 * @param ys2
	 * @param xi
	 * @param yi
	 * @param xi2
	 * @param yi2
	 */
	private void drawTile(Graphics2D g, int xs, int ys, int xs2, int ys2, int xi, int yi, int xi2, int yi2) {
		g.setPaint(this.getBackground());
		g.fillRect(
				xs, ys, xs2-xs, ys2-ys);
		g.drawImage(
				frameImage, 
				xs, ys, xs2, ys2, 
				xi, yi, xi2, yi2, 
				this);
	}

}