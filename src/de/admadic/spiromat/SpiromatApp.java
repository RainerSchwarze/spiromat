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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;


//import de.admadic.license.ModuleLicenseWatchDog;
import de.admadic.spiromat.log.Logger;
import de.admadic.spiromat.model.AppModel;
import de.admadic.spiromat.splash.SplashWindow;
import de.admadic.spiromat.ui.CfgSpi;
import de.admadic.spiromat.ui.MainFrame;
import de.admadic.util.PathManager;

/**
 * @author Rainer Schwarze
 *
 */
public class SpiromatApp {
	static Logger logger;
	static {
		Logger.initialize();
		logger = Logger.getLogger(SpiromatApp.class);
	}
	/** global config instance */
	public static CfgSpi cfg;
	static PathManager pathMan;
	static MainFrame frame;
	/** global license instance */
	/*
	public static SpiromatLicense license;
	static ModuleLicenseWatchDog licenseWatchDog;
	 */

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			SplashWindow.getInstance().showSplash();
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					SpiromatApp.execute();
				}
			});
		} catch (InterruptedException e) {
			/* don't need a printout in this case */
			logger.error("general app error", e);
		} catch (InvocationTargetException e) {
			logger.error("general app error", e);
			// FIXME: do we need an error display here?
		} finally {
			SplashWindow.getInstance().hideSplash();
		}
	}

	static void execute() {
		try {
			initProgramEnvironment();
		} finally {
			/* nothing */
		}
		try {
			initSkin();
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error("skin init error", e);
			/* we ignore it... */
			System.err.println(e.getLocalizedMessage());
		}
		try {
			frame = new MainFrame(AppModel.getInstance());
			frame.initContents();
			frame.setVisible(true);
	
			SplashWindow.getInstance().hideSplash();

			/*
			license = new SpiromatLicense(cfg, frame);
			licenseWatchDog = new ModuleLicenseWatchDog(frame, license);
			license.licenseProcess();
			 */
		} catch (Throwable t) {
			JOptionPane.showMessageDialog(
					null, 
					"Could not start the application.\nMessage: " + t.getLocalizedMessage(),
					"Error starting application",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * 
	 */
	private static void initProgramEnvironment() {
		// String sysbaseStr;
		// sysbaseStr = PathManager.getCodeBase(SpiromatApp.class);
		pathMan = new PathManager();
		pathMan.init(
				"admadic", 
				"spiromat", 
				Version.versionFS, 
				SpiromatApp.class);

		cfg = new CfgSpi();
		cfg.initCfgPaths(pathMan);
		cfg.initialize(true);

		
		SplashWindow.getInstance().setMessage(buildLicInfo());
	}

	static String buildLicInfo() {
		String ln = cfg.getStringValue(CfgSpi.KEY_UI_MAIN_LIC_NAME, null); 
		String lc = cfg.getStringValue(CfgSpi.KEY_UI_MAIN_LIC_COMPANY, null);
		String ls = cfg.getStringValue(CfgSpi.KEY_UI_MAIN_LIC_SN, null);
		String output = ""; //$NON-NLS-1$

		if (ln!=null) output += ln;
		if (lc!=null) {
			if (!output.equals("")) output += " "; //$NON-NLS-1$ //$NON-NLS-2$
			output += lc;
		}
		if (ls!=null) {
			if (!output.equals("")) { //$NON-NLS-1$
				output = Messages.getString("SpiromatApp.registeredTo") + output + "<br>"; //$NON-NLS-1$ //$NON-NLS-2$
			}
			// output += "" + ls;
		}
		if (!output.equals("")) output = "<html>" + output; //$NON-NLS-1$ //$NON-NLS-2$
		return output;
	}

	static void initSkin() throws Exception {
		// String themepack = getParameter(themepackTagName);
		String themepack = "admadicthemepack.zip"; //$NON-NLS-1$
		URL themepackURL;

		// if no themepack has been provided in the applet tag
		// use the default themepack provided in the jar of the applet
		if (themepack == null) {
			themepackURL = SpiromatApp.class.getResource("themepack.zip"); //$NON-NLS-1$
		} else {
			// a themepack has been provided, relative to the codebase
			themepackURL = SpiromatApp.class.getResource(themepack);
			// themepackURL = new URL(getCodeBase(), themepack);
		}

		Class<?> skinlafclass = Class.forName("com.l2fprod.gui.plaf.skin.SkinLookAndFeel"); //$NON-NLS-1$
		Method loadthpkmth = skinlafclass.getMethod("loadThemePack", new Class[]{URL.class}); //$NON-NLS-1$
		Class skinclass = Class.forName("com.l2fprod.gui.plaf.skin.Skin"); //$NON-NLS-1$
		Method setskinmth = skinlafclass.getMethod("setSkin", new Class[]{skinclass}); //$NON-NLS-1$
		Object lafobj;
		Object skinobj;

		skinobj = loadthpkmth.invoke(null, new Object[]{themepackURL});
		setskinmth.invoke(null, new Object[]{skinobj});
		lafobj = skinlafclass.newInstance();
		UIManager.setLookAndFeel((LookAndFeel) lafobj);
		// app specific:
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);

//		Skin skin = SkinLookAndFeel.loadThemePack(themepackURL);
//		SkinLookAndFeel.setSkin(skin);
//		UIManager.setLookAndFeel(new SkinLookAndFeel());
	}
}
