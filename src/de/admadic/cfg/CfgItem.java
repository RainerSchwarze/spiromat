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

import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.Method;


/**
 * @author Rainer Schwarze
 *
 */
public class CfgItem implements Cloneable {
	String ciKey;
	String ciValue;
	/**
	 * @author Rainer Schwarze
	 *
	 */
	public enum Type {
		/**
		 * 
		 */
		STRING,
		/**
		 * 
		 */
		BOOLEAN,
		/**
		 * 
		 */
		INT,
		/**
		 * 
		 */
		RECTANGLE,
		/**
		 * 
		 */
		POINT,
		/**
		 * 
		 */
		OBJECT,
		/**
		 * 
		 */
		ARRAY_OF_OBJECT
	}
	/**
	 * 
	 */
	public static final int FLG_NONE = 0;
	/**
	 * 
	 */
	public static final int FLG_NORESET = 1 << 0; 
	CfgItem.Type ciType;
	String ciObjClassName;
	int ciFlags;

	/**
	 * @param key
	 * @param value
	 * @return	Returns the created CfgItem
	 */
	public static CfgItem create(String key, Object value) {
		CfgItem ci;
		ci = new CfgItem(key);
		try {
			ci.putObjectValue(value, true);
		} catch (CfgException e) {
			//e.printStackTrace();
			return null;
		}
		return ci;
	}

	/**
	 * @param keyCode
	 * @param valueCode
	 * @return	Returns the created CfgItem
	 */
	public static CfgItem createFromEncoded(String keyCode, String valueCode) {
		CfgItem ci;
		ci = new CfgItem();
		try {
			ci.decodeKey(keyCode);
			ci.decodeValue(valueCode);
		} catch (Exception e) {
			//e.printStackTrace();
			return null;
		}
		return ci;
	}

	/**
	 * 
	 */
	public CfgItem() {
		super();
		this.ciKey = null;
		this.ciFlags = FLG_NONE;
		try {
			putObjectValue(null, true);
		} catch (CfgException e) {
			// cannot happen
		}
	}
	/**
	 * @param key
	 */
	public CfgItem(String key) {
		super();
		this.ciKey = key;
		this.ciFlags = FLG_NONE;
		try {
			putObjectValue(null, true);
		} catch (CfgException e) {
			// cannot happen
		}
	}
	/**
	 * @param key
	 * @param value
	 * @param flags
	 * @throws CfgException 
	 */
	public CfgItem(String key, Object value, int flags) throws CfgException {
		super();
		this.ciKey = key;
		this.ciFlags = flags;
		putObjectValue(value, true);
	}
	/**
	 * @param key
	 * @param value
	 * @throws CfgException 
	 */
	public CfgItem(String key, Object value) throws CfgException {
		this(key, value, FLG_NONE);
	}

	/**
	 * @param value The value to set.
	 */
	protected void setCiValue(String value) {
		this.ciValue = value;
	}
	
	/**
	 * @return Returns the ciValue.
	 */
	public String getCiValue() {
		return ciValue;
	}

	/**
	 * @param obj
	 */
	public void setTypeOfObject(Object obj) {
		Type t;
		t = CfgItem.getTypeOfObject(obj);
		if (t==Type.OBJECT) {
			ciObjClassName = obj.getClass().getName();
		} else {
			ciObjClassName = null;
		}
		ciType = t;
	}

	/**
	 * 
	 * @param obj
	 * @return	Returns the type id of the object
	 */
	protected static Type getTypeOfObject(Object obj) {
		Type t;
		if (obj instanceof String) {
			t = Type.STRING;
		} else if (obj instanceof Integer) {
			t = Type.INT;
		} else if (obj instanceof Boolean) {
			t = Type.BOOLEAN;
		} else if (obj instanceof Rectangle) {
			t = Type.RECTANGLE;
		} else if (obj instanceof Point) {
			t = Type.POINT;
		} else {
			t = Type.OBJECT;
		}
		return t;
	}

	/**
	 * @return	Returns the cloned object
	 * @throws CloneNotSupportedException
	 * @see java.lang.Object#clone()
	 */
	@Override
	public CfgItem clone() throws CloneNotSupportedException {
		CfgItem dst = (CfgItem)super.clone();
		dst.ciKey = this.ciKey;
		dst.ciValue = this.ciValue;
		dst.ciType = this.ciType;
		dst.ciFlags = this.ciFlags;
		dst.ciObjClassName = this.ciObjClassName;
		return dst;
	}

	/**
	 * @return	Returns the encoded string representation of the key
	 */
	public String encodeKey() {
		String v;
		v = ciKey;
		return v;
	}
	
	/**
	 * @param v
	 */
	public void decodeKey(String v) {
		ciKey = v;
	}

	/**
	 * @return	Returns the encoded value
	 */
	public String encodeValue() {
		return ciValue;
	}
	
