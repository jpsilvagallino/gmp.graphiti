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
package es.com.upm.graphiti.exporter.svg;

import java.awt.BasicStroke;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.LinearGradientPaint;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.io.File;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import java.net.URL;
import javax.imageio.ImageIO;

import org.apache.batik.svggen.ExtensionHandler;
import org.apache.batik.svggen.ImageHandler;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.PlatformGraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Polygon;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.algorithms.styles.Font;
import org.eclipse.graphiti.mm.algorithms.styles.GradientColoredArea;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.algorithms.styles.LocationType;
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
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.internal.platform.ExtensionManager;
import org.eclipse.graphiti.ui.platform.IImageProvider;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import org.w3c.dom.Document;

import es.com.upm.flow.adaptor.provider.LocalImageProvider;


/**
 * Test Class to export Graphiti to SVG via Pictograms Package.
 * @author jpsalazar
 *
 */
public class DiagramGraphicsAdaptor extends SVGGraphics2D {
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
	/**
	 * 
	 */
	private String diagramTypeId;
	/**
	 * Extended Constructor from SVGGraphics2D
	 * @param domFactory - Factory which will produce Elements for the DOM tree this Graphics2D generates.
	 */
	public DiagramGraphicsAdaptor(Document domFactory) {
		super(domFactory);
	}
	/**
	 * Extended Constructor from SVGGraphics2D
	 * @param document - Factory which will produce Elements for the DOM tree this Graphics2D generates.
	 * @param imageHandler - defines how images are referenced in the generated SVG fragment
	 * @param extensionHandler -defines how Java 2D API extensions map to SVG Nodes.
	 */
	public DiagramGraphicsAdaptor(Document document, ImageHandler imageHandler,
			ExtensionHandler extensionHandler) {
		super(document, imageHandler, extensionHandler, false);
	}
	/**
	 * Extended Constructor from SVGGraphics2D
	 * @param graphics
	 */
	public DiagramGraphicsAdaptor(SVGGraphics2D graphics) {
		super(graphics);
	}
	public DiagramGraphicsAdaptor(SVGGeneratorContext ctx, boolean textAsShapes) {
		super(ctx, textAsShapes);
		// TODO Auto-generated constructor stub
	}
	/**
	 * Analyze the elements included in Diagram in order to paint them into Graphics.
	 * @param example - Diagram to be painted.
	 */
	public void analyze(Diagram example) {
		styles = createStyle(example.getStyles());
		colors = example.getColors();
		fonts = example.getFonts();
		diagramTypeId = example.getDiagramTypeId();
		setDefaults();
		analyzeElement((ContainerShape) example);
		analyzeConnector(example);
	}
	private void setDefaults() {
		setPaint(getColor(colors.get(0)));
		setFont(fonts.get(0));
	}
	/**
	 * Analyze List of Shape contained in Container Shape.
	 * @param cs - Container Shape to be analyzed.
	 * @return
	 */
	public boolean analyzeElement(ContainerShape cs) {
		paint(cs);
		List<Shape> shapes = cs.getChildren();
		Iterator<Shape> it = shapes.iterator();
		while (it.hasNext()) {
			Shape s = it.next();
//			setFont(fonts.get(0));
			if (s instanceof ContainerShape) {
				analyzeElement((ContainerShape) s);
			} else if (s instanceof Shape) {
				paint(s);
			}
		}
		setDefaults();
		return false;
	}
	/**
	 * Analyze List of Connection contained in Diagram.
	 * @param d - Diagram to be analyzed.
	 */
	private void analyzeConnector(Diagram d) {
		List<Connection> connections = d.getConnections();
		Iterator<Connection> itc = connections.iterator();
		while (itc.hasNext()) {
			Connection c = itc.next();
			paint(c);
		}		
	}
	/**
	 * Paint Connection in the SVGGraphics2D.
	 * @param c - Connection to be painted.
	 */
	private void paint(Connection c) {
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
			GraphicsAlgorithm g = origin.getGraphicsAlgorithm();
			if (origin instanceof FixPointAnchor) {
				notMidPoint = true;
			} else if (origin instanceof BoxRelativeAnchor) {
			} else if (origin instanceof ChopboxAnchor) {
				notMidPoint = false;
				g = ((PictogramElement)origin.eContainer()).getGraphicsAlgorithm();
			}
			Point firstPoint = getLocation(bendP.get(0),
					il, g.getWidth(), g.getHeight(), notMidPoint);
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
		paintGraphicsAlgorithm(ga);
		List<ConnectionDecorator> cDeco = c.getConnectionDecorators();
		Iterator<ConnectionDecorator> itcd = cDeco.iterator();
		while (itcd.hasNext()) {
			ConnectionDecorator cd = itcd.next();
			translate(x + cd.getLocation(), y);
			rotate(theta);
			paintGraphicsAlgorithm(cd.getGraphicsAlgorithm());
			rotate(-theta);
			translate(-(x + cd.getLocation()), -y);
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
	/**
	 * Method obtained from FlowGridLayer in com.isb.bks.flow.features.visual
	 * where is painted the pyjama background.
	 * @param diagram - Diagram to be painted
	 */
	
	protected void paintGrid(Diagram diagram) {

		GraphicsAlgorithm graphicsAlgorithm = diagram.getGraphicsAlgorithm();
		java.awt.Rectangle clip = new java.awt.Rectangle(
				graphicsAlgorithm.getWidth(), graphicsAlgorithm.getHeight());

		org.eclipse.graphiti.mm.algorithms.styles.Color background = Graphiti.getGaService().getBackgroundColor(graphicsAlgorithm, true);
		org.eclipse.graphiti.mm.algorithms.styles.Color foreground = Graphiti.getGaService().getForegroundColor(graphicsAlgorithm, true);
		
		int gridX = diagram.getGridUnit();
		//Not used in bks.FLOW
		int gridY = diagram.getVerticalGridUnit();

		setBackground(getColor(background));
		fill(clip);

		if (gridX > 0) {

			int c = 0;
			int i = 0;
			while (i % gridX != 0)
				i++;

			int right = clip.x + clip.width;
			for (; i < right; i += gridX) {
				c++;
				if (c%2 == 0)
					setPaint(getColor(foreground));
				else
					setPaint(getColor(background));
				fillRect(i, clip.y, gridX, clip.height);
			}
		}
	}

	/**
	 * Paint Shape into SVGGraphics2D
	 * @param s - Shape to be painted.
	 */
	private void paint(Shape s) {
		GraphicsAlgorithm ga = s.getGraphicsAlgorithm();
		List<Anchor> anchors = s.getAnchors();
		Iterator<Anchor> itg = anchors.iterator();
		int x = ga.getX();
		int y = ga.getY();
		if (s instanceof Diagram) {
			paintGrid((Diagram) s);
		} else if (s instanceof ContainerShape) {
			paintGraphicsAlgorithm(ga);
		} else {
			ILocation il = Graphiti.getPeLayoutService().getLocationRelativeToDiagram(s.getContainer());
			x = il.getX();
			y = il.getY();
//			x = s.getContainer().getGraphicsAlgorithm().getX();
//			y = s.getContainer().getGraphicsAlgorithm().getY();
			translate(x, y);
			paintGraphicsAlgorithm(ga);
			translate(-x, -y);
		}
		while (itg.hasNext()) {
			Anchor anc = itg.next();
			if (anc instanceof FixPointAnchor) {
				Point p = ((FixPointAnchor) anc).getLocation();
				x = ga.getX() + p.getX();
				y = ga.getY() + p.getY();
			}
			translate(x, y);
			paintGraphicsAlgorithm(anc.getGraphicsAlgorithm());
			translate(-x, -y);
		}
	}
	/**
	 * Paint GraphicsAlgorithm. Set the common attributes to all the AWT elements.
	 * @param ga - GraphicsAlgorithm to be painted.
	 * @return boolean to check.
	 */
	public boolean paintGraphicsAlgorithm(GraphicsAlgorithm ga) {
		if (ga == null) {
			return false;
		}
		if (ga.getBackground() != null) {
			setBackground(getColor(ga.getBackground()));
		}
		if (ga.getForeground() != null) {
			setPaint(getColor(ga.getForeground()));
		}
//		Composite composite = getComposite();
//		if (composite instanceof AlphaComposite) {
//			AlphaComposite newComposite = AlphaComposite.getInstance(((AlphaComposite) composite).getRule(), rect.getTransparency().floatValue());
//			setComposite(newComposite);
//		}
		if (ga.getLineVisible()) {
//			BasicStroke stroke = new BasicStroke(rect.getLineWidth());
			setStroke(createStroke(ga));
		} else {
			setStroke(new BasicStroke(0));
		}
		if (ga.getStyle() != null) {
			setPaint(styles.get(ga.getStyle().getId()));
//			setStroke(new BasicStroke(ga.getStyle().getLineWidth()));
		}
		if (ga instanceof Rectangle) {
			paint((Rectangle) ga);
		} else if (ga instanceof RoundedRectangle) {
			paint((RoundedRectangle) ga);
		} else if (ga instanceof Text) {
			paint((Text) ga);
		} else if (ga instanceof Polygon) {
			paint((Polygon) ga);
		} else if (ga instanceof Polyline) {
			paint((Polyline) ga);
		} else if (ga instanceof Image) {
			paint((Image) ga);
		}
		EList<GraphicsAlgorithm> gAlgo = ga.getGraphicsAlgorithmChildren();
		Iterator<GraphicsAlgorithm> itg = gAlgo.iterator();
		while (itg.hasNext()) {
			GraphicsAlgorithm g = itg.next();
			translate(ga.getX(), ga.getY());
//			setClip(ga.getX(), ga.getY(), ga.getWidth(), ga.getHeight());
			paintGraphicsAlgorithm(g);
			translate(-ga.getX(), -ga.getY());
		}
		return true;
	}
	/**
	 * Paint the element Rectangle in Graphiti.mm.algorithms.
	 * Link the attributes in Rectangle to java.AWT.Rectangle. 
	 * @param rect - Rectangle to be painted.
	 */
	private void paint(Rectangle rect) {
		java.awt.Rectangle rectangle = new java.awt.Rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
		if (rect.getFilled()) {
			fill(rectangle);
		}
		if (rect.getStyle() != null) {
			setPaint(getColor(rect.getStyle().getForeground()));
		} else if (rect.getForeground() != null) {
			setPaint(getColor(rect.getForeground()));
		}
		if (rect.getLineVisible()) {
			draw(rectangle);
		}
	}
	/**
	 * Paint the element RoundedRectangle in Graphiti.mm.algorithms.
	 * Link the attributes in RoundedRectangle to java.AWT.RoundedRectangle2D.Float. 
	 * @param rrect - RoundedRectangle to be painted.
	 */
	private void paint(RoundedRectangle rrect) {
		RoundRectangle2D.Float rrectangle = new RoundRectangle2D.Float(
				rrect.getX(), rrect.getY(), rrect.getWidth(), rrect.getHeight(), rrect.getCornerWidth(), rrect.getCornerHeight());
		if (rrect.getFilled()) {
			fill(rrectangle);
		}
		if (rrect.getStyle().getForeground() != null) {
			setPaint(getColor(rrect.getStyle().getForeground()));
		} else if (rrect.getForeground() != null) {
			setPaint(getColor(rrect.getForeground()));
		}
		if (rrect.getLineVisible()) {
			draw(rrectangle);
		}
	}
	/**
	 * 
	 * Paint the element Text in Graphiti.mm.algorithms.
	 * Link the attributes in Text to draw a String in Graphics2D.
	 * @param text - Text to be painted.
	 */
	private void paint(Text text) {
		//Clip, Transform, Paint, Font and Composite
		setFont(text.getFont());
		FontMetrics fm   = getFontMetrics();
		String title = text.getValue();
		boolean reduced = false;
		if (text.getWidth() > 0 && fm.stringWidth(title) > text.getWidth()) {
			while (fm.stringWidth(title) > text.getWidth() - fm.stringWidth("...")) {
				title = title.substring(0, title.length() - 1);
				reduced = true;
			}
		}
		if (reduced) {
			title = title.concat("...");
		}
	
		// Center text horizontally and vertically
		java.awt.geom.Rectangle2D rect = fm.getStringBounds(title, this);
		int textHeight = (int)(rect.getHeight()); 
		int textWidth  = (int)(rect.getWidth());
		int x = text.getX();
		switch (text.getHorizontalAlignment().getValue()) {
		case Orientation.ALIGNMENT_TOP_VALUE:
			x = (text.getWidth() - textWidth)  / 2 + text.getX();	
			break;
		case Orientation.ALIGNMENT_CENTER_VALUE:
			x = (text.getWidth() - fm.stringWidth(text.getValue())) /2 + text.getX();
			break;
		default:
			break;
		}
		int y = text.getY();
		switch (text.getVerticalAlignment().getValue()) {
		case Orientation.ALIGNMENT_TOP_VALUE:
			y = (text.getHeight() - textHeight) / 2  + fm.getAscent() + text.getY();
			break;
		case Orientation.ALIGNMENT_CENTER_VALUE:
			y = (text.getHeight()) /2 + text.getY();
			break;
		default:
			break;
		}
		drawString(title, x, y);  // Draw the string.
	}
	/**
	 * 
	 * Paint the element Polygon in Graphiti.mm.algorithms.
	 * Link the attributes in Polygon to java.AWT.Polygon. 
	 * @param pol - Polygon to be painted.
	 */
	private void paint(Polygon pol) {
		EList<Point> points = pol.getPoints();
		java.awt.Polygon p = new java.awt.Polygon();
		for (int i = 0; i < points.size(); i++) {
			p.addPoint(points.get(i).getX(), points.get(i).getY());
		}
		if (pol.getFilled())  {
			fill(p);
		}
		if (pol.getStyle() != null) {
			setPaint(getColor(pol.getStyle().getForeground()));
		} else if (pol.getForeground() != null) {
			setPaint(getColor(pol.getForeground()));
		}
		if (pol.getLineVisible()) {
			draw(p);
		}
	}
	/**
	 * Paint the element Polyline in Graphiti.mm.algorithms.
	 * Link the attributes in Polyline to java.AWT.geom.GeneralPath. 
	 * @param pol - Polyline to be painted.
	 */
	private void paint(Polyline pol) {
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
		draw(path);
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
	/**
	 * Paint the element Image in Graphiti.mm.algorithms.
	 * Link the attributes in Image to java.AWT.Image. 
	 * @param im - Image to be painted.
	 */
	private void paint(Image im) {
		if (im.getId() != null) {
			java.awt.Image image = getImage(im.getId());
			if (image != null) {
				drawImage(image, im.getX(), im.getY(), im.getWidth(), im.getHeight(), null);
			}
			
		}
	}
	/**
	 * Obtain the Image corresponding to the id String which is given as parameter.
	 * A ImageProvider is needed in order to identify the image by Id.
	 * Code obtained from
	 * 			org.eclipse.graphiti.ui.internal.services.impl.ImageService
	 *  in the method
	 *  		 createImageDescriptorForId
	 * @param id - String with the Id of the Image registered in the ImageProvider.
	 * @return java.AWT.Image to be painted.
	 */
	private java.awt.Image getImage(String id) {
		if (id != null) {
			try  {
				IImageProvider imageProviders[] = ExtensionManager.getSingleton().getImageProviders();
				for (int i = 0; i < imageProviders.length; i++) {
					IImageProvider imageProvider = imageProviders[i];
					String imageFilePath = imageProvider.getImageFilePath(id);
					if (imageFilePath != null) {
						String pluginId = imageProvider.getPluginId();
						if (pluginId != null) {
//							 Bundle bundle = Platform.getBundle(pluginId);
//							 // look for the image (this will check both the plugin and fragment folders
//							 URL fullPathString = BundleUtility.find(bundle, imageFilePath);
							 BufferedImage img = null;
							 try {
								 img = ImageIO.read(new File(getPluginDir(pluginId).concat(imageFilePath)));
							 } catch (IOException e) {};
							return img;
						}
						break;
					}
				}
			} catch (Exception e) {
				LocalImageProvider fip = new LocalImageProvider();
				String imageFilePath = fip.getImageFilePath(id);
				BufferedImage img = null;
				try {
					img = ImageIO.read(new File(imageFilePath));
				} catch (IOException ex) {};
				return img;
			};
		}
		return null;
	}
		@SuppressWarnings("deprecation")
		public static String getPluginDir(String pluginId)
		{
			/* get bundle with the specified id */
			Bundle bundle = Platform.getBundle(pluginId);
			if( bundle == null )
				throw new RuntimeException("Could not resolve plugin: " + pluginId + "\r\n" +
						"Probably the plugin has not been correctly installed.\r\n" +
						"Running eclipse from shell with -clean option may rectify installation.");
			
			/* resolve Bundle::getEntry to local URL */
			URL pluginURL = null;
			try {
				pluginURL = Platform.resolve(bundle.getEntry("/"));
			} catch (IOException e) {
				throw new RuntimeException("Could not get installation directory of the plugin: " + pluginId);
			}
			String pluginInstallDir = pluginURL.getPath().trim();
			if( pluginInstallDir.length() == 0 )
				throw new RuntimeException("Could not get installation directory of the plugin: " + pluginId);
			
			/* since path returned by URL::getPath starts with a forward slash, that
			 * is not suitable to run commandlines on Windows-OS, but for Unix-based
			 * OSes it is needed. So strip one character for windows. There seems
			 * to be no other clean way of doing this. */
			if( Platform.getOS().compareTo(Platform.OS_WIN32) == 0 )
				pluginInstallDir = pluginInstallDir.substring(1);
			
			return pluginInstallDir;
		}

	/**
	 * Sets the java.AWT.Font to paint in Graphics2D. Also returns it.
	 * @param font - Font in Graphiti.mm.algorithms.style.
	 * @return java.AWT.Font painted.
	 */
	private java.awt.Font setFont(Font font) {
		if (font != null) {
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
			setFont(awtFont);
			return awtFont;
		}
		return null;
	}
	/**
	 * Copied from GraphicsToGraphics2DAdaptor because there is private.
	 * Form the Stroke to be painted with the needed GraphicsAlgorithm attributes  
	 * @param ga - GraphicsAlgorithm 
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
	/**
	 * Create the HashMap which links the List of Styles with the java.AWT.LinearGradientPaint
	 * @param styles - List of Style in Graphiti.mm.algorithms.style
	 * @return HashMap where is linked the java.AWT.LinearGradientPaint with the Style Id.
	 */
	private HashMap<String, LinearGradientPaint> createStyle(EList<Style> styles) {
		Iterator<Style> it = styles.iterator();
		HashMap<String, LinearGradientPaint> lgp = new HashMap<String, LinearGradientPaint>(styles.size());
		while (it.hasNext()) {
			Style st = it.next();
			GradientColoredArea gc = st.getRenderingStyle().getAdaptedGradientColoredAreas().getAdaptedGradientColoredAreas().get(0).getGradientColor().get(0);
			Point2D start = new Point2D.Float(0, 0);
			Point2D end = new Point2D.Float(0,100);
			float x = 0, y = 0, xE = 100, yE = 100;
			switch(gc.getStart().getLocationType().getValue()) {
			case LocationType.LOCATION_TYPE_ABSOLUTE_END_VALUE: {
				x = -gc.getStart().getLocationValue();
				y = -gc.getStart().getLocationValue();
			}
			case LocationType.LOCATION_TYPE_ABSOLUTE_START_VALUE: {
				x = gc.getStart().getLocationValue();
				y = gc.getStart().getLocationValue();
			}
			default: {
				
			}
			}
			switch(gc.getEnd().getLocationType().getValue()) {
			case LocationType.LOCATION_TYPE_ABSOLUTE_END_VALUE: {
				xE = -gc.getEnd().getLocationValue();
				yE = -gc.getEnd().getLocationValue();
			}
			case LocationType.LOCATION_TYPE_ABSOLUTE_START_VALUE: {
				xE = gc.getEnd().getLocationValue();
				yE = gc.getEnd().getLocationValue();
			}
			default: {
				
			}
			}
			Point2D _start = new Point2D.Float(x, y);
			Point2D _end = new Point2D.Float(xE, yE);
			//float[] dist = {gc.getStart().getLocationValue().floatValue(),(float) (1.0 - gc.getEnd().getLocationValue().floatValue())};
			float[] dist = {(float) (0.0),(float) (1.0)};
			java.awt.Color[] colors = {getColor(gc.getStart().getColor()), getColor(gc.getEnd().getColor())};
			LinearGradientPaint p = new LinearGradientPaint(start, end, dist, colors);
			lgp.put(st.getId(), p);
		}
		return lgp;
	}
	/**
	 * Converts a Graphiti.mm.algorithms.style.Color into a java.AWT.Color
	 * @param toConvert - Color to be converted.
	 * @return java.AWT.Color obtained from Color in Graphiti.
	 */
	protected java.awt.Color getColor(Color toConvert) {

		return new java.awt.Color(toConvert.getRed(), toConvert.getGreen(), toConvert.getBlue());
	}
	@Override
	public void draw(java.awt.Shape s) {
		// TODO Auto-generated method stub
		super.draw(s);
	}
	@Override
	public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
		// TODO Auto-generated method stub
		super.drawRenderableImage(img, xform);
	}
	@Override
	public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
		// TODO Auto-generated method stub
		super.drawRenderedImage(img, xform);
	}
	@Override
	public void drawString(String str, float x, float y) {
		// TODO Auto-generated method stub
		super.drawString(str, x, y);
	}
	@Override
	public void drawString(AttributedCharacterIterator iterator, float x, float y) {
		// TODO Auto-generated method stub
		super.drawString(iterator, x, y);
	}
	@Override
	public void fill(java.awt.Shape s) {
		// TODO Auto-generated method stub
		super.fill(s);
	}
	@Override
	public GraphicsConfiguration getDeviceConfiguration() {
		// TODO Auto-generated method stub
		return super.getDeviceConfiguration();
	}
	@Override
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		// TODO Auto-generated method stub
		super.copyArea(x, y, width, height, dx, dy);
	}
	@Override
	public Graphics create() {
		// TODO Auto-generated method stub
		return super.create();
	}
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
	}
	@Override
	public boolean drawImage(java.awt.Image img, int x, int y, ImageObserver observer) {
		// TODO Auto-generated method stub
		return super.drawImage(img, x, y, observer);
	}
	@Override
	public boolean drawImage(java.awt.Image img, int x, int y, int width, int height,
			ImageObserver observer) {
		// TODO Auto-generated method stub
		return super.drawImage(img, x, y, width, height, observer);
	}
	@Override
	public FontMetrics getFontMetrics(java.awt.Font f) {
		// TODO Auto-generated method stub
		return super.getFontMetrics(f);
	}
	@Override
	public void setXORMode(java.awt.Color c1) {
		// TODO Auto-generated method stub
		super.setXORMode(c1);
	}
}