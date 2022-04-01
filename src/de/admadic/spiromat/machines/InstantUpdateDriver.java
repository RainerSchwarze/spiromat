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
package de.admadic.spiromat.machines;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import de.admadic.spiromat.SpiromatException;
import de.admadic.spiromat.model.AppModel;
import de.admadic.spiromat.model.DocModel;
import de.admadic.spiromat.model.FigureSpec;
import de.admadic.spiromat.model.ModelPropertyChangeListener;
import de.admadic.spiromat.model.ModelPropertyChangeSupport;

/**
 * @author Rainer Schwarze
 *
 */
public class InstantUpdateDriver extends AbstractDriver implements 
PropertyChangeListener, ModelPropertyChangeListener {
	// This is used to get rid of excess render requests. On slow machines,
	// there may be queued in more requests than can be handled in the time
	// frame. In that case this instance marks the occurence of requests and 
	// forwards a repaint request after a certain hold off time.
	private DebounceRunner renderTrigger;

	ModelPropertyChangeSupport modelPropSupport;
	
	/**
	 * @param machine
	 */
	public InstantUpdateDriver(Machine machine) {
		super(machine);
		modelPropSupport = new ModelPropertyChangeSupport(
				ModelPropertyChangeSupport.MASK_ALL, this, false);
	}

	/**
	 * 
	 * @see de.admadic.spiromat.machines.IDriver#attach()
	 */
	public void attach() {
		renderTrigger = new DebounceRunner(
				new Runnable() {
					public void run() {
						doRender();
					}
				}, 
				true);
		modelPropSupport.attachToAppModel();
	}

	/**
	 * 
	 * @see de.admadic.spiromat.machines.IDriver#detach()
	 */
	public void detach() {
		modelPropSupport.detachFromAppModel();
		renderTrigger.stopThread();
	}


	/**
	 * 
	 */
	protected void doRender() {
		machine.renderFull();
	}

	/**
	 * @param evt
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		String propName = evt.getPropertyName();
		if (propName!=null) {
			if (
					propName.equals(AppModel.INNER_RADIUS) ||
					propName.equals(AppModel.OUTER_RADIUS) ||
					propName.equals(AppModel.LAMBDA)
				) {
				// a change of the parameters clears the figure if hold on is active:
				if (AppModel.getInstance().getHoldFigure()) {
					AppModel.getInstance().setHoldFigure(false);
				}
				// trigger a new rendering:
				renderTrigger.setHasValue(true);
			}
		} else {
			throw new SpiromatException("Cannot yet handle unspecified PropertyChangeEvents"); //$NON-NLS-1$
		}
	}

	/**
	 * @param evt
	 */
	public void figPropertyChange(PropertyChangeEvent evt) {
		String propName = evt.getPropertyName();
		if (propName!=null) {
			if (
					propName.equals(FigureSpec.PROP_COLOR) ||
					propName.equals(FigureSpec.PROP_INNER_RADIUS) ||
					propName.equals(FigureSpec.PROP_OUTER_RADIUS) ||
					propName.equals(FigureSpec.PROP_PEN_HOLE_POS)
				) {
				// a change of the parameters clears the figure if hold on is active:
				if (AppModel.getInstance().getHoldFigure()) {
					AppModel.getInstance().setHoldFigure(false);
				}
				// trigger a new rendering:
				renderTrigger.setHasValue(true);
			}
		} else {
			throw new SpiromatException("Cannot yet handle unspecified PropertyChangeEvents"); //$NON-NLS-1$
		}
	}

	/**
	 * @param evt
	 */
	public void docPropertyChange(PropertyChangeEvent evt) {
		String propName = evt.getPropertyName();
		if (propName!=null) {
			if (propName.equals(DocModel.PROPERTY_FIGURE_LIST)) {
				// ?
			}
		}
	}

	/**
	 * @param evt
	 */
	public void appPropertyChange(PropertyChangeEvent evt) {
		String propName = evt.getPropertyName();
		if (propName!=null) {
			if (propName.equals(AppModel.DOC_MODEL)) {
				// ? 
			}
		}
	}
}
