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

/**
 * @author Rainer Schwarze
 *
 */
public interface CfgPersistenceItemized {
	/**
	 * Prepare the persistence operation
	 * @param path
	 */
	abstract public void prepareLoad(String path);

	/**
	 * Prepare the persistence operation
	 * @param path
	 */
	abstract public void prepareStore(String path);

	/**
	 * @return	Returns the keys available in this persistence instance
	 */
	abstract public String[] getKeys();

	/**
	 * @param key
	 * @return	Returns the CfgItem
	 */
	abstract public CfgItem loadCfgItem(String key);

	/**
	 * @param ci
	 */
	abstract public void storeCfgItem(CfgItem ci);

	/**
	 * Finish the load operation and release resources 
	 */
	abstract public void finalizeLoad();

	/**
	 * Finish the store operation and release resources
	 */
	abstract public void finalizeStore();
}