	/**
	 * @param v
	 */
	public void decodeValue(String v) {
		ciValue = v;
	}

	/**
	 * @return	Returns the string encoding of the meta information
	 * @throws CfgException
	 */
	public String encodeMeta() throws CfgException {
		String v;
		v = "[" + type2String() + "," + flags2String() + "]";
		return v;
	}

	/**
	 * @param v
	 * @throws CfgException
	 */
	public void decodeMeta(String v) throws CfgException {
		// "[t,f]"
		int b1, b2;
		b1 = v.indexOf('[');
		b2 = v.indexOf(']');
		if ((b1<0) || (b2<0)) {
			throw new CfgException(
					"Invalid encoded representation: " + v + 
					" (missing brackets)");
		}
		if (b1>=b2) {
			throw new CfgException(
					"Invalid encoded representation: " + v + 
					" (invalid bracket placement)");
		}
		String tmps = v.substring(b1+1, b2);
		String [] sa = tmps.split(",");
		if (sa.length!=2) {
			throw new CfgException(
					"Invalid encoded representation: " + v + 
					" (specification format invalid)");
		}
		string2Type(sa[0]);
		string2Flags(sa[1]);
	}
	
	protected String type2String() throws CfgException {
		String v;
		switch (ciType) {
		case STRING: 	v = "s"; break;
		case BOOLEAN: 	v = "b"; break;
		case INT: 		v = "i"; break;
		case RECTANGLE: v = "r"; break;
		case POINT:		v = "p"; break; 
		case OBJECT:	
			v = "o";
			v += ":";
			v += ciObjClassName;
			break;
		case ARRAY_OF_OBJECT: v = "a"; break;
		default:
			// cannot happen!
			throw new CfgException("Type not supported: " + ciType);
		}
		return v;
	}

	protected void string2Type(String v) throws CfgException {
		Type t;
		if 		(v.equals("s")) t = Type.STRING;
		else if (v.equals("b")) t = Type.BOOLEAN;
		else if (v.equals("i")) t = Type.INT;
		else if (v.equals("r")) t = Type.RECTANGLE;
		else if (v.equals("p")) t = Type.POINT;
		else if (v.startsWith("o")) t = Type.OBJECT;
		else if (v.equals("a")) t = Type.ARRAY_OF_OBJECT;
		else {
			throw new CfgException("Type code not supported: " + v);
		}
		ciType = t;
		if (t==Type.OBJECT) {
			int p1 = v.indexOf(':');
			if (p1>=0) {
				// ok we have it
				ciObjClassName = v.substring(p1+1);
			} else {
				// keep it unknown -> set to that in setType
				ciObjClassName = null;
			}
		}
	}

	protected String flags2String() {
		String v;
		v = "";
		v += ((ciFlags & FLG_NORESET)!=0) ? "r" : "";
		if (v.equals("")) {
			v = "n";
		}
		return v;
	}

	protected void string2Flags(String v) {
		ciFlags = FLG_NONE;
		if (v.indexOf("r")>=0) {
			ciFlags |= FLG_NORESET;
		}
	}
	/**
	 * @return Returns the flags.
	 */
	public int getCiFlags() {
		return ciFlags;
	}

	/**
	 * @param ciFlags The ciFlags to set.
	 */
	public void setCiFlags(int ciFlags) {
		this.ciFlags = ciFlags;
	}

	/**
	 * @param ciFlags The ciFlags to set.
	 * @param set 
	 */
	public void setCiFlags(int ciFlags, boolean set) {
		if (set) {
			this.ciFlags |= ciFlags;
		} else {
			this.ciFlags &= ~ciFlags;
		}
	}

	/**
	 * @return Returns the key.
	 */
	public String getCiKey() {
		return ciKey;
	}
	/**
	 * @return Returns the type.
	 */
	public CfgItem.Type getCiType() {
		return ciType;
	}

	/**
	 * @param value
	 * @throws CfgException
	 */
	public void putObjectValue(Object value) throws CfgException {
		putObjectValue(value, false);
	}
	
	/**
	 * putValue shall be reserved for the value type Object.
	 * 
	 * @param value
	 * @param changeType 
	 * @throws CfgException 
	 */
	public void putObjectValue(Object value, boolean changeType) throws CfgException {
		String s;
		Type t;
		if (value==null) {
			this.ciValue = "";
			this.ciType = null;
			return;
		}
		t = CfgItem.getTypeOfObject(value);
		if (getCiType()!=t) {
			if (changeType) {
				setTypeOfObject(value);
			} else {
				throw new CfgException("Type mismatch");
			}
		}
		switch (getCiType()) {
		case RECTANGLE:
			Rectangle r = (Rectangle)value;
			s = r.x + "," + r.y + "," + r.width + "," + r.height;
			break;
		case POINT:
			Point p = (Point)value;
			s = p.x + "," + p.y;
			break;
		default:
			s = value.toString();
			break;
		}
		this.ciValue = s;
	}

