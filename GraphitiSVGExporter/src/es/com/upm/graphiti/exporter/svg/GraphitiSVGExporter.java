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

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.CachedImageHandlerBase64Encoder;
import org.apache.batik.svggen.GenericImageHandler;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import es.com.upm.graphiti.exporter.xmi.GraphitiDiagramLoader;

public class GraphitiSVGExporter {
	/**
	 * Graphiti Model Extension.
	 */
	private static final String _EXTENSION = ".pictograms"; //$NON-NLS-1$
	/**
	 * Adaptor Graphiti to AWT.Graphics to export to SVG.
	 */
	DiagramGraphicsAdaptor svgGenerator; 
	/**
	 * Selection of the file via Dialog
	 * @return
	 */
	public String getFile() {
		FileDialog dialog = new FileDialog(new Frame(), "My Model Selection");
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
	/**
	 * Get a DOM implementation and initialize DiagramGraphitiAdaptor.
	 */
	public void createSVGDocument() {
		// Get a DOMImplementation.
		DOMImplementation domImpl =
	    	      GenericDOMImplementation.getDOMImplementation();
	    
	    // Create an instance of org.w3c.dom.Document.
	    String svgNS = "http://www.w3.org/2000/svg";
	    Document document = domImpl.createDocument(svgNS, "svg", null);
	    
	    SVGGeneratorContext ctx = SVGGeneratorContext.createDefault(document);
	    ctx.setExtensionHandler(new GradientExtensionHandler());
	    GenericImageHandler ihandler = new CachedImageHandlerBase64Encoder();
	    ctx.setGenericImageHandler(ihandler);
	    ctx.setEmbeddedFontsOn(false);
	    
	    // Create an instance of the SVG Generator.
//	    svgGenerator = new DiagramGraphicsAdaptor(document, ctx.getImageHandler(), ctx.getExtensionHandler());
	    svgGenerator = new DiagramGraphicsAdaptor(ctx, false);
	}
	/**
	 * Exports the SVGGraphics into a SVG File.
	 * @param file 
	 * @return String - Path to the SVG File.
	 * @throws FileNotFoundException 
	 * @throws UnsupportedEncodingException 
	 * @throws SVGGraphics2DIOException 
	 */
	public String exporter(String file, String folderDestination) throws UnsupportedEncodingException, FileNotFoundException, SVGGraphics2DIOException {
		createSVGDocument();
	    Diagram dModel = GraphitiDiagramLoader.create(file);
		if (dModel == null) {
			return null; 
		}
		svgGenerator.analyze(dModel);
		folderDestination += file.substring(0, file.lastIndexOf('.'))+".svg";
		// Finally, stream out SVG to the standard output using
	    // UTF-8 encoding.
		File svgFile = new File(folderDestination);
		svgFile.getParentFile().mkdirs();
		boolean useCSS = false; // we dont want to use CSS style attributes
	    Writer out = new OutputStreamWriter(
	    		new FileOutputStream(svgFile), "UTF-8");
	    svgGenerator.stream(out, useCSS);
	    return folderDestination;
	}
	/**
	 * Exports the SVGGraphics into a SVG File.
	 * @param file 
	 * @return String - Path to the SVG File.
	 * @throws FileNotFoundException 
	 * @throws UnsupportedEncodingException 
	 * @throws SVGGraphics2DIOException 
	 */
	public String exporter(Diagram dModel, String file, String folderDestination) throws UnsupportedEncodingException, FileNotFoundException, SVGGraphics2DIOException {
		createSVGDocument();
		if (dModel == null) {
			return null; 
		}
		svgGenerator.analyze(dModel);
		folderDestination += file.substring(0, file.lastIndexOf('.'))+".svg";
		// Finally, stream out SVG to the standard output using
	    // UTF-8 encoding.
		File svgFile = new File(folderDestination);
		svgFile.getParentFile().mkdirs();
	    boolean useCSS = false; // we dont want to use CSS style attributes
	    Writer out = new OutputStreamWriter(
	    		new FileOutputStream(svgFile), "UTF-8");
	    svgGenerator.stream(out, useCSS);
	    return folderDestination;
	}

	public static void main(String[] args) throws IOException {
		GraphitiSVGExporter gSVGExporter = new GraphitiSVGExporter();
		String file = gSVGExporter.getFile();
		String folderPath = "R:/Juan Pablo/WebTool/SVG/";
		System.out.println("FILE EXPORTED::> " + gSVGExporter.exporter(file, folderPath));
		System.exit(0);
  }
}
