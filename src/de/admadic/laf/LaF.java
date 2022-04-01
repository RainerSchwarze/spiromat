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
package de.admadic.laf;

import java.util.logging.Logger;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * @author Rainer Schwarze
 *
 */
public class LaF {
	final static boolean LOG = true;
	private final static Logger logger = 
		(LOG) ? Logger.getLogger("de.admadic") : null;

	protected Logger getLogger() { return logger; } 

	private String name;
	private String className;
	private boolean doesDecoration;
	private ClassLoader classLoader;
	private String themepackPath;
	private int type;

	/** Unknown Look and Feel (...) */
	public static final int TYPE_NONE = 0;
	/** Primary Look and Feel (probably SkinLF) */
	public static final int TYPE_PRIMARY = 1;
	/** System Look and Feel (such as Java, Windows, etc.) */
	public static final int TYPE_SYSTEM = 2;
	/** Extra/Custom Look and Feel (such as Kunststoff, etc.) */
	public static final int TYPE_EXTRA = 3;

	/**
	 * @param name
	 * @param className
	 * @param doesDecoration 
	 */
	public LaF(String name, String className, boolean doesDecoration) {
		this(name, className, doesDecoration, TYPE_NONE);
	}

	/**
	 * @param name
	 * @param className
	 * @param doesDecoration
	 * @param type
	 */
	public LaF(String name, String className, boolean doesDecoration, int type) {
		super();
		this.name = name;
		this.className = className;
		this.doesDecoration = doesDecoration;
		this.type = type;
		this.themepackPath = "";
		if (logger!=null) logger.fine(
				"created with name=" + name + ", className=" + className);
	}

	/**
	 * @return	Returns.
	 */
	public boolean preSelect() {
		if (logger!=null) logger.fine(
				"preselect (name=" + name + ", className=" + className + ")");
		return true;
	}

	/**
	 * @return	Returns.
	 */
	public boolean select() {
		if (logger!=null) logger.fine(
				"preselect (name=" + name + ", className=" + className + ")");
		boolean ok = true;
		try {
			if (classLoader!=null) {
				if (logger!=null) logger.fine(
						"using custom classloader");
				UIManager.put("ClassLoader", this.classLoader);
				Class <?> lafClass = classLoader.loadClass(className);
				Object o = lafClass.newInstance();
				javax.swing.UIManager.setLookAndFeel((LookAndFeel)o);
			} else {
				if (logger!=null) logger.fine(
					"using system classloader");
				javax.swing.UIManager.setLookAndFeel(className);
			}
		} catch (ClassNotFoundException e) {
			// FIXME: all catches: remove stack output
			// e.printStackTrace();
			if (logger!=null) logger.severe(
					"Error selecting LaF: " + e.getMessage());
			return false;
		} catch (InstantiationException e) {
			// e.printStackTrace();
			if (logger!=null) logger.severe(
					"Error selecting LaF: " + e.getMessage());
			return false;
		} catch (IllegalAccessException e) {
			// e.printStackTrace();
			if (logger!=null) logger.severe(
					"Error selecting LaF: " + e.getMessage());
			return false;
		} catch (UnsupportedLookAndFeelException e) {
			// e.printStackTrace();
			if (logger!=null) logger.severe(
					"Error selecting LaF: " + e.getMessage());
			return false;
		}
		return ok;
	}

	/**
	 * @return	Returns.
	 */
	public boolean postSelect() {
		try {
			//if (com.l2fprod.util.OS.isOneDotFourOrMore()) 
			{
				boolean winDecYes;
				winDecYes = UIManager.getLookAndFeel().getSupportsWindowDecorations(); 
				if (winDecYes) {
					java.lang.reflect.Method method;
						method = javax.swing.JFrame.class.getMethod(
							"setDefaultLookAndFeelDecorated",
							new Class[] { boolean.class });
					method.invoke(null, new Object[] { Boolean.TRUE });
	
					method = javax.swing.JDialog.class.getMethod(
							"setDefaultLookAndFeelDecorated",
							new Class[] { boolean.class });
					method.invoke(null, new Object[] { Boolean.TRUE });
				} else {
					java.lang.reflect.Method method = 
						javax.swing.JFrame.class.getMethod(
							"setDefaultLookAndFeelDecorated",
							new Class[] { boolean.class });
					method.invoke(null, new Object[] { Boolean.FALSE });
	
					method = javax.swing.JDialog.class.getMethod(
							"setDefaultLookAndFeelDecorated",
							new Class[] { boolean.class });
					method.invoke(null, new Object[] { Boolean.FALSE });
				}
			}
//		} catch (SecurityException e) {
//			// FIXME: remove exc
//			e.printStackTrace();
//		} catch (NoSuchMethodException e) {
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			e.printStackTrace();
		} catch (Exception e) {
			// e.printStackTrace();
			if (logger!=null) logger.severe(
					"Error setting up LaF decorations: " + e.getMessage());
		}
		return true;
	}

	/**
	 * @param skin
	 * @return	Returns.
	 */
	public boolean selectSkin(LaFSkin skin) {
		if (skin==null) { /* no warn */ }
		return true;
	}

	/**
	 * @return Returns the className.
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @return Returns the doesDecoration.
	 */
	public boolean isDoesDecoration() {
		return doesDecoration;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}


	/**
	 * @return Returns the themepackPath.
	 */
	public String getThemepackPath() {
		return themepackPath;
	}

	/**
	 * @param themepackPath The themepackPath to set.
	 */
	public void setThemepackPath(String themepackPath) {
		this.themepackPath = themepackPath;
		if (this.themepackPath==null) 
			this.themepackPath = "";
	}

	/**
	 * @return Returns the classLoader.
	 */
	public ClassLoader getClassLoader() {
		return classLoader;
	}

	/**
	 * @param classLoader The classLoader to set.
	 */
	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	/**
	 * @return Returns the type.
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type The type to set.
	 */
	public void setType(int type) {
		this.type = type;
	}
}
