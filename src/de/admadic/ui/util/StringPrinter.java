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
package de.admadic.ui.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;


/**
 * @author Rainer Schwarze
 *
 */
public class StringPrinter {
	final static boolean DBG = false;
	String text;
	JFrame parent;
	JDialog parent2;
	int fontSize;
	Font font;

	/**
	 * @param parent a JFrame specifying the parent for potential visual feedback.
	 * @param text	A String giving the text to print.
	 */
	public StringPrinter(JFrame parent, String text) {
		this.text = text;
		this.parent = parent;
	}

	/**
	 * @param parent
	 * @param text
	 */
	public StringPrinter(JDialog parent, String text) {
		this.text = text;
		this.parent2 = parent;
	}

	/**
	 * @param fontSize
	 */
	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	/**
	 * @param font
	 */
	public void setFont(Font font) {
		this.font = font;
	}

	/**
	 * @param inThread 
	 */
	synchronized public void doPrint(boolean inThread) {
		if (DBG) System.out.println("Printing...");
		BusyDialog dlg;
		Font printFont;

		printFont = (font!=null) ? font : new Font(
				"Monospaced", Font.PLAIN, 
				(fontSize>0) ? fontSize : 12);

		// collect data
		// FIXME: fixup busy dialog handling. The dialog should stay modal 
		if (inThread) {
			dlg = new BusyDialog(
					parent, 
					"Printing", 
					"Printing in background. Please wait...", 
					false);
			dlg.setVisible(true);
			// do the actual printing in another thread
			new Thread(new PrintingThread(text, printFont)).start();
			dlg.setVisible(false);
		} else {
			dlg = new BusyDialog(
					parent, 
					"Printing", 
					"Printing... Please wait...", 
					false);
			dlg.setVisible(true);
			new PrintingThread(text, printFont).run();
			dlg.setVisible(false);
		}
	}

	/**
	 * @author Rainer Schwarze
	 */
	public static class BusyDialog extends JDialog {
		JTextArea text;

		/**
		 * 
		 */
		 private static final long serialVersionUID = 1L;

		/**
		 * @param owner
		 * @param title
		 * @param msg 
		 * @param modal
		 * @throws HeadlessException
		 */
		public BusyDialog(Frame owner, String title, String msg, boolean modal) throws HeadlessException {
			super(owner, title, modal && false); // always non-modal for now
			this.setResizable(false);
			this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			JPanel panel = new JPanel();
			getContentPane().setLayout(new BorderLayout());
			getContentPane().add(panel, BorderLayout.CENTER);
			panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

			panel.setLayout(new BorderLayout());
			text = new JTextArea();
			text.setEditable(false);
			text.setOpaque(false);
			panel.add(text, BorderLayout.CENTER);
			text.setText(msg);
			pack();
			this.setLocationRelativeTo(owner);
		}
	}

	/**
	 * @author Rainer Schwarze
	 *
	 */
	public static class PrintingThread implements Runnable {
		String text;
		Font font;
		
		/**
		 * @param text
		 * @param font 
		 */
		public PrintingThread(String text, Font font) {
			super();
			this.text = text;
			this.font = font;
			if (DBG) System.out.println("PrintingThread: <init>");
		}
		
		/**
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			if (DBG) System.out.println("PrintingThread: run");
			PrinterJob job = PrinterJob.getPrinterJob ();
			Book book = new Book ();
			// cover page could be appended to book here
			PrintTestPagePainter fapp = new PrintTestPagePainter(text, font);
			if (DBG) System.out.println("PrintingThread: calling pageDialog");
			PageFormat opf = job.defaultPage();
			PageFormat pf = job.pageDialog(opf);
			if (opf==pf) {
				if (DBG) System.out.println("PrintingThread: pageDialog cancelled");
				return;
			}
			int count = fapp.calculatePageCount(pf);
			if (DBG) System.out.println("PrintingThread: found " + count + " pages.");
			book.append(fapp, pf, count);
			job.setPageable(book);
			if (DBG) System.out.println("PrintingThread: calling printDialog");
			if (job.printDialog()) {
				try {
					if (DBG) System.out.println("PrintingThread: calling job.print");
					job.print();
				}
				catch (PrinterException e) {
					// e.printStackTrace();
					JOptionPane.showMessageDialog(
							null, 
							"An error occured during printing:\n"+
							e.getMessage(),
							"Printing Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	/**
	 * @author Rainer Schwarze
	 *
	 */
	public static class PrintTestPagePainter implements Printable {
		String text;
		ArrayList<ArrayList<String>> pages;
		PageFormat curPageFormat;
		Font printFont;
		
		/**
		 * @param text
		 * @param font 
		 */
		public PrintTestPagePainter(String text, Font font) {
			super();
			this.text = fixEmptyLFs(text);
			this.printFont = font;
			if (DBG) System.out.println("PrintTestPagePainter: <init>");
		}

		String fixEmptyLFs(String s) {
			String out = s;

			if (out.startsWith("\n")) {
				out = " " + out;
			}

			while (out.indexOf("\n\n")>=0) {
				out = out.replaceAll("\n\n", "\n \n");
			}
			return out;
		}

