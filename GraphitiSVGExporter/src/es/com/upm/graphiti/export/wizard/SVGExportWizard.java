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
package es.com.upm.graphiti.export.wizard;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.eclipse.ui.wizards.datatransfer.FileSystemExportWizard;
import org.eclipse.core.resources.IResource;

import es.com.upm.graphiti.exporter.svg.GraphitiSVGExporter;

/**
 * @author jpsalazar
 *
 */
public class SVGExportWizard extends FileSystemExportWizard {
	
	private SVGWizardExportResourcesPage mainPage;
	@Override
	public boolean performFinish() {
		List<Object> ire = (List<Object>) mainPage.getSelectedResources();
		String folderDestination = mainPage.getDestinationValue();
		for (Object obj  :  ire ) {
			String fileName = ((IResource) obj).getFullPath().toString();
			
			if (fileName != null) {
				GraphitiSVGExporter gSVGExporter = new GraphitiSVGExporter();
				try {
					gSVGExporter.exporter(fileName, folderDestination);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SVGGraphics2DIOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return true;		
	}
	@Override
	public void addPages() {
		mainPage = new SVGWizardExportResourcesPage("pageName", null);
        addPage(mainPage);	
	}
}
