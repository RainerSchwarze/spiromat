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

/**
 * Cfg.java
 *
 * #created#  
 */
package de.admadic.cfg;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;



/**
 * @author Rainer Schwarze
 *
 * The base class for configuration handling.
 * Applications should subclass Cfg and provide specific handling
 * there.
 * <P>
 * An example may be CfgCalc.
 */
public class Cfg implements CfgProvider {
	// set to true to turn on debugging output
	private static boolean DBGforce = false;

	// FIXME: decouple the persistent methods into an interface and 
	// an implementing class/object

	/* **************************************************************
	 * Fields
	 * **************************************************************
	 */

	/**
	 * Primary storage for key-value pairs. 
	 */
	protected Hashtable <String,CfgItem> entries;

	/**
	 * Primary storage for ObjectSpecifications 
	 */
	protected ArrayList<CfgObjectSpecification> objectSpecifications;

	/**
	 * Hashtable to easily map names of ObjectSpecifications to 
	 * the corresponding ObjectSpecifications.
	 */
	protected Hashtable<String,CfgObjectSpecification> name2ObjSpc;

	/**
	 * Hashtable to easily map objects of ObjectSpecifications to 
	 * the corresponding ObjectSpecifications.
	 */
	protected Hashtable<Object,CfgObjectSpecification> obj2ObjSpc;

	/**
	 * Primary storage for keys which shall be removed.
	 * The Cfg class uses Preferences classes as backend.
	 * When removing entries from the Cfg instance, these
	 * entries are not immediately removed from the backend
	 * Preferences. As soon as the backend is accessed (either
	 * load or store) the remove queue is processed, the keys
	 * are removed in the backend and the queue is cleared.
	 */
	protected ArrayList<String> removeQueue;

	/*
	 * Cfg space which serves as a default storage. This
	 * is needed for operations like "reset to defaults"
	 */
	Cfg defaultCfg;
	boolean _isDefault = false;

	protected CfgPersistenceGrouped persGroupedBackend; 

	/* **************************************************************
	 * Construction methods
	 * **************************************************************
	 */

	/**
	 * Constructs an empty Cfg instance
	 */
	public Cfg() {
		super();
		persGroupedBackend = null;
		defaultCfg = new Cfg(true);
		initCollections();
	}

	/**
	 * This is a private constructor to create the default config space
	 * without creating a stack overflow.
	 * This method should be called with def=true and it is private to prevent
	 * that it is accidentally called.
	 * @param def
	 */
	private Cfg(boolean def) {
		super();
		_isDefault = def;
		initCollections();
	}

	/**
	 * Initialize internal collections.
	 * This method is usually used by the constructors of this class. 
	 */
	protected void initCollections() {
		entries = new Hashtable <String,CfgItem>();
		objectSpecifications = new ArrayList<CfgObjectSpecification>();
		name2ObjSpc = new Hashtable<String,CfgObjectSpecification>();
		obj2ObjSpc = new Hashtable<Object,CfgObjectSpecification>();
		removeQueue = new ArrayList<String>();
	}

	/* **************************************************************
	 * General operations
	 * **************************************************************
	 */

	/**
	 * @param cpb
	 */
	public void registerPersistanceBackend(CfgPersistenceGrouped cpb) {
		persGroupedBackend = cpb;
	}

	/**
	 * Sets the default settings.
	 * The current settings are cleared.
	 */
	public void setDefaults() {
		clearAllSettings(false);
		copyAllSettingsFromDefault(false);
	}

	/**
	 * Registers (adds) an object for loading/storing its fields.
	 * The list of fields is given as a parameter to this method.
	 * The configuration settings generated for this object will look like 
	 * "path.name.fieldname". (path may contain dots)
	 * 
	 * @param path		a String specifying the path in the key name
	 * @param name		a String specifying the name in the key name
	 * @param fieldList	a String[] specifying the list of fields
	 * @param object	the Object to be configured
	 */
	public void registerObject(
			String path, String name, 
			String [] fieldList, 
			Object object) {
		CfgObjectSpecification os = new CfgObjectSpecification(
				path, name, fieldList, object);
		objectSpecifications.add(os);
		name2ObjSpc.put(path + "." + name, os);
		obj2ObjSpc.put(object, os);
	}

