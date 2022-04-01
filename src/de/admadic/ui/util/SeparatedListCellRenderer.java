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

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.StringTokenizer;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JSeparator;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 * @author Rainer Schwarze
 *
 */
public class SeparatedListCellRenderer extends JLabel implements
		ListCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JSeparator separator;

    static Border m_noFocusBorder;
    FontMetrics m_fm = null;
    Insets m_insets = new Insets(0, 0, 0, 0);

    int m_defaultTab = 8;
    int [] m_tabs = null;

    int maxcharwidth = -1;

	/**
	 * 
	 */
	public SeparatedListCellRenderer() {
		// setOpaque(true);
		setBorder(new EmptyBorder(1, 1, 1, 1));
		separator = new JSeparator(SwingConstants.HORIZONTAL);
	}

	/**
	 * @param list
	 * @param value
	 * @param index
	 * @param isSelected
	 * @param cellHasFocus
	 * @return	Returns a component representing the cell rendering.
	 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
	 */
	public Component getListCellRendererComponent(
			JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		if (value==null) {
			return separator;
		}
		String str = value.toString();
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		setFont(list.getFont());
		setText(str);
		// setText(str.replace('\t', ' '));
		return this;
	}

	/**
	 * @param defaultTab
	 */
	public void setDefaultTab(int defaultTab) {
		m_defaultTab = defaultTab;
	}
	
	/**
	 * @return	Returns the default tab settings.
	 */
	public int getDefaultTab() {
		return m_defaultTab;
	}
	
	/**
	 * @param tabs
	 */
	public void setTabs(int[] tabs) {
		m_tabs = tabs;
	}
	
	/**
	 * @return	Returns the tab array
	 */
	public int[] getTabs() {
		return m_tabs;
	}
	
	/**
	 * @param index
	 * @return	Returns the tab at the given index.
	 */
	public int getTab(int index) {
		if (m_tabs == null)
			return m_defaultTab * index;
		
		int len = m_tabs.length;
		if (index >= 0 && index < len)
			return m_tabs[index];
		
		return m_tabs[len - 1] + m_defaultTab * (index - len + 1);
	}
	
	/**
	 * @param g
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {
		m_fm = g.getFontMetrics();
		int charw = maxcharwidth;
		if (charw<0) {
			int [] ws = m_fm.getWidths();
			int cnt = 0;
			int sum = 0;
			for (int i : ws) {
				if (i<=0) continue;
				cnt++;
				sum += i;
			}
			if (cnt==0) {
				sum = 10;
			} else {
				sum = sum / cnt;
			}
			charw = sum;
		}
		if (maxcharwidth<0) 
			maxcharwidth = charw;

		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		getBorder().paintBorder(this, g, 0, 0, getWidth(), getHeight());
		
		g.setColor(getForeground());
		g.setFont(getFont());
		m_insets = getInsets();
		int x = m_insets.left;
		int y = m_insets.top + m_fm.getAscent();
		
		StringTokenizer st = new StringTokenizer(getText(), "\t");
		while (st.hasMoreTokens()) {
			String sNext = st.nextToken();
			g.drawString(sNext, x, y);
			x += m_fm.stringWidth(sNext);
			
			if (!st.hasMoreTokens())
				break;
			int index = 0;
			while (x >= charw*getTab(index))
				index++;
			x = charw*getTab(index);
		}
	}

	/**
	 * 
	 * @see javax.swing.JLabel#updateUI()
	 */
	@Override
	public void updateUI() {
		maxcharwidth = -1;
		super.updateUI();
	}
}
