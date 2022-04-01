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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.admadic.spiromat.SpiromatException;
import de.admadic.spiromat.log.Logger;
import de.admadic.spiromat.math.SpiroMath;
import de.admadic.spiromat.math.Util;
import de.admadic.spiromat.model.AppModel;
import de.admadic.spiromat.model.DocModel;
import de.admadic.spiromat.model.FigureSpec;
import de.admadic.spiromat.model.InnerGearSpec;
import de.admadic.spiromat.model.ModelUtil;
import de.admadic.spiromat.model.OuterGearSpec;
import de.admadic.spiromat.shapes.Drawable;
import de.admadic.spiromat.shapes.FigureView;
import de.admadic.spiromat.shapes.InnerGearView;
import de.admadic.spiromat.shapes.OuterGearView;
import de.admadic.spiromat.ui.SpiromatCanvas;
import de.admadic.spiromat.util.Knob;
import de.admadic.spiromat.util.PrimitiveTimerProbe;

/**
 * Provides a Spiromat Machine which instantly draws the figure without 
 * animation or other control.
 * 
 * @author Rainer Schwarze
 */
public class Machine extends AbstractMachine {
	private final static Logger logger = Logger.getLogger(Machine.class);

	// This is used to get rid of excess render requests. On slow machines,
	// there may be queued in more requests than can be handled in the time
	// frame. In that case this instance marks the occurence of requests and 
	// forwards a repaint request after a certain hold off time.
	private DebounceRunner renderTrigger;

	private HashMap<FigureSpec,FigureView> figureList = 
		new HashMap<FigureSpec, FigureView>();	// the list of all figures
	private FigureView figure;	// the figure to be drawn - nothing else
	private OuterGearView outerGear;		// the outer (negative) gear
	private InnerGearView innerGear;		// the inner gear (with pen hole)
	private SpiroMath spiroMath;		// module calculating the details
	private Knob knob;					// calculates angles from mouse

	private OuterGearSpec outerGearSpec;
	private InnerGearSpec innerGearSpec;

	private ArrayList<Drawable> drawables = new ArrayList<Drawable>();
	
	AbstractDriver instantDriver;
	AbstractDriver mouseDriver;
	AbstractDriver animatedDriver;

	AbstractDriver driver;

	/**
	 * Creates an instance of the InstantUpdateMachine.
	 * This method initializes the figure and registeres this machine in
	 * the SpiromatCanvas. The rendering is initialized and the
	 * figure is once drawn.
	 * 
	 * @param canvas
	 */
	public Machine(SpiromatCanvas canvas) {
		super(canvas);
		logger.debug("created instance"); //$NON-NLS-1$

		renderTrigger = new DebounceRunner(
				new Runnable() {
					public void run() {
						renderFull();
					}
				}, 
				true);	// FIXME: would false be ok here?

		instantDriver = new InstantUpdateDriver(this);
		mouseDriver = new MouseControlledDriver(this);
		animatedDriver = new AnimatedDriver(this);

		// create the common gears:
		innerGearSpec = ModelUtil.createStandardInnerGearSpec();
		outerGearSpec = ModelUtil.createStandardOuterGearSpec();
		
		installDriver(instantDriver, false);
	}

	/**
	 * @return the drawables
	 */
	public List<Drawable> getDrawables() {
		return drawables;
	}

	/**
	 * 
	 * @see de.admadic.spiromat.machines.AbstractMachine#attachToModel()
	 */
	@Override
	public void attachToModel() {
		logger.debug("attaching to model"); //$NON-NLS-1$
		outerGear = new OuterGearView(outerGearSpec);
		innerGear = new InnerGearView(innerGearSpec);
		spiroMath = ModelUtil.createStandardSpiroMath();
		knob = new Knob();

		super.attachToModel();

		updateFigureList();

		// set current display situation:
		outerGear.setVisible(getAppModel().getShowGears());
		innerGear.setVisible(getAppModel().getShowGears());
		if (figure!=null) figure.setVisible(true); // getAppModel().getShowFigureActually());

		attachGears();
		
		getCanvas().prepareRender();
		renderFull();
	}

