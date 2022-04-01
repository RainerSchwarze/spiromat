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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * @author Rainer Schwarze
 *
 */
public class ColorGradient {
	protected class GradientItem implements Comparable {
		Double pos;
		Color color;
		/**
		 * @param pos
		 * @param color
		 */
		public GradientItem(Double pos, Color color) {
			super();
			this.pos = pos;
			this.color = color;
		}
		/**
		 * @param pos
		 * @param color
		 */
		public GradientItem(double pos, Color color) {
			super();
			this.pos = Double.valueOf(pos);
			this.color = color;
		}

		/**
		 * @return Returns the color.
		 */
		public Color getColor() {
			return color;
		}
		/**
		 * @param color The color to set.
		 */
		public void setColor(Color color) {
			this.color = color;
		}
		/**
		 * @return Returns the pos.
		 */
		public Double getPos() {
			return pos;
		}
		/**
		 * @param pos The pos to set.
		 */
		public void setPos(Double pos) {
			this.pos = pos;
		}

		/**
		 * @param o
		 * @return	Returns +/-1 if this is greater/smaller than o
		 * @see java.lang.Comparable#compareTo(Object)
		 */
		public int compareTo(Object o) {
			Double p;
			if (o instanceof Double) {
				p = (Double)o;
			} else {
				p = ((GradientItem)o).pos;
			}
			if (p.doubleValue()<this.pos.doubleValue()) return +1;
			if (p.doubleValue()>this.pos.doubleValue()) return -1;
			return 0;
		}
	}

	Vector<GradientItem> gradientItems;

	/**
	 * 
	 */
	public ColorGradient() {
		super();
		gradientItems = new Vector<GradientItem>();
	}

	/**
	 * @param c0
	 * @param c1
	 */
	public ColorGradient(Color c0, Color c1) {
		super();
		gradientItems = new Vector<GradientItem>();
		setColor(0.0, c0);
		setColor(1.0, c1);
	}

	/**
	 * Sample:
	 * new ColorGradient(
	 *     new Object[][] {
	 *        {Double.valueOf(0.0), Color.RED},
	 *        {Double.valueOf(0.1), Color.BLUE},
	 *        {Double.valueOf(0.3), Color.GREEN},
	 *        {Double.valueOf(1.0), Color.YELLOW},
	 *     }
	 * );
	 * Note: the Double and the Color are cast from the Object elements.
	 * If that fails the appropriate exceptions will be thrown.
	 * @param posColorList
	 */
	public ColorGradient(Object [][] posColorList) {
		super();
		gradientItems = new Vector<GradientItem>();
		for (int i=0; i<posColorList.length; i++) {
			Object [] oa = posColorList[i];
			Double d = (Double)oa[0];
			Color c = (Color)oa[1];
			setColorImpl(d, c);
		}
		sortImpl();
	}

	/**
	 * @return	A ColorGradient with data 'jet'
	 */
	public static ColorGradient JET() {
        ColorGradient cg = new ColorGradient(
        		new Object[][] {
        			{Double.valueOf(0.0/8), Color.decode("#0000C0")},
        			{Double.valueOf(1.0/8), Color.decode("#0000FF")},
        			{Double.valueOf(3.0/8), Color.decode("#00FFFF")},
        			{Double.valueOf(5.0/8), Color.decode("#FFFF00")},
        			{Double.valueOf(7.0/8), Color.decode("#FF0000")},
        			{Double.valueOf(8.0/8), Color.decode("#C00000")},
        		}
        );
		return cg;
	}
	
	/**
	 * @param pos
	 * @param c
	 */
	public void setColor(double pos, Color c) {
		setColor(Double.valueOf(pos), c);
	}

	/**
	 * @param pos
	 * @param c
	 */
	public void setColor(Double pos, Color c) {
		setColorImpl(pos, c);
		sortImpl();
	}

	/**
	 * @param pos
	 * @return	Returns the color interpolated for the position.
	 */
	public Color calculateColorAtPos(double pos) {
		/*
		 * Results of binarySearch:
		 * list = 0.10 0.50 0.80 0.90 
		 * binS: 0.50 -> 1
		 * binS: 0.40 -> -2
		 * binS: 0.60 -> -3
		 * binS: 0.00 -> -1
		 * binS: 1.00 -> -5
		 */
		Color c = null;
		GradientItem gi, gi2;
		int idx = Collections.binarySearch((List)gradientItems, Double.valueOf(pos));
		if (idx>=0) { 
			// exists, direct hit:
			gi = gradientItems.elementAt(idx);
			gi2 = null;
		} else {
			idx = (-idx) - 1;
			if (idx==0) {
				gi = gradientItems.firstElement();
				gi2 = null;
			} else if (idx==gradientItems.size()) {
				gi = gradientItems.lastElement();
				gi2 = null;
			} else {
				gi = gradientItems.elementAt(idx-1);
				gi2 = gradientItems.elementAt(idx);
			}
		}
		if (gi2==null) {
			c = new Color(gi.getColor().getRGB());
		} else {
			Color c0 = gi.getColor();
			Color c1 = gi2.getColor();
			double d0 = gi.getPos().doubleValue();
			double d1 = gi2.getPos().doubleValue();
			c = interpolateColor(d0, d1, pos, c0, c1);
		}
		
		return c;
	}

	private float interpolate(double d0, double d1, double d, float y0, float y1) {
		return (float)(y0 + (y1 - y0)*(d - d0)/(d1 - d0));
	}
	
	private Color interpolateColor(double d0, double d1, double d, Color c0, Color c1) {
		float [] rgb0 = c0.getRGBColorComponents(null);
		float [] rgb1 = c1.getRGBColorComponents(null);
		float [] rgbx = new float[3];

		for (int i=0; i<3; i++) {
			rgbx[i] = interpolate(d0, d1, d, rgb0[i], rgb1[i]);
		}
		
		return new Color(rgbx[0], rgbx[1], rgbx[2]);
	}

	private void setColorImpl(Double pos, Color c) {
		gradientItems.add(new GradientItem(pos, c));
	}

	private void sortImpl() {
		Collections.sort(gradientItems);
	}
}
