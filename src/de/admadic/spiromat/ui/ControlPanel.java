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
import java.beans.PropertyChangeEvent;
import java.util.HashMap;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.UIManager;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.admadic.spiromat.SpiromatException;
import de.admadic.spiromat.actions.ActionFactory;
import de.admadic.spiromat.log.Logger;
import de.admadic.spiromat.model.AppModel;
import de.admadic.spiromat.model.DocModel;
import de.admadic.spiromat.model.FigureSpec;
import de.admadic.spiromat.model.ModelPropertyChangeListener;
import de.admadic.spiromat.model.ModelPropertyChangeSupport;

/**
 * Provides a view of the control panel with buttons and value input fields.
 * 
 * @author Rainer Schwarze
 */
public class ControlPanel extends JPanel implements ModelPropertyChangeListener {
	/** */
	private static final long serialVersionUID = 1L;

	protected final static Logger logger = Logger.getLogger(ControlPanel.class);

	protected AppModel appModel;
	
	private DocPanel docPanel;
	
	JToggleButton btn_animated;
	JToggleButton btn_mouseControlled;
	JToggleButton btn_instantUpdate;

	JButton btn_clear;
	JButton btn_fill;

	SliderEdit sld_innerRadius;
	SliderEdit sld_outerRadius;
	SliderEdit sld_lambda;
	JButton btn_start;
	JButton btn_pause;

	JToggleButton btn_showGears;
	JToggleButton btn_showFigure;
	JToggleButton btn_showPicture;
	JToggleButton btn_autoFill;

	/* these are maps defining enable/disable state for components: */
	HashMap<IEnabledProxy, Boolean> instantMap;
	HashMap<IEnabledProxy, Boolean> mouseMap;
	HashMap<IEnabledProxy, Boolean> animatedMap;
	HashMap<IEnabledProxy, Boolean> previewMap;
	
	ModelPropertyChangeSupport modelPropChange = new ModelPropertyChangeSupport(this);
	
	/**
	 * @param appModel 
	 * @param docPanel 
	 */
	public ControlPanel(AppModel appModel, DocPanel docPanel) {
		super();
		this.appModel = appModel;
		this.docPanel = docPanel;

		UIManager.getDefaults().put(
				"ToolTip.font",   //$NON-NLS-1$
				UIManager.getFont("ToolTip.font").deriveFont(14.0f));  //$NON-NLS-1$
		createContents();
		initMaps();
		updateContentsValues();
		linkContents();
	}

	/**
	 * 
	 */
	private void initMaps() {
		instantMap = new HashMap<IEnabledProxy, Boolean>();
		mouseMap = new HashMap<IEnabledProxy, Boolean>();
		animatedMap = new HashMap<IEnabledProxy, Boolean>();
		previewMap = new HashMap<IEnabledProxy, Boolean>();

		Object [] mapCodes = {
				// action id							IMAP
				ActionFactory.OUTER_GEAR_VALUE_ACTION,	"YNNN",  //$NON-NLS-1$
				ActionFactory.INNER_GEAR_VALUE_ACTION,	"YNNN",  //$NON-NLS-1$
				ActionFactory.PEN_HOLE_POS_VALUE_ACTION,"YNNN",  //$NON-NLS-1$
				ActionFactory.SHOW_GEARS_ACTION, 		"YNNN",  //$NON-NLS-1$
				ActionFactory.SHOW_FIGURE_ACTION, 		"YNNN",  //$NON-NLS-1$
				ActionFactory.ANIMATE_ACTION, 			"YNYN", // <- ATN!  //$NON-NLS-1$
				ActionFactory.MOUSE_CONTROLLED_ACTION,	"YYNN", // <- ATN!  //$NON-NLS-1$
				ActionFactory.FILL_ACTION, 				"YNNN",  //$NON-NLS-1$
				ActionFactory.CLEAR_ACTION, 			"YNNN",  //$NON-NLS-1$
				ActionFactory.AUTO_FILL_ACTION, 		"YNNN",  //$NON-NLS-1$
				ActionFactory.SHOW_PICTURE_ACTION, 		"YNNY", // <- ATN!  //$NON-NLS-1$

				ActionFactory.ADD_FIGURE_ACTION, 		"YNNY", //<-+- ATN!  //$NON-NLS-1$
				ActionFactory.REMOVE_FIGURE_ACTION,		"YNNY", //  v  //$NON-NLS-1$
				ActionFactory.MOVE_FIGURE_UP_ACTION,	"YNNY",  //$NON-NLS-1$
				ActionFactory.MOVE_FIGURE_DOWN_ACTION,	"YNNY",  //$NON-NLS-1$

				ActionFactory.NEW_DOC_ACTION,			"YNNY",  //$NON-NLS-1$
				ActionFactory.OPEN_DOC_ACTION,			"YNNY",  //$NON-NLS-1$
				ActionFactory.SAVE_DOC_ACTION,			"YNNY",  //$NON-NLS-1$
				ActionFactory.SAVE_AS_DOC_ACTION,		"YNNY",  //$NON-NLS-1$
				ActionFactory.EXPORT_DOC_ACTION,		"YNNY",  //$NON-NLS-1$

				docPanel,								"YNNY",  //$NON-NLS-1$
		};

		for (int i=0; i<mapCodes.length; i+=2) {
			IEnabledProxy proxy = null;
			if (mapCodes[i+0] instanceof String) {
				Action action = ActionFactory.get((String) mapCodes[i+0]);
				proxy = EnabledProxyFactory.wrap(action);
			} else if (mapCodes[i+0] instanceof JComponent) {
				JComponent comp = (JComponent) mapCodes[i+0];
				proxy = EnabledProxyFactory.wrap(comp);
			} else {
				throw new SpiromatException("invalid enabled map key type");  //$NON-NLS-1$
			}
			String flags = (String)mapCodes[i+1];
			instantMap.put(
					proxy, flags.charAt(0)=='Y' ? Boolean.TRUE : Boolean.FALSE);
			mouseMap.put(
					proxy, flags.charAt(1)=='Y' ? Boolean.TRUE : Boolean.FALSE);
			animatedMap.put(
					proxy, flags.charAt(2)=='Y' ? Boolean.TRUE : Boolean.FALSE);
			previewMap.put(
					proxy, flags.charAt(3)=='Y' ? Boolean.TRUE : Boolean.FALSE);
		}
	}

