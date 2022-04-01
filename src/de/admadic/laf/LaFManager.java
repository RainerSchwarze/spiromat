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

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Logger;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.EventListenerList;

/**
 * Order of calling:
 * 
 * - preSelect()
 * - selectSkin()
 * - select()
 * - postSelect()
 * - updateUI()
 * 
 * 
 * @author Rainer Schwarze
 *
 */
public class LaFManager {
	final static boolean LOG = true;
	final static Logger logger = (LOG) ? Logger.getLogger("de.admadic") : null;


	ArrayList<Component> components;
	ArrayList<LaF> laFs;
	Hashtable<LaF,ArrayList<LaFSkin>> skins;
	LaF selectedLaF;
	LaFSkin selectedLaFSkin;

	LaF fallBackLaF;
	
	EventListenerList listenerList = new EventListenerList();
	LaFChangeEvent lafChangeEvent = null;
	ClassLoader classLoader = null;

	/**
	 * 
	 */
	public LaFManager() {
		super();
		components = new ArrayList<Component>();
		laFs = new ArrayList<LaF>();
		skins = new Hashtable<LaF,ArrayList<LaFSkin>>();
		selectedLaF = null;
		selectedLaFSkin = null;
	}

	/**
	 * Registers a custom class loader.
	 * That can be used in case look and feels are provided as jar files
	 * which are not in the classpath. Then the custom class loader can for 
	 * instance be a URLClassLoader which provides access to additional jars 
	 * which are not in the standard classpath.
	 * @param cl
	 */
	public void setClassLoader(ClassLoader cl) {
		this.classLoader = cl;
		UIManager.put("ClassLoader", this.classLoader);
	}

	/**
	 * Initialize the standard look and feels which are supplied in nearly
	 * any Java (desktop) environment.
	 */
	public void initStandardLaFs() {
		addLaF("Java", UIManager.getCrossPlatformLookAndFeelClassName(), true);
		addLaF("System", UIManager.getSystemLookAndFeelClassName(), false);
		addLaF("Windows", "com.sun.java.swing.plaf.windows.WindowsLookAndFeel", false);
		addLaF("GTK", "com.sun.java.swing.plaf.gtk.GTKLookAndFeel", false);
		addLaF("Motif", "com.sun.java.swing.plaf.motif.MotifLookAndFeel", false);
	}

	/**
	 * Add a component to the supervised list of components which shall be 
	 * updated when the LaF changes.
	 * 
	 * @param c
	 */
	public void addComponent(Component c) {
		components.add(c);
	}

	/**
	 * Add a component to the supervised list of components which shall be 
	 * updated when the LaF changes.
	 * 
	 * @param c
	 */
	public void addComponentToHead(Component c) {
		components.add(0, c);
	}

	/**
	 * Remove a component from the supervised list of components which shall be 
	 * updated when the LaF changes.
	 * 
	 * @param c
	 */
	public void removeComponent(Component c) {
		components.remove(c);
	}

	/**
	 * Implementation method for adding a LaF.
	 * @param laf
	 */
	protected void addLaFImpl(LaF laf) {
		if (classLoader!=null) {
			laf.setClassLoader(classLoader);
		}
		laFs.add(laf);
		skins.put(laf, new ArrayList<LaFSkin>());
		if (
				laf.getClassName()!=null && 
				laf.getClassName().equals(
						UIManager.getCrossPlatformLookAndFeelClassName())
			) {
			fallBackLaF = laf;
		}
	}

	/**
	 * Implementation method for removing a LaF.
	 * @param laf
	 */
	protected void removeLaFImpl(LaF laf) {
		laFs.remove(laf);
		skins.remove(laf);
		if (fallBackLaF==laf) {	// FIXME: check whether == or equals!
			fallBackLaF = null;
		}
	}

	/**
	 * @param laf
	 * @param skin
	 */
	public void addSkin(LaF laf, LaFSkin skin) {
		skins.get(laf).add(skin);
	}

	/**
	 * @param laf
	 * @param skin
	 */
	public void removeSkin(LaF laf, LaFSkin skin) {
		skins.get(laf).remove(skin);
	}

	/**
	 * @param laf
	 * @param name
	 * @param dataName
	 */
	public void addSkin(LaF laf, String name, String dataName) {
		addSkin(laf, new LaFSkin(name, dataName));
	}

