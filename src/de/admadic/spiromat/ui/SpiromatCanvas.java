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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JPanel;

import de.admadic.spiromat.Globals;
import de.admadic.spiromat.log.Logger;
import de.admadic.spiromat.model.AppModel;
import de.admadic.spiromat.shapes.Drawable;
import de.admadic.spiromat.util.PrimitiveTimerProbe;

/**
 * Provides a drawing canvas.
 * This is the actual drawing canvas for the spiromat. 
 * 
 * @author Rainer Schwarze
 */
public class SpiromatCanvas extends JPanel {
	/** */
	private static final long serialVersionUID = 1L;
	final static Logger logger = Logger.getLogger(SpiromatCanvas.class);

	// FIXME: performance: when tooltips disappear a complete repaint is done, that could be optimized with checking the clip region!
	
	private Dimension dimensionForTransform = null;
	
	private AffineTransform canvasTransform;	// the transformation to apply to all drawn shapes.

	// FIXME: we might not need the backBuffer when we turn the volatileBackBuffer into the main backbuffer.
	// the backbuffers are written to from outside the EDT, also these
	// buffers have a size of Globals.MODEL_WIDTH x ..._HEIGHT.
	private BufferedImage stayingBackBuffer = null;
	private BufferedImage volatileBackBuffer = null;
	private boolean hasVirginBuffers = false;

	// the renderLock is used to protect data relevant for display:
	private Object renderLock = new Object();

	final Color BGCOLOR;
	
	/**
	 * Creates an instance of this class. This method initializes the list
	 * of Drawables, sets the background and registers the listeners for the 
	 * changes of component size.
	 */
	public SpiromatCanvas() {
		super();
		BGCOLOR = AppModel.getInstance().getColorCanvas();
		this.setBackground(BGCOLOR);
		this.setOpaque(true);
		// this.setIgnoreRepaint(true);
	}

	/**
	 * Creates new back buffer instances for the staying and the volatile 
	 * back buffer.
	 */
	void createNewBackBuffers() {
		logger.debug("creating new back buffers..."); //$NON-NLS-1$
		synchronized (renderLock) {
			PrimitiveTimerProbe tp1 = new PrimitiveTimerProbe(logger);
			if (stayingBackBuffer==null) {
				stayingBackBuffer = new BufferedImage(
						Globals.MODEL_WIDTH, Globals.MODEL_HEIGHT, 
						BufferedImage.TYPE_INT_RGB);
			}
			tp1.probeAndReset("created staying back buffer", PrimitiveTimerProbe.UNIT_US); //$NON-NLS-1$
			if (volatileBackBuffer==null) {
				volatileBackBuffer = new BufferedImage(
						Globals.MODEL_WIDTH, Globals.MODEL_HEIGHT, 
						BufferedImage.TYPE_INT_RGB);
			}
			tp1.probeAndReset("created volatile back buffer", PrimitiveTimerProbe.UNIT_US); //$NON-NLS-1$
			// we need to initialize the background of the staying backbuffer:
			{
				Graphics2D g = stayingBackBuffer.createGraphics();
				tp1.probeAndReset("retrieved graphics", PrimitiveTimerProbe.UNIT_US); //$NON-NLS-1$
				try {
					g.setColor(BGCOLOR);
					g.fillRect(0, 0, Globals.MODEL_WIDTH, Globals.MODEL_HEIGHT);
					tp1.probeAndReset("filled", PrimitiveTimerProbe.UNIT_US); //$NON-NLS-1$
				} finally {
					g.dispose();
					tp1.probeAndReset("disposed", PrimitiveTimerProbe.UNIT_US); //$NON-NLS-1$
				}
			}
			// mark all drawables as not-drawn
			hasVirginBuffers = true;
			tp1.probeAndReset("done", PrimitiveTimerProbe.UNIT_US); //$NON-NLS-1$
		}
	}

	/**
	 * @return	Returns true, if the backbuffer needs to be adjusted.
	 */
	private boolean needUpdateForSizeChange() {
		// rsc: unsure about this:
		if (dimensionForTransform==null && this.isDisplayable()) {
			return true;
		}
		// can't check in this case:
		if (dimensionForTransform==null) return false;

		if (
				dimensionForTransform.getWidth()!=this.getWidth() ||
				dimensionForTransform.getHeight()!=this.getHeight() 
			) {
			return true;
		}
		
		return false;
	}

