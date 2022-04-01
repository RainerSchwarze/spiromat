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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.JOptionPane;

import de.admadic.cfg.Cfg;
import de.admadic.cfg.CfgItem;
import de.admadic.cfg.CfgPersistenceXML;
import de.admadic.util.FileUtil;
import de.admadic.util.PathManager;


/**
 * @author Rainer Schwarze
 *
 */
public class CfgSpi extends Cfg {
	static boolean DBG = false;

	// FIXME: the ITEM_SEPARATOR is used as a regexp in String.split.
	// that handling should be fixed in case we get regexp characters here!
	/** Separator character for multi-entry string values. (is ':') */
	public final static char ITEM_SEPARATOR = ':';
	/** Convenience String for ITEM_SEPARATOR */
	public final static String ITEM_SEPARATOR_STR = ""+ITEM_SEPARATOR; //$NON-NLS-1$

	/** The path for use with the java.util.prefs.Preferences classes */
	public final static String PREFERENCES_PATH = "/de.admadic.spiromat"; //$NON-NLS-1$


	/** Name of licensee */
	public final static String 
	KEY_UI_MAIN_LIC_NAME = "ui.main.lic.name"; //$NON-NLS-1$

	/** Company of licensee */
	public final static String 
	KEY_UI_MAIN_LIC_COMPANY = "ui.main.lic.company"; //$NON-NLS-1$

	/** Serial Number of licensee */
	public final static String 
	KEY_UI_MAIN_LIC_SN = "ui.main.lic.sn"; //$NON-NLS-1$

	/** Key for serial number */
	public final static String 
	KEY_UI_SN_STAT_SN = "ui.sns.sn"; //$NON-NLS-1$

	Object [][] stdValues = {
			{KEY_UI_MAIN_LIC_NAME, ""}, //$NON-NLS-1$
			{KEY_UI_MAIN_LIC_COMPANY, ""}, //$NON-NLS-1$
			{KEY_UI_MAIN_LIC_SN, ""}, //$NON-NLS-1$
			{KEY_UI_SN_STAT_SN, ""}, //$NON-NLS-1$
	};
	

	Properties props;
	String cfgPath;
	String cfgFile;

	String cfgFullName;

	/**
	 * Constructs an instance of CfgSpi.
	 * CfGCalc(true) is called 
	 */
	public CfgSpi() {
		// a Logger cannot be used yet.
		super();
		{
			String tmp = System.getProperty("admadic.debug"); //$NON-NLS-1$
			if (tmp!=null && tmp.toLowerCase().equals("yes")) { //$NON-NLS-1$
				DBG = true;
			}
		}
		props = new Properties();
	}

	protected void error(String msg) {
		error(msg, true);
	}