	void updateComponentStatus(HashMap<IEnabledProxy, Boolean> map) {
		for (IEnabledProxy proxy : map.keySet()) {
			proxy.setEnabled(map.get(proxy).booleanValue());
		}
	}
	
	/**
	 * 
	 */
	private void createContents() {
		FormLayout fl = new FormLayout(
				"12px, d:grow(0.33), 5px, d:grow(0.33), 5px, d:grow(0.33), 12px",  //$NON-NLS-1$
				"12px, d, 5px, d, 5px, d, 10px, " +	// radii, lambda  //$NON-NLS-1$
				"d, 5px, d, 5px, d, 5px, " +		// anim, mouse + sep + options  //$NON-NLS-1$
				((docPanel!=null) ? "d, 5px, d:grow, 5px" : "") // sep, docpanel   //$NON-NLS-1$ //$NON-NLS-2$
				);
		fl.setColumnGroups(new int[][]{{2, 4, 6}});
		this.setLayout(fl);
		CellConstraints cc = new CellConstraints();
		
		this.add(
				sld_outerRadius = new SliderEdit(
						ActionFactory.get(ActionFactory.OUTER_GEAR_VALUE_ACTION), 
						4, 100),
				cc.xywh(2, 2, 5, 1));
		this.add(
				sld_innerRadius = new SliderEdit(
						ActionFactory.get(ActionFactory.INNER_GEAR_VALUE_ACTION), 
						4, 100),
				cc.xywh(2, 4, 5, 1));
		this.add(
				sld_lambda = new SliderEdit(
						ActionFactory.get(ActionFactory.PEN_HOLE_POS_VALUE_ACTION), 
						0, 100),
				cc.xywh(2, 6, 5, 1));

//		this.add(
//				new JSeparator(JSeparator.HORIZONTAL),
//				cc.xywh(2, 7, 5, 1));

		this.add(
				btn_mouseControlled = Util.createJToggleButton(
						ActionFactory.get(ActionFactory.MOUSE_CONTROLLED_ACTION)),
				cc.xywh(2, 8, 1, 1));
		this.add(
				btn_animated = Util.createJToggleButton(
						ActionFactory.get(ActionFactory.ANIMATE_ACTION)), 
				cc.xywh(4, 8, 1, 1));

		this.add(
				btn_autoFill = Util.createJToggleButton(
						ActionFactory.get(ActionFactory.AUTO_FILL_ACTION)),
				cc.xywh(6, 8, 1, 1));
//		this.add(
//				btn_showPicture = Util.createJToggleButton(
//						ActionFactory.get(ActionFactory.SHOW_PICTURE_ACTION)),
//				cc.xywh(8, 8, 1, 1));

		this.add(
				new JLabel(Messages.getString("ControlPanel.labelDisplayOptions")),  //$NON-NLS-1$
				cc.xywh(2, 10, 5, 1));

		this.add(
				btn_showFigure = Util.createJToggleButton(
						ActionFactory.get(ActionFactory.SHOW_FIGURE_ACTION)),
				cc.xywh(2, 12, 1, 1));
		this.add(
				btn_showGears = Util.createJToggleButton(
						ActionFactory.get(ActionFactory.SHOW_GEARS_ACTION)),
				cc.xywh(4, 12, 1, 1));
//		this.add(
//				btn_fill = Util.createJButton(
//						ActionFactory.get(ActionFactory.FILL_ACTION)),
//				cc.xywh(4, 12, 1, 1));
		this.add(
				btn_clear = Util.createJButton(
						ActionFactory.get(ActionFactory.CLEAR_ACTION)),
				cc.xywh(6, 12, 1, 1));

		if (this.docPanel!=null) {
			this.add(
					new JSeparator(JSeparator.HORIZONTAL),
					cc.xywh(2, 14, 5, 1));
			this.add(
					this.docPanel,
					cc.xywh(2, 16, 5, 1, CellConstraints.FILL, CellConstraints.FILL));
		}

		sld_innerRadius.setFont(sld_innerRadius.getFont().deriveFont(12.0f)); //.deriveFont(Font.BOLD));  //$NON-NLS-1$
		sld_outerRadius.setFont(sld_outerRadius.getFont().deriveFont(12.0f)); //.deriveFont(Font.BOLD));  //$NON-NLS-1$
		sld_lambda.setFont(sld_lambda.getFont().deriveFont(12.0f)); //.deriveFont(Font.BOLD));  //$NON-NLS-1$
		
		sld_innerRadius.setToolTipText(Messages.getString("ControlPanel.toolTipInnerGear"));  //$NON-NLS-1$
		sld_outerRadius.setToolTipText(Messages.getString("ControlPanel.toolTipOuterGear"));  //$NON-NLS-1$
		sld_lambda.setToolTipText(Messages.getString("ControlPanel.toolTipPenHolePos"));  //$NON-NLS-1$
	}