	/**
	 * @param name
	 * @param className
	 * @param doesDecoration
	 */
	public void addLaF(String name, String className, boolean doesDecoration) {
		addLaF(name, className, doesDecoration, null, LaF.TYPE_NONE);
	}

	/**
	 * @param name
	 * @param className
	 * @param doesDecoration
	 * @param handlerClassName
	 * @param typei 
	 */
	public void addLaF(String name, String className, boolean doesDecoration, 
			String handlerClassName, int typei) {
		LaF laf = null;
		if (handlerClassName==null) {
			if (logger!=null) logger.fine(
					"addLaF: creating simple LaF without handler class for: " + 
					name + ":" + className);
			laf = new LaF(name, className, doesDecoration, typei);
		} else {
			if (logger!=null) logger.fine(
					"addLaF: creating simple LaF with handler class for: " + 
					name + ":" + className + ":" + handlerClassName);
			try {
				Class [] ctorArgsTypes = { 
						String.class, String.class, boolean.class, int.class 
						};
				Object [] args = {
						new String(name), 
						new String(className), 
						new Boolean(doesDecoration),
						new Integer(typei)
						};
				// FIXME: create proper classLoader handling!
				Class<?> cls;
				if (classLoader!=null) {
					if (logger!=null) logger.fine(
							"addLaF: using custom class loader for: " + 
							handlerClassName);
					cls = classLoader.loadClass(handlerClassName);
				} else {
					if (logger!=null) logger.fine(
							"addLaF: using system class loader for: " + 
							handlerClassName);
					cls = Class.forName(handlerClassName);
				}
				Constructor<?> ctor = cls.getDeclaredConstructor(ctorArgsTypes);
				Object obj = ctor.newInstance(args);
				if (logger!=null) logger.fine(
						"addLaF: LaF instance created.");
				laf = (LaF)obj;
//			} catch (ClassNotFoundException e) {
//				e.printStackTrace();
//			} catch (SecurityException e) {
//				e.printStackTrace();
//			} catch (NoSuchMethodException e) {
//				e.printStackTrace();
//			} catch (IllegalArgumentException e) {
//				e.printStackTrace();
//			} catch (InstantiationException e) {
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				e.printStackTrace();
//			} catch (InvocationTargetException e) {
//				e.printStackTrace();
			} catch (Exception e) {
				// e.printStackTrace();
				if (logger!=null) logger.severe(
						"Error adding LaF: " + e.getMessage());
			}
		}
		if (laf==null) {
			// FIXME: error
			if (logger!=null) logger.warning(
					"addLaF: failed to create LaF instance for " + name);
			return;
		}
		addLaFImpl(laf);
	}

	/**
	 * @param name
	 * @param className
	 */
	public void removeLaF(String name, String className) {
		LaF laf = getLaF(name, className);
		if (laf!=null) {
			removeLaFImpl(laf);
		}
	}

	/**
	 * 
	 */
	public void clearAllLaFs() {
		laFs.clear();
		skins.clear(); // Hashtable<LaF,ArrayList<LaFSkin>>();
	}
	
	/**
	 * 
	 */
	public void updateUI() {
		ArrayList<Boolean> displayableArray = new ArrayList<Boolean>(components.size());
		ArrayList<Boolean> visibleArray = new ArrayList<Boolean>(components.size());
		boolean useDec;
		useDec = UIManager.getLookAndFeel().getSupportsWindowDecorations();
		for (int i=0; i<components.size(); i++) {
			Component c = components.get(i);
			boolean displayable = c.isDisplayable();
			boolean visible = c.isVisible();

			displayableArray.add(i, new Boolean(displayable));
			visibleArray.add(i, new Boolean(visible));

			if (c instanceof Frame) {
				Frame frm = (Frame)c;
				if (displayable) {
					frm.dispose();
				}
				frm.setUndecorated(useDec);
			}
			if (c instanceof Dialog) {
				Dialog dlg = (Dialog)c;
				if (displayable) {
					dlg.dispose();
				}
				dlg.setUndecorated(useDec);
			}
			SwingUtilities.updateComponentTreeUI(c);
			if (c instanceof Window) {
				// pack makes it displayable again always
				((Window)c).pack();
			}
		}
		for (int i=components.size()-1; i>=0; i--) {
			Component c = components.get(i);
			if (visibleArray.get(i).booleanValue()) {
				c.setVisible(true);
			}
		}
	}

