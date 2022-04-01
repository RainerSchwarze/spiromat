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
package de.admadic.spiromat.model;

import de.admadic.spiromat.log.Logger;
import de.admadic.spiromat.math.SpiroMath;
import de.admadic.spiromat.math.Util;

/**
 * @author Rainer Schwarze
 *
 */
public class FigureModel {
	final static Logger logger = Logger.getLogger(FigureModel.class);

	SpiroMath spiroMath;

	double phiMin;
	double phiMax;
	double phiSpan;

	float [] xAry;
	float [] yAry;
	int [] xAryInt;
	int [] yAryInt;
	int startIndex;
	int endIndex;

	int drawnStartIndex = -1;
	int drawnEndIndex = -1;

	double deltaPhi = Math.PI * 2 / 50;

//	private boolean visible = true;

	/**
	 * @param rBig 
	 * @param rSmall 
	 * @param lambda 
	 * 
	 */
	public FigureModel(double rBig, double rSmall, double lambda) {
		this(rBig, rSmall, lambda, 0.0);
	}

	/**
	 * @param rBig 
	 * @param rSmall 
	 * @param lambda 
	 * @param phiStart 
	 */
	public FigureModel(double rBig, double rSmall, double lambda, double phiStart) {
		super();
		setParameters(rBig, rSmall, lambda, phiStart);
	}

	/**
	 * @param rBig 
	 * @param rSmall 
	 * @param lambda 
	 * @param phiStart 
	 */
	public void setParameters(double rBig, double rSmall, double lambda, double phiStart) {
		logger.debug("setting new parameters... (clearing data!)"); //$NON-NLS-1$
		spiroMath = new SpiroMath(rBig, rSmall, lambda);

		phiMin = phiStart;
		phiMax = phiStart;
		phiSpan = Util.calculateRounds((int)rBig, (int)rSmall) * Math.PI * 2;

		xAry = new float[17];
		yAry = new float[17];
		xAryInt = new int[17];
		yAryInt = new int[17];
		startIndex = 5;
		endIndex = 5;

		drawnStartIndex = -1;
		drawnEndIndex = -1;
	}
	

	/**
	 * @param phi
	 * @return Returns true, if the list of points was changed, otherwise false.
	 */
	public synchronized boolean addPoints(double phi) {
		logger.debug("addPoints: phi=" + phi); //$NON-NLS-1$
		if (phi>=phiMin && phi<=phiMax) {
			return false; // already there
		}
		if (phiMax - phiMin >= phiSpan) {
			return false;
		}

		if (phi<phiMin) {
			// go downwards:
			for (double phiTmp = phiMin; phiTmp > phi; phiTmp -= deltaPhi) {
				addPoint(phiTmp, false);
			}
			addPoint(phi, false);
		} else {
			// go upwards:
			for (double phiTmp = phiMax; phiTmp < phi; phiTmp += deltaPhi) {
				addPoint(phiTmp, true);
			}
			addPoint(phi, true);
		}
		if (phi<phiMin) phiMin = phi;
		if (phi>phiMax) phiMax = phi;
		return true;
	}

	/**
	 * @param phi
	 * @param toEnd append to end or to front
	 */
	private void addPoint(double phi, boolean toEnd) {
		spiroMath.calculate(phi);
		
		double curx = spiroMath.getFigureX();
		double cury = spiroMath.getFigureY();

		// logger.debug("adding " + curx + "/" + cury + " toEnd=" + toEnd);
		
		if (toEnd) {
			if (endIndex>=xAryInt.length) {
				growEnd();
			}
			xAry[endIndex] = (float) curx;
			yAry[endIndex] = (float) cury;
			xAryInt[endIndex] = (int) curx;
			yAryInt[endIndex] = (int) cury;
			endIndex++;
		} else {
			if (startIndex==0) {
				growStart();
			}
			startIndex--;
			xAry[startIndex] = (float) curx;
			yAry[startIndex] = (float) cury;
			xAryInt[startIndex] = (int) curx;
			yAryInt[startIndex] = (int) cury;
		}
		
	}

