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

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.admadic.spiromat.actions.NumericAction;

/**
 * Provides an edit control which combines a slider and a textfield.
 * When the textfield is edited the slider is adjusted and when the
 * slider is adjusted, the textfield is updated.
 * <p>
 * To give visual feedback when the control is enabled/disabled, the 
 * method setEnabled is overridden to call setEnabled on the label, textfield
 * and slider of this instance.
 * <p>
 * Note that the SliderEdit only supports integer values. If a floating point 
 * value is to be used, it must be converted to an integer range.
 * <p>
 * To get notified of changed values, use addValueChangeListener.
 * 
 * @author Rainer Schwarze
 */
public class SliderEdit extends JPanel {

	/** */
	private static final long serialVersionUID = 1L;

	protected JSpinner textField;
	protected JLabel label;
	protected JSlider slider;
	Action action;
	PropertyChangeListener actionPropChange;

	PropertyChangeSupport pcs = null;
	
	/**
	 * @param title 
	 * @param min 
	 * @param max 
	 */
	public SliderEdit(String title, int min, int max) {
		super();
		this.pcs = new PropertyChangeSupport(this);
		this.actionPropChange = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				actionPropertyChange(evt);
			}
		};
		init(title, min, max);
	}

	/**
	 * @param action 
	 * @param min 
	 * @param max 
	 */
	public SliderEdit(Action action, int min, int max) {
		this("", min, max); //$NON-NLS-1$
		setAction(action);
	}

	/**
	 * @param max 
	 * @param min 
	 * @param title 
	 * 
	 */
	private void init(String title, int min, int max) {
		/*
		 *  [ ---- label ------------ ]
		 *  [ ----V(slider)----] [____] (< textfield) 
		 */
		FormLayout fl = new FormLayout(
				"0px, min(p;60dlu):grow, 5px, 24dlu, 0px", //$NON-NLS-1$
				"0px, p, 5px, 12dlu, 0px"); //$NON-NLS-1$
		CellConstraints cc = new CellConstraints();
		setLayout(fl);
		add(label = new JLabel(title), cc.xywh(2, 2, 3, 1));
		add(textField = new JSpinner(
				new SpinnerNumberModel(min, min, max, 1)), 
				cc.xy(4, 4, CellConstraints.FILL, CellConstraints.FILL));
		add(slider = new JSlider(JSlider.HORIZONTAL), cc.xy(2, 4, CellConstraints.FILL, CellConstraints.FILL));

		this.setBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createLoweredBevelBorder(),
						BorderFactory.createEmptyBorder(5, 5, 5, 5)
				));
		
		slider.setMinimum(min);
		slider.setMaximum(max);

		slider.getModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				textField.setValue(new Integer(slider.getValue()));
				fireValueChange(-1, slider.getValue());
			}
		});
		textField.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int vi;
				vi = ((Integer)(textField.getValue())).intValue();
				setValue(vi);
				// ((SmallScrollbar)slider).fireAdjustment();
			}
		});