	/**
	 * Update the instance when the size has changed.
	 */
	protected void updateForSizeChange() {
		logger.debug("update for size change..."); //$NON-NLS-1$
		dimensionForTransform = new Dimension(this.getWidth(), this.getHeight());
		updateTransformation();
	}

	/**
	 * Updates the transformation used for the drawing the shapes.
	 */
	protected synchronized void updateTransformation() {
		// sync: protect the transformation from being partially incorrect
		Graphics2D g = (Graphics2D) super.getGraphics();
		if (g==null) return;
		try {
			logger.debug("updating transformation"); //$NON-NLS-1$
			canvasTransform = AffineTransform.getTranslateInstance(0, 0);
			int w = this.getWidth();
			int h = this.getHeight();
			int x = Globals.MODEL_WIDTH;
			int y = Globals.MODEL_HEIGHT;
			double xsc = w / (double)Globals.MODEL_WIDTH;
			double ysc = h / (double)Globals.MODEL_HEIGHT;
			if (w>h) {	// landscape
				// the x-scaling is too large, factor it down:
				xsc *= (double)h/w;
				x /= (double)h/w;
			} else {	// portrait
				// the y-scaling is too large, factor it down:
				ysc *= (double)w/h;
				y /= (double)w/h;
			}
			
			canvasTransform.concatenate(AffineTransform.getScaleInstance(xsc, -ysc));
			canvasTransform.concatenate(AffineTransform.getTranslateInstance(x/2, -y/2));
		} finally {
			g.dispose();
		}
	}

	/**
	 * 
	 * @see java.awt.Container#doLayout()
	 */
	@Override
	public void doLayout() {
		logger.debug("doLayout..."); //$NON-NLS-1$
		super.doLayout();
		if (needUpdateForSizeChange())
			updateForSizeChange();
	}
	
	/**
	 * @param g
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int x = this.getX();
		int y = this.getY();
		int width = this.getWidth();
		int height = this.getHeight();
		if (logger.isDebugEnabled()) 
			logger.debug(
					"paint(): x=" + x + " y=" + y +  //$NON-NLS-1$ //$NON-NLS-2$
					" width=" + width + " height=" + height); //$NON-NLS-1$ //$NON-NLS-2$
		if (needUpdateForSizeChange())
			updateForSizeChange();

		synchronized (renderLock) {
			Graphics2D gb = (Graphics2D) g.create();
			try {
				gb.setPaintMode();
				gb.setClip(0, 0, width, height);
				gb.setColor(Color.WHITE);
				gb.fillRect(0, 0, width, height);

				gb.transform(canvasTransform);

				if (stayingBackBuffer!=null) {
					gb.drawImage(
							stayingBackBuffer, 
							-Globals.MODEL_WIDTH/2, -Globals.MODEL_HEIGHT/2,
							null);
				} else {
					if (logger.isDebugEnabled()) 
						logger.debug("stayingBackBuffer==null"); //$NON-NLS-1$
				}
				if (volatileBackBuffer!=null) {
					gb.drawImage(
							volatileBackBuffer, 
							-Globals.MODEL_WIDTH/2, -Globals.MODEL_HEIGHT/2,
							null);
				} else {
					if (logger.isDebugEnabled()) 
						logger.debug("volatileBackBuffer==null"); //$NON-NLS-1$
				}
			} finally {
				gb.dispose();
			}
		}
	}

	/**
	 * Prepares the render capabilities.
	 */
	public void prepareRender() {
		logger.debug("preparing render..."); //$NON-NLS-1$
		synchronized (renderLock) {
			createNewBackBuffers();
		}
	}

	/**
	 * Resets the render state, but does not reset the gears' data state.
	 */
	public void resetRender() {
		logger.debug("resetting render..."); //$NON-NLS-1$
		synchronized (renderLock) {
			createNewBackBuffers();	// drawables are reset in there too
		}
	}
	
	/**
	 * Stops the render capabilities.
	 */
	public void stopRender() {
		logger.debug("stopping render..."); //$NON-NLS-1$
		synchronized (renderLock) {
			// nothing actually
		}
	}

