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
import java.io.IOException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;

import de.admadic.spiromat.model.DocModel;
import de.admadic.spiromat.model.FigureSpec;

/**
 * @author Rainer Schwarze
 *
 */
public class DocumentWriter implements XMLReader {
	DocModel docModel;

	ContentHandler handler;
   // We're not doing namespaces, and we have no
    // attributes on our elements. 
    String nsu = "";  // NamespaceURI //$NON-NLS-1$
    Attributes atts = new AttributesImpl();
    String rootElement = Constants.NODE_SPIROMAT;
  
    StringBuffer indentString = new StringBuffer("\n"); // for readability! //$NON-NLS-1$


	/**
	 * @param docModel
	 */
	public DocumentWriter(DocModel docModel) {
		super();
		this.docModel = docModel;
	}

	/**
	 * 
	 */
	public void write() {
		try {
			File file = docModel.getFile();
	    	TransformerFactory tFactory = TransformerFactory.newInstance();
	        Transformer transformer;
			transformer = tFactory.newTransformer();

	        // Use the parser as a SAX source for input
	        SAXSource source = new SAXSource(this, null);
	        StreamResult result = new StreamResult(file);
	        transformer.transform(source, result);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new SpiromatIOException("could not create output file", e); //$NON-NLS-1$
		}
	}

	/**
	 * @param input
	 * @throws IOException
	 * @throws SAXException
	 * @see org.xml.sax.XMLReader#parse(org.xml.sax.InputSource)
	 */
	public void parse(InputSource input) throws IOException, SAXException {
        try {

            if (handler==null) {
              throw new SAXException("No content handler"); //$NON-NLS-1$
            }
            // Note: 
            // We're ignoring setDocumentLocator(), as well
            handler.startDocument();
            indent(0);
            handler.startElement(nsu, rootElement, rootElement, atts);      
 
            writeVersion();
            writeFigureSpecs();
            writeDocStatus();
            writeAppStatus();

            indent(0);
          handler.endElement(nsu, rootElement, rootElement);
          handler.endDocument();      
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new SpiromatIOException("could not generate file", e); //$NON-NLS-1$
        }
	}

	private String encodeColor(Color color) {
		int code = color.getRGB() & 0x0ffffff;
		return "#" + Integer.toHexString(code); //$NON-NLS-1$
	}

	private void indent(int level) throws SAXException {
		// level = 0 -> top, no indent
		checkIndentString(level);
	    handler.ignorableWhitespace(
	    		indentString.toString().toCharArray(), 
                0,
                level*2 + 1);
	}
	
	/**
	 * @param level
	 */
	private void checkIndentString(int level) {
		if (indentString.length()<(level*2 + 1)) {
			int tmp = level*2 - indentString.length() + 1;
			for (int i=0; i<tmp; i++) {
				indentString.append(' ');
			}
		}
	}

