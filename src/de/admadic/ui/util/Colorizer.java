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
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

import javax.swing.ImageIcon;

/**
 * @author Rainer Schwarze
 *
 */
public class Colorizer {
	static class FColor {
		double red;
		double green;
		double blue;
		double alpha;

		/**
		 * 
		 */
		public FColor() {
			this(0.0, 0.0, 0.0);
		}

		/**
		 * @param fc
		 */
		public FColor(FColor fc) {
			super();
			red = fc.red;
			green = fc.green;
			blue = fc.blue;
			alpha = fc.alpha;
		}

		/**
		 * @param red
		 * @param green
		 * @param blue
		 * @param alpha
		 */
		public FColor(double red, double green, double blue, double alpha) {
			super();
			this.red = red;
			this.green = green;
			this.blue = blue;
			this.alpha = alpha;
		}

		/**
		 * @param red
		 * @param green
		 * @param blue
		 */
		public FColor(double red, double green, double blue) {
			this(red, green, blue, 1.0);
		}

		/**
		 * @param c
		 */
		public FColor(Color c) {
			super();
			red = c.getRed() / 255.0;
			green = c.getGreen() / 255.0;
			blue = c.getBlue() / 255.0;
			alpha = 1.0;
		}
		
		/**
		 * @return	Returns the luminance value
		 */
		public double getLuminance() {
			return 0.3*red + 0.59*green + 0.11*blue;
		}

		/**
		 * @param v
		 */
		public void mul(double v) {
			red *= v;
			green *= v;
			blue *= v;
		}

		/**
		 * 
		 */
		public void spread() {
			red *= 1/0.2;
			green *= 1/0.69;
			blue *= 1/0.11;
		}

		/**
		 * @param cv
		 */
		public void mul(FColor cv) {
			red *= cv.red;
			green *= cv.green;
			blue *= cv.blue;
		}

		/**
		 * @param v
		 */
		public void add(double v) {
			red += v;
			green += v;
			blue += v;
		}

		/**
		 * @param cv
		 */
		public void add(FColor cv) {
			red += cv.red;
			green += cv.green;
			blue += cv.blue;
		}

		/**
		 * 
		 */
		public void normalize() {
			double ceil = 0.0;
			double floor = 0.0;
			if (red>1.0) ceil = Math.max(ceil, red-1.0);
			if (green>1.0) ceil = Math.max(ceil, green-1.0);
			if (blue>1.0) ceil = Math.max(ceil, blue-1.0);

			if (red<0.0) floor = Math.min(floor, red);
			if (green<0.0) floor = Math.min(floor, green);
			if (blue<0.0) floor = Math.min(floor, blue);

			if (ceil>0.0 || floor<0.0) {
				red = (red - floor)/(ceil - floor + 1.0);
				green = (green - floor)/(ceil - floor + 1.0);
				blue = (blue - floor)/(ceil - floor + 1.0);
			}
		}

		/**
		 * @return	Returns a Color instance representing this color value.
		 */
		public Color generateColor() {
			return new Color((int)(red*255), (int)(green*255), (int)(blue*255));
		}
	}

	/**
	 * 
	 */
	protected Colorizer() {
		super();
	}

	// FIXME: optimize Colorizer
	static protected Color adjustColor_Impl(
			Color bg, Color fg, Color cfg, double grade) {
		FColor f_bg = null, f_fg, f_cfg, f_rfg;
		double lbg, lfg, lcfg;
		double ldelta, lcdelta;

		if (bg!=null) f_bg = new FColor(bg);
		f_fg = new FColor(fg);
		f_cfg = new FColor(cfg);

		f_rfg = new FColor(f_fg);

		lbg = (f_bg!=null) ? f_bg.getLuminance() : 0.5;
		lfg = f_fg.getLuminance();
		lcfg = f_cfg.getLuminance();

		// ldelta = Math.abs(lfg - lbg);
		// ldelta = Math.abs(lfg);
		ldelta = 1.0;
		lcdelta = ldelta * grade;

//		f_cfg.add(-0.5);
		f_cfg.add(-lcfg);
		f_cfg.mul(lcdelta);
		f_cfg.spread();
		f_rfg.add(f_cfg);
		f_rfg.normalize();

		return f_rfg.generateColor();
	}
	
	/**
	 * @param bg
	 * @param fg
	 * @param cfg
	 * @return	Returns the adjusted Color.
	 */
	public static Color adjustColor(Color bg, Color fg, Color cfg) {
		return adjustColor(bg, fg, cfg, 0.25);
	}

	/**
	 * @param bg
	 * @param fg
	 * @param cfg
	 * @param grade
	 * @return	Returns the adjusted Color.
	 */
	public static Color adjustColor(Color bg, Color fg, Color cfg, double grade) {
		return adjustColor_Impl(bg, fg, cfg, grade);
	}

	/**
	 * @param img
	 * @param bg
	 * @param fg
	 * @param force
	 * @return	Returns an image with adjusted color.
	 */
	public static ImageIcon adjustImageColor(
			Image img, Color bg, Color fg, Color force) {
		return adjustImageColor_Impl(img, bg, fg, force, 0.25);
	}

	/**
	 * @param img
	 * @param bg
	 * @param fg
	 * @param force
	 * @param grade 
	 * @return	Returns an image with adjusted color.
	 */
	public static ImageIcon adjustImageColor(
			Image img, Color bg, Color fg, Color force, double grade) {
		return adjustImageColor_Impl(img, bg, fg, force, grade);
	}

	protected static ImageIcon adjustImageColor_Impl(
			Image img, Color bg, Color fg, Color force, double grade) {
		Hashtable<Color,Color> colorHash = new Hashtable<Color,Color>();
		int w, h;
		w = img.getWidth(null);
		h = img.getHeight(null);
		BufferedImage bi = new BufferedImage(
				w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = bi.createGraphics();
		g2.drawImage(img, 0, 0, null);
		WritableRaster r = bi.getRaster();
		int [] pixel = new int[4];	// rgb+a
		Color ac, sc;
		for (int y=0; y<h; y++) {
			for (int x=0; x<w; x++) {
				pixel = r.getPixel(x, y, pixel);
				sc = new Color(pixel[0], pixel[1], pixel[2]);
				if (force!=null) {
					sc = force;
				}
				if (colorHash.containsKey(sc)) {
					ac = colorHash.get(sc);
				} else {
					ac = adjustColor(bg, fg, sc, grade);
					if (colorHash.size()<100) {
						colorHash.put(sc, ac);
					}
				}
				pixel[0] = ac.getRed();
				pixel[1] = ac.getGreen();
				pixel[2] = ac.getBlue();
				// keep alpha
				r.setPixel(x, y, pixel);
			}
		}
		ImageIcon ii = new ImageIcon(bi);
		colorHash = null;
		return ii;
	}

	/**
	 * @param img
	 * @param adjustColor
	 * @param bg
	 * @param fg
	 * @param force
	 * @return	Returns an ImageIcon
	 */
	public static ImageIcon makeIcon(Image img, boolean adjustColor, Color bg, Color fg, Color force) {
		ImageIcon ii;
		if (adjustColor) {
			ii = Colorizer.adjustImageColor(img, bg, fg, force, 0.07);
		} else {
			ii = new ImageIcon(img);
		}
		return ii;
	}
}
