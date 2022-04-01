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
package de.admadic.spiromat.model;

import de.admadic.spiromat.Globals;
import de.admadic.spiromat.math.SpiroMath;

/**
 * @author Rainer Schwarze
 */
public class ModelUtil {
	/**
	 * @return	Returns the newly created FigureSpec based on the current
	 * 			settings in the AppModel.
	 */
	static public FigureSpec createStandardFigureSpec() {
		AppModel appModel = AppModel.getInstance();
		FigureSpec fs = new FigureSpec(
				appModel.getOuterRadius(),
				appModel.getInnerRadius(),
				AppModel.getInstance().getLambda(), 
				AppModel.getInstance().getColorFigure());
		if (AppModel.getInstance().getAutoFill()) {
			fs.initFullInterval();
		}
		return fs;
	}

	/**
	 * @param docModel The DocModel, can be null.
	 * @return	Returns the newly created FigureSpec based on the current
	 * 			settings in the AppModel.
	 */
	static public FigureSpec createStandardFigureSpec(DocModel docModel) {
		FigureSpec fs = createStandardFigureSpec();
		if (docModel!=null) {
			fs.setColor(docModel.getNextDefaultColor());
		}
		if (AppModel.getInstance().getAutoFill()) {
			fs.initFullInterval();
		}
		return fs;
	}

	/**
	 * @return	Returns the newly created InnerGearSpec based on the 
	 * 			current settings in the AppModel. 
	 */
	static public InnerGearSpec createStandardInnerGearSpec() {
		AppModel appModel = AppModel.getInstance();
		InnerGearSpec igs = new InnerGearSpec(
				appModel.getLambda(),
				appModel.getInnerRadius(),
				appModel.getColorInnerGear());
		return igs;
	}

	/**
	 * @return	Returns the newly created OuterGearSpec based on the 
	 * 			current settings in the AppModel. 
	 */
	static public OuterGearSpec createStandardOuterGearSpec() {
		AppModel appModel = AppModel.getInstance();
		OuterGearSpec ogs = new OuterGearSpec(
				appModel.getOuterRadius(),
				appModel.getColorOuterGear());
		return ogs;
	}

	/**
	 * FIXME: this should be put into the math package or all that moved into a general factory class.
	 * @return	Returns the newly created SpiroMath instance.
	 */
	static public SpiroMath createStandardSpiroMath() {
		return createStandardSpiroMath(null);
	}

	/**
	 * FIXME: this should be put into the math package or all that moved into a general factory class.
	 * @param docModel The DocModel - may be null.
	 * @return	Returns the newly created SpiroMath instance.
	 */
	static public SpiroMath createStandardSpiroMath(DocModel docModel) {
		SpiroMath spiroMath;
		if (docModel==null) {
			AppModel appModel = AppModel.getInstance();
			spiroMath = new SpiroMath(
					appModel.getOuterRadius() * Globals.MAX_RADIUS / 100,
					appModel.getInnerRadius() * Globals.MAX_RADIUS / 100,
					appModel.getLambda());
		} else {
			FigureSpec fs = docModel.getActiveFigureSpec();
			spiroMath = new SpiroMath(
					fs.getOuterRadius() * Globals.MAX_RADIUS / 100,
					fs.getInnerRadius() * Globals.MAX_RADIUS / 100,
					fs.getPenHolePos());
		}
		return spiroMath;
	}
}
