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
 * CfgObjectSpecification.java
 *
 * #created#  
 */
package de.admadic.cfg;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;


/**
 * @author Rainer Schwarze
 *
 * The class for loading/storing object fields from/to the configuration 
 * space.
 * To use the class, an object together with a name and a configuration
 * path and the list of field names has to be registered. The list of 
 * field names defines the fields which are loaded and stored.
 * <P>
 * The parameters path and name are used to define the base path
 * for the configuration settings. In case an instance is created with the 
 * following parameters: path = "windows", name = "protocol", fieldlist =
 * "x", "y", "width", "height". Then the following configuration space 
 * would be created:<BR>
 * <UL>
 * <LI>windows.protocol.x</LI>
 * <LI>windows.protocol.y</LI>
 * <LI>windows.protocol.width</LI>
 * <LI>windows.protocol.height</LI>
 * </UL>
 * <P>
 * The class checks whether a method postLoadSettings or preStoreSettings
 * exist. The postLoadSettings method is called after loading values 
 * into the objects fields. The preStoreSettings method is called before 
 * storing values from the objects fields into the configuration space.
 * <P>
 * The CfgObjectSpecification supports a reference to a class specification.
 * In that case the instance which references the object does not contain
 * the field list. It contains a reference to an instance which serves
 * as a "class spec". That class spec contains the field list. When an 
 * CfgObjectSpecification with a class ref is processed, it looks up
 * the list of fields via its classRef field.
 * <P>
 * The classRef field must be set explicitely because an 
 * CfgObjectSpecification is not able to look up a corresponding instance
 * by itself.
 */
public class CfgObjectSpecification {
	// force debug output
	static boolean DBGforceOS = false;
	final static boolean LOG = true;
	Logger logger = (LOG) ? Logger.getLogger("de.admadic") : null;

	final String path;	// path of config settings
	final String name;	// name of object settings
	// the config storage name is "path.name"
	String [] fieldList;	// list of field names which are stored
	Object object;	// the actual object to store
	boolean isClass;
	CfgObjectSpecification classRef;

	/**
	 * Constructs an CfgObjectSpecification instance as a "classRef" for
	 * other ObjectSpecifications.
	 * 
	 * @param path		a String defining the path on config space
	 * @param name		a String defining the name in config space
	 * @param list		a String[] containing the field names for 
	 * 					load/store operations
	 */
	public CfgObjectSpecification(String path, String name, String[] list) {
		super();
		this.path = path;
		this.name = name;
		this.fieldList = list;
		this.isClass = true;
	}		
	/**
	 * Constructs an CfgObjectSpecification instance for the given Object.
	 * 
	 * @param path		a String defining the path on config space
	 * @param name		a String defining the name in config space
	 * @param list		a String[] containing the field names for 
	 * 					load/store operations
	 * @param object	an Object to be loaded and stored
	 */
	public CfgObjectSpecification(String path, String name, String[] list, Object object) {
		super();
		this.path = path;
		this.name = name;
		this.fieldList = list;
		this.object = object;
		this.isClass = false;
	}

	/**
	 * Sets the reference to the CfgObjectSpecification which contains the
	 * field list for this instance.
	 * 
	 * @param classRef	an CfgObjectSpecification containing the field list.
	 */
	public void setClassRef(CfgObjectSpecification classRef) {
		this.classRef = classRef;
	}

	/**
	 * Load the objects specified fields from the given configuration.
	 *  
	 * @param cfg	the Cfg to use
	 */
	public void loadSettings(Cfg cfg) {
		if (isClass) {
			System.err.println("Cfg: CfgObjectSpecification: loadSettings called: isClass=true for name=" + name);
			return;
		}
		loadAndStoreSettings(cfg, true);
	}

	/**
	 * Stores the objects specified fields to the given configuration.
	 * 
	 * @param cfg	the Cfg to use
	 */
	public void storeSettings(Cfg cfg) {
		if (isClass) {
			System.err.println("Cfg: CfgObjectSpecification: loadSettings called: isClass=true for name=" + name);
			return;
		}
		loadAndStoreSettings(cfg, false);
	}

