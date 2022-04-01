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

import de.admadic.spiromat.model.AppModel;
import de.admadic.spiromat.model.DocModel;
import de.admadic.spiromat.model.ModelPropertyChangeListener;
import de.admadic.spiromat.model.ModelPropertyChangeSupport;
import de.admadic.spiromat.ui.SpiromatCanvas;

/**
 * Abstract base machine for a spiromat.
 * 
 * To use a machine:
 * - create an instance
 * - attach to model
 * 
 * Select another machine:
 * - detach old from model if there is any
 * - create new machine
 * - attach to model
 * 
 * @author Rainer Schwarze
 */
abstract public class AbstractMachine implements ModelPropertyChangeListener {
	private SpiromatCanvas canvasRef;

	ModelPropertyChangeSupport modelPropSupport;
	
	/**
	 * @param canvas 
	 */
	public AbstractMachine(SpiromatCanvas canvas) {
		super();
		this.modelPropSupport = new ModelPropertyChangeSupport(this);
		this.canvasRef = canvas;
	}

	/**
	 * @return the app model
	 */
	public AppModel getAppModel() {
		return AppModel.getInstance();
	}

	/**
	 * @return the doc model
	 */
	public DocModel getDocModel() {
		return AppModel.getInstance().getDocModel();
	}

	/**
	 * @return the canvas
	 */
	public SpiromatCanvas getCanvas() {
		return canvasRef;
	}

	/**
	 * Starts the machine.
	 * This may mean that the machine is starting to spin, but it may also
	 * mean that the machine is ready for spin input.
	 */
	abstract public void startMachine();

	/**
	 * Stops the machine.
	 * This may mean that the machine is stopping to spin, but it may also 
	 * mean that the machine is not ready any more for spin input.
	 */
	abstract public void stopMachine();

	/**
	 * Pauses the machine.
	 * The machine is waiting at the last state and continues there upon call
	 * of continue(). With an animated machine this means, that no time elapses.
	 */
	abstract public void pauseMachine();

	/**
	 * Continues the machine.
	 */
	abstract public void continueMachine();

	/**
	 * Resets the machine.
	 * All state information of the machine is reset. This is by definition
	 * called in startMachine.
	 */
	abstract public void resetMachine();

	/**
	 * Destroys the machine.
	 */
	abstract public void destroyMachine();
	
	/**
	 * Attaches the Machine as a listener to the Model.
	 */
	public void attachToModel() {
		modelPropSupport.attachToAppModel();
	}

	/**
	 * Detaches the Machine as a listener from the Model.
	 */
	public void detachFromModel() {
		modelPropSupport.detachFromAppModel();
	}

	/**
	 * @param e
	 * @see de.admadic.spiromat.model.ModelPropertyChangeListener#appPropertyChange(java.beans.PropertyChangeEvent)
	 */
	abstract public void appPropertyChange(PropertyChangeEvent e);

	/**
	 * @param e
	 * @see de.admadic.spiromat.model.ModelPropertyChangeListener#docPropertyChange(java.beans.PropertyChangeEvent)
	 */
	abstract public void docPropertyChange(PropertyChangeEvent e);

	/**
	 * @param e
	 * @see de.admadic.spiromat.model.ModelPropertyChangeListener#figPropertyChange(java.beans.PropertyChangeEvent)
	 */
	abstract public void figPropertyChange(PropertyChangeEvent e);
}
