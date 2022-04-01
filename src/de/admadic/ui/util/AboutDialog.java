/**
 *
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
 */
package de.admadic.ui.util;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;


/**
 * @author Rainer Schwarze
 *
 */
public class AboutDialog extends de.admadic.ui.util.Dialog 
		implements ActionListener {

	/** */
	private static final long serialVersionUID = 1L;
	final static boolean LOG = true;
	Logger logger = (LOG) ? Logger.getLogger("de.admadic") : null;

	class JPanelForLogo extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		Image logo = null;
		/**
		 * respath can be for instance "de/admadic/calculator/ui/res/"
		 * @param respath
		 * @param imgname
		 */
		public void initBgImage(String respath, String imgname) {
			MediaTracker mt = new MediaTracker(this);
			if (respath==null) {
				respath = "de/admadic/calculator/ui/res/";
			}
			java.net.URL url = this.getClass().getClassLoader().getResource(
					respath + imgname);
			logo = Toolkit.getDefaultToolkit().getImage(url);
			mt.addImage(logo, 0);
			try {
				mt.waitForAll();
			} catch (InterruptedException e) {
				// e.printStackTrace();
			}
		}

		/**
		 * @param logo
		 */
		public void setLogo(Image logo) {
			this.logo = logo;
		}

		@Override
		protected void paintComponent(Graphics arg0) {
			if (logo==null) {
				super.paintComponent(arg0);
			} else {
				arg0.drawImage(
						logo, 
						(getWidth() - logo.getWidth(null))/2, 
						(getHeight() - logo.getHeight(null))/2, 
						null, null);
//				arg0.drawImage(logo, 
//						0, 0, getWidth(), getHeight(),
//						0, 0, logo.getWidth(null), logo.getHeight(null),
//						null);
			}
		}
	}

	JTextArea textAppInfo;
	JTextArea textLicInfo;
	JTextArea textCopyright;
	JTextArea textInfo;
	JTextArea textSoftLibs;
	JPanelForLogo panelLogo;
	JPanel panelButtons;
	JButton btnClose;
	Vector<JButton> btnExtras;

	
	/**
	 * @param frame
	 */
	public AboutDialog(JFrame frame) {
		super(frame);
		initGUI();
		setLogo(frame.getIconImage());
	}

	void setAppInfo(String txt) {
		textAppInfo.setText(txt);
	}

	void setCopyright(String txt) {
		textCopyright.setText(txt);
	}

	void setInfo(String txt) {
		textInfo.setText(txt);
	}

	void setLic(String txt) {
		textLicInfo.setText(txt);
	}

	void setSoftLibs(String txt) {
		textSoftLibs.setText(txt);
	}

	/**
	 * @param iconName
	 */
	public void setLogo(String iconName) {
		if (iconName!=null) {
			panelLogo.initBgImage(null, iconName);
		}
	}
	
	/**
	 * @param icon
	 */
	public void setLogo(Image icon) {
		if (icon!=null) {
			panelLogo.setLogo(icon);
			//panelLogo.setImage(new ImageIcon(icon));
		}
	}

	/**
	 * @param app
	 * @param copy
	 * @param inf
	 */
	public void setAboutInfo(String app, String copy, String inf) {
		setAboutInfo(app, copy, inf, null, null);
	}

	/**
	 * @param app
	 * @param copy
	 * @param inf
	 * @param lic
	 */
	public void setAboutInfo(String app, String copy, String inf, String lic) {
		setAboutInfo(app, copy, inf, lic, null);
	}

	/**
	 * @param app
	 * @param copy
	 * @param inf
	 * @param lic
	 * @param softLibs 
	 */
	public void setAboutInfo(String app, String copy, String inf, String lic, String softLibs) {
		textAppInfo.setText(app);
		textCopyright.setText(copy);
		textInfo.setText(inf);
		if (lic!=null) textLicInfo.setText(lic);
		if (softLibs!=null) {
			JComponent sep;
			sep = DefaultComponentFactory.getInstance().createSeparator(
				"Software Libraries:");
			this.getContentPane().add(
					sep,
					new CellConstraints("2, 10, 3, 1, default, default"));
			{
				// FIXME: heavily depending on structure for the forms label.
				// probably to be done by UI props.
				try {
					JPanel pan = (JPanel)sep;
					for (int i=0; i<pan.getComponentCount(); i++) {
						pan.getComponent(i).setForeground(java.awt.Color.GRAY);
					}
				} catch (Exception e) { /* nothing */ }
			}
			setSoftLibs(softLibs);
		}
		this.invalidate();
		this.pack();
	}

	private void initGUI() {
		try {
			/*
			 * +-------------------------------+
			 * +-------------------------------+
			 * |                               |
			 * | [logo]  [app info]            |
			 * |                               |
			 * |         [license info]        |
			 * |                               |
			 * |         [copyright]           |
			 * |                               |
			 * |         [text info]           |
			 * + - - - - - - - - - - - - - - - +
			 * |         [software libs]       |
			 * + - - - - - - - - - - - - - - - +
			 * |    [close] [...]              |
			 * +-------------------------------+
			 */
			this.setTitle("About");
			this.setModal(true);

			FormLayout thisLayout = new FormLayout(
					"5px, p, 5px, p, 5px", 
					"5px, p, 5px, p, 5px, p, 5px, p, 5px, p, 5px, p, 5px, p, 5px");
			this.getContentPane().setLayout(thisLayout);
			panelLogo = new JPanelForLogo();
			this.getContentPane().add(
				panelLogo,
				new CellConstraints("2, 2, 1, 3, default, default"));
			panelLogo.setPreferredSize(new java.awt.Dimension(64, 64));
			textAppInfo = new JTextArea();
			this.getContentPane().add(
				textAppInfo,
				new CellConstraints("4, 2, 1, 1, default, default"));
			textAppInfo.setText("Application Info");
			textAppInfo.setEditable(false);
			textAppInfo.setOpaque(false);
			textLicInfo = new JTextArea();
			this.getContentPane().add(
				textLicInfo,
				new CellConstraints("4, 6, 1, 1, default, default"));
			textLicInfo.setText("Registration Information");
			textLicInfo.setEditable(false);
			textLicInfo.setOpaque(false);
			textCopyright = new JTextArea();
			this.getContentPane().add(
				textCopyright,
				new CellConstraints("4, 4, 1, 1, default, default"));
			textCopyright.setText("Copyright");
			textCopyright.setEditable(false);
			textCopyright.setOpaque(false);
			textInfo = new JTextArea();
			this.getContentPane().add(
				textInfo,
				new CellConstraints("4, 8, 1, 1, default, default"));
			textInfo.setText("Further Information");
			textInfo.setEditable(false);
			textInfo.setOpaque(false);

			textSoftLibs = new JTextArea();
			this.getContentPane().add(
				textSoftLibs,
				new CellConstraints("4, 12, 1, 1, default, default"));
			textSoftLibs.setText("");
			textSoftLibs.setEditable(false);
			textSoftLibs.setOpaque(false);
			textSoftLibs.setForeground(java.awt.Color.GRAY);

			panelButtons = new JPanel();
			this.getContentPane().add(
					panelButtons,
					new CellConstraints("2, 14, 3, 1, default, default"));

			btnClose = new JButton();
			panelButtons.add(btnClose);
			btnClose.setText("Close");
			btnClose.setActionCommand("close");
			btnClose.addActionListener(this);
			//setSize(400, 300);
			this.pack();
			this.setLocationRelativeTo(this.getParent()); // center
		} catch (Exception e) {
			// e.printStackTrace();
			if (logger!=null) logger.severe(
					"Error creating about dialog: " + e.getMessage());
		}
	}

	/**
	 * @param btn
	 */
	public void addExtraButton(JButton btn) {
		if (btnExtras==null) {
			btnExtras = new Vector<JButton>();
		}
		btnExtras.add(btn);
		panelButtons.add(btn);
	}

	/**
	 * @param e
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("close")) {
			setVisible(false);
		}
	}
}
