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


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Logger;

import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.IXMLParser;
import net.n3.nanoxml.IXMLReader;
import net.n3.nanoxml.StdXMLReader;
import net.n3.nanoxml.XMLElement;
import net.n3.nanoxml.XMLException;
import net.n3.nanoxml.XMLParserFactory;
import net.n3.nanoxml.XMLWriter;
import de.admadic.util.FileUtil;

/**
 * Limitations:
 * The class currently cannot handle different node-names in the XML file.
 * The data with the wrong node name may be lost.
 * 
 * @author Rainer Schwarze
 */
public class CfgPersistenceXML implements 
	CfgPersistenceItemized, CfgPersistenceGrouped {
	final static boolean LOG = true;
	Logger logger = (LOG) ? Logger.getLogger("de.admadic") : null;

	CfgProvider cfgProvider;
	String fileName;
	/*
	 * When this tree cache is null, the data was not yet loaded.
	 * For any public method: When the tree cache is null, the tree cache
	 * must be initialised, which means, that it is newly generated.
	 * It shall not be loaded.
	 */
	IXMLElement xmlTreeCache;

	/**
	 * 
	 */
	public CfgPersistenceXML() {
		this(null, null);
	}

	/**
	 * @param cp 
	 */
	public CfgPersistenceXML(CfgProvider cp) {
		this(cp, null);
	}

	/**
	 * @param cp 
	 * @param fileName 
	 */
	public CfgPersistenceXML(CfgProvider cp, String fileName) {
		super();
		this.cfgProvider = cp;
		this.fileName = fileName;
	}

	// ************************************************
	// Grouped Interface
	// ************************************************

	/**
	 * @param cp
	 * @see de.admadic.cfg.CfgPersistenceGrouped#registerCfgProvider(de.admadic.cfg.CfgProvider)
	 */
	public void registerCfgProvider(CfgProvider cp) {
		cfgProvider = cp;
	}

	/**
	 * @param fileName
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	protected void putToCfgProvider(String path) throws CfgException {
		Vector<?> v;
		IXMLElement map;
		map = getNodeMap(xmlTreeCache, path);

		// find the entry's:
		v = map.getChildrenNamed("entry");
		for (Object object : v) {
			IXMLElement xml = (IXMLElement)object;
			String keyCode = xml.getAttribute("key", null);
			String valueCode = xml.getAttribute("value", null);
			String metaCode = xml.getAttribute("meta", null);
			if (keyCode==null || valueCode==null || metaCode==null) {
				throw new Error(
						"CfgPersistenceXML: Invalid config format. "+
						"entry: key, value or meta is null");
			}
			CfgItem ci = new CfgItem();
			ci.decodeKey(keyCode);
			ci.decodeMeta(metaCode);
			ci.decodeValue(valueCode);
			cfgProvider.putCfgItem(ci.getCiKey(), ci);
		}
	}

	protected void getFromCfgProvider(String path) {
		IXMLElement map, entry;
		map = getNodeMap(xmlTreeCache, path);
		if (map==null) {
			throw new Error(
					"CfgPersistenceXML: map (for node name = " + path + ")"+
					" not there.");
		}

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
		
				entry = getEntry(map, keyCode);
				if (entry==null) {
					entry = map.createElement("entry");
					map.addChild(entry);
				}
				entry.setAttribute("key", keyCode);
				entry.setAttribute("value", valueCode);
				entry.setAttribute("meta", metaCode);
			} catch (CfgException e) {
				//e.printStackTrace();
				System.err.println("Cfg: error creating encoding. " + e);
			}
		}
	}

	protected void mergeIntoTreeCache(IXMLElement xmlPref, String path) {
		IXMLElement mapCache, map;
		mapCache = getNodeMap(xmlTreeCache, path);
		map = getNodeMap(xmlPref, path);
		Vector<?> mapv = map.getChildrenNamed("entry");
		for (Object object : mapv) {
			IXMLElement xml = (IXMLElement)object;
			String key = xml.getAttribute("key", null);
			if (key==null) continue;
			IXMLElement xmlCache = getEntry(mapCache, key);
			if (xmlCache==null) {
				xmlCache = mapCache.createElement("entry");
				mapCache.addChild(xmlCache);
			}
			String value = xml.getAttribute("value", null);
			String meta = xml.getAttribute("meta", null);
			if (value==null || meta==null) {
				throw new Error(
						"CfgPersistenceXML: "+
						"missing value or meta info in loaded data");
			}
			xmlCache.setAttribute("key", key);
			xmlCache.setAttribute("value", value);
			xmlCache.setAttribute("meta", meta);
		}
	}

	/**
	 * @param path
	 * @see de.admadic.cfg.CfgPersistenceGrouped#load(java.lang.String)
	 */
	public void load(String path) {
		try {
			String readName = FileUtil.fixFileName1(fileName);
//			URL readURL = new URL("file", null, -1, fileName);
//			readName = readURL.toString();
//			readName = fileName;
//			System.out.println("xml read spec = " + readName);
			if (xmlTreeCache==null) {
				createTree(path);
			}
			IXMLParser parser;
			parser = XMLParserFactory.createDefaultXMLParser();
			IXMLReader reader = StdXMLReader.fileReader(readName);
			parser.setReader(reader);
			IXMLElement xmlPref = (IXMLElement)parser.parse();
			mergeIntoTreeCache(xmlPref, path);

			putToCfgProvider(path);
		} catch (ClassNotFoundException e) {
			// e.printStackTrace();
			if (logger!=null) logger.severe(
					"Error loading config: " + e.getMessage());
		} catch (InstantiationException e) {
			// e.printStackTrace();
			if (logger!=null) logger.severe(
					"Error loading config: " + e.getMessage());
		} catch (IllegalAccessException e) {
			// e.printStackTrace();
			if (logger!=null) logger.severe(
					"Error loading config: " + e.getMessage());
		} catch (FileNotFoundException e) {
			// e.printStackTrace(); 
			// <- we do not need that because its a "frequent" situation
			// not really an error.
			createTree(path);
		} catch (IOException e) {
			// e.printStackTrace();
			if (logger!=null) logger.severe(
					"IO error loading config: " + e.getMessage());
		} catch (XMLException e) {
			// e.printStackTrace();
			if (logger!=null) logger.severe(
					"XML error loading config: " + e.getMessage());
		} catch (CfgException e) {
			// e.printStackTrace();
			if (logger!=null) logger.severe(
					"Cfg error loading config: " + e.getMessage());
		}
	}

	/**
	 * @param path
	 * @see de.admadic.cfg.CfgPersistenceGrouped#store(java.lang.String)
	 */
	public void store(String path) {
		initTree(path);

		getFromCfgProvider(path);

		doStore();
	}

	/**
	 * Note: The configuration is not automatically saved! Call save for that.
	 * @param path
	 * @param keys
	 * @see de.admadic.cfg.CfgPersistenceGrouped#removeKeys(java.lang.String, java.util.Enumeration)
	 */
	public void removeKeys(String path, Enumeration<String> keys) {
		initTree(path);

		IXMLElement map, entry;
		map = getNodeMap(xmlTreeCache, path);
		if (map==null) {
			throw new Error(
					"CfgPersistenceXML: map (for node name = " + path + ")"+
					" not there.");
		}
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			entry = getEntry(map, key);
			if (entry!=null) {
				map.removeChild(entry);
			}
		}

		doStore();
	}

	protected void doStore() {
		try {
			String lf;
			try {
				lf = System.getProperty("line.separator");
			} catch (Exception e) { 
				lf = null;
			}
			if (lf==null) {
				lf = "\n";
			}
			java.io.Writer output;
			output = new FileWriter(new File(fileName));
			output.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + lf);
			//output.append("<!DOCTYPE preferences SYSTEM \"cfg.dtd\">" + lf);
			IXMLElement xmltree = xmlTreeCache;
			XMLWriter xmlwriter = new XMLWriter(output);
			xmlwriter.write(xmltree, true);	
		} catch (IOException e) {
			// e.printStackTrace();
			if (logger!=null) logger.severe(
					"IO error storing config: " + e.getMessage());
		}
	}

	/**
	 * @param path
	 * @return	returns.
	 * @see de.admadic.cfg.CfgPersistenceGrouped#clear(java.lang.String)
	 */
	public boolean clear(String path) {
		initTree(path);

		IXMLElement map, entry;
		map = getNodeMap(xmlTreeCache, path);
		if (map==null) {
			throw new Error(
					"CfgPersistenceXML: map (for node name = " + path + ")"+
					" not there.");
		}
		Enumeration<?> en = map.enumerateChildren();
		while (en.hasMoreElements()) {
			entry = (IXMLElement)en.nextElement();
			map.removeChild(entry);
		}
		doStore();
		return true;
	}

	// ************************************************
	// Itemized Interface
	// ************************************************

	/**
	 * @param path
	 * @see de.admadic.cfg.CfgPersistenceItemized#prepareLoad(java.lang.String)
	 */
	public void prepareLoad(String path) {
		// not yet done
	}

	/**
	 * @param path
	 * @see de.admadic.cfg.CfgPersistenceItemized#prepareStore(java.lang.String)
	 */
	public void prepareStore(String path) {
		// not yet done
	}

	/**
	 * @return	Returns the keys available in this storage.
	 * @see de.admadic.cfg.CfgPersistenceItemized#getKeys()
	 */
	public String[] getKeys() {
		return null;
	}

	/**
	 * @param key
	 * @return	Returns the cfg item for that key.
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
		// not implemented
	}

	/**
	 * 
	 * @see de.admadic.cfg.CfgPersistenceItemized#finalizeLoad()
	 */
	public void finalizeLoad() {
		// not implemented
	}

	/**
	 * 
	 * @see de.admadic.cfg.CfgPersistenceItemized#finalizeStore()
	 */
	public void finalizeStore() {
		// not implemented
	}

	// ****************************************
	// Internal Functions
	// ****************************************

	protected IXMLElement getNodeMap(IXMLElement tree, String path) {
		Vector<?> v;
		IXMLElement xml, xmlRoot, xmlNode, xmlMap;

		// find the user root:
		v = tree.getChildrenNamed("root");
		xmlRoot = null;
		for (Object object : v) {
			xml = (IXMLElement)object;
			String tmp = xml.getAttribute("type", null);
			if (tmp==null) continue;
			if (!tmp.equals("user")) continue;
			xmlRoot = xml;
			break;
		}
		if (xmlRoot==null) {
			throw new Error(
					"CfgPersistenceXML: Invalid config format. "+
					"root with type=user missing.");
		}

		// find the node:
		v = xmlRoot.getChildrenNamed("node");
		xmlNode = null;
		for (Object object : v) {
			xml = (IXMLElement)object;
			String tmp = xml.getAttribute("name", null);
			if (tmp==null) continue;
			if (!tmp.equals(path)) continue;
			xmlNode = xml;
			break;
		}
		if (xmlNode==null) {
			throw new Error(
					"CfgPersistenceXML: Invalid config format. "+
					"root.node (name=" + path + ") missing.");
		}
		// get the map:
		xmlMap = xmlNode.getFirstChildNamed("map");

		return xmlMap;
	}

	// FIXME: insert a hash method for fast access
	protected IXMLElement getEntry(IXMLElement map, String key) {
		Vector<?> v = map.getChildrenNamed("entry");
		for (Object object : v) {
			IXMLElement xml = (IXMLElement)object;
			String keyCode = xml.getAttribute("key", null);
			if (keyCode.equals(key)) {
				return xml;
			}
		}
		return null;
	}

	protected void initTree(String path) {
		// we need a merge op
		if (xmlTreeCache==null) {
			load(path);
		}
		if (xmlTreeCache==null) {
			createTree(path);
		}
	}

	protected void createTree(String path) {
		IXMLElement pref, root, rootmap, node, map;
		pref = new XMLElement("preferences");
		root = pref.createElement("root");
		pref.addChild(root);
		root.setAttribute("type", "user");
		rootmap = root.createElement("map");
		root.addChild(rootmap);
		node = root.createElement("node");
		root.addChild(node);
		node.setAttribute("name", path);
		map = node.createElement("map");
		node.addChild(map);
		// now xmlTreeCache is initialized
		xmlTreeCache = pref;
	}

}