	/**
	 * Renders the canvas with its registered Drawables.
	 * Note: right now it simply issues a repaint(1). Thus it can be called
	 * from outside the EDT.
	 * @param drawables 
	 */
	public void render(List<Drawable> drawables) {
		logger.debug("rendering (and repaint'ing)..."); //$NON-NLS-1$
		// we need to protect renderImpl.
		synchronized (renderLock) {
			renderImpl(drawables);
		}
		repaint(1);
	}

	/**
	 * Renders the canvas with its registered Drawables into the 
	 * staying and the volatile back buffer. The staying backbuffer is painted
	 * first and is duplicated into the volatile backbuffer. Then the
	 * volatile painting for the volatile backbuffer is carried out.
	 * All that is done to save the efforts of drawing an image with 
	 * transparency which increases the duration easily by a factor of 3.
	 * 
	 * <b>Note: This method is not protected by a synchronized(renderLock)! Make
	 * sure that the method calling it takes care of the protection!</b>
	 */
	void renderImpl(List<Drawable> drawables) {
		if (stayingBackBuffer==null || volatileBackBuffer==null) {
			logger.error("no backbuffers have been created!"); //$NON-NLS-1$
			return;
		}

		if (hasVirginBuffers) {
			hasVirginBuffers = false;
			for (Drawable d : drawables) {
				d.drawReset();
			}
		}
		
		// this takes 60 us on my machine. As long as the drawing takes
		// about 6 ms and more, we can keep it inside the loop:
		Graphics2D gs = stayingBackBuffer.createGraphics();
		try {
			// we don't need background clearing because that's done when the
			// back buffer is created.
			if (AppModel.getInstance().getAntialiasing()) {
				gs.setRenderingHint(
						RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
			}
			gs.translate(Globals.MODEL_WIDTH/2, Globals.MODEL_HEIGHT/2);
			for (Drawable d : drawables) {
				if (logger.isDebugEnabled()) logger.debug("drawStayingParts: #=" + d); //$NON-NLS-1$
				d.drawStayingParts(gs);
			}
		} finally {
			gs.dispose();
		}

		// CHECKME: performance: if a Graphics.drawImage is faster than a copyData, change that code
		stayingBackBuffer.copyData(volatileBackBuffer.getRaster());
		
		// this takes 60 us on my machine. As long as the drawing takes
		// about 6 ms and more, we can keep it inside the loop:
		Graphics2D gv = volatileBackBuffer.createGraphics();
		try {
			if (AppModel.getInstance().getAntialiasing()) {
				gv.setRenderingHint(
						RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
			}
			gv.translate(Globals.MODEL_WIDTH/2, Globals.MODEL_HEIGHT/2);
			for (Drawable d : drawables) {
				if (logger.isDebugEnabled()) logger.debug("drawVolatileParts: #=" + d); //$NON-NLS-1$
				d.drawVolatileParts(gv);
			}
		} finally {
			gv.dispose();
		}
	}

	/**
	 * Transforms the given coordinates according to the canvas transformation.
	 * This is mainly used for the MouseControlledMachine.
	 * 
	 * @param a	An int[] with [0]=x, [1]=y of a point
	 */
	public void transformToCanvasSpace(int[] a) {
		double [] src = {a[0], a[1]};
		double [] dst = {0.0, 0.0};
		try {
			synchronized (this) {
				canvasTransform.inverseTransform(src, 0, dst, 0, 1);
			}
		} catch (NoninvertibleTransformException e) {
			// FIXME: this would be a support case
			// e.printStackTrace();
			dst = new double[]{0.0, 0.0};
		}
		a[0] = (int) dst[0];
		a[1] = (int) dst[1];
	}

	/**
	 * Clears the canvas.
	 */
	public void clear() {
		logger.debug("clearing (its a no-op!?)..."); //$NON-NLS-1$
		// we don't want to clear in the middle of a render call:
		synchronized (renderLock) {
			// maybe clear the back buffer?
			createNewBackBuffers();
		}
	}

	/**
	 * @return	Returns the component which actually receives the mouse events.
	 */
	public Component getCanvasComponent() {
		return this;
	}
}
