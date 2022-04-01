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
package de.admadic.util;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Rainer Schwarze
 *
 */
public class DateUtil {

	/**
	 * @param ya
	 * @param ma
	 * @param da
	 * @param yb
	 * @param mb
	 * @param db
	 * @return	Returns the difference between the given dates in days.
	 */
	public static int calculateDifference(
			int ya, int ma, int da, int yb, int mb, int db) {
		Date a = new Date(ya-1900, ma-1, da);
		Date b = new Date(yb-1900, mb-1, db);
		return calculateDifference(a, b);
	}

	

	/**
	 * @param fromDate
	 * @param toDate
	 * @return	Returns the difference between the given dates in days.
	 */
	public static int calculateDifference(Date fromDate, Date toDate)
	{
		int tempDifference = 0;
		int difference = 0;
		Calendar earlier = Calendar.getInstance();
		Calendar later = Calendar.getInstance();

		earlier.setTime(fromDate);
		later.setTime(toDate);

//		if (fromDate.compareTo(toDate) < 0) {
//			earlier.setTime(fromDate);
//			later.setTime(toDate);
//		} else {
//			earlier.setTime(toDate);
//			later.setTime(fromDate);
//		}

		while (earlier.get(Calendar.YEAR) != later.get(Calendar.YEAR)) {
			tempDifference = 365 * (later.get(Calendar.YEAR) - earlier
					.get(Calendar.YEAR));
			difference += tempDifference;

			earlier.add(Calendar.DAY_OF_YEAR, tempDifference);
		}

		if (earlier.get(Calendar.DAY_OF_YEAR) != later
				.get(Calendar.DAY_OF_YEAR)) {
			tempDifference = later.get(Calendar.DAY_OF_YEAR)
					- earlier.get(Calendar.DAY_OF_YEAR);
			difference += tempDifference;

			earlier.add(Calendar.DAY_OF_YEAR, tempDifference);
		}

		return difference;
	}
}