	/**
	 * Unregisters (removes) an object for loading/storing its fields.
	 * Note: it is not checked whether the object is actually in the list
	 * of objects. The usual exceptions will be thrown, if the object is not
	 * in the collections.
	 * 
	 * @param object	the Object to be removed
	 */
	public void unregisterObject(Object object) {
		CfgObjectSpecification os;
		os = obj2ObjSpc.get(object);
		name2ObjSpc.remove(os.path + "." + os.name);
		obj2ObjSpc.remove(object);
		objectSpecifications.remove(os);
	}

	
	/* **************************************************************
	 * Standard put/get Value methods (primary interface to this class)
	 * **************************************************************
	 */

	/**
	 * putValue shall be reserved for the value type Object.
	 * Other classes derived from Object shall be accessed by a different name!
	 * (See putPointValue)
	 * 
	 * @param name
	 * @param value
	 */
	public void putValue(String name, Object value) {
		CfgItem ci;
		ci = getCfgItem(name);
		if (ci!=null) {
			// its there, so store it:
			try {
				ci.putObjectValue(value, false);
			} catch (CfgException e) {
				//e.printStackTrace();
				// FIXME: should we throw here? Should anybody be interested
				// in an error condition?
				// make a warning output for now:
				System.err.println(
						"Cfg: warning: could not store CfgItem: " + 
						e);
			}
		} else {
			// its not there, so create it:
			ci = CfgItem.create(name, value);
			putCfgItem(name, ci);
		}
	}

	/**
	 * getValue shall be reserved for the value type Object.
	 * Other classes derived from Object shall be accessed by a different name!
	 * (See getPointValue)
	 * 
	 * @param name
	 * @param defValue
	 * @return	the value from the config space, the given default value if the
	 * 			config space does not contain the key
	 */
	public Object getValue(String name, Object defValue) {
		CfgItem ci;
		ci = getCfgItem(name);
		if (ci==null) return defValue;
		Object ret = ci.getObjectValue(defValue);
		return ret;
	}

	/**
	 * Removes the given key from the configuration.
	 * @param name
	 */
	public void removeValue(String name) {
		removeCfgItem(name);
	}

	/**
	 * @param name
	 * @param defValue
	 * @return	the value from config space; defValue if the key is not there
	 */
	public String getStringValue(String name, String defValue) {
		CfgItem ci;
		ci = getCfgItem(name);
		if (ci==null) return defValue;
		return ci.getStringValue(defValue);
	}

	/**
	 * @param name
	 * @param defValue
	 * @return	the value from config space; defValue if the key is not there
	 */
	public boolean getBooleanValue(String name, boolean defValue) {
		CfgItem ci;
		ci = getCfgItem(name);
		if (ci==null) return defValue;
		return ci.getBooleanValue(defValue);
	}

	/**
	 * @param name
	 * @param defValue
	 * @return	the value from config space; defValue if the key is not there
	 */
	public int getIntValue(String name, int defValue) {
		CfgItem ci;
		ci = getCfgItem(name);
		if (ci==null) return defValue;
		return ci.getIntValue(defValue);
	}

	/**
	 * @param name
	 * @param defValue
	 * @return	the value from config space; defValue if the key is not there
	 */
	public Rectangle getRectangleValue(String name, Rectangle defValue) {
		CfgItem ci;
		ci = getCfgItem(name);
		if (ci==null) return defValue;
		return ci.getRectangleValue(defValue);
	}

	/**
	 * @param name
	 * @param defValue
	 * @return	the value from config space; defValue if the key is not there
	 */
	public Point getPointValue(String name, Point defValue) {
		CfgItem ci;
		ci = getCfgItem(name);
		if (ci==null) return defValue;
		return ci.getPointValue(defValue);
	}

	/**
	 * @param name
	 * @param value
	 */
	public void putStringValue(String name, String value) {
		putValue(name, value);
	}

	/**
	 * @param name
	 * @param value
	 */
	public void putBooleanValue(String name, boolean value) {
		putValue(name, new Boolean(value));
	}

	/**
	 * @param name
	 * @param value
	 */
	public void putIntValue(String name, int value) {
		putValue(name, new Integer(value));
	}

	/**
	 * @param name
	 * @param r
	 */
	public void putRectangleValue(String name, Rectangle r) {
		putValue(name, r);
	}

	/**
	 * @param name
	 * @param p
	 */
	public void putPointValue(String name, Point p) {
		putValue(name, p);
	}