	/**
	 * 
	 */
	private void updateFigureList() {
		logger.debug("updating figureList"); //$NON-NLS-1$
		DocModel docModel = getDocModel();
		figureList.clear();
		drawables.clear();
		figure = null;
		int count = docModel.getFigureSpecCount();
		int actf = docModel.getActiveFigureIndex();
		for (int i=0; i<count; i++) {
			FigureSpec fs = docModel.getFigureSpec(i);
			FigureView f;
			f = new FigureView(fs);
			figureList.put(fs, f);
			if (i==actf && !AppModel.getInstance().getShowPicture()) {
				figure = f;
			} else {
				drawables.add(f);
			}
		}
		if (!AppModel.getInstance().getShowPicture()) {
			if (figure!=null) 
				drawables.add(figure);
			drawables.add(outerGear);
			drawables.add(innerGear);
		}
	}

	/**
	 * 
	 */
	private void updateViewStatus() {
		boolean b = getAppModel().getShowFigure();
		if (getAppModel().getShowPicture()) {
			b = true;
		}
		for (FigureView f : figureList.values()) {
			if (f!=figure) {
				f.setVisible(b);
			}
		}
		if (figure!=null) {
			figure.setVisible(true); // (<- always) getAppModel().getShowFigureActually());
		}
	}

	/**
	 * 
	 * @see de.admadic.spiromat.machines.AbstractMachine#detachFromModel()
	 */
	@Override
	public void detachFromModel() {
		logger.debug("detaching from model"); //$NON-NLS-1$
		super.detachFromModel();
		figureList.clear();
		drawables.clear();
		outerGear = null;
		innerGear = null;
		figure = null;
		spiroMath = null;
		knob = null;
	}

	/**
	 * @param newDriver
	 * @param resetGears 
	 */
	private void installDriver(AbstractDriver newDriver, boolean resetGears) {
		logger.debug("installing new driver"); //$NON-NLS-1$
		AbstractDriver oldDriver = driver;
		driver = newDriver;

		if (oldDriver!=null) oldDriver.detach();
		// if (resetGears) resetGears();
		if (newDriver!=null) newDriver.attach();
	}

	/**
	 * @return the knob
	 */
	public Knob getKnob() {
		return knob;
	}

	/**
	 * @return the spiroMath
	 */
	public SpiroMath getSpiroMath() {
		return spiroMath;
	}

	/**
	 * 
	 * @see de.admadic.spiromat.machines.AbstractMachine#pauseMachine()
	 */
	@Override
	public void pauseMachine() {
		logger.debug("pauseMachine"); //$NON-NLS-1$
		/* nothing */
	}

	/**
	 * 
	 * @see de.admadic.spiromat.machines.AbstractMachine#resetMachine()
	 */
	@Override
	public void resetMachine() {
		logger.debug("resetMachine"); //$NON-NLS-1$
		/* nothing */
	}

	/**
	 * 
	 * @see de.admadic.spiromat.machines.AbstractMachine#startMachine()
	 */
	@Override
	public void startMachine() {
		logger.debug("startMachine"); //$NON-NLS-1$
		/* nothing */
	}

	/**
	 * 
	 * @see de.admadic.spiromat.machines.AbstractMachine#stopMachine()
	 */
	@Override
	public void stopMachine() {
		logger.debug("stopMachine"); //$NON-NLS-1$
		/* nothing */
	}

	/**
	 * 
	 * @see de.admadic.spiromat.machines.AbstractMachine#continueMachine()
	 */
	@Override
	public void continueMachine() {
		logger.debug("continueMachine"); //$NON-NLS-1$
		/* nothing */
	}

	/**
	 * 
	 * @see de.admadic.spiromat.machines.AbstractMachine#destroyMachine()
	 */
	@Override
	public void destroyMachine() {
		logger.debug("destroyMachine"); //$NON-NLS-1$
		renderTrigger.stopThread();
	}

	/**
	 * Calculates the new figure data, updates the Figure instance and issues
	 * a render of the canvas.
	 */
	public void renderFull() {
		int rounds = Util.calculateRounds(
				getAppModel().getOuterRadius(), 
				getAppModel().getInnerRadius());

		logger.debug("renderFull: (using " + rounds + " rounds)"); //$NON-NLS-1$ //$NON-NLS-2$

		if (getAppModel().getHoldFigure()) {
			// nothing
		} else {
			// - actually reset the canvas:
			logger.debug("calling canvas.resetRender"); //$NON-NLS-1$
			getCanvas().resetRender();

			attachGears();
		}

		logger.debug("calling canvas.clear"); //$NON-NLS-1$
		getCanvas().clear();
		logger.debug("calling canvas.render"); //$NON-NLS-1$
		getCanvas().render(getDrawables());
	}

