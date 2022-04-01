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
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.Document;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Rainer Schwarze
 *
 */
public class LaFAddDialog extends de.admadic.ui.util.Dialog 
implements ActionListener {

	class LaFTableModel extends AbstractTableModel {
		/** */
		private static final long serialVersionUID = 1L;

		private String[] columns = null;
		private Object[][] data = null;

		/**
		 * @param columns
		 */
		public void setColumns(String [] columns) {
			this.columns = columns;
		}

		/**
		 * @param data
		 */
		public void setData(Object [][] data) {
			this.data = data;
		}
		
		/**
		 * @param index
		 * @return	Returns the class of the given column
		 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
		 */
		@Override
		public Class<?> getColumnClass(int index) {
			return String.class;
		}

		/**
		 * @param index
		 * @return	Returns the name of the given column.
		 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
		 */
		@Override
		public String getColumnName(int index) {
			if (columns==null) return null;
			if (index<0 || index>=columns.length) return null;
			return columns[index];
		}

		/**
		 * @param obj
		 * @param row
		 * @param column
		 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
		 */
		@Override
		public void setValueAt(Object obj, int row, int column) {
			if (data==null) return;
			if (row<0 || row>=data.length) return;
			if (column<0 || column>=data[row].length) return;
			data[row][column] = obj;
		}

		/**
		 * @return	Returns the number of rows
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		public int getRowCount() {
			if (data==null) return 0;
			return data.length;
		}

		/**
		 * @return	Returns the number of columns
		 * @see javax.swing.table.TableModel#getColumnCount()
		 */
		public int getColumnCount() {
			if (columns==null) return 0;
			return columns.length;
		}

		/**
		 * @param row
		 * @param column
		 * @return	Returns the data element at the given cell
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		public Object getValueAt(int row, int column) {
			if (data==null) return null;
			if (row<0 || row>=data.length) return null;
			if (column<0 || data[row]==null || column>=data[row].length) return null;
			return data[row][column];
		}

		/**
		 * @param row
		 * @param column
		 * @return	Returns true, if the given cell is editable, false otherwise.
		 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
		 */
		@Override
		public boolean isCellEditable(int row, int column) {
			if (column==0) return true;
			return false;
		}
	}
	
	JLabel labelClassFilter;
	JTextField textClassFilter;
	JLabel labelClassList;
	JList listClasses;
	FilteredListModel listModel;
	JScrollPane scrollClasses;
	JPanel panelButtons;
	JButton buttonOk;
	JButton buttonCancel;

	JLabel labelLaFs;
	JScrollPane scrollLaFs;
	JTable tableLaFs;
	LaFTableModel tableModelLaFs;

	String [] tableColumnsLaFs;
	String [][] tableDataLaFs;
	boolean lafTableNotifyFlag = true;

	JPanel panelLaFButtons;
	
	JButton buttonAddAllLaFs;
	JButton buttonAddLaF;
	JButton buttonRemAllLaFs;
	JButton buttonRemLaF;

	JPanel panelFilterModes;
	JToggleButton buttonFilterBegin;
	JToggleButton buttonFilterInside;
	JToggleButton buttonFilterEnd;
	ButtonGroup btnGrpFilter;

	JTextArea textMsg;
	
	boolean requireInList = true;
	String filter;
	String selectedClassName;
	
	final static String CMD_OK = "cmd.ok";
	final static String CMD_CANCEL = "cmd.cancel";
	final static String CMD_ADD_LAF = "cmd.add.laf";
	final static String CMD_ADD_ALLLAFS = "cmd.add.alllafs";
	final static String CMD_REM_LAF = "cmd.rem.laf";
	final static String CMD_REM_ALLLAFS = "cmd.rem.alllafs";

	ArrayList<String> listClassNamesEngine;

	ArrayList<String> resultDisplayNames;
	ArrayList<String> resultClassNames;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param arg0
	 * @throws HeadlessException
	 */
	public LaFAddDialog(Frame arg0) throws HeadlessException {
		super(arg0);
		initGUI();
	}

	/**
	 * @param arg0
	 * @throws HeadlessException
	 */
	public LaFAddDialog(Dialog arg0) throws HeadlessException {
		super(arg0);
		initGUI();
	}

	protected void initGUI() {
		FormLayout fl = new FormLayout(
				"12px, 250px, "+		// class management
				"12px, p, "+			// LaF buttons
				"12px, 250px, 12px"		// LaF table
				,
				"12px, "+				// top spacer
				"p, 5px, p, 12px, "+	// filter label and text
				"p, 5px, 150px, 12px, "+	// list label and list
				"p, 12px,"+				// buttons
				"p, 12px"				// msg panel
				);
		this.setLayout(fl);
		CellConstraints cc = new CellConstraints();

		// primary creation:
		labelClassFilter = new JLabel("Class filter:");
		textClassFilter = new JTextField("");
		labelClassList = new JLabel("List of classes for potential Look-and-Feels:");
		listClasses = new JList();
		scrollClasses = new JScrollPane(listClasses);
		panelButtons = new JPanel();
		buttonOk = new JButton("OK");
		panelButtons.add(buttonOk);
		buttonCancel = new JButton("Cancel");
		panelButtons.add(buttonCancel);

		labelLaFs = new JLabel("Look-and-Feels to be installed:");
		tableLaFs = new JTable();
		tableModelLaFs = new LaFTableModel();
		scrollLaFs = new JScrollPane(tableLaFs);

		buttonFilterBegin = new JToggleButton("|<-");
		buttonFilterInside = new JToggleButton("><");
		buttonFilterEnd = new JToggleButton("->|");
		panelFilterModes = new JPanel();

		textMsg = new JTextArea();
		{
			FormLayout pfl = new FormLayout(
					"p:grow, 5px, p, 5px, p, 5px, p",
					"p");
			CellConstraints pcc = new CellConstraints();
			panelFilterModes.setLayout(pfl);
			panelFilterModes.add(textClassFilter, 		pcc.xy(1, 1));
			panelFilterModes.add(buttonFilterBegin, 	pcc.xy(3, 1));
			panelFilterModes.add(buttonFilterInside, 	pcc.xy(5, 1));
			panelFilterModes.add(buttonFilterEnd,	 	pcc.xy(7, 1));
		}

		buttonAddLaF = new JButton("+ >");
		buttonAddAllLaFs = new JButton(">>");
		buttonRemLaF = new JButton("X");
		buttonRemAllLaFs = new JButton("XX");
		{
			FormLayout pfl = new FormLayout(
					"p",
					"p, 5px, p, 5px, p, 5px, p");
			CellConstraints pcc = new CellConstraints();
			panelLaFButtons = new JPanel();
			panelLaFButtons.setLayout(pfl);
			panelLaFButtons.add(buttonAddLaF, 		pcc.xy(1, 1));
			panelLaFButtons.add(buttonAddAllLaFs,	pcc.xy(1, 3));
			panelLaFButtons.add(buttonRemLaF,		pcc.xy(1, 5));
			panelLaFButtons.add(buttonRemAllLaFs,	pcc.xy(1, 7));
		}

		// placement:
		this.add(labelClassFilter, 	cc.xy(2, 2));
//		this.add(textClassFilter, 	cc.xy(2, 4));
		this.add(panelFilterModes, 	cc.xy(2, 4));	// FIXME: change that pos.
		this.add(labelClassList, 	cc.xy(2, 6));
		this.add(scrollClasses, 	cc.xy(2, 8, 
				CellConstraints.DEFAULT, CellConstraints.TOP));
		this.add(panelButtons, 		cc.xywh(2, 12, 5, 1));

		this.add(panelLaFButtons, 	cc.xy(4, 8, 
				CellConstraints.DEFAULT, CellConstraints.TOP));

		this.add(labelLaFs, 		cc.xy(6, 6, 
				CellConstraints.DEFAULT, CellConstraints.TOP));
		this.add(scrollLaFs, 		cc.xy(6, 8));

		this.add(textMsg, 			cc.xywh(2, 10, 5, 1));

		// detailed initialization:
		listModel = new FilteredListModel();
		listModel.setFilterType(FilteredListModel.FLT_END);
		listClasses.setModel(listModel);
//		listClasses.setPrototypeCellValue(
//				"javax.swing.plaf.metal.MetalLookAndFeel");
//		listClasses.setFixedCellWidth(400);
		listClasses.getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);

		scrollClasses.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollClasses.setHorizontalScrollBarPolicy(
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
//		scrollClasses.setPreferredSize(
//				new java.awt.Dimension(250, 150));

		buttonOk.setActionCommand(CMD_OK);
		buttonCancel.setActionCommand(CMD_CANCEL);
		buttonOk.addActionListener(this);
		buttonCancel.addActionListener(this);

		this.registerEnterAction(buttonOk);
		this.registerEscapeAction();

		btnGrpFilter = new ButtonGroup();
		btnGrpFilter.add(buttonFilterBegin);
		btnGrpFilter.add(buttonFilterInside);
		btnGrpFilter.add(buttonFilterEnd);
		// select before having the item listener, we don't want another tick:
		buttonFilterEnd.setSelected(true);
		ItemListener il = new ItemListener() {
			public void itemStateChanged(ItemEvent ev) {
				Object o = ev.getSource();
				int ft = FilteredListModel.FLT_INSIDE;
				if (o==buttonFilterBegin) {
					ft = FilteredListModel.FLT_BEGIN;
				} else if (o==buttonFilterInside) {
					ft = FilteredListModel.FLT_INSIDE;
				} else if (o==buttonFilterEnd) {
					ft = FilteredListModel.FLT_END;
				} // else already done with init situation
				listModel.setFilterType(ft);
				listModel.updateFilter();
			}
		};
		buttonFilterBegin.addItemListener(il);
		buttonFilterInside.addItemListener(il);
		buttonFilterEnd.addItemListener(il);
		buttonFilterBegin.setMargin(new java.awt.Insets(1, 1, 1, 1));
		buttonFilterInside.setMargin(new java.awt.Insets(1, 1, 1, 1));
		buttonFilterEnd.setMargin(new java.awt.Insets(1, 1, 1, 1));

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

		scrollLaFs.setHorizontalScrollBarPolicy(
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollLaFs.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		tableColumnsLaFs = new String[]{"Display Name", "Class Name"};
		tableDataLaFs = new String[0][0];
		tableModelLaFs.setColumns(tableColumnsLaFs);
		tableModelLaFs.setData(tableDataLaFs);
		tableLaFs.setModel(tableModelLaFs);
//		tableLaFs.getColumnModel().getColumn(0).setPreferredWidth(100);
//		tableLaFs.getColumnModel().getColumn(1).setPreferredWidth(100);
//		tableLaFs.setPreferredScrollableViewportSize(
//				new java.awt.Dimension(250, 150));

		buttonAddLaF.setActionCommand(CMD_ADD_LAF);
		buttonAddAllLaFs.setActionCommand(CMD_ADD_ALLLAFS);
		buttonRemLaF.setActionCommand(CMD_REM_LAF);
		buttonRemAllLaFs.setActionCommand(CMD_REM_ALLLAFS);

		buttonAddLaF.addActionListener(this);
		buttonAddAllLaFs.addActionListener(this);
		buttonRemLaF.addActionListener(this);
		buttonRemAllLaFs.addActionListener(this);

		textMsg.setOpaque(false);
		textMsg.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		textMsg.setRows(2);
		textMsg.setText("Ok");

		// set tool tips:
		buttonFilterBegin.setToolTipText("Match at beginning of class name");
		buttonFilterEnd.setToolTipText("Match at end of class name");
		buttonFilterInside.setToolTipText("Match anywhere in class name");
		buttonAddLaF.setToolTipText("Add selected class to Look-and-Feel table");
		buttonAddAllLaFs.setToolTipText("Add all listed classes to Look-and-Feel table");
		buttonRemLaF.setToolTipText("Remove selected entry from Look-and-Feel table");
		buttonRemAllLaFs.setToolTipText("Remove all entries from Look-and-Feel table");
		scrollLaFs.setToolTipText("List of Look-and-Feels which shall be installed");
		scrollClasses.setToolTipText("List of potentially available Look-and-Feels");
		
		this.pack();
		this.setLocationRelativeTo(this.getOwner());
		this.setModal(true);
	}

	protected void errorMsg(String msg, boolean error) {
		textMsg.setText(msg);
		textMsg.setForeground(error ? Color.RED : Color.BLACK);
	}
	
	protected String getDisplayNameFromClassName(String className) {
		String displayName = className;
		String domainName;
		int dot1, dot2;
		dot1 = className.indexOf(".");
		dot2 = className.indexOf(".", dot1+1);
		if (dot1>=0 && dot2>dot1) {
			domainName = className.substring(dot1+1, dot2);
		} else {
			dot1 = className.lastIndexOf(".");
			if (dot1>=0) {
				domainName = className.substring(0, dot1);
			} else {
				domainName = "";
			}
		}
		if (domainName.length()>0) domainName += "-";

		// Remove the LookAndFeel part:
		if (displayName.endsWith("LookAndFeel")) {
			displayName = displayName.substring(
					0, 
					displayName.length() - "LookAndFeel".length());
		}
		// remove the package part:
		int lastDotPos = displayName.lastIndexOf(".");
		if (lastDotPos>=0) {
			displayName = displayName.substring(lastDotPos+1);
		}
		if (displayName.length()<1) {
			displayName = className;
		} else {
			displayName = domainName + displayName;
		}
		return displayName;
	}
	
	/**
	 * @param ev
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent ev) {
		String cmd = ev.getActionCommand();
		errorMsg("Ok", false);
		if (cmd.equals(CMD_OK)) {
			// do OK
			// prepare LaF list!
			resultDisplayNames = new ArrayList<String>();
			resultClassNames = new ArrayList<String>();
			for (int i = 0; i < tableDataLaFs.length; i++) {
				resultDisplayNames.add(tableDataLaFs[i][0]);
				resultClassNames.add(tableDataLaFs[i][1]);
			}

			setResultCode(RESULT_OK);
			setVisible(false);
		} else if (cmd.equals(CMD_CANCEL)) {
			// do Cancel
			setResultCode(RESULT_CANCEL);
			setVisible(false);
		} else if (cmd.equals(CMD_ADD_LAF)) {
			String className = (String)listClasses.getSelectedValue();
			if (className==null) {
				errorMsg("No class selected from list.", true);
				return;
			}
			String displayName = getDisplayNameFromClassName(className);
			lafTableAdd(displayName, className);
		} else if (cmd.equals(CMD_ADD_ALLLAFS)) {
			lafTableNotify(false);
			int count = listModel.getSize();
			for (int i = 0; i < count; i++) {
				String className = (String)listModel.getElementAt(i);
				String displayName = getDisplayNameFromClassName(className);
				lafTableAdd(displayName, className);
			}
			lafTableNotify(true);
		} else if (cmd.equals(CMD_REM_LAF)) {
			int sel = tableLaFs.getSelectedRow();
			if (sel<0) {
				errorMsg("No entry selected in Look-and-Feel table.", true);
				return;
			}
			lafTableRemove(sel);
		} else if (cmd.equals(CMD_REM_ALLLAFS)) {
			lafTableRemoveAll();
		} else {
			// nothing
		}
	}

	protected void lafTableNotify(boolean notify) {
		this.lafTableNotifyFlag = notify;
		if (notify) {
			tableModelLaFs.fireTableStructureChanged();
		}
	}

	protected void lafTableAdd(String displayName, String className) {
		String [][] newdata = new String[tableDataLaFs.length+1][2];
		for (int i = 0; i < tableDataLaFs.length; i++) {
			newdata[i][0] = tableDataLaFs[i][0];
			newdata[i][1] = tableDataLaFs[i][1];
		}
		newdata[newdata.length-1][0] = displayName;
		newdata[newdata.length-1][1] = className;
		tableDataLaFs = newdata;
		tableModelLaFs.setData(tableDataLaFs);
		if (lafTableNotifyFlag) {
			tableModelLaFs.fireTableRowsInserted(
					newdata.length-1, newdata.length-1);
		}
	}

	protected void lafTableRemove(int index) {
		int newcount = tableDataLaFs.length-1;
		String [][] newdata = new String[newcount][2];
		if (newcount>0) { 
			int newidx = 0;
			for (int i = 0; i < index; i++) {
				newdata[newidx][0] = tableDataLaFs[i][0];
				newdata[newidx][1] = tableDataLaFs[i][1];
				newidx++;
			}
			for (int i = index+1; i < tableDataLaFs.length; i++) {
				newdata[newidx][0] = tableDataLaFs[i][0];
				newdata[newidx][1] = tableDataLaFs[i][1];
				newidx++;
			}
		}
		tableDataLaFs = newdata;
		tableModelLaFs.setData(tableDataLaFs);
		if (lafTableNotifyFlag) {
			tableModelLaFs.fireTableRowsDeleted(
					index, index);
		}
	}

	protected void lafTableRemoveAll() {
		String [][] newdata = new String[0][2];
		tableDataLaFs = newdata;
		tableModelLaFs.setData(tableDataLaFs);
		if (lafTableNotifyFlag) {
			tableModelLaFs.fireTableStructureChanged();
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

	/**
	 * @return	Returns the number of entries for new LaFs.
	 */
	public int getResultNamesCount() {
		if (resultDisplayNames==null) return 0;
		return resultDisplayNames.size();
	}

	/**
	 * @param index
	 * @return	Returns the display name at the given index.
	 */
	public String getResultDisplayName(int index) {
		if (resultDisplayNames==null) return null;
		return resultDisplayNames.get(index);
	}

	/**
	 * @param index
	 * @return	Returns the class name at the given index.
	 */
	public String getResultClassName(int index) {
		if (resultClassNames==null) return null;
		return resultClassNames.get(index);
	}

	/**
	 * @return	Returns the list of new Display Names
	 */
	public String[] getResultDisplayNames() {
		String [] res = null;
		if (resultDisplayNames==null) return res;
		res = new String[resultDisplayNames.size()];
		return resultDisplayNames.toArray(res);
	}

	/**
	 * @return	Returns the list of new Class Names
	 */
	public String[] getResultClassNames() {
		String [] res = null;
		if (resultClassNames==null) return res;
		res = new String[resultClassNames.size()];
		return resultClassNames.toArray(res);
	}
}
