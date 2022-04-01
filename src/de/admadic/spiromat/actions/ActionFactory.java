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
package de.admadic.spiromat.actions;

import java.awt.event.ActionEvent;
import java.util.HashMap;

import javax.swing.Action;

import de.admadic.spiromat.log.Logger;

/**
 * @author Rainer Schwarze
 *
 */
public class ActionFactory {
	static Logger logger = Logger.getLogger(ActionFactory.class);
	
	static private HashMap<String, Action> actions = new HashMap<String, Action>();

	/** id for about action */
	public static final String ABOUT_ACTION = "about"; //$NON-NLS-1$
	/** id for new document action */
	public static final String NEW_DOC_ACTION = "newDoc"; //$NON-NLS-1$
	/** id for open document action */
	public static final String OPEN_DOC_ACTION = "openDoc"; //$NON-NLS-1$
	/** id for save document action */
	public static final String SAVE_DOC_ACTION = "saveDoc"; //$NON-NLS-1$
	/** id for save as document action */
	public static final String SAVE_AS_DOC_ACTION = "saveAsDoc"; //$NON-NLS-1$
	/** id for export document action */
	public static final String EXPORT_DOC_ACTION = "exportDoc"; //$NON-NLS-1$
	/** id for adding a new figure action */
	public static final String ADD_FIGURE_ACTION = "addFigure"; //$NON-NLS-1$
	/** id for removing a figure action */
	public static final String REMOVE_FIGURE_ACTION = "removeFigure"; //$NON-NLS-1$
	/** id for move figure up action */
	public static final String MOVE_FIGURE_UP_ACTION = "moveFigureUp"; //$NON-NLS-1$
	/** id for move figure down action */
	public static final String MOVE_FIGURE_DOWN_ACTION = "moveFigureDown"; //$NON-NLS-1$
	/** id for animating figure action */
	public static final String ANIMATE_ACTION = "animate"; //$NON-NLS-1$
	/** id for mouse controlled figure action */
	public static final String MOUSE_CONTROLLED_ACTION = "mouseControlled"; //$NON-NLS-1$
	/** id for clear figure action */
	public static final String CLEAR_ACTION = "clear"; //$NON-NLS-1$
	/** id for fill figure action */
	public static final String FILL_ACTION = "fill"; //$NON-NLS-1$
	/** id for close action */
	public static final String CLOSE_ACTION = "close"; //$NON-NLS-1$
	/** id for show picture action */
	public static final String SHOW_PICTURE_ACTION = "showPicture"; //$NON-NLS-1$
	/** id for show figure action */
	public static final String SHOW_FIGURE_ACTION = "showFigure"; //$NON-NLS-1$
	/** id for show gears action */
	public static final String SHOW_GEARS_ACTION = "showGears"; //$NON-NLS-1$
	/** id for auto fill action */
	public static final String AUTO_FILL_ACTION = "autoFill"; //$NON-NLS-1$
	/** id for antialias action */
	public static final String ANTIALIAS_ACTION = "antialias"; //$NON-NLS-1$
	/** id for outer gear value action */
	public static final String OUTER_GEAR_VALUE_ACTION = "outerGearValue"; //$NON-NLS-1$
	/** id for inner gear value action */
	public static final String INNER_GEAR_VALUE_ACTION = "innerGearValue"; //$NON-NLS-1$
	/** id for pen hole pos value action */
	public static final String PEN_HOLE_POS_VALUE_ACTION = "penHolePosValue"; //$NON-NLS-1$

	static private HashMap<String, Class<? extends Action>> actionClasses = new HashMap<String, Class<? extends Action>>();

	static {
		actionClasses.put(ABOUT_ACTION, AboutAction.class);
		actionClasses.put(NEW_DOC_ACTION, NewDocAction.class);
		actionClasses.put(OPEN_DOC_ACTION, OpenDocAction.class);
		actionClasses.put(SAVE_DOC_ACTION, SaveDocAction.class);
		actionClasses.put(SAVE_AS_DOC_ACTION, SaveAsDocAction.class);
		actionClasses.put(EXPORT_DOC_ACTION, ExportDocAction.class);
		actionClasses.put(ADD_FIGURE_ACTION, AddFigureAction.class);
		actionClasses.put(REMOVE_FIGURE_ACTION, RemoveFigureAction.class);
		actionClasses.put(MOVE_FIGURE_UP_ACTION, MoveFigureUpAction.class);
		actionClasses.put(MOVE_FIGURE_DOWN_ACTION, MoveFigureDownAction.class);
		actionClasses.put(ANIMATE_ACTION, AnimateAction.class);
		actionClasses.put(MOUSE_CONTROLLED_ACTION, MouseControlledAction.class);
		actionClasses.put(CLEAR_ACTION, ClearAction.class);
		actionClasses.put(FILL_ACTION, FillAction.class);
		actionClasses.put(SHOW_FIGURE_ACTION, ShowFigureAction.class);
		actionClasses.put(SHOW_PICTURE_ACTION, ShowPictureAction.class);
		actionClasses.put(SHOW_GEARS_ACTION, ShowGearsAction.class);
		actionClasses.put(AUTO_FILL_ACTION, AutoFillAction.class);
		actionClasses.put(ANTIALIAS_ACTION, AntialiasAction.class);
		actionClasses.put(OUTER_GEAR_VALUE_ACTION, OuterRadiusValueAction.class);
		actionClasses.put(INNER_GEAR_VALUE_ACTION, InnerRadiusValueAction.class);
		actionClasses.put(PEN_HOLE_POS_VALUE_ACTION, PenHolePosValueAction.class);
		actionClasses.put(CLOSE_ACTION, CloseAction.class);
	}
	
	/**
	 * @param id
	 * @return	Returns the action for the given id.
	 */
	public static Action create(String id) {
		Class<? extends Action> cls = actionClasses.get(id);
		if (cls==null) {
			logger.error("no class registered for action with id " + id); //$NON-NLS-1$
			throw new Error("no class registered for action with id " + id); //$NON-NLS-1$
		}
		Action action = null;
		try {
			action = cls.newInstance();
		} catch (InstantiationException e) {
			// e.printStackTrace();
			logger.error("error instantiating Action class for id " + id, e); //$NON-NLS-1$
			throw new Error("error instantiating Action class for id " + id, e); //$NON-NLS-1$
		} catch (IllegalAccessException e) {
			// e.printStackTrace();
			logger.error("error instantiating Action class for id " + id, e); //$NON-NLS-1$
			throw new Error("error instantiating Action class for id " + id, e); //$NON-NLS-1$
		}
		return action;
	}

	/**
	 * @param id
	 * @return	Returns the action for the given id.
	 */
	public static Action get(String id) {
		if (!actions.containsKey(id)) {
			actions.put(id, create(id));
		}
		return actions.get(id);
	}

	/**
	 * @param src
	 * @param cmd
	 * @return	Returns the ActionEvent created from the given parameters.
	 */
	public static ActionEvent createEvent(Object src, String cmd) {
		return new ActionEvent(
				src, ActionEvent.RESERVED_ID_MAX + 1, cmd);	
	}
}