	/**
	 * @param nameBase
	 * @return	the array of objects
	 */
	public Object[] getValueArray(String nameBase) {
		Object [] objs;
		Object obj;
		int count;
		int index;
		index = 0;
		count = 0;
		objs = new Object[count];
		while (true) {
			obj = getValue(nameBase + index, (Object)null);
			if (obj==null) 
				break;
			if (index>=count) {
				// resize:
				int newcount = count*2 + 1;
				Object [] newobjs = new Object[newcount];
				System.arraycopy(objs, 0, newobjs, 0, count);

				objs = newobjs;
				count = newcount;
			}
			objs[index] = obj;
			index++;
		}
		// "index" is the # of actual entries
		Object [] robjs = new Object[index];
		System.arraycopy(objs, 0, robjs, 0, index);
		return robjs;
	}

	/**
	 * @param nameBase
	 */
	public void removeValueArray(String nameBase) {
		int index;
		index = 0;
		while (true) {
			if (!hasValueImpl(nameBase + index)) {
				break;
			}
			removeCfgItem(nameBase + index);
			index++;
		}
	}

	// FIXME: add a simple array method which stores a comma separated list
	// in a single entry
	/**
	 * @param nameBase
	 * @param array
	 */
	public void putValueArray(String nameBase, Object [] array) {
		removeValueArray(nameBase);
		for (int i = 0; i < array.length; i++) {
			putValue(nameBase + i, array[i]);
		}
	}

	/* **************************************************************
	 * Persistent storage methods:
	 * **************************************************************
	 */

	/**
	 * @param o
	 */
	public void loadObjectFromPreferences(Object o) {
		CfgObjectSpecification os;
		os = obj2ObjSpc.get(o);
		if (os==null) return;
		if (os.isClass) return;
		os.loadSettings(this);
	}

	/**
	 * @param o
	 */
	public void storeObjectToPreferences(Object o) {
		CfgObjectSpecification os;
		os = obj2ObjSpc.get(o);
		if (os==null) return;
		if (os.isClass) return;
		os.storeSettings(this);
	}

	/**
	 * 
	 */
	public void loadObjectsFromPreferences() {
		if (DBGforce) System.err.println("Cfg: loadObjectsFromPreferences:");
		for (CfgObjectSpecification os : objectSpecifications) {
			if (os==null) continue;
			if (os.isClass) continue;
			os.loadSettings(this);
		}
	}

	/**
	 * 
	 */
	public void storeObjectsToPreferences() {
		if (DBGforce) System.err.println("Cfg: storeObjectsToPreferences:");
		for (CfgObjectSpecification os : objectSpecifications) {
			if (os==null) continue;
			if (os.isClass) continue;
			os.storeSettings(this);
		}
	}

	/**
	 * @param path
	 */
	public void loadPreferences(String path) {
		if (removeQueue.size()>0) {
			persGroupedBackend.removeKeys(path, Collections.enumeration(removeQueue));
			clearRemoveQueue();
		}
		persGroupedBackend.load(path);
	}

	/**
	 * @param path
	 */
	public void storePreferences(String path) {
		if (removeQueue.size()>0) {
			persGroupedBackend.removeKeys(path, Collections.enumeration(removeQueue));
			clearRemoveQueue();
		}
		persGroupedBackend.store(path);
	}

	/**
	 * Clears the preferences backends content.
	 * @param path
	 * @return Returns true, if the operation succeeded, false otherwise
	 */
	public boolean clearPreferences(String path) {
		return persGroupedBackend.clear(path);
	}
	
	/* ******************************************************************
	 * Interface: CfgProvider
	 * ******************************************************************
	 */

	/**
	 * @param name
	 * @see de.admadic.cfg.CfgProvider#removeCfgItem(java.lang.String)
	 */
	public void removeCfgItem(String name) {
		if (DBGforce) System.err.println("Cfg: removeCfgItem: name=" + name);
		entries.remove(name);
		addToRemoveQueue(name);
	}

	/**
	 * @param name
	 * @return Returns the CfgItem
	 * @see de.admadic.cfg.CfgProvider#getCfgItem(java.lang.String)
	 */
	public CfgItem getCfgItem(String name) {
		if (!entries.containsKey(name)) {
			if (DBGforce) System.err.println("Cfg: getCfgItem: name=" + name + " not found - returning null");
			return null;
		}
		return entries.get(name);
	}

