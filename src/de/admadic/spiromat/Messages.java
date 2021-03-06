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
package de.admadic.spiromat;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author Rainer Schwarze
 *
 */
public class Messages {
	private static final String BUNDLE_NAME = "de.admadic.spiromat.messages"; //$NON-NLS-1$

	private static String localeLanguage = "en"; //$NON-NLS-1$
	
	private static ResourceBundle RESOURCE_BUNDLE = null;

	private Messages() {
		/* nothing */
	}

	/**
	 * @param localeLanguage
	 */
	public static void setLocaleLanguage(String localeLanguage) {
		Messages.localeLanguage = localeLanguage;
	}
	
	/**
	 * @param key
	 * @return	Returns the String for the key.
	 */
	public static String getString(String key) {
		if (RESOURCE_BUNDLE==null) {
			RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, new Locale(localeLanguage));
		}
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