	/**
	 * Lets the canvas draw the current state.
	 */
	public void renderUpdate() {
		int rounds = Util.calculateRounds(
				getAppModel().getOuterRadius(), 
				getAppModel().getInnerRadius());

		logger.debug("renderUpdate: (using " + rounds + " rounds)"); //$NON-NLS-1$ //$NON-NLS-2$

		if (getAppModel().getHoldFigure()) {
			// nothing
		} else {
			// - actually reset the canvas:
			logger.debug("calling canvas.resetRender"); //$NON-NLS-1$
			getCanvas().resetRender();
		}

		logger.debug("calling canvas.clear"); //$NON-NLS-1$
		getCanvas().clear();
		logger.debug("calling canvas.render"); //$NON-NLS-1$
		getCanvas().render(getDrawables());
	}

	/**
	 * This method should be called from within a MachineDriver which creates 
	 * some kind of stepwise figure drawing (for instance animated driver
	 * or mouse controlled driver).
	 * 
	 * This method is reponsible for updating the DocModels "members" - there 
	 * is no need to update them manually somewhere else.
	 * 
	 * @param phi
	 * @param mousex
	 * @param mousey
	 */
	protected void renderStep(double phi, int mousex, int mousey) {
		// FIXME: lock this
		if (logger.isDebugEnabled()) 
			logger.debug("renderStep: mouse=" + mousex + " / " + mousey + " phi=" + phi); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		spiroMath.calculate(phi);

		// we need to update the model data:
		getDocModel().getActiveFigureSpec().setCursorAngle(phi);

		outerGear.setState(0.0, 0, 0);
		innerGear.setState(
				spiroMath.getSmallGearDirection(), 
				spiroMath.getSmallGearCenterX(), 
				spiroMath.getSmallGearCenterY());

		logger.debug("calling canvas.render"); //$NON-NLS-1$
		getCanvas().render(getDrawables());
	}

	/**
	 * 
	 */
	private void attachGears() {
		logger.debug("attaching gears"); //$NON-NLS-1$
		FigureSpec fs = getDocModel().getActiveFigureSpec();
		if (fs!=null) {
			spiroMath = ModelUtil.createStandardSpiroMath(getDocModel());
			double phi = 0.0;
			if (AppModel.getInstance().getAutoFill()) {
				fs.initFullInterval();
			}
			phi = fs.getCursorAngle();
			logger.debug("attaching inner gear for phi=" + phi); //$NON-NLS-1$
			spiroMath.calculate(phi);
			outerGearSpec.updateFromFigureSpec(fs);
			innerGearSpec.updateFromFigureSpec(fs);
			innerGear.setState(
					spiroMath.getSmallGearDirection(), 
					spiroMath.getSmallGearCenterX(), 
					spiroMath.getSmallGearCenterY());
			knob.setAngle(phi);
		}
	}

