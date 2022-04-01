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
package de.admadic.spiromat.model.io;

import java.awt.Color;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.admadic.spiromat.log.Logger;
import de.admadic.spiromat.model.DocModel;
import de.admadic.spiromat.model.FigureSpec;

/**
 * @author Rainer Schwarze
 *
 */
public class DocumentReader extends DefaultHandler {
	static Logger logger = Logger.getLogger(DocumentReader.class);

	File file;
	DocModel docModel;

	private boolean inSpiromat = false;
	private boolean versionOk = false;
	private LinkedList<String> currentPath = new LinkedList<String>();
	private StringBuffer currentPathString = new StringBuffer();

	private FigureSpec currentFigureSpec = null;

	private boolean hasActiveFigureIndex = false;
	
	/**
	 * @param file 
	 * 
	 */
	public DocumentReader(File file) {
		super();
		this.file = file;
	}

	/**
	 * 
	 */
	public void read() {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser;
		try {
			parser = factory.newSAXParser();
			parser.parse(file, this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new SpiromatIOException("error loading file", e); //$NON-NLS-1$
		}
		if (docModel==null || !versionOk) {
			throw new SpiromatIOException("could not load file"); //$NON-NLS-1$
		}
	}

	/**
	 * @return the docModel
	 */
	public DocModel getDocModel() {
		return docModel;
	}

	/**
	 * @throws SAXException
	 * @see org.xml.sax.helpers.DefaultHandler#startDocument()
	 */
	@Override
	public void startDocument() throws SAXException {
		logger.info("startDocument"); //$NON-NLS-1$
		docModel = new DocModel();
	}

	/**
	 * @throws SAXException
	 * @see org.xml.sax.helpers.DefaultHandler#endDocument()
	 */
	@Override
	public void endDocument() throws SAXException {
		logger.info("endDocument"); //$NON-NLS-1$
		// nothing to do at the end
	}

	/**
	 * @param uri
	 * @param localName
	 * @param qName
	 * @param attributes
	 * @throws SAXException
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		logger.info("startElement:" + //$NON-NLS-1$
				" uri=" + uri + //$NON-NLS-1$
				" localName=" + localName +  //$NON-NLS-1$
				" qName=" + qName); //$NON-NLS-1$
        String eName = localName; // element name
        if ("".equals(eName)) eName = qName; // namespaceAware = false //$NON-NLS-1$
        appendToPath(eName);
        if (!inSpiromat) {
            if (eName.equals(Constants.NODE_SPIROMAT)) {
            	inSpiromat = true;
            	logger.info("now inSpiromat=true"); //$NON-NLS-1$
            	return;
            }
        	logger.info("not in spiromat and also not root tag"); //$NON-NLS-1$
        	return;
        }
        if (!versionOk) {
        	logger.info("version not yet verified..."); //$NON-NLS-1$
        	if (eName.equals(Constants.NODE_VERSION)) {
        		coreCheckVersion(attributes);	// throws if bad
            	logger.info("version verified!"); //$NON-NLS-1$
        		return; // ok
        	}
        	logger.info("ignoring as long as version is not yet found..."); //$NON-NLS-1$
			// ignore other elements as long as version is not found
    		return;
        }

        logger.info("---> " + currentPathString); //$NON-NLS-1$
        if (currentPathString.toString().equals(Constants.PATH_FIGURESPEC)) {
        	coreCreateFigureSpec(attributes);
        	return;
        } else if (currentPathString.toString().equals(Constants.PATH_FIGURESPEC_STATUS)) {
        	coreSetFigureSpecStatus(attributes);
        	return;
        } else if (currentPathString.toString().equals(Constants.PATH_DOCSTATUS_PARAM)) {
        	coreSetDocStatus(attributes);
        	if (!hasActiveFigureIndex) {
        		throw new SpiromatIOException("the active figure was not specified"); //$NON-NLS-1$
        	}
        	return;
        } else if (currentPathString.toString().equals(Constants.PATH_APPSTATUS_PARAM)) {
        	coreSetAppStatus(attributes);
        	return;
        } 
	}

	/**
	 * @param uri
	 * @param localName
	 * @param qName
	 * @throws SAXException
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		logger.info("endElement:" + //$NON-NLS-1$
				" uri=" + uri + //$NON-NLS-1$
				" localName=" + localName +  //$NON-NLS-1$
				" qName=" + qName); //$NON-NLS-1$
        String eName = localName; // element name
        if ("".equals(eName)) eName = qName; // namespaceAware = false //$NON-NLS-1$

        if (currentPathString.toString().equals(Constants.PATH_FIGURESPEC)) {
        	currentFigureSpec = null;
        } else if (currentPathString.toString().equals(Constants.PATH_SPIROMAT)) {
        	inSpiromat = false;
        }
        
        removeFromPath(eName);
	}

	/**
	 * @param attributes
	 */
	private void coreCreateFigureSpec(Attributes attributes) {
		int outerRadius = -1;
		int innerRadius = -1;
		double penHolePos = -1.0;
		Color color = null;
		double startAngle = -1.0;
		double endAngle = -1.0;
		double cursorAngle = -1.0;

		String tmp = null;

		tmp = attributes.getValue("", Constants.ATTR_OUTERRADIUS); //$NON-NLS-1$
		try {
			outerRadius = Integer.parseInt(tmp);
		} catch (NumberFormatException e) {
			throw new SpiromatIOException("error parsing outer radius of FigureSpec"); //$NON-NLS-1$
		}
		tmp = attributes.getValue("", Constants.ATTR_INNERRADIUS); //$NON-NLS-1$
		try {
			innerRadius = Integer.parseInt(tmp);
		} catch (NumberFormatException e) {
			throw new SpiromatIOException("error parsing inner radius of FigureSpec"); //$NON-NLS-1$
		}
		tmp = attributes.getValue("", Constants.ATTR_PENHOLEPOS); //$NON-NLS-1$
		try {
			penHolePos = Double.parseDouble(tmp);
		} catch (NumberFormatException e) {
			throw new SpiromatIOException("error parsing pen hole position of FigureSpec"); //$NON-NLS-1$
		}
		tmp = attributes.getValue("", Constants.ATTR_COLOR); //$NON-NLS-1$
		try {
			color = Color.decode("0x" + tmp.substring(1)); //$NON-NLS-1$
		} catch (NumberFormatException e) {
			throw new SpiromatIOException("error parsing pen hole position of FigureSpec"); //$NON-NLS-1$
		}
		tmp = attributes.getValue("", Constants.ATTR_STARTANGLE); //$NON-NLS-1$
		try {
			startAngle = Double.parseDouble(tmp);
		} catch (NumberFormatException e) {
			throw new SpiromatIOException("error parsing pen hole position of FigureSpec"); //$NON-NLS-1$
		}
		tmp = attributes.getValue("", Constants.ATTR_ENDANGLE); //$NON-NLS-1$
		try {
			endAngle = Double.parseDouble(tmp);
		} catch (NumberFormatException e) {
			throw new SpiromatIOException("error parsing pen hole position of FigureSpec"); //$NON-NLS-1$
		}
		tmp = attributes.getValue("", Constants.ATTR_CURSORANGLE); //$NON-NLS-1$
		try {
			cursorAngle = Double.parseDouble(tmp);
		} catch (NumberFormatException e) {
			throw new SpiromatIOException("error parsing pen hole position of FigureSpec"); //$NON-NLS-1$
		}

		currentFigureSpec = new FigureSpec(outerRadius, innerRadius, penHolePos, color);
		currentFigureSpec.setAngles(startAngle, endAngle, cursorAngle);
		docModel.addFigureSpec(currentFigureSpec);
	}

	/**
	 * @param attributes
	 */
	private void coreSetFigureSpecStatus(Attributes attributes) {
		String tmp;
		tmp = attributes.getValue("", Constants.ATTR_VISIBLE); //$NON-NLS-1$
		if (tmp!=null) {
			boolean b = Boolean.parseBoolean(tmp);
			// currentFigureSpec.set
			logger.warn("attribute visible is not supported"); //$NON-NLS-1$
		}
		// nothing more...
	}


	/**
	 * @param attributes
	 */
	private void coreSetDocStatus(Attributes attributes) {
		String key = attributes.getValue("", Constants.ATTR_KEY); //$NON-NLS-1$
		String value = attributes.getValue("", Constants.ATTR_VALUE); //$NON-NLS-1$
		logger.info("docStatus handling: key=" + key + " value=" + value); //$NON-NLS-1$ //$NON-NLS-2$
		if (key.equals(Constants.ATTR_KEY_ACTIVEFIGUREINDEX)) {
			int v = 0;
			try {
				v = Integer.parseInt(value);
			} catch (NumberFormatException e) {
				throw new SpiromatIOException("could not parse value for activeFigureIndex"); //$NON-NLS-1$
			}
			logger.info("found activeFigureIndex with value=" + v); //$NON-NLS-1$
			docModel.setActiveFigureIndex(v);
			hasActiveFigureIndex = true;
		} else if (key.equals(Constants.ATTR_KEY_DEFAULTCOLORINDEX)) {
			int v = 0;
			try {
				v = Integer.parseInt(value);
			} catch (NumberFormatException e) {
				throw new SpiromatIOException("could not parse value for defaultColorIndex"); //$NON-NLS-1$
			}
			logger.info("found defaultColorIndex with value=" + v); //$NON-NLS-1$
			docModel.setDefaultColorIndex(v);
		}
	}

	/**
	 * @param attributes
	 */
	private void coreSetAppStatus(Attributes attributes) {
		// nothing so far
	}

	/**
	 * @param name
	 */
	private void appendToPath(String name) {
    	currentPath.push(name);
		currentPathString.append('/');
		currentPathString.append(name);
		logger.info("path is now: " + currentPathString); //$NON-NLS-1$
	}

	/**
	 * @param name
	 */
	private void removeFromPath(String name) {
		String tos = currentPath.pop();
		if (!tos.equals(name)) {
			throw new SpiromatIOException("parser error"); //$NON-NLS-1$
		}
		currentPathString.delete(0, currentPathString.length());
		Iterator<String> it = currentPath.descendingIterator();
		while (it.hasNext()) {
			currentPathString.append('/');
			currentPathString.append(it.next());
		}
		logger.info("path is now: " + currentPathString); //$NON-NLS-1$
	}

	/**
	 * @param attributes
	 * @throws SpiromatIOException 
	 */
	private void coreCheckVersion(Attributes attributes) throws SpiromatIOException {
		String docVer = attributes.getValue(Constants.ATTR_DOCVER);
		if (docVer==null) {
			throw new SpiromatVersionException("no document version found"); //$NON-NLS-1$
		}
		String appMinVer = attributes.getValue(Constants.ATTR_APPMINVER);
		if (appMinVer==null) {
			throw new SpiromatVersionException("no application version found"); //$NON-NLS-1$
		}
		// FIXME: improve version comparison to allow passing of 1.0.5==1.0.?
		if (docVer.equals("1.0")) { //$NON-NLS-1$
			versionOk = true;
		} else {
			throw new SpiromatVersionException("document version not supported"); //$NON-NLS-1$
		}
	}
}