	/**
	 * @param name
	 * @param ci
	 * @see de.admadic.cfg.CfgProvider#putCfgItem(java.lang.String, de.admadic.cfg.CfgItem)
	 */
	public void putCfgItem(String name, CfgItem ci) {
		if (DBGforce) System.err.println("Cfg: putCfgItem: name=" + name);
		if (!name.equals(ci.getCiKey())) {
			System.err.println(
					"Cfg: CfgItem is accessed by a different name: " +
					name + " instead of " + ci.getCiKey());
		}
		entries.put(name, ci);
	}

	/**
	 * @return	Returns the keys of the config space
	 */
	public Enumeration<String> getCfgItemKeys() {
		return entries.keys();
		//return (String[])entries.keySet().toArray();
	}

	/* ******************************************************************
	 * PROTECTED
	 * ******************************************************************
	 */

	/**
	 * @return Returns the defaultCfg.
	 */
	protected Cfg getDefaultCfg() {
		return defaultCfg;
	}

	/**
	 * Clears all settings.
	 * This is usually used to reset the configuration data for instance
	 * before resetting to default values.
	 * @param removeNoReset 
	 */
	protected void clearAllSettings(boolean removeNoReset) {
		CfgItem ci;
		//String value;
		String key;
		Object[] keyset = entries.keySet().toArray();
		for (Object okey : keyset) {
			if (okey==null) continue;
			key = (String)okey;
			ci = getCfgItem(key);
			//value = ci.encodeValue();
			if (!removeNoReset && ((ci.getCiFlags() & CfgItem.FLG_NORESET)!=0)) {
				continue; // should not remove!
			}
			if (DBGforce) System.err.println("remove key: " + key);
			removeCfgItem(key);
		}
	}

	/**
	 * Copies all settings from the default space to this space.
	 * This method should not be called directly.
	 * @param overWriteExisting 
	 */
	protected void copyAllSettingsFromDefault(boolean overWriteExisting) {
		CfgItem ci, ci2;
		Set<String> keyset = getDefaultCfg().entries.keySet();
		for (String key : keyset) {
			if (key==null) continue;
			ci = getDefaultCfg().getCfgItem(key);
			ci2 = getCfgItem(key);
			if (ci2!=null && !overWriteExisting) {
				continue;
			}
			if (DBGforce) System.err.println("copying key: " + key);
			putCfgItem(key, ci);
		}
	}

	/**
	 * Finds the index of a value in an array storage set.
	 * Arrays may be stored as key.0=value0, key.1=value1, ...
	 * This method finds the index at which a value is store, for instance
	 * findIndexOfValue("key.", "value1") would return 1.
	 * If the value is not in the list, -1 is returned.
	 * Note: the method starts looking with index 0 (zero). If for an index to
	 * be tested, the key is not in the configuration space, it is assumed, 
	 * that the maximum number of entries of the array has been reached and
	 * the search is stopped. For instance in "key.0", "key.1", "key.3" the
	 * index 3 would never be accessed, because 2 is already missing.
	 * 
	 * @param keyBase	a String giving the base part of the key name
	 * @param value		a String giving the value to look for
	 * @return			an int specifying the index of the value in the array 
	 * 					(-1 if not found)
	 */
	protected int findIndexOfValue(String keyBase, String value) {
		String key, v2;
		int index = 0;
		while (true) {
			key = keyBase + index;
			if (!entries.containsKey(key)) 
				return -1;	// not found, stop searching
			try {
				v2 = entries.get(key).encodeValue();
			} catch (Exception e) {
				// cannot happen?!
				continue;
			}
			//was: getValueImpl();
			if (v2.equals(value)) {
				return index;
			}
			index++;
		}
	}

	protected boolean hasValueImpl(String name) {
		return entries.containsKey(name);
	}

	protected void addToRemoveQueue(String name) {
		if (removeQueue.contains(name)) {
			// nothing
		} else {
			removeQueue.add(name);
			if (DBGforce) System.err.println("added " + name + " to remove queue");
		}
	}

	protected void clearRemoveQueue() {
		removeQueue.clear();
	}
	
	protected void putEncodedCfgItem(String keyCode, String valueCode) {
		CfgItem ci;
		ci = CfgItem.createFromEncoded(keyCode, valueCode);
		putCfgItem(ci.getCiKey(), ci);
	}
}
