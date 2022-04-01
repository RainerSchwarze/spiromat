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

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Document;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Rainer Schwarze
 *
 */
public class ClassSelectorDialog extends de.admadic.ui.util.Dialog 
implements ActionListener {
	
	JLabel labelClassName;
	JTextField textClassName;
	JLabel labelClassFilter;
	JTextField textClassFilter;
	JLabel labelClassList;
	JList listClasses;
	FilteredListModel listModel;
	JScrollPane scrollClasses;
	JPanel panelButtons;
	JButton buttonOk;
	JButton buttonCancel;

	boolean requireInList = true;
	String filter;
	String selectedClassName;
	
	final static String CMD_OK = "cmd.ok";
	final static String CMD_CANCEL = "cmd.cancel";

	ArrayList<String> listClassNamesEngine;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param arg0
	 * @throws HeadlessException
	 */
	public ClassSelectorDialog(Frame arg0) throws HeadlessException {
		super(arg0);
		initGUI();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws HeadlessException
	 */
	public ClassSelectorDialog(Frame arg0, boolean arg1)
			throws HeadlessException {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws HeadlessException
	 */
	public ClassSelectorDialog(Frame arg0, String arg1)
			throws HeadlessException {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws HeadlessException
	 */
	public ClassSelectorDialog(Frame arg0, String arg1, boolean arg2)
			throws HeadlessException {
		super(arg0, arg1, arg2);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public ClassSelectorDialog(Frame arg0, String arg1, boolean arg2,
			GraphicsConfiguration arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	/**
	 * @param arg0
	 * @throws HeadlessException
	 */
	public ClassSelectorDialog(Dialog arg0) throws HeadlessException {
		super(arg0);
		initGUI();
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws HeadlessException
	 */
	public ClassSelectorDialog(Dialog arg0, String arg1)
			throws HeadlessException {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws HeadlessException
	 */
	public ClassSelectorDialog(Dialog arg0, String arg1, boolean arg2)
			throws HeadlessException {
		super(arg0, arg1, arg2);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 * @throws HeadlessException
	 */
	public ClassSelectorDialog(Dialog arg0, String arg1, boolean arg2,
			GraphicsConfiguration arg3) throws HeadlessException {
		super(arg0, arg1, arg2, arg3);
	}

	protected void initGUI() {
		FormLayout fl = new FormLayout(
				"12px, p, 12px",
				"12px, p, 5px, p, 12px, p, 5px, p, 12px, p, 5px, p, 12px, p, 12px"
				);
		this.setLayout(fl);

		this.setTitle("Select Classes");
		
		labelClassName = new JLabel("Class name:");
		this.add(labelClassName, new CellConstraints(
			"2, 2, 1, 1, default, default"));

		textClassName = new JTextField("");
		this.add(textClassName, new CellConstraints(
			"2, 4, 1, 1, default, default"));

		labelClassFilter = new JLabel("Class filter:");
		this.add(labelClassFilter, new CellConstraints(
			"2, 6, 1, 1, default, default"));

		textClassFilter = new JTextField("");
		this.add(textClassFilter, new CellConstraints(
			"2, 8, 1, 1, default, default"));

		labelClassList = new JLabel("Class list:");
		this.add(labelClassList, new CellConstraints(
			"2, 10, 1, 1, default, default"));

		listModel = new FilteredListModel();
		listClasses = new JList();
		listClasses.setModel(listModel);
//		listClasses.setPrototypeCellValue(
//				"javax.swing.plaf.metal.MetalLookAndFeel");
//		listClasses.setFixedCellWidth(400);
		listClasses.getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);
		listClasses.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					textClassName.setText((String)listClasses.getSelectedValue());
				} else {
					textClassName.setText((String)listClasses.getSelectedValue());
				}
			}
		});
		scrollClasses = new JScrollPane(listClasses);
		this.add(scrollClasses, new CellConstraints(
			"2, 12, 1, 1, default, default"));
		scrollClasses.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollClasses.setHorizontalScrollBarPolicy(
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		panelButtons = new JPanel();
		buttonOk = new JButton("OK");
		panelButtons.add(buttonOk);
		buttonCancel = new JButton("Cancel");
		panelButtons.add(buttonCancel);

		buttonOk.setActionCommand(CMD_OK);
		buttonCancel.setActionCommand(CMD_CANCEL);
		buttonOk.addActionListener(this);
		buttonCancel.addActionListener(this);

		this.add(panelButtons, new CellConstraints(
			"2, 14, 1, 1, default, default"));

		this.registerEnterAction(buttonOk);
		this.registerEscapeAction();

		Document doc = textClassFilter.getDocument();
		doc.addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent ev) {
				updateList(ev);
			}

			public void removeUpdate(DocumentEvent ev) {
				updateList(ev);
			}

			public void changedUpdate(DocumentEvent ev) {
				// according to doc, we don't get that.
			}

			public void updateList(DocumentEvent ev) {
				if (ev==null) { /* no warn */ }
				String text = textClassFilter.getText();
				listModel.updateFilter(text);
			}
		});
		
		this.pack();
		this.setLocationRelativeTo(this.getOwner());
	}

	/**
	 * @param ev
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent ev) {
		String cmd = ev.getActionCommand();
		if (cmd.equals(CMD_OK)) {
			// do OK
			String className;
			className = textClassName.getText();
			if (!checkIsInList(className)) {
				JOptionPane.showMessageDialog(
						this, 
						"Class Name not in List!", 
						"Class not in list", 
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			selectedClassName = className;
			setWindowCloseCode(RESULT_OK);
			setVisible(false);
		} else if (cmd.equals(CMD_CANCEL)) {
			// do Cancel
			setWindowCloseCode(RESULT_CANCEL);
			setVisible(false);
		} else {
			// nothing
		}
	}

	protected boolean checkIsInList(String className) {
		boolean res = false;
		if (listClassNamesEngine.contains(className)) {
			return true;
		}
		return res;
	}

	/**
	 * @return Returns the requireInList.
	 */
	public boolean isRequireInList() {
		return requireInList;
	}

	/**
	 * @param requireInList The requireInList to set.
	 */
	public void setRequireInList(boolean requireInList) {
		this.requireInList = requireInList;
	}

	/**
	 * @param list
	 */
	public void addClasses(String [] list) {
		if (listClassNamesEngine==null) {
			listClassNamesEngine = new ArrayList<String>();
		}
		for (String cl : list) {
			listClassNamesEngine.add(cl);
		}
	}

	/**
	 * 
	 */
	public void updateControls() {
		listModel.clear();
		listModel.addList(listClassNamesEngine);
		updateControlsFilter();
		this.pack();
	}

	protected void updateControlsFilter() {
		listModel.updateFilter(filter);
		if (listModel.getSize()==1) {
			listClasses.setSelectedIndex(0);
		}
		if (!isVisible())
			this.pack();
	}
	
	/**
	 * @return Returns the filter.
	 */
	public String getFilter() {
		return filter;
	}

	/**
	 * @param filter The filter to set.
	 */
	public void setFilter(String filter) {
		this.filter = filter;
		this.textClassFilter.setText(filter);
		updateControlsFilter();
	}

	/**
	 * @return Returns the selectedClassName.
	 */
	public String getSelectedClassName() {
		return selectedClassName;
	}

	/**
	 * @param selectedClassName The selectedClassName to set.
	 */
	public void setSelectedClassName(String selectedClassName) {
		this.selectedClassName = selectedClassName;
	}
}