//		textField.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				String v = textField.getText();
//				int vi;
//				try {
//					vi = Integer.parseInt(v);
//				} catch (NumberFormatException ex) {
//					// cannot accept 
//					return;
//				}
//				if (vi<slider.getMinimum()) {
//					vi = slider.getMinimum();
//				}
//				if (vi>(slider.getMaximum())) {
//					vi = slider.getMaximum();
//				}
//				setValue(vi);
//				// ((SmallScrollbar)slider).fireAdjustment();
//			}
//		});
		textField.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				int rot = e.getWheelRotation();
				setValue(slider.getValue() - rot);
				// ((SmallScrollbar)slider).fireAdjustment();
			}
		});
	}

	/**
	 * @param listener
	 * @see java.beans.PropertyChangeSupport#addPropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void addValueChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	/**
	 * @param oldValue
	 * @param newValue
	 * @see java.beans.PropertyChangeSupport#firePropertyChange(java.lang.String, int, int)
	 */
	void fireValueChange(int oldValue, int newValue) {
		// deprecated:
		pcs.firePropertyChange("VALUE", oldValue, newValue); //$NON-NLS-1$

		if (action==null) return;
		ActionEvent ae = new ActionEvent(this, ActionEvent.RESERVED_ID_MAX + 1, "VALUE"); //$NON-NLS-1$
		Integer v = new Integer(newValue);
		action.putValue(NumericAction.INTEGER_VALUE_KEY, v);
		action.actionPerformed(ae);
	}

	/**
	 * @param listener
	 * @see java.beans.PropertyChangeSupport#removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void removeValueChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	/**
	 * @param action
	 */
	public void setAction(Action action) {
		if (this.action!=null) {
			this.action.removePropertyChangeListener(actionPropChange);
		}
		this.action = action;
		if (this.action==null) return;

		Object v = null;
		if ((v = this.action.getValue(Action.NAME))!=null) {
			label.setText((String)v);
		}
		if ((v = this.action.getValue(Action.SHORT_DESCRIPTION))!=null) {
			String text = (String)v;
			super.setToolTipText(text);
			textField.setToolTipText(text);
			label.setToolTipText(text);
			slider.setToolTipText(text);
		}
		if ((v = this.action.getValue(Action.SMALL_ICON))!=null) {
			Icon icon = (Icon)v;
			label.setIcon(icon);
		}
		this.action.addPropertyChangeListener(actionPropChange);
	}
	
	/**
	 * @param evt
	 */
	protected void actionPropertyChange(PropertyChangeEvent evt) {
		String propName = evt.getPropertyName();
		if (propName!=null) {
			if (propName.equals(Action.NAME)) {
				label.setText((String)action.getValue(Action.NAME));
			} else if (propName.equals(Action.SMALL_ICON)) {
				label.setIcon((Icon)action.getValue(Action.SMALL_ICON));
			} else if (propName.equals(Action.SHORT_DESCRIPTION)) {
				String text = (String)action.getValue(Action.SHORT_DESCRIPTION);
				super.setToolTipText(text);
				textField.setToolTipText(text);
				label.setToolTipText(text);
				slider.setToolTipText(text);
			} else if (propName.equals("enabled")) { //$NON-NLS-1$
				setEnabled(action.isEnabled());
			} else {
				// ?
			}
		}
	}

	/**
	 * @return	Returns the current value of the control.
	 * @see java.awt.Scrollbar#getValue()
	 */
	public int getValue() {
		return slider.getValue();
	}

	/**
	 * @param newValue
	 * @see java.awt.Scrollbar#setValue(int)
	 */
	public void setValue(int newValue) {
		int min = slider.getMinimum();
		int max = slider.getMaximum();
		if (newValue<min) newValue = min;
		if (newValue>max) newValue = max;
		slider.setValue(newValue);
		textField.setValue(new Integer(newValue)); //$NON-NLS-1$
	}
	
	/**
	 * @param b
	 * @see java.awt.Component#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean b) {
		super.setEnabled(b);
		textField.setEnabled(b);
		label.setEnabled(b);
		slider.setEnabled(b);
	}

	/**
	 * @param icon
	 * @see javax.swing.JLabel#setIcon(javax.swing.Icon)
	 */
	public void setLabelIcon(Icon icon) {
		label.setIcon(icon);
	}

	/**
	 * @param font
	 * @see javax.swing.JComponent#setFont(java.awt.Font)
	 */
	@Override
	public void setFont(Font font) {
		super.setFont(font);
		if (label!=null)
			label.setFont(font);
		if (textField!=null)
			textField.setFont(font);
	}


	/**
	 * @param text
	 * @see javax.swing.JComponent#setToolTipText(java.lang.String)
	 */
	@Override
	public void setToolTipText(String text) {
		super.setToolTipText(text);
		textField.setToolTipText(text);
		label.setToolTipText(text);
		slider.setToolTipText(text);
	}
}
