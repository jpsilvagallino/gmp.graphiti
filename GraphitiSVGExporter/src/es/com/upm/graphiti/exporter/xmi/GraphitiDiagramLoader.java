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
package es.com.upm.graphiti.exporter.xmi;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FilenameFilter;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramsPackage;
//import org.eclipse.graphiti.mm.pictograms.impl.PictogramsPackageImpl;
/**
 * 
 * @author jpsalazar
 *
 */
public class GraphitiDiagramLoader {
	public static Diagram create(String filename) {
		if (filename == null) {
			return null;
		}
		PictogramsPackage.eINSTANCE.eClass();
//		PictogramsPackageImpl.init();
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("pictograms", new XMIResourceFactoryImpl());
	    ResourceSet resSet = new ResourceSetImpl();
	    // Get the resource
	    Resource resource = resSet.getResource(URI.createURI(filename), true);
	    // Get the first model element and cast it to the right type, in my
	    // example everything is hierarchical included in this first node
	    for (EObject obj : resource.getContents()) {
	    	if (obj instanceof Diagram) {
	    	    return (Diagram) obj;
	    	}
	    }
	    return null;
	  }
	public static void main(String[] args) {
		FileDialog dialog = new FileDialog(new Frame(), "My File Selection");
		dialog.setFilenameFilter(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (name.endsWith(".pictograms")) {
					return true;
				}
				return false;
			}
		});
		dialog.setVisible(true);
		String file = dialog.getFile();
		if (file != null) {
			GraphitiDiagramLoader.create(file);
		}
		System.exit(0);
	}
}
