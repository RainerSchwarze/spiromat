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

/**
 * @author Rainer Schwarze
 *
 */
public interface CfgPersistenceGrouped {
	/**
	 * @param cp
	 */
	abstract public void registerCfgProvider(CfgProvider cp);

	// FIXME: check whether store should deal with handling of removed keys...
	// (so that the user of this interface is not required to call
	// removeKeys manually)

	/**
	 * @param path
	 */
	abstract public void load(String path);
	/**
	 * @param path
	 */
	abstract public void store(String path);

	/**
	 * @param path
	 * @param keys
	 */
	abstract public void removeKeys(String path, Enumeration<String> keys);

	/**
	 * Clears all settings at the given path.
	 * @param path
	 * @return Returns true for success, false for error
	 */
	abstract public boolean clear(String path);
}
