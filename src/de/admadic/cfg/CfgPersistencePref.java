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
package de.admadic.cfg;

import java.util.Enumeration;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * @author Rainer Schwarze
 *
 */
public class CfgPersistencePref implements CfgPersistenceGrouped,
		CfgPersistenceItemized {

	protected final static boolean DBGforce = false;
	final static boolean LOG = true;
	Logger logger = (LOG) ? Logger.getLogger("de.admadic") : null;

	CfgProvider cfgProvider;

	/**
	 * Creates an instance of CfgPersistencePref. 
	 */
	public CfgPersistencePref() {
		this(null);
	}

	/**
	 * @param cp
	 */
	public CfgPersistencePref(CfgProvider cp) {
		super();
		cfgProvider = cp;
	}

	/**
	 * @param cp
	 * @see de.admadic.cfg.CfgPersistenceGrouped#registerCfgProvider(de.admadic.cfg.CfgProvider)
	 */
	public void registerCfgProvider(CfgProvider cp) {
		cfgProvider = cp;
	}

	/**
	 * @param path
	 * @param keys
	 * @see de.admadic.cfg.CfgPersistenceGrouped#removeKeys(java.lang.String, java.util.Enumeration)
	 */
	public void removeKeys(String path, Enumeration<String> keys) {
		Preferences pref = Preferences.userRoot();
		pref = pref.node(path);
		String key;
		while (keys.hasMoreElements()) {
			key = keys.nextElement();
			if (DBGforce) System.err.println("load pref: removing " + key);
			pref.remove(key + ".data");
			pref.remove(key + ".meta");
		}
	}

	/**
	 * @param path
	 * @see de.admadic.cfg.CfgPersistenceGrouped#load(java.lang.String)
	 */
	public void load(String path) {
		// load all the settings from the persistence backend
		try {
			if (DBGforce) System.err.println("Cfg: loadPreferences: path=" + path);
			Preferences pref = Preferences.userRoot();
			pref = pref.node(path);
			String valueCode;
			String metaCode;
			String keyCode;
			String [] keys = pref.keys();
			for (String k : keys) {
				if (k==null) continue;
				if (k.endsWith(".meta")) continue;
				// k = "... .data":
				if (!k.endsWith(".data")) {
					// error!
					continue;
				}
				k = k.replaceAll("\\.data$", "");
				keyCode = k;
				valueCode = pref.get(k + ".data", "");
				metaCode =  pref.get(k + ".meta", "");
				if (DBGforce) System.err.println("loaded pref " + k + "=" + valueCode + " / " + metaCode);
				CfgItem ci;
				ci = new CfgItem();
				ci.decodeKey(keyCode);
				ci.decodeMeta(metaCode);
				ci.decodeValue(valueCode);
				cfgProvider.putCfgItem(ci.getCiKey(), ci);
			}
		} catch (BackingStoreException e) {
			// FIXME: fix the exception printing
			// e.printStackTrace();
			if (logger!=null) logger.severe(
					"configuration error (pref): " + e.getMessage());
		} catch (CfgException e) {
			// e.printStackTrace();
			if (logger!=null) logger.severe(
					"configuration error (pref): " + e.getMessage());
		} finally {
			// nothing to do
		}
	}

	/**
	 * @param path
	 * @see de.admadic.cfg.CfgPersistenceGrouped#store(java.lang.String)
	 */
	public void store(String path) {
		// store all the settings to the persistence backend
		if (DBGforce) System.err.println("Cfg: storePreferences: path=" + path);
		Preferences pref = Preferences.userRoot();
		pref = pref.node(path);
		String valueCode;
		String keyCode;
		String metaCode;
		CfgItem ci;
		Enumeration<String> keyset;
		keyset = cfgProvider.getCfgItemKeys();
		String key;
		//for (String key : keyset) {
		while (keyset.hasMoreElements()) {
			key = keyset.nextElement();
			try {
				if (key==null) continue;
				ci = cfgProvider.getCfgItem(key);
				keyCode = ci.encodeKey();
				valueCode = ci.encodeValue();
				metaCode = ci.encodeMeta();
				if (DBGforce) System.err.println("save pref: " + key + ": " + keyCode + "=" + valueCode + " / " + metaCode);
				pref.put(keyCode + ".data", valueCode);
				pref.put(keyCode + ".meta", metaCode);
			} catch (CfgException e) {
				//e.printStackTrace();
				// System.err.println("Cfg: error creating encoding. " + e);
				// e.printStackTrace();
				if (logger!=null) logger.severe(
						"configuration error (pref): " + e.getMessage());

			}
		}
	}

	/**
	 * @param path
	 * @see de.admadic.cfg.CfgPersistenceGrouped#clear(java.lang.String)
	 */
	public boolean clear(String path) {
		try {
			Preferences pref = Preferences.userRoot();
			pref = pref.node(path);
			String[] keys;
			keys = pref.keys();
			for (String key : keys) {
				if (DBGforce) System.err.println("load pref: removing " + key);
				pref.remove(key);
			}
			return true;
		} catch (BackingStoreException e) {
			//e.printStackTrace();
			// could not retrieve the keys from the backend
			// we stay silent about it for now
			// FIXME: make this an error
			return false;
		}
	}
	
	/**
	 * @param path
	 * @see de.admadic.cfg.CfgPersistenceItemized#prepareLoad(java.lang.String)
	 */
	public void prepareLoad(String path) {
		// initialize the prefence backend
	}

	/**
	 * @param path
	 * @see de.admadic.cfg.CfgPersistenceItemized#prepareStore(java.lang.String)
	 */
	public void prepareStore(String path) {
		// initialize the preference backend
	}

	/**
	 * @return	Returns an array of available keys
	 * @see de.admadic.cfg.CfgPersistenceItemized#getKeys()
	 */
	public String[] getKeys() {
		return null;
	}

	/**
	 * @param key
	 * @return	Returns the CfgItem for the specified key
	 * @see de.admadic.cfg.CfgPersistenceItemized#loadCfgItem(java.lang.String)
	 */
	public CfgItem loadCfgItem(String key) {
		return null;
	}

	/**
	 * @param ci
	 * @see de.admadic.cfg.CfgPersistenceItemized#storeCfgItem(de.admadic.cfg.CfgItem)
	 */
	public void storeCfgItem(CfgItem ci) {
		// store the CfgItem
	}

	/**
	 * 
	 * @see de.admadic.cfg.CfgPersistenceItemized#finalizeLoad()
	 */
	public void finalizeLoad() {
		// remove resources
	}

	/**
	 * 
	 * @see de.admadic.cfg.CfgPersistenceItemized#finalizeStore()
	 */
	public void finalizeStore() {
		// remove resources
	}

}