	/**
	 * 
	 * @param name
	 * @param className
	 * @return	Returns the LaF object for the laf-name or classname.
	 */
	public LaF getLaF(String name, String className) {
		if (name==null && className==null) 
			return null;
		boolean match;
		for (LaF laf : laFs) {
			match = true;
			if (name!=null && !name.equals(laf.getName())) {
				match = false;
			}
			if (className!=null && !className.equals(laf.getClassName())) {
				match = false;
			}
			if (match) {
				return laf;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param laf
	 * @param name
	 * @return	Returns the LaFSkin for the given LaF and the skin name.
	 */
	protected LaFSkin getLaFSkin(LaF laf, String name) {
		if (laf==null || name==null) 
			return null;
		ArrayList<LaFSkin> sk = skins.get(laf);
		for (LaFSkin skin : sk) {
			if (name.equals(skin.name)) {
				return skin;
			}
		}
		return null;
	}

	/**
	 * @return Returns the selectedLaF.
	 */
	public LaF getSelectedLaF() {
		return selectedLaF;
	}

	/**
	 * @return Returns the selectedLaFSkin.
	 */
	public LaFSkin getSelectedLaFSkin() {
		return selectedLaFSkin;
	}

	/**
	 * @param name
	 * @return	Returns true if successful, false otherwise.
	 */
	public boolean selectLaF(String name) {
		return selectLaF(name, null);
	}

	/**
	 * @param name
	 * @param skinName
	 * @return	Returns true if successful, false otherwise.
	 */
	public boolean selectLaF(String name, String skinName) {
		if (logger!=null) logger.fine("selectLaF: " + name + ", " + skinName);
		LaF laf = getLaF(name, null);
		if (laf==null) {
			// FIXME: make this an error?
			if (logger!=null) logger.warning("could not find LaF " + name);
			return false;
		}
		LaFSkin skin = null;
		if (skinName!=null)
			skin = getLaFSkin(laf, skinName);

		LaF oldLaF = selectedLaF;
		LaFSkin oldSkin = selectedLaFSkin;

		fireLaFChangeBegin(name, skinName);

		boolean ok = true;

		try {
			if (ok) {
				if (logger!=null) logger.fine("preSelect");
				ok = laf.preSelect();
			}
			if (ok && skin!=null) {
				if (logger!=null) logger.fine("selectSkin");
				ok = laf.selectSkin(skin);
			}
			if (ok) {
				if (logger!=null) logger.fine("select");
				ok = laf.select();
			}
			if (ok) {
				if (logger!=null) logger.fine("postSelect");
				ok = laf.postSelect();
			}
		} catch (NullPointerException e) {
			if (logger!=null) logger.warning(e.getMessage());
			ok = false;
		} catch (Exception e) {
			if (logger!=null) logger.warning(e.getMessage());
			ok = false;
		} catch (Error e) {
			if (logger!=null) logger.warning(e.getMessage());
			ok = false;
		}

		if (!ok) {
			if (logger!=null) logger.warning("selectLaF: failed. restoring old settings");
			if (oldLaF!=null) {
				oldLaF.preSelect();
				if (oldSkin!=null) oldLaF.selectSkin(oldSkin);
				oldLaF.select();
				oldLaF.postSelect();
			} else {
				// try to use Java LaF as fallback:
				laf = fallBackLaF;
				// was: laf = getLaF(CfgCalc.UI_LAF_STD_XPLATFORM_NAME, null);
				if (laf!=null) {
					laf.preSelect();
					laf.select();
					laf.postSelect();
				}
			}
			fireLaFChangeFailed(name, skinName);
		} else {
			selectedLaF = laf;
			selectedLaFSkin = skin;
			fireLaFChanged(name, skinName);
		}

		updateUI();
		fireLaFChangedUI(name, skinName);
		return ok;
	}

	/**
	 * @return	Returns an array of available LaF names.
	 */
	public String[] getLaFNames() {
		return getLaFNames(LaF.TYPE_NONE);
//		int count = laFs.size();
//		String [] names = new String[count];
//		for (int i=0; i<names.length; i++) {
//			names[i] = laFs.get(i).getName();
//		}
//		return names;
	}

	/**
	 * Returns the names of the LaFs which match the given type. If TYPE_NONE
	 * is given, all LaFs names are returned.
	 * @param typei
	 * @return	Returns an array of available LaF names with the given type.
	 */
	public String[] getLaFNames(int typei) {
		int count = 0;
		String [] names = null;
		if (typei==LaF.TYPE_NONE) {
			count = laFs.size();
			names = new String[count];
			for (int i=0; i<names.length; i++) {
				names[i] = laFs.get(i).getName();
			}
		} else {
			for (int i=0; i<laFs.size(); i++) {
				if (laFs.get(i).getType()==typei) {
					count++;
				}
			}
			names = new String[count];
			int idx = 0;
			for (int i=0; i<laFs.size(); i++) {
				if (laFs.get(i).getType()==typei) {
					names[idx] = laFs.get(i).getName();
					idx++;
				}
			}
		}
		return names;
	}


	/**
	 * 
	 * @param laf
	 * @return	Returns an array of available Skin names for the LaF.
	 */
	public String[] getSkinNames(LaF laf) {
		ArrayList<LaFSkin> sk = skins.get(laf);
		int count = sk.size();
		String [] names = new String[count];
		for (int i=0; i<names.length; i++) {
			names[i] = sk.get(i).name;
		}
		return names;
	}

	/**
	 * @param menu
	 */
	public void createLaFSkinMenu(JMenu menu) {
		createLaFSkinMenu(menu, true);
	}
	
	/**
	 * @param menu
	 * @param asSubMenu
	 */
	public void createLaFSkinMenu(JMenu menu, boolean asSubMenu) {
		LaF laf;
		String [] lafNames;

		if (laFs.size()<1) {
			JMenuItem lafMenuItem;
			// no LaFs there
			lafMenuItem = new JMenuItem();
			menu.add(lafMenuItem);
			lafMenuItem.setText("<No Skins>");
			lafMenuItem.setEnabled(false);
			return;
		}

		ActionListener al = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String cmd = e.getActionCommand();
				String lafName, skinName;
				int colonpos;
				colonpos = cmd.indexOf(LaFConstants.CMD_ITEM_SEPARATOR);
				if (colonpos>=0) {
					lafName = cmd.substring(0, colonpos);
					skinName = cmd.substring(colonpos+1);
				} else {
					lafName = cmd;
					skinName = null;
				}
				boolean ok;
				try {
					ok = LaFManager.this.selectLaF(lafName, skinName);
				} catch (Exception ex) {
					ok = false;
				}
				if (!ok) {
					JOptionPane.showMessageDialog(
							null, 
							"An Error occured during activation of\n"+
							"Look-And-Feel = '" + lafName + 
							"' , Skin = '" + skinName + "'.\n" + 
							"You may want to contact customer support.",
							"Error selecting Skin",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		};

		if (logger!=null) logger.fine("create menu PRIMARY...");
		lafNames = this.getLaFNames(LaF.TYPE_PRIMARY);
		for (int i = 0; i < lafNames.length; i++) {
			laf = this.getLaF(lafNames[i], null);
			createLaFSkinMenu_doLaFItem(menu, laf, asSubMenu, al);
		}

		if (logger!=null) logger.fine("create menu SYSTEM...");
		lafNames = this.getLaFNames(LaF.TYPE_SYSTEM);
		if (lafNames.length>0) 
			menu.add(new JSeparator());
		for (int i = 0; i < lafNames.length; i++) {
			laf = this.getLaF(lafNames[i], null);
			createLaFSkinMenu_doLaFItem(menu, laf, asSubMenu, al);
		}

		if (logger!=null) logger.fine("create menu EXTRA...");
		lafNames = this.getLaFNames(LaF.TYPE_EXTRA);
		if (lafNames.length>0) 
			menu.add(new JSeparator());
		for (int i = 0; i < lafNames.length; i++) {
			laf = this.getLaF(lafNames[i], null);
			createLaFSkinMenu_doLaFItem(menu, laf, asSubMenu, al);
		}
	}

	protected void createLaFSkinMenu_doLaFItem(
			JMenu menu, LaF laf, boolean asSubMenu, ActionListener al) {
		if (logger!=null) {
			logger.fine(
					"create menu item laf: " + 
					" name=" + laf.getName() + " cname=" + laf.getClassName());
		}
		LaFSkin lafSkin;
		JMenu lafMenu = null;
		JMenuItem lafMenuItem;
		JMenuItem skinMenuItem;
		String [] skinNames;
		// do we have skins?
		skinNames = this.getSkinNames(laf);
		if (skinNames!=null && skinNames.length>0) {
			if (asSubMenu) {
				lafMenu = new JMenu();
				menu.add(lafMenu);
				lafMenu.setText(laf.getName());
			} else {
				lafMenuItem = new JMenuItem();
				menu.add(lafMenuItem);
				lafMenuItem.setText(laf.getName() + ":");
				lafMenuItem.setEnabled(false);
				//lafMenuItem.setActionCommand(laf.getName());
				//lafMenuItem.addActionListener(al);
			}
			for (int j = 0; j < skinNames.length; j++) {
				lafSkin = this.getLaFSkin(laf, skinNames[j]);
				if (logger!=null) {
					logger.fine(
							"create menu item skin: " + 
							" name=" + lafSkin.getName() + " dname=" + lafSkin.getDataName());
				}
				skinMenuItem = new JMenuItem();
				if (asSubMenu) {
					lafMenu.add(skinMenuItem);
				} else {
					menu.add(skinMenuItem);
				}
				skinMenuItem.setText(lafSkin.getName());
				skinMenuItem.setActionCommand(
						laf.getName() + ":" + lafSkin.getName());
				skinMenuItem.addActionListener(al);
			}
		} else {
			lafMenuItem = new JMenuItem();
			menu.add(lafMenuItem);
			lafMenuItem.setText(laf.getName());
			lafMenuItem.setActionCommand(laf.getName());
			lafMenuItem.addActionListener(al);
		}
	}

	 /**
	 * @param l
	 */
	public void addLaFChangeListener(LaFChangeListener l) {
	     listenerList.add(LaFChangeListener.class, l);
	 }

	/**
	 * @param l
	 */
	public void removeLaFChangeListener(LaFChangeListener l) {
		listenerList.remove(LaFChangeListener.class, l);
	}
	
	// Notify all listeners that have registered interest for
	// notification on this event type.  The event instance 
	// is lazily created using the parameters passed into 
	// the fire method.
	protected void fireLaFChanged(String lafName, String skinName) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==LaFChangeListener.class) {
				// Lazily create the event:
				if (lafChangeEvent == null)
					lafChangeEvent = new LaFChangeEvent(this);
				lafChangeEvent.setLafName(lafName);
				lafChangeEvent.setSkinName(skinName);
				((LaFChangeListener)listeners[i+1]).lafChanged(lafChangeEvent);
			}
		}
	}

	protected void fireLaFChangeBegin(String lafName, String skinName) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==LaFChangeListener.class) {
				// Lazily create the event:
				if (lafChangeEvent == null)
					lafChangeEvent = new LaFChangeEvent(this);
				lafChangeEvent.setLafName(lafName);
				lafChangeEvent.setSkinName(skinName);
				((LaFChangeListener)listeners[i+1]).lafChangeBegin(lafChangeEvent);
			}
		}
	}

	protected void fireLaFChangeFailed(String lafName, String skinName) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==LaFChangeListener.class) {
				// Lazily create the event:
				if (lafChangeEvent == null)
					lafChangeEvent = new LaFChangeEvent(this);
				lafChangeEvent.setLafName(lafName);
				lafChangeEvent.setSkinName(skinName);
				((LaFChangeListener)listeners[i+1]).lafChangeFailed(lafChangeEvent);
			}
		}
	}

	protected void fireLaFChangedUI(String lafName, String skinName) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==LaFChangeListener.class) {
				// Lazily create the event:
				if (lafChangeEvent == null)
					lafChangeEvent = new LaFChangeEvent(this);
				lafChangeEvent.setLafName(lafName);
				lafChangeEvent.setSkinName(skinName);
				((LaFChangeListener)listeners[i+1]).lafChangedUI(lafChangeEvent);
			}
		}
	}
}
