/*******************************************************************************
 * Copyright (c) 2013 COM Center for Open Middleware
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     COM Center For Open Middleware - initial API and implementation
 *     Juan Pablo Salazar <jpsalazar@dit.upm.es>
 *******************************************************************************/
package es.com.upm.graphiti.exporter.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class DashedStrokeDemo extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 209900746971506910L;

	public void init() {
		setBackground(Color.white);
	}
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		float dash[] = { 10.0f };
		g2.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
		g2.setPaint(Color.blue);

		Rectangle r = new Rectangle(100,100,250,250);
		
		g2.draw(r);
	}
	
	@SuppressWarnings("deprecation")
	public static void main(String s[]) {
		JFrame f = new JFrame();
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		DashedStrokeDemo p = new DashedStrokeDemo();
		f.getContentPane().add("Center", p);
		p.init();
		f.pack();
		f.setSize(new Dimension(500, 500));
		f.show();
	}
}