	/**
	 * @return	Returns the AppModel.
	 */
	protected AppModel getModel() {
		return appModel;
	}

	/**
	 * 
	 */
	private void linkContents() {
		getModel().addComponent(AppModel.OUTER_RADIUS, sld_outerRadius);
		getModel().addComponent(AppModel.INNER_RADIUS, sld_innerRadius);
		getModel().addComponent(AppModel.LAMBDA, sld_lambda);

		getModel().addComponent(AppModel.MOUSE_CONTROLLED, btn_mouseControlled);
		getModel().addComponent(AppModel.ANIMATED, btn_animated);

		getModel().addComponent(AppModel.SHOW_GEARS, btn_showGears);
		getModel().addComponent(AppModel.SHOW_FIGURE, btn_showFigure);
		getModel().addComponent(AppModel.AUTO_FILL, btn_autoFill);
	}

	/**
	 * 
	 */
	private void updateContentsValues() {
		sld_outerRadius.setValue(appModel.getOuterRadius());
		sld_innerRadius.setValue(appModel.getInnerRadius());
		sld_lambda.setValue((int) (appModel.getLambda()*100));

		btn_showFigure.setSelected(appModel.getShowFigure());
		btn_showGears.setSelected(appModel.getShowGears());

		btn_autoFill.setSelected(appModel.getAutoFill());
	}

	/**
	 * @param e
	 * @see de.admadic.spiromat.model.ModelPropertyChangeListener#appPropertyChange(java.beans.PropertyChangeEvent)
	 */
	public void appPropertyChange(PropertyChangeEvent e) {
		String n = e.getPropertyName();
		if (n.equals(AppModel.MOUSE_CONTROLLED)) {
			if (getModel().getMouseControlled()) {
				logger.debug("turning buttons off");  //$NON-NLS-1$
				if (btn_start!=null) btn_start.setEnabled(true);
				if (btn_pause!=null) btn_pause.setEnabled(false);
				updateComponentStatus(mouseMap);
			}
		} else if (n.equals(AppModel.ANIMATED)) {
			if (getModel().getAnimated()) {
				logger.debug("turning buttons on");  //$NON-NLS-1$
				if (btn_start!=null) btn_start.setEnabled(true);
				if (btn_pause!=null) btn_pause.setEnabled(true);
				updateComponentStatus(animatedMap);
			}
		} else if (n.equals(AppModel.INSTANT_UPDATE)) {
			if (getModel().getInstantUpdate()) {
				updateComponentStatus(instantMap);
			}
		} else if (n.equals(AppModel.SHOW_PICTURE)) {
			if (getModel().getShowPicture()) {
				updateComponentStatus(previewMap);
			} else {
				updateComponentStatus(instantMap);
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
			if (propName.equals(DocModel.PROPERTY_ACTIVE_FIGURE)) {
				updateParamsForFigureSpec(
						AppModel.getInstance().getDocModel().getActiveFigureSpec());
			}
		}
	}

	/**
	 * @param figureSpec
	 */
	private void updateParamsForFigureSpec(FigureSpec figureSpec) {
		if (figureSpec==null) return;

		DocModel docModel = AppModel.getInstance().getDocModel();
		docModel.setActiveFigureSpecLocked(true);
		sld_outerRadius.setValue(figureSpec.getOuterRadius());
		sld_innerRadius.setValue(figureSpec.getInnerRadius());
		sld_lambda.setValue((int) (figureSpec.getPenHolePos() * 100));
		docModel.setActiveFigureSpecLocked(false);
	}

	/**
	 * @param e
	 * @see de.admadic.spiromat.model.ModelPropertyChangeListener#figPropertyChange(java.beans.PropertyChangeEvent)
	 */
	public void figPropertyChange(PropertyChangeEvent e) {
		/* nothing */
	}
}
