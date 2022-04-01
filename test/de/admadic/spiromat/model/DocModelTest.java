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
package de.admadic.spiromat.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.SerializationUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.admadic.spiromat.util.BitmapExport;
import de.admadic.spiromat.util.FileExport;

/**
 * @author Rainer Schwarze
 *
 */
public class DocModelTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		/* nothing */
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		/* nothing */
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		/* nothing */
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		/* nothing */
	}

	/**
	 * Test method for graphics export.
	 */
	@Test
	public void testGraphicsExport() {
		DocModel docModel = new DocModel();
		docModel.setDefaults();
		docModel.getActiveFigureSpec().initFullInterval();
		docModel.addFigureSpec(ModelUtil.createStandardFigureSpec(docModel));
		docModel.addFigureSpec(ModelUtil.createStandardFigureSpec(docModel));
		docModel.getFigureSpec(1).setOuterRadius(75);
		docModel.getFigureSpec(2).setPenHolePos(0.5);
		docModel.getFigureSpec(1).initFullInterval();
		docModel.getFigureSpec(2).initFullInterval();

		byte [] data = SerializationUtils.serialize(docModel);
		DocModel previewSource = (DocModel) SerializationUtils.deserialize(data);

		BitmapExport be = new BitmapExport(previewSource);
		BufferedImage bi = be.export();

		try {
			FileExport.export(bi, new File("tmp-output.png"), FileExport.Format.AUTO);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