	/**
	 * Loads/stores the objects fields from/to the Cfg given.
	 * 
	 * @param cfg	the Cfg to use.
	 * @param load	true for load, false for store
	 */
	protected void loadAndStoreSettings(Cfg cfg, boolean load) {
		if (DBGforceOS) System.err.println("ObjSpc: loadAndStoreSettings load=" + load);

		if (isClass) {
			System.err.println("Cfg: CfgObjectSpecification: load/storeSettings called: isClass=true for name=" + name);
			return;
		}
		String [] fl;
		fl = fieldList;
		if (fl==null) {
			if (classRef!=null)
				fl = classRef.fieldList;
		}
		if (fl==null || fl.length==0) {
			if (DBGforceOS) System.err.println("ObjSpc: object " + name + " has no fields to access.");
			return; // nothing to store
		}
		if (object==null) {
			System.err.println("Cfg: CfgObjectSpecification: load/storeSettings called: object==null for name=" + name);
			return;
		}

		Field field;
		Class cls;
		Object fieldValue;
		cls = object.getClass();
		Method method;

		if (!load) { // == store
			try {
				if (DBGforceOS) System.err.println("ObjSpc: trying to get preStoreSettings for object " + name);
				method = cls.getDeclaredMethod("preStoreSettings", (Class[])null);
				method.invoke(object, (Object[])null);
			} catch (SecurityException e) {
				// e.printStackTrace();
				if (logger!=null) logger.severe(
						"configration error: " + e.getMessage());
			} catch (NoSuchMethodException e) {
				// e.printStackTrace();
				if (logger!=null) logger.severe(
						"configration error: " + e.getMessage());
				if (DBGforceOS) System.err.println("ObjSpc: no preStoreSettings for object " + name);
			} catch (IllegalArgumentException e) {
				// e.printStackTrace();
				if (logger!=null) logger.severe(
						"configration error: " + e.getMessage());
			} catch (IllegalAccessException e) {
				// e.printStackTrace();
				if (logger!=null) logger.severe(
						"configration error: " + e.getMessage());
			} catch (InvocationTargetException e) {
				// e.printStackTrace();
				if (logger!=null) logger.severe(
						"configration error: " + e.getMessage());
			}
		}

		for (int i=0; i<fl.length; i++) {
			try {
				String paramName;
				paramName = path + "." + name + "." + fl[i];
				if (DBGforceOS) System.err.println("ObjSpc: accessing field " + fl[i] + " for object " + name);
				field = cls.getDeclaredField(fl[i]);
				if (load) {
					// load
					fieldValue = cfg.getValue(paramName, (Object)null);
					if (fieldValue!=null) {
						field.set(object, fieldValue);
					} else {
						// ?
					}
				} else {
					// store
					fieldValue = field.get(object);
					cfg.putValue(paramName, fieldValue);
				}
			} catch (SecurityException e) {
				// e.printStackTrace();
				if (logger!=null) logger.severe(
						"configration error: " + e.getMessage());
			} catch (NoSuchFieldException e) {
				// e.printStackTrace();
				if (logger!=null) logger.severe(
						"configration error: " + e.getMessage());
			} catch (IllegalArgumentException e) {
				// e.printStackTrace();
				if (logger!=null) logger.severe(
						"configration error: " + e.getMessage());
			} catch (IllegalAccessException e) {
				// e.printStackTrace();
				if (logger!=null) logger.severe(
						"configration error: " + e.getMessage());
			}
		}

		if (load) {
			try {
				if (DBGforceOS) System.err.println("ObjSpc: trying to get postLoadSettings for object " + name);
				method = cls.getDeclaredMethod("postLoadSettings", (Class[])null);
				method.invoke(object, (Object[])null);
			} catch (SecurityException e) {
				// e.printStackTrace();
				if (logger!=null) logger.severe(
						"configration error: " + e.getMessage());
			} catch (NoSuchMethodException e) {
				//e.printStackTrace();
				if (DBGforceOS) System.err.println("ObjSpc: no postLoadSettings for object " + name);
			} catch (IllegalArgumentException e) {
				// e.printStackTrace();
				if (logger!=null) logger.severe(
						"configration error: " + e.getMessage());
			} catch (IllegalAccessException e) {
				// e.printStackTrace();
				if (logger!=null) logger.severe(
						"configration error: " + e.getMessage());
			} catch (InvocationTargetException e) {
				// e.printStackTrace();
				if (logger!=null) logger.severe(
						"configration error: " + e.getMessage());
			}
		}
	}
}