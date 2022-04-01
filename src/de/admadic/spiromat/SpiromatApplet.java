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
package de.admadic.spiromat;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;

import javax.swing.JApplet;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import de.admadic.spiromat.log.Logger;
import de.admadic.spiromat.model.AppModel;
import de.admadic.spiromat.ui.MainView;

/**
 * Provides the main starting point for the Spiromat applet.
 * 
 * CHECKME: Strings are not externalized here!
 * 
 * @author Rainer Schwarze
 */
public class SpiromatApplet extends JApplet {
	/** */
	private static final long serialVersionUID = 1L;

	private MainView mainView;
	private AppModel model;

	// this class is the first thing which gets started, so we initialize
	// the logger system here: 
	static {
		Logger.initialize();
	}

	// create a Logger instance for this class:
	static Logger logger = Logger.getLogger(SpiromatApplet.class);

	HashMap<String,String> parameters = new HashMap<String, String>();
	
	/**
	 * @throws HeadlessException
	 */
	public SpiromatApplet() throws HeadlessException {
		super();
	}

	/**
	 * @return	Returns a String containing information about the applet.
	 * @see java.applet.Applet#getAppletInfo()
	 */
	@Override
	public String getAppletInfo() {
		// return super.getAppletInfo();
		return 
			"Spiromat Applet - (c) 2007 by admaDIC GbR, Leipzig, Germany, www.admadic.de"; 
	}

	/**
	 * @return	Returns the parameter information for this applet.
	 * @see java.applet.Applet#getParameterInfo()
	 */
	@Override
	public String[][] getParameterInfo() {
		return  new String [][] {
				{"lang",    "string", "language to select"},	// $NON-NLS-2$ $NON-NLS-3$

				{"outerradius", "4..100", "radius of outer gear"},	// $NON-NLS-2$ $NON-NLS-3$
				{"innerradius", "4..100", "radius of inner gear"},	// $NON-NLS-2$ $NON-NLS-3$
				{"penholepos", "0.0 .. 1.0", "position of pen hole in inner gear"},	// $NON-NLS-2$ $NON-NLS-3$
				{"innercolor", "color", "color code for inner gear"},	// $NON-NLS-2$ $NON-NLS-3$
				{"outercolor", "color", "color code for outer gear"},	// $NON-NLS-2$ $NON-NLS-3$
				{"figurecolor", "color", "color code for figure"},	// $NON-NLS-2$ $NON-NLS-3$

//		         {"repeat", "boolean", "repeat image loop"},
//		         {"imgs",   "url",     "images directory"}
		};
	}

	/**
	 * Initializes the applet by creating a view instance and adding that to 
	 * the applet's main container.
	 * 
	 * @see java.applet.Applet#init()
	 */
	@Override
	public void init() {
		logger.debug("init"); 
		super.init();

		// determine parameters:
		String localeLanguage = getParameter("LANG");
		if (localeLanguage!=null) {
			Messages.setLocaleLanguage(localeLanguage);
		}
		readParameters();
		
		try {
			initSkin();
		} catch (Exception e) {
			// don't do nothing
		}
		this.setLayout(new BorderLayout());
		model = AppModel.getInstance();
		model.setParameters(parameters);
		mainView = new MainView(model);
		this.add(mainView);
	}

	/**
	 */
	private void readParameters() {
		readParameter("outerradius", AppModel.OUTER_RADIUS);
		readParameter("innerradius", AppModel.INNER_RADIUS);
		readParameter("penholepos", AppModel.LAMBDA);
		readParameter("figurecolor", AppModel.COLOR_FIGURE);
		readParameter("outercolor", AppModel.COLOR_OUTER_GEAR);
		readParameter("innercolor", AppModel.COLOR_INNER_GEAR);
	}

	/**
	 * @param appletParm
	 * @param modelParm
	 */
	private void readParameter(String appletParm, String modelParm) {
		String tmp = getParameter(appletParm);
		if (tmp!=null) {
			parameters.put(modelParm, tmp);
		}
	}

	void initSkin() throws Exception {
		// String themepack = getParameter(themepackTagName);
		String themepack = "admadicthemepack.zip"; 
		URL themepackURL;

		// if no themepack has been provided in the applet tag
		// use the default themepack provided in the jar of the applet
		if (themepack == null) {
			themepackURL = SpiromatApplet.class.getResource("themepack.zip"); 
		} else {
			// a themepack has been provided, relative to the codebase
			themepackURL = SpiromatApplet.class.getResource(themepack);
			// themepackURL = new URL(getCodeBase(), themepack);
		}

		Class<?> skinlafclass = Class.forName("com.l2fprod.gui.plaf.skin.SkinLookAndFeel"); 
		Method loadthpkmth = skinlafclass.getMethod("loadThemePack", new Class[]{URL.class}); 
		Class skinclass = Class.forName("com.l2fprod.gui.plaf.skin.Skin"); 
		Method setskinmth = skinlafclass.getMethod("setSkin", new Class[]{skinclass}); 
		Object lafobj;
		Object skinobj;

		skinobj = loadthpkmth.invoke(null, new Object[]{themepackURL});
		setskinmth.invoke(null, new Object[]{skinobj});
		lafobj = skinlafclass.newInstance();
		UIManager.setLookAndFeel((LookAndFeel) lafobj);

//		Skin skin = SkinLookAndFeel.loadThemePack(themepackURL);
//		SkinLookAndFeel.setSkin(skin);
//		UIManager.setLookAndFeel(new SkinLookAndFeel());
	}
	
	/**
	 * Starts the "execution" of the applet.
	 * 
	 * @see java.applet.Applet#start()
	 */
	@Override
	public void start() {
		logger.debug("start"); 
		super.start();
		mainView.start();
	}

	/**
	 * Stops the "execution" of the applet.
	 * 
	 * @see java.applet.Applet#stop()
	 */
	@Override
	public void stop() {
		logger.debug("stop"); 
		super.stop();
		mainView.stop();
	}

	/**
	 * Destroys the applet.
	 * 
	 * @see java.applet.Applet#destroy()
	 */
	@Override
	public void destroy() {
		logger.debug("stop"); 
		super.destroy();
		mainView.destroy();
	}
}
