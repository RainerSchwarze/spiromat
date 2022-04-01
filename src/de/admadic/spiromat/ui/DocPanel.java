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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.admadic.spiromat.actions.ActionFactory;
import de.admadic.spiromat.actions.MoveFigureDownAction;
import de.admadic.spiromat.actions.MoveFigureUpAction;
import de.admadic.spiromat.actions.RemoveFigureAction;
import de.admadic.spiromat.log.Logger;
import de.admadic.spiromat.model.AppModel;
import de.admadic.spiromat.model.DocModel;
import de.admadic.spiromat.model.FigureSpec;
import de.admadic.spiromat.model.ModelPropertyChangeListener;
import de.admadic.spiromat.model.ModelPropertyChangeSupport;

/**
 * @author Rainer Schwarze
 *
 */
public class DocPanel extends JPanel implements ModelPropertyChangeListener {
	static Logger logger = Logger.getLogger(DocPanel.class);
	
	/** */
	private static final long serialVersionUID = 1L;

	DocModel docModel;
	private JLabel figuresLabel;
	FigureTableModel figureTableModel;
	JTable figureTable;
	private JScrollPane figureScroller;

	private JButton btnAddFigure;
	private JButton btnRemoveFigure;
	private JButton btnMoveUp;
	private JButton btnMoveDown;
	
	ModelPropertyChangeSupport modelPropSupport;

