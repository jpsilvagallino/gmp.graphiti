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
import java.awt.GraphicsConfiguration;
import java.awt.LinearGradientPaint;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.AttributedCharacterIterator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.ExtensionHandler;
import org.apache.batik.svggen.ImageHandler;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
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
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;


import es.com.upm.flow.adaptor.provider.LocalImageProvider;
import es.com.upm.graphiti.exporter.svg.GradientExtensionHandler;
import es.com.upm.graphiti.exporter.xmi.GraphitiDiagramLoader;


/**
 * Test Class to export Graphiti to SVG via Pictograms Package.
 * @author jpsalazar
 *
 */
public class TestSVGGen extends SVGGraphics2D {
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
	 * Extended Constructor from SVGGraphics2D
	 * @param domFactory - Factory which will produce Elements for the DOM tree this Graphics2D generates.
	 */
	public TestSVGGen(Document domFactory) {
		super(domFactory);
	}
	/**
	 * Extended Constructor from SVGGraphics2D
	 * @param document - Factory which will produce Elements for the DOM tree this Graphics2D generates.
	 * @param imageHandler - defines how images are referenced in the generated SVG fragment
	 * @param extensionHandler -defines how Java 2D API extensions map to SVG Nodes.
	 */
	public TestSVGGen(Document document, ImageHandler imageHandler,
			ExtensionHandler extensionHandler) {
		super(document, imageHandler, extensionHandler, false);
	}
	/**
	 * Extended Constructor from SVGGraphics2D
	 * @param graphics
	 */
	public TestSVGGen(SVGGraphics2D graphics) {
		super(graphics);
	}
	/**
	 * Graphiti Model Extension.
	 */
	private static final String _EXTENSION = ".pictograms"; //$NON-NLS-1$
	/**
	 * Analyze the elements included in Diagram in order to paint them into Graphics.
	 * @param example - Diagram to be painted.
	 */
	public void analyze(Diagram example) {
		styles = createStyle(example.getStyles());
		colors = example.getColors();
		fonts = example.getFonts();
		example.getDiagramTypeId();
		analyzeElement((ContainerShape) example);
		analyzeConnector(example);
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
			setFont(fonts.get(0));
			if (s instanceof ContainerShape) {
				analyzeElement((ContainerShape) s);
			} else if (s instanceof Shape) {
				paint(s);
			}
		}
		
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
		int x = ga.getX();
		int y = ga.getY();
		if (c instanceof FreeFormConnection) {
			Anchor origin = ((FreeFormConnection) c).getStart();
			ILocation il = Graphiti.getPeLayoutService().getLocationRelativeToDiagram(origin);
			x = il.getX();
			y = il.getY();
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
			
			// Cuidado, se deberían COPIAR LOS PUNTOS
			((Polyline) ga).getPoints().add(Graphiti.getGaService().createPoint(x, y));
			
			List<Point> bendP = ((FreeFormConnection) c).getBendpoints();
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
			((Polyline) ga).getPoints().add(Graphiti.getGaService().createPoint(x, y));
		}
		paintGraphicsAlgorithm(ga);
		List<ConnectionDecorator> cDeco = c.getConnectionDecorators();
		Iterator<ConnectionDecorator> itcd = cDeco.iterator();
		while (itcd.hasNext()) {
			ConnectionDecorator cd = itcd.next();
			translate(x + cd.getLocation(), y);
			paintGraphicsAlgorithm(cd.getGraphicsAlgorithm());
			translate(-x, -y);
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
		if (s instanceof ContainerShape) {
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
	public boolean paintGraphicsAlgorithm(GraphicsAlgorithm ga) {
		if (ga == null) {
			return true;
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
			paint((Image)ga);
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
		return false;
	}
	private void paint(Rectangle rect) {
		if (rect.getBackground() != null) {
			setBackground(getColor(rect.getBackground()));
		}
		
//		Composite composite = getComposite();
//		if (composite instanceof AlphaComposite) {
//			AlphaComposite newComposite = AlphaComposite.getInstance(((AlphaComposite) composite).getRule(), rect.getTransparency().floatValue());
//			setComposite(newComposite);
//		}
		if (rect.getLineVisible()) {
//			BasicStroke stroke = new BasicStroke(rect.getLineWidth());
			setStroke(createStroke(rect));
		} else {
			setStroke(new BasicStroke(0));
		}
		if (rect.getStyle() != null) {
			setColor(getColor(rect.getStyle().getForeground()));
			setPaint(styles.get(rect.getStyle().getId()));
			setStroke(new BasicStroke(rect.getStyle().getLineWidth()));
		} else if (rect.getForeground() != null) {
			setColor(getColor(rect.getForeground()));
		}
		if (rect.getFilled()) {
			fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
		} else {
			drawRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
		}
	}	
	private void paint(RoundedRectangle rrect) {
		if (rrect.getBackground() != null) {
			setBackground(getColor(rrect.getBackground()));
		}
		
//		Composite composite = getComposite();
//		if (composite instanceof AlphaComposite) {
//			AlphaComposite newComposite = AlphaComposite.getInstance(((AlphaComposite) composite).getRule(), rrect.getTransparency().floatValue());
//			setComposite(newComposite);
//		}
		if (rrect.getLineVisible()) {
//			BasicStroke stroke = new BasicStroke(rrect.getLineWidth());
			setStroke(createStroke(rrect));
		} else {
			setStroke(new BasicStroke(0));
		}
		if (rrect.getStyle() != null) {
			setColor(getColor(rrect.getStyle().getForeground()));
			setPaint(styles.get(rrect.getStyle().getId()));
			setStroke(new BasicStroke(rrect.getStyle().getLineWidth()));
		} else if (rrect.getForeground() != null) {
			setColor(getColor(rrect.getForeground()));
		}
//		RenderingStyle st = rrect.getStyle().getRenderingStyle();
//		st.getAdaptedGradientColoredAreas();

		if (rrect.getFilled()) {
			fillRoundRect(rrect.getX(), rrect.getY(), rrect.getWidth(), rrect.getHeight(), rrect.getCornerWidth(), rrect.getCornerHeight());
		} else {
			drawRoundRect(rrect.getX(), rrect.getY(), rrect.getWidth(), rrect.getHeight(), rrect.getCornerWidth(), rrect.getCornerHeight());
		}
	}
	private void paint(Text text) {
		//Clip, Transform, Paint, Font and Composite
		setFont(text.getFont());
		
//		Composite composite = getComposite();
//		if (composite instanceof AlphaComposite) {
//			AlphaComposite newComposite = AlphaComposite.getInstance(((AlphaComposite) composite).getRule(), text.getTransparency().floatValue());
//			setComposite(newComposite);
//		}
		if (text.getLineVisible()) {
//			BasicStroke stroke = new BasicStroke(text.getLineWidth());
			setStroke(createStroke(text));
		} else {
			setStroke(new BasicStroke(0));
		}
		if (text.getStyle() != null) {
			setColor(getColor(text.getStyle().getForeground()));
			setPaint(styles.get(text.getStyle().getId()));
			setStroke(new BasicStroke(text.getStyle().getLineWidth()));
		} else if (text.getForeground() != null) {
			setColor(getColor(text.getForeground()));
		}
		
		FontMetrics fm   = getFontMetrics();
		java.awt.geom.Rectangle2D rect = fm.getStringBounds(text.getValue(), this);

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
		
		drawString(text.getValue(), x, y);  // Draw the string.
	}	
	private void paint(Polygon pol) {
		if (pol.getBackground() != null) {
			setBackground(getColor(pol.getBackground()));
		}
		
//		Composite composite = getComposite();
//		if (composite instanceof AlphaComposite) {
//			AlphaComposite newComposite = AlphaComposite.getInstance(((AlphaComposite) composite).getRule(), pol.getTransparency().floatValue());
//			setComposite(newComposite);
//		}
		if (pol.getLineVisible()) {
//			BasicStroke stroke = new BasicStroke(pol.getLineWidth());
			setStroke(createStroke(pol));
		} else {
			setStroke(new BasicStroke(0));
		}
		if (pol.getStyle() != null) {
			setColor(getColor(pol.getStyle().getForeground()));
			setPaint(styles.get(pol.getStyle().getId()));
			setStroke(new BasicStroke(pol.getStyle().getLineWidth()));
		} else if (pol.getForeground() != null) {
			setColor(getColor(pol.getForeground()));
		}
		EList<Point> points = pol.getPoints();
		java.awt.Polygon p = new java.awt.Polygon();
		for (int i = 0; i < points.size(); i++) {
			p.addPoint(points.get(i).getX(), points.get(i).getY());
		}
		if (pol.getFilled())  {
			fillPolygon(p);
		} else {
			drawPolygon(p);
		}
	}
	private void paint(Polyline pol) {
		if (pol.getBackground() != null) {
			setBackground(getColor(pol.getBackground()));
		}
		
//		Composite composite = getComposite();
//		if (composite instanceof AlphaComposite) {
//			AlphaComposite newComposite = AlphaComposite.getInstance(((AlphaComposite) composite).getRule(), pol.getTransparency().floatValue());
//			setComposite(newComposite);
//		}
		if (pol.getLineVisible()) {
//			BasicStroke stroke = new BasicStroke(pol.getLineWidth());
			setStroke(createStroke(pol));
		} else {
			setStroke(new BasicStroke(0));
		}		
		if (pol.getStyle() != null) {
			setColor(getColor(pol.getStyle().getForeground()));
			setPaint(styles.get(pol.getStyle().getId()));
			setStroke(new BasicStroke(pol.getStyle().getLineWidth()));
		} else if (pol.getForeground() != null) {
			setColor(getColor(pol.getForeground()));
		}
		EList<Point> points = pol.getPoints();
		int [] xpoints = new int[points.size()];
		int [] ypoints = new int[points.size()];
		for (int i = 0; i < points.size(); i++) {
			xpoints[i] = points.get(i).getX();
			ypoints[i] = points.get(i).getY();
		}
		drawPolyline(xpoints, ypoints, points.size());
	}
	
	private void paint(Image im) {
		java.awt.Image image = getImage(im.getId());
		if (image != null) {
			drawImage(image, im.getX(), im.getY(), im.getWidth(), im.getHeight(), null);
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
	private java.awt.Font setFont(Font font) {
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
		
		java.awt.Font awtFont = new java.awt.Font(font.getName(), style, font.getSize());
		setFont(awtFont);
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

    // Get a DOMImplementation.
    DOMImplementation domImpl =
      GenericDOMImplementation.getDOMImplementation();

    // Create an instance of org.w3c.dom.Document.
    String svgNS = "http://www.w3.org/2000/svg";
    Document document = domImpl.createDocument(svgNS, "svg", null);

    SVGGeneratorContext ctx = SVGGeneratorContext.createDefault(document);
    ctx.setExtensionHandler(new GradientExtensionHandler());
	ctx.setEmbeddedFontsOn(true);
	
	// Create an instance of the SVG Generator.
    TestSVGGen svgGenerator = new TestSVGGen(document, ctx.getImageHandler(), ctx.getExtensionHandler());
	//TestSVGGen svgGenerator = new TestSVGGen(document);
    //Gestión de archivo a Mapear
    String file = svgGenerator.getFile();
	if (file != null) {
		svgGenerator.analyze(GraphitiDiagramLoader.create(file));
	} else {
		System.exit(0);
	}

    // Finally, stream out SVG to the standard output using
    // UTF-8 encoding.
    boolean useCSS = false; // we want to use CSS style attributes
    Writer out = new OutputStreamWriter(
    		new FileOutputStream(new File("R:/Juan Pablo/WebTool/SVG/"+file.substring(0, file.indexOf('.'))+".svg")), "UTF-8");
    svgGenerator.stream(out, useCSS);
    System.out.println("FILE EXPORTED::> R:/Juan Pablo/WebTool/SVG/"+file.substring(0, file.indexOf('.'))+".svg");
//    JSVGCanvas canvas = new JSVGCanvas();
//    JFrame f = new JFrame();
//    f.getContentPane().add(canvas);
//    canvas.setSVGDocument(document);
//    f.pack();
//    f.setVisible(true);
    System.exit(0);
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