	/**
	 * @param evt
	 */
	@Override
	public void appPropertyChange(PropertyChangeEvent evt) {
		String propName = evt.getPropertyName();
		if (propName!=null) {
			logger.debug("app prop change: " + propName); //$NON-NLS-1$
			if (propName.equals(AppModel.SHOW_GEARS)) {
				innerGear.setVisible(getAppModel().getShowGears());
				outerGear.setVisible(getAppModel().getShowGears());
				renderTrigger.setHasValue(true);
			} else if (propName.equals(AppModel.SHOW_FIGURE_TEMPORARILY)) {
				if (figure!=null) figure.setVisible(getAppModel().getShowFigureActually());
				// renderTrigger.setHasValue(true);
			} else if (propName.equals(AppModel.SHOW_FIGURE)) {
				updateViewStatus();
				getCanvas().clear();
				renderTrigger.setHasValue(true);
			} else if (propName.equals(AppModel.SHOW_PICTURE)) {
				updateFigureList();
				updateViewStatus();
				renderFull();
			} else if (propName.equals(AppModel.ANIMATED)) {
				if (getAppModel().getAnimated()) {
					installDriver(animatedDriver, true);
					getAppModel().setHoldFigure(false);
					getAppModel().setShowFigureTemporarily(true);
					if (!getAppModel().getShowGears()) {
						getAppModel().setShowGears(true);
					}
				}
			} else if (propName.equals(AppModel.MOUSE_CONTROLLED)) {
				if (getAppModel().getMouseControlled()) {
					installDriver(mouseDriver, true);
					getAppModel().setHoldFigure(false);
					getAppModel().setShowFigureTemporarily(true);
					if (!getAppModel().getShowGears()) {
						getAppModel().setShowGears(true);
					}
				}
			} else if (propName.equals(AppModel.INSTANT_UPDATE)) {
				if (getAppModel().getInstantUpdate()) {
					installDriver(instantDriver, false);
					// FIXME: need it? getAppModel().setHoldFigure(true);
					// getModel().setShowFigureTemporarily(false);
					renderUpdate();
				}
			} else if (propName.equals(AppModel.HOLD_FIGURE)) {
				// has this property been turned off?
				if (
						getAppModel().getInstantUpdate() &&
						((Boolean)evt.getOldValue()).booleanValue() &&
						!((Boolean)evt.getNewValue()).booleanValue()) {
					getAppModel().setShowFigureTemporarily(false);
					renderFull();
				}
			} else if (propName.equals(AppModel.CLEAR_FIGURE)) {
				// this gets only fired with the false->true transition:
				getDocModel().getActiveFigureSpec().clear();
				getKnob().reset();
				attachGears();	// FIXME: is that OK?
				renderUpdate();
			} else if (propName.equals(AppModel.FILL_FIGURE)) {
				// this gets only fired with the false->true transition:
				getDocModel().getActiveFigureSpec().initFullInterval();
				renderFull();
			} else if (propName.equals(AppModel.AUTO_FILL)) {
				// has this property been turned on?
				logger.debug("autoFill:" +  //$NON-NLS-1$
						" iu=" + getAppModel().getInstantUpdate() + //$NON-NLS-1$
						" ov=" + evt.getOldValue() + //$NON-NLS-1$
						" nv=" + evt.getNewValue() //$NON-NLS-1$
						);
				if (
						getAppModel().getInstantUpdate() &&
						!((Boolean)evt.getOldValue()).booleanValue() &&
						((Boolean)evt.getNewValue()).booleanValue()) {
					logger.debug("auto fill turned on"); //$NON-NLS-1$
					FigureSpec fs = getDocModel().getActiveFigureSpec();
					if ((fs.getEndAngle() - fs.getStartAngle())==0.0) {
						logger.debug("empty figure detected, filling"); //$NON-NLS-1$
						fs.initFullInterval();
						renderFull();
					}
				}
			} else if (propName.equals(AppModel.DOC_MODEL)) {
				updateFigureList();
				FigureSpec fs = getDocModel().getActiveFigureSpec();
				if (logger.isDebugEnabled()) {
					logger.debug(
							"active figure: index=" + getDocModel().getActiveFigureIndex() + //$NON-NLS-1$
							"phi=" + (fs!=null ? fs.getCursorAngle() : "<null>")); //$NON-NLS-1$ //$NON-NLS-2$
				}
				figure = fs!=null ? figureList.get(fs) : null;
				attachGears();
				renderUpdate();
			} else {
				logger.debug("unhandled property " + propName + " changed."); //$NON-NLS-1$ //$NON-NLS-2$
			}
		} else {
			throw new SpiromatException("Cannot yet handle unspecified PropertyChangeEvents"); //$NON-NLS-1$
		}
	}

	/**
	 * @param evt
	 */
	@Override
	public void docPropertyChange(PropertyChangeEvent evt) {
		String propName = evt.getPropertyName();
		if (propName!=null) {
			logger.debug("doc propChange: " + propName); //$NON-NLS-1$
			if (propName.equals(DocModel.PROPERTY_FIGURE_LIST)) {
				updateFigureList();
				updateViewStatus();
			} else if (propName.equals(DocModel.PROPERTY_ACTIVE_FIGURE)) {
				if (logger.isDebugEnabled()) {
					FigureSpec fs = getDocModel().getActiveFigureSpec();
					logger.debug(
							"active figure: index=" + getDocModel().getActiveFigureIndex() + //$NON-NLS-1$
							"phi=" + (fs!=null ? fs.getCursorAngle() : "<null>")); //$NON-NLS-1$ //$NON-NLS-2$
				}
				updateFigureList();
				updateViewStatus();
//				figure = figureList.get(getDocModel().getActiveFigureSpec());
//
				attachGears();
				renderUpdate();
			}
		}
	}

	/**
	 * @param evt
	 * @see de.admadic.spiromat.machines.AbstractMachine#figPropertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void figPropertyChange(PropertyChangeEvent evt) {
		String propName = evt.getPropertyName();
		if (propName!=null) {
			logger.debug("fig propChange: " + propName); //$NON-NLS-1$
		}
	}
}