	/**
	 * 
	 */
	private void growStart() {
		int extension = xAryInt.length / 4;
		int newsize = xAryInt.length + extension;

		if (logger.isDebugEnabled()) logger.debug("growing start by " + extension + " elements"); //$NON-NLS-1$ //$NON-NLS-2$

		int [] tmpxi = new int[newsize];
		int [] tmpyi = new int[newsize];
		float [] tmpxf = new float[newsize];
		float [] tmpyf = new float[newsize];

		System.arraycopy(xAryInt, startIndex, tmpxi, extension + startIndex, endIndex - startIndex);
		System.arraycopy(yAryInt, startIndex, tmpyi, extension + startIndex, endIndex - startIndex);
		System.arraycopy(xAry, startIndex, tmpxf, extension + startIndex, endIndex - startIndex);
		System.arraycopy(yAry, startIndex, tmpyf, extension + startIndex, endIndex - startIndex);

		startIndex += extension;
		endIndex += extension;
		xAryInt = tmpxi;
		yAryInt = tmpyi;
		xAry = tmpxf;
		yAry = tmpyf;
		if (drawnStartIndex>=0) {
			drawnStartIndex += extension;
			drawnEndIndex += extension;
		}
	}

	/**
	 * 
	 */
	private void growEnd() {
		int extension = xAryInt.length / 4;
		int newsize = xAryInt.length + extension;

		if (logger.isDebugEnabled()) logger.debug("growing end by " + extension + " elements"); //$NON-NLS-1$ //$NON-NLS-2$

		int [] tmpxi = new int[newsize];
		int [] tmpyi = new int[newsize];
		float [] tmpxf = new float[newsize];
		float [] tmpyf = new float[newsize];

		System.arraycopy(xAryInt, startIndex, tmpxi, startIndex, endIndex - startIndex);
		System.arraycopy(yAryInt, startIndex, tmpyi, startIndex, endIndex - startIndex);
		System.arraycopy(xAry, startIndex, tmpxf, startIndex, endIndex - startIndex);
		System.arraycopy(yAry, startIndex, tmpyf, startIndex, endIndex - startIndex);

		xAryInt = tmpxi;
		yAryInt = tmpyi;
		xAry = tmpxf;
		yAry = tmpyf;
	}

	/**
	 * Access this function with a synchronized block locking on this 
	 * FigureModel instance!
	 *  
	 * @return	Returns the x coordinates of the figures points.
	 */
	public int[] getPointsX() {
		int [] tmp = new int[endIndex - startIndex];
		System.arraycopy(xAryInt, startIndex, tmp, 0, endIndex - startIndex);
		return tmp;
	}

	/**
	 * Access this function with a synchronized block locking on this 
	 * FigureModel instance!
	 * 
	 * @return	Returns the y coordinates of the figures points.
	 */
	public int[] getPointsY() {
		int [] tmp = new int[endIndex - startIndex];
		System.arraycopy(yAryInt, startIndex, tmp, 0, endIndex - startIndex);
		return tmp;
	}

	/**
	 * Access this function with a synchronized block locking on this 
	 * FigureModel instance!
	 * 
	 * @return	Returns true, if new data is there at the start.
	 */
	public boolean isDirtyAtStart() {
		return drawnStartIndex!=startIndex;
	}

	/**
	 * Access this function with a synchronized block locking on this 
	 * FigureModel instance!
	 * 
	 * @return	Returns true, if new data is there at the end.
	 */
	public boolean isDirtyAtEnd() {
		return drawnEndIndex!=endIndex;
	}

	/**
	 * Access this function with a synchronized block locking on this 
	 * FigureModel instance!
	 * @return	Returns the x data which has not yet been drawn.
	 */
	public int[] getDirtyAtStartPointsX() {
		if (drawnStartIndex<0) {
			logger.debug("not drawn yet, returning nothing"); //$NON-NLS-1$
			return new int[0];
		}
		if (drawnStartIndex==startIndex) {
			logger.debug("no new data, returning nothing"); //$NON-NLS-1$
			return new int[0];
		}
		int count = drawnStartIndex - startIndex + 1;
		if (endIndex==drawnStartIndex) count--;
		if (logger.isDebugEnabled()) 
			logger.debug("returning " + count + " elements, si,ei,dsi,dei=" + //$NON-NLS-1$ //$NON-NLS-2$
				startIndex + "," + endIndex + "," + drawnStartIndex + "," + drawnEndIndex); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		int [] tmp = new int[count];
		System.arraycopy(xAryInt, startIndex, tmp, 0, count);
		return tmp;
	}