	/**
	 */
	public DocPanel() {
		super();
		// do not yet attach - we do it when all controls have been set up:
		modelPropSupport = new ModelPropertyChangeSupport(
				ModelPropertyChangeSupport.MASK_ALL, this, false);
		UIManager.getDefaults().put(
				"Table.font",  //$NON-NLS-1$
				UIManager.getFont("Table.font").deriveFont(14.0f)); //$NON-NLS-1$

		FormLayout fl = new FormLayout(
				"0px, d:grow(0.25), 5px, d:grow(0.25), 5px, d:grow(0.25), 5px, d:grow(0.25), 0px", //$NON-NLS-1$
				"0px, d, 5px, d, 5px, d:grow, 0px"); //$NON-NLS-1$
		fl.setColumnGroups(new int[][]{{2, 4, 6, 8}});
		this.setLayout(fl);
		CellConstraints cc = new CellConstraints();
		
		figuresLabel = new JLabel("Figures:"); //$NON-NLS-1$
		figureTableModel = new FigureTableModel();

		figureTable = new JTable(figureTableModel);
		figureTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		figureTable.setShowVerticalLines(false);
		figureTable.setDefaultRenderer(Color.class, new ColorCellRenderer());
		figureTable.setDefaultRenderer(FigureSpec.class, new FigureSpecTableCellRenderer());
		figureTable.setDefaultEditor(Color.class, new ColorCellEditor());
		HeaderRenderer hr = new HeaderRenderer();
		figureTable.getColumnModel().getColumn(0).setPreferredWidth(10);
		figureTable.getColumnModel().getColumn(1).setPreferredWidth(80);
		figureTable.getColumnModel().getColumn(2).setPreferredWidth(10);
		figureTable.getColumnModel().getColumn(0).setHeaderRenderer(hr);
		figureTable.getColumnModel().getColumn(1).setHeaderRenderer(hr);
		figureTable.getColumnModel().getColumn(2).setHeaderRenderer(hr);

		figureTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		figureTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				// note: we only change the active figure spec, if:
				// - selection is finished (no mouse moving)
				// - new index is different from old
				if (e.getValueIsAdjusting()) return;
				int sel = figureTable.getSelectedRow();
				if (sel==docModel.getActiveFigureIndex()) return;
				docModel.setActiveFigureIndex(sel);
			}
		});

		figureScroller = new JScrollPane(
				figureTable,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		figuresLabel.setLabelFor(figureTable);

		this.add(figuresLabel, cc.xywh(2, 2, 7, 1));
		this.add(btnAddFigure = Util.createJButton(ActionFactory.get(ActionFactory.ADD_FIGURE_ACTION)), cc.xy(2, 4));
		this.add(btnRemoveFigure = Util.createJButton(ActionFactory.get(ActionFactory.REMOVE_FIGURE_ACTION)), cc.xy(4, 4));
		this.add(btnMoveUp = Util.createJButton(ActionFactory.get(ActionFactory.MOVE_FIGURE_UP_ACTION)), cc.xy(6, 4));
		this.add(btnMoveDown = Util.createJButton(ActionFactory.get(ActionFactory.MOVE_FIGURE_DOWN_ACTION)), cc.xy(8, 4));
		this.add(figureScroller, cc.xywh(2, 6, 7, 1, CellConstraints.FILL, CellConstraints.FILL));

		((MoveFigureUpAction)ActionFactory.get(
				ActionFactory.MOVE_FIGURE_UP_ACTION)).setListModelRef(
						figureTable.getSelectionModel());
		((MoveFigureDownAction)ActionFactory.get(
				ActionFactory.MOVE_FIGURE_DOWN_ACTION)).setListModelRef(
						figureTable.getSelectionModel());
		((RemoveFigureAction)ActionFactory.get(
				ActionFactory.REMOVE_FIGURE_ACTION)).setListModelRef(
						figureTable.getSelectionModel());
		
		figureScroller.setPreferredSize(new Dimension(150, 100));

		docModel = AppModel.getInstance().getDocModel();
		updateDataModel();
		logger.debug("attaching modelPropSupport"); //$NON-NLS-1$
		modelPropSupport.attachToAppModel();
	}

	/**
	 * 
	 */
	protected void updateFigModel() {
		int sel = docModel.getActiveFigureIndex();
		if (sel<0) return;

		figureTableModel.fireTableRowsUpdated(sel, sel);
	}

	/**
	 * 
	 */
	protected void updateDataModel() {
		figureTableModel.setFigureSpecsRef(docModel.getFigureListReference());
	}

	/**
	 * @param enabled
	 * @see javax.swing.JComponent#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		figureTable.setEnabled(enabled);
	}


	/**
	 * @author Rainer Schwarze
	 */
	static class FigureTableModel extends AbstractTableModel {
		/** */
		private static final long serialVersionUID = 1L;
		private WeakReference<ArrayList<FigureSpec>> figureSpecsRef;

		private String [] columnNames = {Messages.getString("DocPanel.tableHeadingActive"), Messages.getString("DocPanel.tableHeadingParams"), Messages.getString("DocPanel.tableHeadingColor")}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		private Class<?> [] columnClasses = {
				Boolean.class,
				FigureSpec.class,
				Color.class,
		};
		// FIXME: table model for docpanel: active is not editable - we do it via selection
		private boolean [] columnEditables = { false, false, true };
		
		/**
		 * 
		 */
		public FigureTableModel() {
			super();
			figureSpecsRef = new WeakReference<ArrayList<FigureSpec>>(null);
		}

		/**
		 * @return the figureSpecsRef
		 */
		public WeakReference<ArrayList<FigureSpec>> getFigureSpecsRef() {
			return figureSpecsRef;
		}

		/**
		 * @return	Returns the referenced list of figurespecs - may be null.
		 */
		public ArrayList<FigureSpec> getFigureSpecs() {
			return figureSpecsRef.get();
		}

		/**
		 * @param figureSpecsRef the figureSpecsRef to set
		 */
		public void setFigureSpecsRef(
				WeakReference<ArrayList<FigureSpec>> figureSpecsRef) {
			logger.debug("setting new figure list in table model"); //$NON-NLS-1$
			this.figureSpecsRef = figureSpecsRef;
			ArrayList<FigureSpec> tmp = figureSpecsRef.get();
			if (tmp!=null)
				fireTableDataChanged();
		}

		/**
		 * @return	Returns the number of columns in this table model.
		 * @see javax.swing.table.TableModel#getColumnCount()
		 */
		public int getColumnCount() {
			return columnNames.length;
		}

		/**
		 * @return	Returns the number of rows.
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		public int getRowCount() {
			ArrayList<FigureSpec> l = getFigureSpecs();
			return (l==null) ? 0 : l.size();
		}

		/**
		 * @param rowIndex
		 * @param columnIndex
		 * @return	Returns the value for the specified cell.
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		public Object getValueAt(int rowIndex, int columnIndex) {
			ArrayList<FigureSpec> l = getFigureSpecs();
			if (l==null) return null;

			FigureSpec fs = l.get(rowIndex);
			switch (columnIndex) {
			case 0:
				return fs.isActive();
				// break;
			case 1:
				return fs;
				// break;
			case 2:
				return fs.getColor();
				// break;
			}
			return null;
		}

		/**
		 * @param columnIndex
		 * @return	Returns the class for the column.
		 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
		 */
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return columnClasses[columnIndex];
		}

		/**
		 * @param column
		 * @return	Returns the name for the specified column.
		 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
		 */
		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}

		/**
		 * @param rowIndex
		 * @param columnIndex
		 * @return	Returns true, if the cell is editable.
		 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
		 */
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if (
					columnIndex==0 && 
					rowIndex==AppModel.getInstance().getDocModel().getActiveFigureIndex()
				) {
				return false;
			}
			return columnEditables[columnIndex];
		}

		/**
		 * @param aValue
		 * @param rowIndex
		 * @param columnIndex
		 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
		 */
		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			ArrayList<FigureSpec> l = getFigureSpecs();
			if (l==null) return;
			FigureSpec fs = l.get(rowIndex);
			switch (columnIndex) {
			case 0:
				if (!fs.isActive()) {
					AppModel.getInstance().getDocModel().setActiveFigureIndex(rowIndex);
				}
				// special handling for active transfer
				break;
			case 1:
				// invalid!
				break;
			case 2:
				fs.setColor((Color)aValue);
				break;
			}
			fireTableCellUpdated(rowIndex, columnIndex);
		}
	}
	
	static class HeaderRenderer extends DefaultTableCellRenderer {
		/** */
		private static final long serialVersionUID = 1L;

		Color fgcolor = UIManager.getColor("TableHeader.foreground"); //$NON-NLS-1$
		Color bgcolor = UIManager.getColor("TableHeader.background"); //$NON-NLS-1$

		/**
		 * @param table
		 * @param value
		 * @param isSelected
		 * @param hasFocus
		 * @param row
		 * @param column
		 * @return	Returns the component used to render the cell.
		 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
		 */
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
					row, column);
			JLabel l = (JLabel)c;
			l.setBackground(bgcolor);
			l.setForeground(fgcolor);
			return c;
		}
	
	}

	/**
	 * @param e
	 * @see de.admadic.spiromat.model.ModelPropertyChangeListener#appPropertyChange(java.beans.PropertyChangeEvent)
	 */
	public void appPropertyChange(PropertyChangeEvent e) {
		String propName = e.getPropertyName();
		if (propName!=null) {
			if (propName.equals(AppModel.DOC_MODEL)) {
				logger.debug("detected docModel change -> updating data model"); //$NON-NLS-1$
				docModel = AppModel.getInstance().getDocModel();
				updateDataModel();
			}
		}
	}

	/**
	 * @param e
	 * @see de.admadic.spiromat.model.ModelPropertyChangeListener#docPropertyChange(java.beans.PropertyChangeEvent)
	 */
	public void docPropertyChange(PropertyChangeEvent e) {
		String propName = e.getPropertyName();
		if (propName!=null) {
			if (propName.equals(DocModel.PROPERTY_FIGURE_LIST)) {
				updateDataModel();
			} else if (propName.equals(DocModel.PROPERTY_FIGURE_LIST_CONTENT_CHANGED)) {
				// FIXME: sledge hammer is too heavy:
				figureTableModel.fireTableRowsUpdated(0, figureTableModel.getRowCount()-1);
			} else if (propName.equals(DocModel.PROPERTY_ACTIVE_FIGURE)) {
				// FIXME: this is a bit of a sledge hammer:
				figureTableModel.fireTableRowsUpdated(
						0, figureTableModel.getRowCount()-1);
			}
		}
	}

	/**
	 * @param e
	 * @see de.admadic.spiromat.model.ModelPropertyChangeListener#figPropertyChange(java.beans.PropertyChangeEvent)
	 */
	public void figPropertyChange(PropertyChangeEvent e) {
		String propName = e.getPropertyName();
		if (propName!=null) {
			if (
					propName.equals(FigureSpec.PROP_OUTER_RADIUS) ||
					propName.equals(FigureSpec.PROP_INNER_RADIUS) ||
					propName.equals(FigureSpec.PROP_PEN_HOLE_POS) ||
					propName.equals(FigureSpec.PROP_COLOR)
					) {
				updateFigModel();
			}
		}
	}
}