	/**
	 * @throws SAXException 
	 * 
	 */
	private void writeAppStatus() throws SAXException {
		indent(1);
	    AttributesImpl attrs = new AttributesImpl();
	    handler.startElement(nsu, "", Constants.NODE_APPSTATUS, attrs); //$NON-NLS-1$
		indent(2);
	    AttributesImpl fsattrs = new AttributesImpl();
	    fsattrs.addAttribute(nsu, "", Constants.ATTR_KEY, "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	    fsattrs.addAttribute(nsu, "", Constants.ATTR_VALUE, "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	    handler.startElement(nsu, "", Constants.NODE_PARAM, fsattrs); //$NON-NLS-1$
		indent(2);
	    handler.endElement(nsu, "", Constants.NODE_PARAM); //$NON-NLS-1$
		indent(1);
	    handler.endElement(nsu, "", Constants.NODE_APPSTATUS); //$NON-NLS-1$
	}

	/**
	 * @throws SAXException 
	 * 
	 */
	private void writeDocStatus() throws SAXException {
		indent(1);
	    AttributesImpl attrs = new AttributesImpl();
	    handler.startElement(nsu, "", Constants.NODE_DOCSTATUS, attrs); //$NON-NLS-1$
		indent(2);
	    AttributesImpl fsattrs = null;

	    fsattrs = new AttributesImpl();
	    fsattrs.addAttribute(nsu, "", Constants.ATTR_KEY, "type", Constants.ATTR_KEY_ACTIVEFIGUREINDEX); //$NON-NLS-1$ //$NON-NLS-2$
	    fsattrs.addAttribute(nsu, "", Constants.ATTR_VALUE, "type", ""+docModel.getActiveFigureIndex()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	    handler.startElement(nsu, "", Constants.NODE_PARAM, fsattrs); //$NON-NLS-1$
		indent(2);
	    handler.endElement(nsu, "", Constants.NODE_PARAM); //$NON-NLS-1$

	    fsattrs = new AttributesImpl();
	    fsattrs.addAttribute(nsu, "", Constants.ATTR_KEY, "type", Constants.ATTR_KEY_DEFAULTCOLORINDEX); //$NON-NLS-1$ //$NON-NLS-2$
	    fsattrs.addAttribute(nsu, "", Constants.ATTR_VALUE, "type", ""+docModel.getDefaultColorIndex()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	    handler.startElement(nsu, "", Constants.NODE_PARAM, fsattrs); //$NON-NLS-1$
		indent(2);
	    handler.endElement(nsu, "", Constants.NODE_PARAM); //$NON-NLS-1$

	    indent(1);
	    handler.endElement(nsu, "", Constants.NODE_DOCSTATUS); //$NON-NLS-1$
	}

	/**
	 * @throws SAXException 
	 * 
	 */
	private void writeFigureSpecs() throws SAXException {
		indent(1);
	    AttributesImpl attrs = new AttributesImpl();
	    AttributesImpl emptyAttrs = new AttributesImpl();
	    handler.startElement(nsu, "", Constants.NODE_FIGURESPECS, attrs); //$NON-NLS-1$
	    int count = docModel.getFigureSpecCount();
	    for (int i=0; i<count; i++) {
			indent(2);
	    	FigureSpec fs = docModel.getFigureSpec(i);
		    AttributesImpl fsattrs = new AttributesImpl();
		    fsattrs.addAttribute(nsu, "", Constants.ATTR_OUTERRADIUS, "type", ""+fs.getOuterRadius()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		    fsattrs.addAttribute(nsu, "", Constants.ATTR_INNERRADIUS, "type", ""+fs.getInnerRadius()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		    fsattrs.addAttribute(nsu, "", Constants.ATTR_PENHOLEPOS, "type", ""+fs.getPenHolePos()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		    fsattrs.addAttribute(nsu, "", Constants.ATTR_COLOR, "type", ""+encodeColor(fs.getColor())); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		    fsattrs.addAttribute(nsu, "", Constants.ATTR_STARTANGLE, "type", ""+fs.getStartAngle()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		    fsattrs.addAttribute(nsu, "", Constants.ATTR_ENDANGLE, "type", ""+fs.getEndAngle()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		    fsattrs.addAttribute(nsu, "", Constants.ATTR_CURSORANGLE, "type", ""+fs.getCursorAngle()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		    handler.startElement(nsu, "", Constants.NODE_FIGURESPEC, fsattrs); //$NON-NLS-1$
			indent(3);
	    	handler.startElement(nsu, "", Constants.NODE_STATUS, emptyAttrs); //$NON-NLS-1$
			indent(3);
	    	handler.endElement(nsu, "", Constants.NODE_STATUS); //$NON-NLS-1$
			indent(2);
		    handler.endElement(nsu, "", Constants.NODE_FIGURESPEC); //$NON-NLS-1$
	    }
		indent(1);
	    handler.endElement(nsu, "", Constants.NODE_FIGURESPECS); //$NON-NLS-1$
	}

	/**
	 * @throws SAXException 
	 * 
	 */
	private void writeVersion() throws SAXException {
		indent(1);
	    AttributesImpl attrs = new AttributesImpl();
	    attrs.addAttribute(nsu, "", Constants.ATTR_DOCVER, "type", "1.0"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	    attrs.addAttribute(nsu, "", Constants.ATTR_APPMINVER, "type", "0.0"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	    handler.startElement(nsu, "", Constants.NODE_VERSION, attrs); //$NON-NLS-1$
		indent(1);
	    handler.endElement(nsu, "", Constants.NODE_VERSION); //$NON-NLS-1$
	}

	/**
	 * @return	Returns the current content handler.
	 * @see org.xml.sax.XMLReader#getContentHandler()
	 */
	public ContentHandler getContentHandler() {
		return handler;
	}

	/**
	 * @param handler
	 * @see org.xml.sax.XMLReader#setContentHandler(org.xml.sax.ContentHandler)
	 */
	public void setContentHandler(ContentHandler handler) {
		this.handler = handler;
	}


	// /////////////////////////////////////////////////////////
	// ignore the rest
	
	/**
	 * @return	Returns the current DTD handler.
	 * @see org.xml.sax.XMLReader#getDTDHandler()
	 */
	public DTDHandler getDTDHandler() {
		return null;
	}

	/**
	 * @return	Returns the current entity resolver.
	 * @see org.xml.sax.XMLReader#getEntityResolver()
	 */
	public EntityResolver getEntityResolver() {
		return null;
	}

	/**
	 * @return	Returns the current error handler.
	 * @see org.xml.sax.XMLReader#getErrorHandler()
	 */
	public ErrorHandler getErrorHandler() {
		return null;
	}

	/**
	 * @param name
	 * @return	Returns the feature for the given name.
	 * @throws SAXNotRecognizedException
	 * @throws SAXNotSupportedException
	 * @see org.xml.sax.XMLReader#getFeature(java.lang.String)
	 */
	public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
		return false;
	}

	/**
	 * @param name
	 * @return	Returns the property for the given name.
	 * @throws SAXNotRecognizedException
	 * @throws SAXNotSupportedException
	 * @see org.xml.sax.XMLReader#getProperty(java.lang.String)
	 */
	public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
		return null;
	}

	/**
	 * @param systemId
	 * @throws IOException
	 * @throws SAXException
	 * @see org.xml.sax.XMLReader#parse(java.lang.String)
	 */
	public void parse(String systemId) throws IOException, SAXException {
		/* nothing */
	}

	/**
	 * @param handler
	 * @see org.xml.sax.XMLReader#setDTDHandler(org.xml.sax.DTDHandler)
	 */
	public void setDTDHandler(DTDHandler handler) {
		/* nothing */
	}

	/**
	 * @param resolver
	 * @see org.xml.sax.XMLReader#setEntityResolver(org.xml.sax.EntityResolver)
	 */
	public void setEntityResolver(EntityResolver resolver) {
		/* nothing */
	}

	/**
	 * @param handler
	 * @see org.xml.sax.XMLReader#setErrorHandler(org.xml.sax.ErrorHandler)
	 */
	public void setErrorHandler(ErrorHandler handler) {
		/* nothing */
	}

	/**
	 * @param name
	 * @param value
	 * @throws SAXNotRecognizedException
	 * @throws SAXNotSupportedException
	 * @see org.xml.sax.XMLReader#setFeature(java.lang.String, boolean)
	 */
	public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
		/* nothing */
	}

	/**
	 * @param name
	 * @param value
	 * @throws SAXNotRecognizedException
	 * @throws SAXNotSupportedException
	 * @see org.xml.sax.XMLReader#setProperty(java.lang.String, java.lang.Object)
	 */
	public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
		/* nothing */
	}
}