	protected void error(String msg, boolean gui) {
		// a Logger cannot always be used, therefore a plain screen output 
		// needs to be done.
		System.err.println("Spiromat: " + msg); //$NON-NLS-1$
		if (gui) {
			JOptionPane.showMessageDialog(
					null,
					msg,
					Messages.getString("CfgSpi.configErrorTitle"), //$NON-NLS-1$
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * @param pm 
	 * 
	 */
	public void initCfgPaths(PathManager pm) {
		File file = null;
		FileInputStream fin;

		if (DBG) System.out.println("CfgSpi: initCfgPaths"); //$NON-NLS-1$
		try {
			file = new File(
					pm.getPathString(PathManager.SYS_CFG_DIR) + 
					"/cfg.cfg"); //$NON-NLS-1$
			if (file.exists()) {
				fin = new FileInputStream(file);
				props.load(fin);
			} else {
				InputStream is = this.getClass().getResourceAsStream(
						"/de/admadic/spiromat/cfg/cfg.cfg"); //$NON-NLS-1$
				props.load(is);
			}
		} catch (FileNotFoundException e) {
			// FIXME: make that a real error!
			if (DBG) e.printStackTrace();
			error(
					"The main cfg file could not be found! (looking for cfg.cfg)\n"+ //$NON-NLS-1$
					"sys-cfg-dir = " + pm.getPathString(PathManager.SYS_CFG_DIR)); //$NON-NLS-1$
		} catch (IOException e) {
			if (DBG) e.printStackTrace();
			error("a cfg file exists, but could not be read! (" +  //$NON-NLS-1$
					((file!=null) ? file.toString() : "<null>") + ")"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		cfgPath = props.getProperty("de.admadic.spiromat.ui.CfgSpi.cfgpath"); //$NON-NLS-1$
		cfgFile = props.getProperty("de.admadic.spiromat.ui.CfgSpi.cfgfile"); //$NON-NLS-1$

		if (cfgPath==null) {
			cfgPath = pm.getPathString(PathManager.USR_CFG_DIR);
		}

		// FIXME: we may not need that...
//		FileUtil.copyFile(
//				pm.getPathString(PathManager.SYS_CFG_DIR) + "/cfg.dtd",
//				pm.getPathString(PathManager.USR_CFG_DIR) + "/cfg.dtd");

		file = PathManager.expandFilename(cfgPath);
		cfgPath = file.toString();
		file = new File(file, cfgFile);

		cfgFullName = file.toString();

//		System.out.println("cfgfile = " + cfgFullName);
	}

	/**
	 * @return	Returns true, if the cfg file exists, false otherwise.
	 */
	public boolean existsCfgFile() {
		File f = new File(cfgFullName);
//		System.out.println("file " + f + " exists?: " + f.exists());
		return f.exists();
	}

	/**
	 * @param src
	 * @return Returns true, if successfully copied
	 */
	public boolean copyCfgFile(String src) {
		return FileUtil.copyTextFile(src, cfgFullName);
	}

	protected void tryPath() {
		File dir = new File(cfgPath);
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				error("Could not create cfg path (" + cfgPath + ")"); //$NON-NLS-1$ //$NON-NLS-2$
				// FIXME: maybe make that an error
			}
		}
	}

	/**
	 * The standard preferences declared in this class are installed.
	 * If loadPref is true, the preferences are loaded from preferences space.
	 * The preferences space is initialized to work on base of XML storage.
	 * 
	 * @param loadPref	a boolean indicating whether to load preferences
	 * 					or not 
	 */
	public void initialize(boolean loadPref) {
		initStandard();

//		// make it work with Preferences classes:
//		registerPersistanceBackend(new CfgPersistencePref(this));
		// (the Preferences classes create awfully named directories in
		// Unix which is impossible to maintain and support for end customers)

		// lets use XML:
		registerPersistanceBackend(
				new CfgPersistenceXML(this, cfgFullName));

		if (loadPref) {
			loadPreferences(CfgSpi.PREFERENCES_PATH);
		}

		postLoadFixup();
	}

	/**
	 * @param cfgname
	 * @return	Returns a Cfg instance with registered backend.
	 */
	public static Cfg createTmpCfg(String cfgname) {
		Cfg tmpCfg = new Cfg();
		tmpCfg.registerPersistanceBackend(
				new CfgPersistenceXML(tmpCfg, cfgname));
		return tmpCfg;
	}
	
	/**
	 * Initializes the standard settings defined in the source code of this 
	 * class. 
	 */
	public void initStandard() {
		String key;
		for (int i=0; i<stdValues.length; i++) {
			if (stdValues[i].length!=2) {
				error(
						"stdCfgEntry x[" + i + "] does not have 2 elements.", //$NON-NLS-1$ //$NON-NLS-2$
						false); // no gui error
			} else {
				key = (String)stdValues[i][0]; 
				getDefaultCfg().putValue(key, stdValues[i][1]);
			}
		}
		getDefaultCfg().getCfgItem(KEY_UI_MAIN_LIC_NAME).setCiFlags(
				CfgItem.FLG_NORESET, true);
		getDefaultCfg().getCfgItem(KEY_UI_MAIN_LIC_COMPANY).setCiFlags(
				CfgItem.FLG_NORESET, true);
		getDefaultCfg().getCfgItem(KEY_UI_MAIN_LIC_SN).setCiFlags(
				CfgItem.FLG_NORESET, true);
		getDefaultCfg().getCfgItem(KEY_UI_SN_STAT_SN).setCiFlags(
				CfgItem.FLG_NORESET, true);

		setDefaults();
	}


	/**
	 * Fixes some settings in case they are not present or inconsistent.
	 * <p>
	 * Especially the different LISTUSE settings are initialized,
	 * if they are not present and the LISTUSEFILLIFEMPTY is enabled. 
	 */
	public void postLoadFixup() {
		/* nothing */
	} // (postLoadFixup)
}
