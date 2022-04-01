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
package de.admadic.spiromat.model.types;

import java.awt.Component;

/**
 * Provides a base class for concrete properties.
 * <p>
 * This class supports the following properties:
 * <ul>
 * <li>name</li>
 * <li>enabled</li>
 * <li>visible</li>
 * <li>value</li>
 * </ul>
 * <p>
 * The <code>name</code> is the name of the property as it would appear
 * in a configuration file. The <code>enabled</code> indicates whether this 
 * property is enabled, which means whether its value can be changed or not.
 * The <code>value</code> represents the value of the property. Note that 
 * primitive types must be wrapped in their object types.
 * 
 * The <code>visible</code> indicates whether this property is visisble 
 * right now. This feature is experimental and may change in the future since
 * the visual appearance would now be linked with the model and that may turn
 * out to be not appropriate for this project. 
 * 
 * FIXME: validate whether we should keep the visible field in Property or not.
 * 
 * @author Rainer Schwarze
 * @param <T> 
 */
public abstract class AbstractProperty<T> implements IProperty<T> {
	final private String name;		// the name of the Property
	private T value = null;	// the value of the Property

	private boolean enabled = true;	// enabled by default
	// experimental
	private boolean visible = true;

	// the PropertyChangeManager which takes care of distributing
	// PropertyChange events:
	protected IPropertyChangeManager manager = null;

	// the instance taking care of updating visual appearance of components
	// registered for this Property:
	private ComponentUpdater componentUpdater = new ComponentUpdater();
	
	/**
	 * Creates an instance of the Property with the given name.
	 * Note that a Property created this way does not have a 
	 * PropertyChangeManager associated and thus cannot propagate
	 * change events.
	 * 
	 * @param name A String specifying the name of the Property.
	 */
	public AbstractProperty(String name) {
		super();
		this.name = name;
	}

	/**
	 * Creates an instance of the Property with the given name and value.
	 * Note that a Property created this way does not have a 
	 * PropertyChangeManager associated and thus cannot propagate
	 * change events.
	 * 
	 * @param name A String specifying the name of the Property.
	 * @param value The value of the Property.
	 */
	public AbstractProperty(String name, T value) {
		super();
		this.name = name;
		setValue(value);
	}

	/**
	 * Creates an instance of the Property with the given name and value.
	 * The given PropertyChangeManager is used to spread PropertyChangeEvents.
	 * 
	 * @param name A String specifying the name of the Property.
	 * @param value The value of the Property.
	 * @param manager The PropertyChangeManager to be used by this Property.
	 */
	public AbstractProperty(String name, T value, IPropertyChangeManager manager) {
		super();
		this.name = name;
		this.manager = manager;
		setValue(value);
	}

	/**
	 * Returns the enabled state of this Property.
	 * 
	 * @return	Returns true, if the property is enabled.
	 * @see de.admadic.spiromat.model.types.IProperty#isEnabled()
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Sets the enabled state of this Property.
	 * 
	 * If any components are registered for this Property, they are
	 * updated to reflect the new enabled state.
	 * 
	 * @param enabled
	 * @see de.admadic.spiromat.model.types.IProperty#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		boolean old = this.enabled;
		this.enabled = enabled;
		if (old!=this.enabled) {
			componentUpdater.setEnabled(enabled);
		}
	}

	/**
	 * ATN: this may change, don't depend on it.
	 * 
	 * @return	Returns whether this property should be visible or not.
	 * @see de.admadic.spiromat.model.types.IProperty#isVisible()
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * ATN: this may change, don't depend on it.
	 * 
	 * @param visible
	 * @see de.admadic.spiromat.model.types.IProperty#setVisible(boolean)
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * Returns the name of this instance.
	 * 
	 * @return	Returns the name of the property.
	 * @see de.admadic.spiromat.model.types.IProperty#getName()
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the value of this instance.
	 * 
	 * @return	Returns the value of the property.
	 * @see de.admadic.spiromat.model.types.IProperty#getValue()
	 */
	public T getValue() {
		return this.value;
	}

	/**
	 * Sets the value of the instance.
	 * 
	 * @param value
	 */
	public void setValue(T value) {
		T oldValue = this.value;
		this.value = value;
		if (manager!=null) {
			manager.firePropertyChange(getName(), oldValue, this.value);
		}
	}

	/**
	 * Adds a Component for updating enabled/disabled with this Property.
	 * 
	 * @param comp
	 * @see de.admadic.spiromat.model.types.ComponentUpdater#addComponent(java.awt.Component)
	 */
	public void addComponent(Component comp) {
		componentUpdater.addComponent(comp);
	}

	/**
	 * Removes a Component from the list of components for which 
	 * enabled/disabled are updated with this Property.
	 * 
	 * @param comp
	 * @see de.admadic.spiromat.model.types.ComponentUpdater#removeComponent(java.awt.Component)
	 */
	public void removeComponent(Component comp) {
		componentUpdater.removeComponent(comp);
	}
}
