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

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Rainer Schwarze
 *
 */
public class StatusLine extends JPanel {
	/** */
	private static final long serialVersionUID = 1L;

	private FormLayout fl;
	
	/**
	 * 
	 */
	public StatusLine() {
		super();
		fl = new FormLayout("d:grow", "d"); //$NON-NLS-1$ //$NON-NLS-2$
		this.setLayout(fl);
	}

	/**
	 * @param comp
	 * @param spec Spec for FormLayout
	 */
	public void addComponent(JComponent comp, String spec) {
		int xValue = this.getComponentCount();
		if (this.getComponentCount()>=1) {
			fl.appendColumn(new ColumnSpec("p")); //$NON-NLS-1$
			fl.appendColumn(new ColumnSpec(spec==null ? "d" : spec)); //$NON-NLS-1$
			this.add(new JSeparator(JSeparator.VERTICAL), new CellConstraints(++xValue, 1));
		}
		this.add(comp, new CellConstraints(++xValue, 1));
	}
}