	/**
	 * Access this function with a synchronized block locking on this 
	 * FigureModel instance!
	 * @return	Returns the x data which has not yet been drawn.
	 */
	public int[] getDirtyAtStartPointsY() {
		if (drawnStartIndex<0) {
			logger.debug("not drawn yet, returning nothing"); //$NON-NLS-1$
			return new int[0];
		}
		if (drawnStartIndex==startIndex) {
			logger.debug("no new data, returning nothing"); //$NON-NLS-1$
			return new int[0];
		}
		int count = drawnStartIndex - startIndex + 1;
		if (endIndex==drawnStartIndex) count--;
		if (logger.isDebugEnabled()) 
			logger.debug("returning " + count + " elements, si,ei,dsi,dei=" + //$NON-NLS-1$ //$NON-NLS-2$
				startIndex + "," + endIndex + "," + drawnStartIndex + "," + drawnEndIndex); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		int [] tmp = new int[count];
		System.arraycopy(yAryInt, startIndex, tmp, 0, count);
		return tmp;
	}

	/**
	 * Access this function with a synchronized block locking on this 
	 * FigureModel instance!
	 * @return	Returns the x data which has not yet been drawn.
	 */
	public int[] getDirtyAtEndPointsX() {
		if (drawnEndIndex<0) {
			logger.debug("not drawn yet, returning full"); //$NON-NLS-1$
			return getPointsX();
		}
		if (drawnEndIndex==endIndex) {
			logger.debug("no new data, returning nothing"); //$NON-NLS-1$
			return new int[0];
		}
		int count = endIndex - drawnEndIndex;
		// if (endIndex==startIndex) count--;
		if (logger.isDebugEnabled()) 
			logger.debug("returning " + count + " elements, si,ei,dsi,dei=" + //$NON-NLS-1$ //$NON-NLS-2$
				startIndex + "," + endIndex + "," + drawnStartIndex + "," + drawnEndIndex); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		int [] tmp = new int[count];
		System.arraycopy(xAryInt, endIndex - count, tmp, 0, count);
		return tmp;
	}

	/**
	 * Access this function with a synchronized block locking on this 
	 * FigureModel instance!
	 * @return	Returns the x data which has not yet been drawn.
	 */
	public int[] getDirtyAtEndPointsY() {
		if (drawnEndIndex<0) {
			logger.debug("not drawn yet, returning full"); //$NON-NLS-1$
			return getPointsY();
		}
		if (drawnEndIndex==endIndex) {
			logger.debug("no new data, returning nothing"); //$NON-NLS-1$
			return new int[0];
		}
		int count = endIndex - drawnEndIndex;
		// if (endIndex==startIndex) count--;
		if (logger.isDebugEnabled()) 
			logger.debug("returning " + count + " elements, si,ei,dsi,dei=" + //$NON-NLS-1$ //$NON-NLS-2$
				startIndex + "," + endIndex + "," + drawnStartIndex + "," + drawnEndIndex); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		int [] tmp = new int[count];
		System.arraycopy(yAryInt, endIndex - count, tmp, 0, count);
		return tmp;
	}

	/**
	 * Access this function with a synchronized block locking on this 
	 * FigureModel instance!
	 */
	public void markDrawn() {
		drawnStartIndex = startIndex;
		drawnEndIndex = endIndex;
	}
	
	/**
	 * Clears the data of the FigureModel.
	 * Note: the storage is not actually cleared, only the reference indexes
	 * are reset to mark an empty array. If memory is truly precious, change 
	 * the source in order to provide a full clean method.
	 */
	public synchronized void clear() {
		logger.debug("clear called (clearing data!)"); //$NON-NLS-1$
		// lets keep the array, its enough to put the index values to the same
		// cell:
		startIndex = xAryInt.length / 2;
		endIndex = startIndex;

		drawnStartIndex = -1;
		drawnEndIndex = -1;

		// FIXME: we need the phiStart for the correct reset here!?
		phiMin = 0.0;
		phiMax = 0.0;
	}

	/**
	 * Clears the drawn status in the model. Actually this method resets the
	 * markers which indicate which points have been drawn already.
	 */
	public void clearDrawn() {
		logger.debug("clearDrawn called"); //$NON-NLS-1$
		drawnStartIndex = -1;
		drawnEndIndex = -1;
	}
}