	/**
	 * getValue shall be reserved for the value type Object.
	 * Other classes derived from Object shall be accessed by a different name!
	 * (See getPointValue)
	 * 
	 * @param defValue
	 * @return	the value from the config space, the given default value if the
	 * 			config space does not contain the key
	 */
	public Object getObjectValue(Object defValue) {
		// type:
		// [i] = int
		// [b] = boolean
		// [s] = string
		// [r] = rectangle
		// [p] = point
		// [o:name] = object
		// w/ [x] -> string
		Object v;
		String s;
		s = ciValue;
		if (s==null) {
			return defValue;
		}

		switch (ciType) {
		case STRING:
			v = new String(ciValue);
			break;
		case INT:
			v = Integer.valueOf(s);
			break;
		case BOOLEAN:
			v = Boolean.valueOf(s);
			break;
		case RECTANGLE:
			Rectangle r = new Rectangle();
			String [] sary; 
			try {
				sary = s.split(",");
				if (sary.length!=4) 
					return defValue;
				r.x = Integer.parseInt(sary[0]);
				r.y = Integer.parseInt(sary[1]);
				r.width = Integer.parseInt(sary[2]);
				r.height = Integer.parseInt(sary[3]);
			} catch (Exception e) {
				// FIXME: create log entry in that case
				// e.printStackTrace();
				return defValue;
			} finally {
				// nothing to do
			}
			v = r;
			break;
		case POINT:
			Point p = new Point();
			String [] sarypt; 
			try {
				sarypt = s.split(",");
				if (sarypt.length!=2) 
					return defValue;
				p.x = Integer.parseInt(sarypt[0]);
				p.y = Integer.parseInt(sarypt[1]);
			} catch (Exception e) {
				// FIXME: create log entry in that case
				// e.printStackTrace();
				return defValue;
			} finally {
				// nothing to do
			}
			v = p;
			break;
		case OBJECT:
			String on, ov;
			on = ciObjClassName;
			ov = ciValue;
			v = instantiateObject(on, ov);
			break;
		default:
			v = new String(s);
			break;
		}

		return v;
	}
	

	/**
	 * Instantiates an object from its class name and a String value 
	 * which is passed to a valueOf method of that class. If the valueOf 
	 * method does not exist, the instantiation fails.
	 * If the class is not found or the valueOf method does not exist for 
	 * it, null is returned.
	 *  
	 * @param className	a String giving the name of the class to instantiate
	 * @param value		a String which is passed to the valueOf method of that
	 * 					class
	 * @return			an Object instantiated
	 */
	protected Object instantiateObject(String className, String value) {
		Object v = null;
		Class cls;
		Method method;
		try {
			cls = Class.forName(className);
			method = cls.getDeclaredMethod("valueOf", new Class[]{String.class});
			v = method.invoke(null, new Object[]{value});
		} catch (Exception e) {
			// don't care, simply return null
			return null;
		}
		// detailed exceptions of the reflection handling:
//		} catch (ClassNotFoundException e) {
//		} catch (SecurityException e) {
//		} catch (NoSuchMethodException e) {
//		} catch (IllegalArgumentException e) {
//		} catch (IllegalAccessException e) {
//		} catch (InvocationTargetException e) {
//		}
		return v;
	}

	/**
	 * @param defValue
	 * @return	Returns the value
	 */
	public boolean getBooleanValue(boolean defValue) {
		Object o = getObjectValue(null);
		if (!(o instanceof Boolean)) {
			return defValue;
		}
		Boolean b = (Boolean)o;
		return b.booleanValue();
	}

	/**
	 * @param defValue
	 * @return	Returns the value
	 */
	public String getStringValue(String defValue) {
		Object o = getObjectValue(null);
		if (!(o instanceof String)) {
			return defValue;
		}
		String s = (String)o;
		return s;
	}

	/**
	 * @param defValue
	 * @return	Returns the value
	 */
	public int getIntValue(int defValue) {
		Object o = getObjectValue(null);
		if (!(o instanceof Integer)) {
			return defValue;
		}
		Integer i = (Integer)o;
		return i.intValue();
	}

	/**
	 * @param defValue
	 * @return	Returns the value
	 */
	public Rectangle getRectangleValue(Rectangle defValue) {
		Object o = getObjectValue(null);
		if (!(o instanceof Rectangle)) {
			return defValue;
		}
		Rectangle r = (Rectangle)o;
		return r;
	}

	/**
	 * @param defValue
	 * @return	Returns the value
	 */
	public Point getPointValue(Point defValue) {
		Object o = getObjectValue(null);
		if (!(o instanceof Point)) {
			return defValue;
		}
		Point p = (Point)o;
		return p;
	}
}
