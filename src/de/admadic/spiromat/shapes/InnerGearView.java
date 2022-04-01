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
package de.admadic.spiromat.shapes;

import java.beans.PropertyChangeEvent;

import de.admadic.spiromat.Globals;
import de.admadic.spiromat.model.InnerGearSpec;

/**
 * @author Rainer Schwarze
 */
public class InnerGearView extends AbstractGearView {
	
	/**
	 * @param innerGearSpec 
	 */
	public InnerGearView(InnerGearSpec innerGearSpec) {
		super(innerGearSpec);
	}

	@Override
	protected void initData() {
		int count = teeth*4 + 1;
		int vcount = count;
		int count2 = 20;
		vcount += count2;
		x = new int[vcount];
		y = new int[vcount];
		double radius = gearSpec.getRadius() * Globals.MAX_RADIUS / 100.0;
		double lambda = ((InnerGearSpec)gearSpec).getPenHolePos();

		{ // inner circle
			double phiStart = 2*Math.PI / teeth / 2;
			for (int i=0; i<count; i++) {
				double phi = i*2*Math.PI / (count-1);
				double r = radius;
				if ((i % 4) < 2) {
					r += teethHeight / 2;
				} else {
					r -= teethHeight / 2;
				}
				r -= strokeWidth/2;
				x[i] = (int) (r*Math.cos(phi + phiStart));
				y[i] = (int) (r*Math.sin(phi + phiStart));
			}
		}
		// the pen-hole:
		double phx = radius * lambda;
		double phy = 0;
		logger.debug("phx = " + phx); //$NON-NLS-1$
		double phiStart = 2*Math.PI / teeth / 2;
		for (int i=0; i<count2; i++) {
			double phi = -i*2*Math.PI / (count2-1);
			double r = getTeethHeight() / 2;
			x[count+i] = (int) (phx + r*Math.cos(phi + phiStart));
			y[count+i] = (int) (phy + r*Math.sin(phi + phiStart));
		}
	}

	/**
	 * @param evt
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		String propName = evt.getPropertyName();
		if (propName!=null) {
			if (propName.equals(InnerGearSpec.PROP_PEN_HOLE_POS)) {
				updateParameters();
			}
		}
	}
}
