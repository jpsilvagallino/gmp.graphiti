package es.com.upm.graphiti.export.wizard;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.dialogs.WizardExportResourcesPage;
import org.eclipse.ui.internal.wizards.datatransfer.WizardFileSystemResourceExportPage1;

public class SVGWizardExportResourcesPage extends WizardFileSystemResourceExportPage1//WizardExportResourcesPage
{

	protected SVGWizardExportResourcesPage(String pageName,
			IStructuredSelection selection) {
		super(pageName, selection);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Graphiti Model Extension.
	 */
	private static final String _EXTENSION = ".pictograms"; //$NON-NLS-1$
	@Override
	protected boolean hasExportableExtension(String resourceName) {
		if (resourceName.endsWith(_EXTENSION)) {
			return true;
		}
		return false;
	}
	@Override
	public String getDestinationValue() {
		// TODO Auto-generated method stub
		return super.getDestinationValue();
	}
	@Override
	public List getSelectedResources() {
		// TODO Auto-generated method stub
		return super.getSelectedResources();
	}

}
