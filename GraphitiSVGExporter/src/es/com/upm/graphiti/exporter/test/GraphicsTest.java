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
import java.awt.FileDialog;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.Polygon;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.algorithms.styles.Font;
import org.eclipse.graphiti.mm.algorithms.styles.GradientColoredArea;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.algorithms.styles.Style;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.ChopboxAnchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;

import es.com.upm.flow.adaptor.provider.LocalImageProvider;
import es.com.upm.graphiti.exporter.xmi.GraphitiDiagramLoader;


/**
 * Test Class to export Graphiti to SVG via Pictograms Package.
 * @author jpsalazar
 *
 */
public class GraphicsTest extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1784543360154786442L;
	/**
	 * Map to the Linear Gradient Paint styles of the Diagram.
	 */
	HashMap<String, LinearGradientPaint> styles;
	/**
	 * List of Colors.
	 */
	List<org.eclipse.graphiti.mm.algorithms.styles.Color> colors;
	/**
	 * List of fonts.
	 */
	List<Font> fonts;
	
	String file ;
	/**
	 * Extended Constructor from Graphics2D
	 * @param graphics 
	 */
	public GraphicsTest() {
		super();
	}
	/**
	 * Graphiti Model Extension.
	 */
	private static final String _EXTENSION = ".pictograms"; //$NON-NLS-1$
	/**
	 * Analyze the elements included in Diagram in order to paint them into Graphics.
	 * @param example - Diagram to be painted.
	 * @param g 
	 */
	public void analyze(Diagram example, Graphics2D g2d) {
		styles = createStyle(example.getStyles());
		colors = example.getColors();
		fonts = example.getFonts();
		example.getDiagramTypeId();
		analyzeElement((ContainerShape) example, g2d);
		analyzeConnector(example, g2d);
	}
	/**
	 * Analyze List of Shape contained in Container Shape.
	 * @param cs - Container Shape to be analyzed.
	 * @param g2d 
	 * @return
	 */
	public boolean analyzeElement(ContainerShape cs, Graphics2D g2d) {
		paint(cs, g2d);
		List<Shape> shapes = cs.getChildren();
		Iterator<Shape> it = shapes.iterator();
		while (it.hasNext()) {
			Shape s = it.next();
			setFont(fonts.get(0), g2d);
			if (s instanceof ContainerShape) {
				analyzeElement((ContainerShape) s, g2d);
			} else if (s instanceof Shape) {
				paint(s, g2d);
			}
		}
		
		return false;
	}
	/**
	 * Analyze List of Connection contained in Diagram.
	 * @param d - Diagram to be analyzed.
	 * @param g2d 
	 */
	private void analyzeConnector(Diagram d, Graphics2D g2d) {
		List<Connection> connections = d.getConnections();
		Iterator<Connection> itc = connections.iterator();
		while (itc.hasNext()) {
			Connection c = itc.next();
			paint(c, g2d);
		}		
	}
	/**
	 * Paint Connection in the SVGGraphics2D.
	 * @param c - Connection to be painted.
	 * @param g2d 
	 */
	private void paint(Connection c, Graphics2D g2d) {
		GraphicsAlgorithm ga = c.getGraphicsAlgorithm();
		int x = 0;
		int y = 0;
		double theta = 0;
		if (c instanceof FreeFormConnection) {
			List<Point> bendP = ((FreeFormConnection) c).getBendpoints();
//			Codigo alternativo al uso de los servicios.
//			if (origin instanceof FixPointAnchor) {
//				Point p = ((FixPointAnchor) origin).getLocation();
//				x = ga.getX() + p.getX();
//				y = ga.getY() + p.getY();
//			}
//			EObject element = origin.eContainer();
//			do {
//				x += ((PictogramElement) element).getGraphicsAlgorithm().getX();
//				y += ((PictogramElement) element).getGraphicsAlgorithm().getY();
//				element = element.eContainer();
//			} while (element != null);
			Anchor origin = ((FreeFormConnection) c).getStart();
			ILocation il = Graphiti.getPeLayoutService().getLocationRelativeToDiagram(origin);
			x = il.getX();
			y = il.getY();
			boolean notMidPoint = true;
			if (origin instanceof FixPointAnchor) {
				notMidPoint = true;
			} else if (origin instanceof BoxRelativeAnchor) {
			} else if (origin instanceof ChopboxAnchor) {
				notMidPoint = false;
			}
			Point firstPoint = getLocation(bendP.get(0),
					il, origin.getGraphicsAlgorithm().getWidth(), origin.getGraphicsAlgorithm().getHeight(), notMidPoint);
			((Polyline) ga).getPoints().add(firstPoint);
			
			// Cuidado, se deberían COPIAR LOS PUNTOS
			for (int i = 0; i < bendP.size(); i++) {
				((Polyline) ga).getPoints().add(Graphiti.getGaService().createPoint(bendP.get(i).getX(), bendP.get(i).getY()));
			}
			x = 0;
			y = 0;
			Anchor target = ((FreeFormConnection) c).getEnd();
			il = Graphiti.getPeLayoutService().getLocationRelativeToDiagram(target);
			x = il.getX();
			y = il.getY();
//			if (target instanceof FixPointAnchor) {
//				Point p = ((FixPointAnchor) target).getLocation();
//				x = ga.getX() + p.getX();
//				y = ga.getY() + p.getY();
//			} else if (target instanceof ChopboxAnchor) {
//			}
//			element = target.eContainer();
//			do {
//				x += ((PictogramElement) element).getGraphicsAlgorithm().getX();
//				y += ((PictogramElement) element).getGraphicsAlgorithm().getY();
//				element = element.eContainer();
//			} while (element != null);
			// Cuidado, se deberían COPIAR LOS PUNTOS
			if (target instanceof FixPointAnchor) {
				notMidPoint = true;
			} else if (target instanceof BoxRelativeAnchor) {
			} else if (target instanceof ChopboxAnchor) {
				notMidPoint = false;
			}
			Point lastPoint = getLocation(bendP.get(bendP.size() - 1),
					il, Graphiti.getPeLayoutService().getGaBoundsForAnchor(target).getWidth(),
					Graphiti.getPeLayoutService().getGaBoundsForAnchor(target).getHeight(), notMidPoint);
			((Polyline) ga).getPoints().add(lastPoint);
			x = lastPoint.getX();
			y = lastPoint.getY();
			theta += Math.atan2(y - bendP.get(bendP.size() - 1).getY(), x - bendP.get(bendP.size() - 1).getX());
		}
		paintGraphicsAlgorithm(ga, g2d);
		List<ConnectionDecorator> cDeco = c.getConnectionDecorators();
		Iterator<ConnectionDecorator> itcd = cDeco.iterator();
		while (itcd.hasNext()) {
			ConnectionDecorator cd = itcd.next();
			g2d.translate(x + cd.getLocation(), y);
			g2d.rotate(theta);
			paintGraphicsAlgorithm(cd.getGraphicsAlgorithm(), g2d);
			g2d.rotate(-theta);
			g2d.translate(-(x + cd.getLocation()), -y);
		}
	}
	/**
	 * Gets a Rectangle from {@link #getBox()} and returns the Point where a
	 * line from the center of the Rectangle to the Point <i>reference</i>
	 * intersects the Rectangle.
	 * 
	 * @param reference
	 *            The reference point
	 * @param midLoc 
	 * @return The anchor location
	 */
	public Point getLocation(Point reference, ILocation loc_rect, int width, int height, boolean notMidLoc) {
		float centerX = loc_rect.getX() - 1;
		float centerY = loc_rect.getY() - 1;
		if (notMidLoc) {
			centerX = loc_rect.getX() + 0.5f * width;
			centerY = loc_rect.getY() + 0.5f * height;
		}

		if (reference.getX() == (int) centerX && reference.getY() == (int) centerY)
			return Graphiti.getCreateService().createPoint((int) centerX, (int) centerY); // This avoids
															// divide-by-zero
		float dx = reference.getX() - centerX;
		float dy = reference.getY() - centerY;

		// r.width, r.height, dx, and dy are guaranteed to be non-zero.
		float scale = 0.5f / Math.max(Math.abs(dx) / width, Math.abs(dy)
				/ height);

		dx *= scale;
		dy *= scale;
		centerX += dx;
		centerY += dy;

		return Graphiti.getCreateService().createPoint(Math.round(centerX), Math.round(centerY));
	}
	public void paint(Graphics g) {
		if (file != null) {
			analyze(GraphitiDiagramLoader.create(file), (Graphics2D) g);
	  	} else {
	  		System.exit(0);
	  	}
	}
	/**
	 * Paint Shape into SVGGraphics2D
	 * @param s - Shape to be painted.
	 * @param g2d 
	 */
	private void paint(Shape s, Graphics2D g2d) {
		GraphicsAlgorithm ga = s.getGraphicsAlgorithm();
		List<Anchor> anchors = s.getAnchors();
		Iterator<Anchor> itg = anchors.iterator();
		int x = ga.getX();
		int y = ga.getY();
		if (s instanceof ContainerShape) {
			paintGraphicsAlgorithm(ga, g2d);
		} else {
			ILocation il = Graphiti.getPeLayoutService().getLocationRelativeToDiagram(s.getContainer());
			x = il.getX();
			y = il.getY();
//			x = s.getContainer().getGraphicsAlgorithm().getX();
//			y = s.getContainer().getGraphicsAlgorithm().getY();
			g2d.translate(x, y);
			paintGraphicsAlgorithm(ga, g2d);
			g2d.translate(-x, -y);
		}
		while (itg.hasNext()) {
			Anchor anc = itg.next();
			if (anc instanceof FixPointAnchor) {
				Point p = ((FixPointAnchor) anc).getLocation();
				x = ga.getX() + p.getX();
				y = ga.getY() + p.getY();
			}
			g2d.translate(x, y);
			paintGraphicsAlgorithm(anc.getGraphicsAlgorithm(), g2d);
			g2d.translate(-x, -y);
		}
	}
	/**
	 * Paint GraphicsAlgorithm. Set the common attributes to all the AWT elements.
	 * @param ga - GraphicsAlgorithm to be painted.
	 * @return boolean to check.
	 */
	public boolean paintGraphicsAlgorithm(GraphicsAlgorithm ga, Graphics2D g2d) {
		if (ga == null) {
			return true;
		}
		if (ga.getBackground() != null) {
			g2d.setBackground(getColor(ga.getBackground()));
		}
		if (ga.getForeground() != null) {
			g2d.setPaint(getColor(ga.getForeground()));
		}
//		Composite composite = getComposite();
//		if (composite instanceof AlphaComposite) {
//			AlphaComposite newComposite = AlphaComposite.getInstance(((AlphaComposite) composite).getRule(), rect.getTransparency().floatValue());
//			setComposite(newComposite);
//		}
		if (ga.getLineVisible()) {
//			BasicStroke stroke = new BasicStroke(rect.getLineWidth());
			g2d.setStroke(createStroke(ga));
		} else {
			g2d.setStroke(new BasicStroke(0));
		}
		if (ga.getStyle() != null) {
			g2d.setPaint(styles.get(ga.getStyle().getId()));
//			setStroke(new BasicStroke(ga.getStyle().getLineWidth()));
		}
		if (ga instanceof Rectangle) {
			paint((Rectangle) ga, g2d);
		} else if (ga instanceof RoundedRectangle) {
			paint((RoundedRectangle) ga, g2d);
		} else if (ga instanceof Text) {
			paint((Text) ga, g2d);
		} else if (ga instanceof Polygon) {
			paint((Polygon) ga, g2d);
		} else if (ga instanceof Polyline) {
			paint((Polyline) ga, g2d);
		} else if (ga instanceof Image) {
			paint((Image)ga, g2d);
		}
		EList<GraphicsAlgorithm> gAlgo = ga.getGraphicsAlgorithmChildren();
		Iterator<GraphicsAlgorithm> itg = gAlgo.iterator();
		while (itg.hasNext()) {
			GraphicsAlgorithm g = itg.next();
			g2d.translate(ga.getX(), ga.getY());
//			setClip(ga.getX(), ga.getY(), ga.getWidth(), ga.getHeight());
			paintGraphicsAlgorithm(g, g2d);
			g2d.translate(-ga.getX(), -ga.getY());
		}
		return false;
	}
	private void paint(Rectangle rect, Graphics2D g2d) {
		java.awt.Rectangle rectangle = new java.awt.Rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
		if (rect.getFilled()) {
			g2d.fill(rectangle);
		}
		if (rect.getStyle() != null) {
			g2d.setPaint(getColor(rect.getStyle().getForeground()));
		} else if (rect.getForeground() != null) {
			g2d.setPaint(getColor(rect.getForeground()));
		}
		if (rect.getLineVisible()) {
			g2d.draw(rectangle);
		}
	}	
	private void paint(RoundedRectangle rrect, Graphics2D g2d) {
		RoundRectangle2D.Float rrectangle = new RoundRectangle2D.Float(
				rrect.getX(), rrect.getY(), rrect.getWidth(), rrect.getHeight(), rrect.getCornerWidth(), rrect.getCornerHeight());
		if (rrect.getBackground() != null) {
			g2d.setBackground(getColor(rrect.getBackground()));
		}
		if (rrect.getFilled()) {
			g2d.fill(rrectangle);
		}
		if (rrect.getStyle().getForeground() != null) {
			g2d.setPaint(getColor(rrect.getStyle().getForeground()));
		} else if (rrect.getForeground() != null) {
			g2d.setPaint(getColor(rrect.getForeground()));
		}
		if (rrect.getLineVisible()) {
			g2d.draw(rrectangle);
		}

	}
	private void paint(Text text, Graphics2D g2d) {
		//Clip, Transform, Paint, Font and Composite
		setFont(text.getFont(), g2d);
		FontMetrics fm   = g2d.getFontMetrics();
		java.awt.geom.Rectangle2D rect = fm.getStringBounds(text.getValue(), g2d);

		int textHeight = (int)(rect.getHeight()); 
		int textWidth  = (int)(rect.getWidth());

		// Center text horizontally and vertically
		int x = text.getX();
		switch (text.getHorizontalAlignment().getValue()) {
		case Orientation.ALIGNMENT_TOP_VALUE:
			x = (text.getWidth() - textWidth)  / 2 + text.getX();	
			break;
		default:
			break;
		}
		int y = text.getY();
		switch (text.getVerticalAlignment().getValue()) {
		case Orientation.ALIGNMENT_TOP_VALUE:
			y = (text.getHeight() - textHeight) / 2  + fm.getAscent() + text.getY();
			break;

		default:
			break;
		}
		
		g2d.drawString(text.getValue(), x, y);  // Draw the string.
	}	
	private void paint(Polygon pol, Graphics2D g2d) {
		EList<Point> points = pol.getPoints();
		java.awt.Polygon p = new java.awt.Polygon();
		for (int i = 0; i < points.size(); i++) {
			p.addPoint(points.get(i).getX(), points.get(i).getY());
		}
		if (pol.getFilled())  {
			g2d.fillPolygon(p);
		}
		if (pol.getStyle() != null) {
			g2d.setPaint(getColor(pol.getStyle().getForeground()));
		} else if (pol.getForeground() != null) {
			g2d.setPaint(getColor(pol.getForeground()));
		}
		if (pol.getLineVisible()) {
			g2d.draw(p);
		}
	}
	private void paint(Polyline pol, Graphics2D g2d) {

		EList<Point> points = pol.getPoints();
		GeneralPath path = new GeneralPath();
		path.moveTo(points.get(0).getX(), points.get(0).getY());
		Point lineTo = Graphiti.getCreateService().createPoint(0, 0);
		Point quadTo = Graphiti.getCreateService().createPoint(0, 0);;
		determineBezierPoints(points.get(0), points.get(1), lineTo, quadTo);
		
		for (int i = 1; i < points.size() - 1; i++) {
			path.lineTo(lineTo.getX(), lineTo.getY());
			determineBezierPoints(points.get(i), points.get(i + 1), lineTo, quadTo);
			path.quadTo(points.get(i).getX(), points.get(i).getY(), quadTo.getX(), quadTo.getY());
//			path.quadTo(points.get(i).getX(), points.get(i).getY(), 
//					points.get(i + 1).getX(), points.get(i + 1).getY() );
		}
		path.lineTo(points.get(points.size() - 1).getX(), points.get(points.size() - 1).getY());
		g2d.draw(path);
	}
	/**
	 * Method to calculate BEZIER Points obtained from 
	 *  org.eclipse.graphiti.ui.internal.figures.GFFigureUtil
	 *  and adapted:
	 *  	- avoiding org.eclipse.draw2d.geometry.Vector
	 *  	- Bezier Distance = 15 [used to calculate
	 *            the rounding of the bezier-curve]
	 * @param c
	 *            The current control-point.
	 * @param q
	 *            The point following the current control-point.
	 * @param r
	 *            The bezier-point, which is calculated in this method. This is
	 *            a "return-value".
	 * @param s
	 *            The bezier-point, which is calculated in this method. This is
	 *            a "return-value".
	 */
	private static void determineBezierPoints(Point c, Point q, Point r, Point s) {
		// Determine v and m
		// Ray v = new Ray();
		int vx = q.getX() - c.getX();
		int vy = q.getY() - c.getY();
		double absV = Math.sqrt(vx * vx + vy * vy);
		// Ray m = new Ray();
		int mx = Math.round(c.getX() + vx / 2);
		int my = Math.round(c.getY() + vy / 2);

		// Determine tolerance
		// Idea:
		// The vector v is the line after the current control-point c.
		// If the sum of the bezier-distances is greater than the half
		// line-length of v,
		// then a simplified calculation for the bezier-points r and s must be
		// done.
		int tolerance = 30;

		// Determine the "results" r and s
		if (absV < tolerance) {
			// use the the midpoint m for r and s
			r.setX(mx);
			r.setY(my);
			s.setX(mx);
			s.setY(my);
		} else {
			double dx = (absV - 15) / absV;
			r.setX(Math.round(c.getX() + (float) dx * vx));
			r.setY(Math.round(c.getY() + (float) dx * vy));
			double dy = 15 / absV;
			s.setX(Math.round(c.getX() + (float) dy * vx));
			s.setY(Math.round(c.getY() + (float) dy * vy));
		}
	}
	private void paint(Image im, Graphics g2d) {
		java.awt.Image image = getImage(im.getId());
		if (image != null) {
			g2d.drawImage(image, im.getX(), im.getY(), im.getWidth(), im.getHeight(), null);
		}
	}
	private java.awt.Image getImage(String id) {
		if (id != null) {
			LocalImageProvider fip = new LocalImageProvider();
			String imPath = fip.getImageFilePath(id);
			BufferedImage img = null;
			try {
				img = ImageIO.read(new File(imPath));
			} catch (IOException e) {};
			return img;
		}
		return null;
	}
	private java.awt.Font setFont(Font font, Graphics2D g2d) {
		int style = java.awt.Font.PLAIN;
		if (font.isBold()) {
			if (font.isItalic()) {
			style = java.awt.Font.BOLD | java.awt.Font.ITALIC;
			} else {
				style = java.awt.Font.BOLD;
			}
		} else if (font.isItalic()) {
			style = java.awt.Font.ITALIC;
		}
		
//		@SuppressWarnings("restriction")
//		org.eclipse.swt.graphics.Font f = DataTypeTransformation.toSwtFont(font);
//		java.awt.Font awtFont = new java.awt.Font(f.getFontData()[0].getName(), f.getFontData()[0].getStyle(), f.getFontData()[0].getHeight());
		int fontSizeScreen = Math.round(Toolkit.getDefaultToolkit().getScreenResolution() * font.getSize() / 72f);
		java.awt.Font awtFont = new java.awt.Font(font.getName(), style, fontSizeScreen);
		g2d.setFont(awtFont);
		return awtFont;
	}
	/**
	 * Copiada de GraphicsToGraphics2DAdaptor porque es privado.
	 * @param ga 
	 */
	private Stroke createStroke(GraphicsAlgorithm ga) {
		int lineWidth = Graphiti.getGaService().getLineWidth(ga, true);
		
		// line style
		LineStyle lineStyle = Graphiti.getGaService().getLineStyle(ga, true);
		
		float factor = lineWidth > 0 ? lineWidth : 3;
		float awt_dash[];
		int awt_cap;
		int awt_join;

		if (lineStyle == LineStyle.DASH) {
			awt_dash = new float[] { factor * 6, factor * 3 };
		} else if (lineStyle == LineStyle.DASHDOT) {
			awt_dash = new float[] { factor * 3, factor, factor, factor };
		} else if (lineStyle == LineStyle.DASHDOTDOT) {
			awt_dash = new float[] { factor * 3, factor, factor, factor, factor, factor };
		} else if (lineStyle == LineStyle.DOT) {
			awt_dash = new float[] { factor, factor };
		} else {
			awt_dash = null;
		}
//		switch (currentState.lineAttributes.cap) {
//		case SWT.CAP_FLAT:
//			awt_cap = BasicStroke.CAP_BUTT;
//			break;
//		case SWT.CAP_ROUND:
//			awt_cap = BasicStroke.CAP_ROUND;
//			break;
//		case SWT.CAP_SQUARE:
//			awt_cap = BasicStroke.CAP_SQUARE;
//			break;
//		default:
			awt_cap = BasicStroke.CAP_BUTT;
//		}

//		switch (currentState.lineAttributes.join) {
//		case SWT.JOIN_BEVEL:
//			awt_join = BasicStroke.JOIN_BEVEL;
//			break;
//		case SWT.JOIN_MITER:
//			awt_join = BasicStroke.JOIN_MITER;
//			break;
//		case SWT.JOIN_ROUND:
//			awt_join = BasicStroke.JOIN_ROUND;
//		default:
			awt_join = BasicStroke.JOIN_MITER;
//		}

		/*
		 * SWT paints line width == 0 as if it is == 1, so AWT is synced up with
		 * that below.
		 */
		Stroke stroke = new BasicStroke(lineWidth != 0 ? lineWidth : 1, awt_cap, awt_join,
				(float) 1.0, awt_dash, 0);
		return stroke;
	}
	private HashMap<String, LinearGradientPaint> createStyle(EList<Style> styles) {
		Iterator<Style> it = styles.iterator();
		HashMap<String, LinearGradientPaint> lgp = new HashMap<String, LinearGradientPaint>(styles.size());
		while (it.hasNext()) {
			Style st = it.next();
			GradientColoredArea gc = st.getRenderingStyle().getAdaptedGradientColoredAreas().getAdaptedGradientColoredAreas().get(0).getGradientColor().get(0);
			Point2D start = new Point2D.Float(0, 0);
			Point2D end = new Point2D.Float(0,100);
			//float[] dist = {gc.getStart().getLocationValue().floatValue(),(float) (1.0 - gc.getEnd().getLocationValue().floatValue())};
			float[] dist = {(float) (0.0),(float) (1.0)};
			java.awt.Color[] colors = {getColor(gc.getStart().getColor()), getColor(gc.getEnd().getColor())};
			LinearGradientPaint p = new LinearGradientPaint(start, end, dist, colors);
			lgp.put(st.getId(), p);
		}
		return lgp;
	}
	protected java.awt.Color getColor(Color toConvert) {

		return new java.awt.Color(toConvert.getRed(), toConvert.getGreen(), toConvert.getBlue());
	}
	public String getFile() {
		FileDialog dialog = new FileDialog(new Frame(), "My File Selection");
		dialog.setFilenameFilter(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (name.endsWith(_EXTENSION)) {
					return true;
				}
				return false;
			}
		});
		dialog.setAlwaysOnTop(true);
		dialog.setVisible(true);
		return dialog.getFile();
  }

  public static void main(String[] args) throws IOException {

	// Create an instance of the SVG Generator.
	//TestSVGGen svgGenerator = new TestSVGGen(document);
    //Gestión de archivo a Mapear
      GraphicsTest svgGenerator = new GraphicsTest();
      svgGenerator.setSize(1000, 1000);
      svgGenerator.file = svgGenerator.getFile();
      svgGenerator.pack();
      svgGenerator.setVisible(true);
  }
}