		/**
		 * @param pf
		 * @return	Returns the number of pages
		 */
		public int calculatePageCount(PageFormat pf) {
			if (DBG) System.out.println("PrintTestPagePainter: calculating pagecount");
			// calculate pagecount based on pf and articles
			ArrayList<ArrayList<String>> pgs = repaginate(pf);
			return pgs.size();
		}

		/**
		 * @param g
		 * @param pf
		 * @param idx
		 * @return	Returns whether the page exists or not.
		 * @throws PrinterException
		 * @see java.awt.print.Printable#print(java.awt.Graphics, java.awt.print.PageFormat, int)
		 */
		public int print(
				Graphics g, 
				PageFormat pf, 
				int idx) 
		throws PrinterException {
			// Printable's method implementation
			if (DBG) System.out.println("PrintTestPagePainter: print");
			if (curPageFormat != pf) {
				curPageFormat = pf;
				pages = repaginate(pf);
			}
			if (idx >= pages.size ()) {
				if (DBG) System.out.println("PrintTestPagePainter: no such page: " + idx);
				return Printable.NO_SUCH_PAGE;
			}
			g.setFont(printFont);
			g.setColor(Color.black);
			renderPage(g, pf, idx);
			return Printable.PAGE_EXISTS;
		}

		int countChar(String s, char c) {
			int cnt = 0;
			for (int i=0; i<s.length(); i++) {
				if (s.charAt(i)==c) {
					cnt++;
				}
			}
			return cnt;
		}

		int[] createIndexArray(String s, char c, int [] ary) {
			if (ary==null) {
				int len = countChar(s, c);
				if (len==0)
					return null;
				ary = new int[len];
			}
			int lfi = 0;
			// find each linefeed and store the index into the
			// message in the linefeedarray
			for (int i=0; i<s.length(); i++) {
				if (s.charAt(i)==c) {
					ary[lfi] = i;
					lfi++;
					if (lfi>=ary.length) 
						break;
				}
			}
			return ary;
		}

		ArrayList<ArrayList<String>> repaginate(PageFormat pf) {
			ArrayList<ArrayList<String>> pgs = 
					new ArrayList<ArrayList<String>>();
			ArrayList<String> pg = null;

			// step through articles, creating pages of lines
			double maxh = pf.getImageableHeight();
			double curh;
			int lineh = printFont.getSize();
			FontRenderContext frc = new FontRenderContext(null, true, true);
			//FontRenderContext frc = g.getFontRenderContext();
			//FontMetrics fontmetrics = null;
			AttributedString atext = new AttributedString(text);
			atext.addAttribute(TextAttribute.FONT, printFont);
			LineBreakMeasurer measurer = 
				new LineBreakMeasurer(atext.getIterator(), frc);
			TextLayout tl;
			float wrapwidth = (float)pf.getImageableWidth();

			int [] linefeedidx;
			int lfi;
			int maxidx;

			linefeedidx = createIndexArray(text, '\n', null);

			curh = 0.0;
			lfi = 0;
			maxidx = 0;
			int lastpos, curpos;
			pg = new ArrayList<String>();
			if (DBG) System.out.println("PrintTestPagePainter: page 1 added");
			pgs.add(pg);
			// the AttributedString does not provide a length() mechanism,
			// so we use the original text for that:
//			try {
				while (measurer.getPosition() < text.length()) {
					lastpos = measurer.getPosition();
					if (linefeedidx==null) {
						tl = measurer.nextLayout(wrapwidth);
					} else {
						if (lfi<linefeedidx.length) {
							maxidx = linefeedidx[lfi];
						} else {
							maxidx = text.length();
						}
						tl = measurer.nextLayout(wrapwidth, maxidx, false);
						if (measurer.getPosition()>=maxidx) {
							lfi++;
						}
					}
					if (tl==null) { /* no warn */ }
					curpos = measurer.getPosition();
					
					if (curpos==lastpos) {
						// break it
						// break; // out of while loop
					}

					curh += lineh;
					if (curh>maxh) {
						// new page:
						curh = 0.0;
						pg = new ArrayList<String>();
						pgs.add(pg);
						if (DBG) System.out.println("PrintTestPagePainter: next page added");
					}

					pg.add(text.substring(lastpos, curpos));
	
//					//curh += tl.getAscent() + tl.getDescent() + tl.getLeading();
//					curh += lineh;
//					if (curh>maxh) {
//						// new page:
//						curh = 0.0;
//						pg = new ArrayList<String>();
//						pgs.add(pg);
//						if (DBG) System.out.println("PrintTestPagePainter: next page added");
//					}
				}
//			} catch (IllegalArgumentException e) {
//				e.printStackTrace();
//			}

			return pgs;
		}

		void renderPage (Graphics g, PageFormat pf, int idx) {
			if (DBG) System.out.println("PrintTestPagePainter: rendering page " + idx);
			// render the lines from the pages list
			int xo = (int) pf.getImageableX();
			int yo = (int) pf.getImageableY();
			int y = printFont.getSize();
			ArrayList<String> page = pages.get(idx);
			Iterator<String> it = page.iterator();
			while (it.hasNext()) {
				String line = it.next();
				g.drawString(line, xo, y + yo);
				y += printFont.getSize();
			}
		}
	}
}
