/*******************************************************************************
 * <copyright>
 *
 * Copyright (c) 2005, 2010 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP AG - initial API, implementation and documentation
 *
 * </copyright>
 *
 *******************************************************************************/
package org.eclipse.graphiti.ui.internal.policy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Shape;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Handle;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.tb.ISelectionInfo;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;
import org.eclipse.graphiti.ui.internal.config.IConfigurationProvider;
import org.eclipse.graphiti.ui.internal.figures.GFPolylineConnection;
import org.eclipse.graphiti.ui.internal.parts.IConnectionEditPart;
import org.eclipse.graphiti.ui.internal.util.DataTypeTransformation;
import org.eclipse.graphiti.ui.internal.util.draw2d.GFColorConstants;
import org.eclipse.graphiti.ui.internal.util.draw2d.GFConnectionEndpointHandle;
import org.eclipse.graphiti.util.IColorConstant;
import org.eclipse.swt.graphics.Color;

/**
 * An EditPolicy, which gives feedback for if a ConnectionEditPart is selected.
 * This includes the connection-handles at the start/end of the connection. It
 * does not create any commands.
 * 
 * @see org.eclipse.graphiti.ui.internal.policy.IEditPolicyFactory#createConnectionHighlightEditPolicy()
 * @noinstantiate This class is not intended to be instantiated by clients.
 * @noextend This class is not intended to be subclassed by clients.
 */
public class ConnectionHighlightEditPolicy extends ConnectionEndpointEditPolicy {

	private Map<IFigure, Color> figureToColor = new HashMap<IFigure, Color>();

	private Map<Shape, Integer> shapeToLineStyle = new HashMap<Shape, Integer>();

	private IConfigurationProvider configurationProvider;

	/**
	 * Creates a new ConnectionHighlightEditPolicy.
	 * 
	 * @param configurationProvider
	 *            The IConfigurationProvider.
	 */
	protected ConnectionHighlightEditPolicy(IConfigurationProvider configurationProvider) {
		setConfigurationProvider(configurationProvider);
	}

	protected final IConfigurationProvider getConfigurationProvider() {
		return configurationProvider;
	}

	/**
	 * Is called when the ConnectionEditPart is 'selected'. It then highlights
	 * the selected ConnectionEditPart.
	 * 
	 * @see org.eclipse.gef.editpolicies.SelectionHandlesEditPolicy#addSelectionHandles()
	 */
	@Override
	protected void addSelectionHandles() {
		if (checkReconnectionFeature()) {
			super.addSelectionHandles();
		}
		showHighlight();
	}

	private boolean checkReconnectionFeature() {
		return true;
		// return (null != getConfigurationProvider().getFeatureProvider().getReconnectionFeature(new ReconnectionContext(null, null,
		// null)));
	}

	/**
	 * Is called when the previously 'selected' ConnectionEditPart is
	 * 'deselected'. It then disables the highlight of the selected
	 * ConnectionEditPart.
	 * 
	 * @see org.eclipse.gef.editpolicies.SelectionHandlesEditPolicy#removeSelectionHandles()
	 */
	@Override
	protected void removeSelectionHandles() {
		super.removeSelectionHandles();

		removeHighlight();
	}

	protected void showHighlight() {
		// remove previous highlight
		removeHighlight();

		// determine new highlight-values
		Color newForeground = GFColorConstants.HANDLE_BG;
		int newLineStyle = Graphics.LINE_DASH;

		if (getHost() != null && getHost().getModel() instanceof Connection) {
			Connection connection = (Connection) getHost().getModel();
			IToolBehaviorProvider tbp = getConfigurationProvider().getDiagramTypeProvider().getCurrentToolBehaviorProvider();
			ISelectionInfo selectionInfo = tbp.getSelectionInfoForConnection(connection);
			if (selectionInfo != null) {
				IColorConstant selectionColor = selectionInfo.getColor();
				if (selectionColor != null) {
					newForeground = DataTypeTransformation.toSwtColor(getConfigurationProvider(), selectionColor);
				}
				LineStyle selectionLineStyle = selectionInfo.getLineStyle();
				if (selectionLineStyle != null) {
					newLineStyle = DataTypeTransformation.toDraw2dLineStyle(selectionLineStyle);
				}
			}
		}

		// store old highlight-values and set new highlight-values
		// important: get old colors via getLocalForeGround() to ignore parent
		figureToColor.put(getConnectionFigure(), getConnectionFigure().getLocalForegroundColor());
		getConnectionFigure().setForegroundColor(newForeground);
		shapeToLineStyle.put(getConnectionFigure(), getConnectionFigure().getLineStyle());
		getConnectionFigure().setLineStyle(newLineStyle);

		if (getConnectionFigure() instanceof GFPolylineConnection) {
			GFPolylineConnection polylineConnection = (GFPolylineConnection) getConnectionFigure();
			List<IFigure> allDecorations = polylineConnection.getAllDecorations();
			for (IFigure decoration : allDecorations) {
				if (decoration != null) {
					figureToColor.put(decoration, decoration.getLocalForegroundColor());
					decoration.setForegroundColor(newForeground);
					if (decoration instanceof Shape) {
						Shape decorationShape = (Shape) decoration;
						shapeToLineStyle.put(decorationShape, new Integer(decorationShape.getLineStyle()));
						decorationShape.setLineStyle(newLineStyle);
					}
				}
			}
		}
	}

	protected void removeHighlight() {
		Set<IFigure> colorFigures = figureToColor.keySet();
		for (IFigure colorFigure : colorFigures) {
			Color oldColor = figureToColor.get(colorFigure);
			colorFigure.setForegroundColor(oldColor);
		}

		Set<Shape> lineStyleShapes = shapeToLineStyle.keySet();
		for (Shape lineStyleShape : lineStyleShapes) {
			int lineStyle = shapeToLineStyle.get(lineStyleShape);
			lineStyleShape.setLineStyle(lineStyle);
		}

		figureToColor.clear();
		shapeToLineStyle.clear();
	}

	// ===================== private helper methods ===========================

	private Shape getConnectionFigure() {
		return (Shape) ((GraphicalEditPart) getHost()).getFigure();
	}

	@Override
	protected void hideSelection() {
		super.hideSelection();
		if (getHost() instanceof IConnectionEditPart) {
			IConnectionEditPart cep = (IConnectionEditPart) getHost();
			cep.forceVisualRefresh();
		}
	}

	private void setConfigurationProvider(IConfigurationProvider configurationProvider) {
		this.configurationProvider = configurationProvider;
	}

	@Override
	protected List<Handle> createSelectionHandles() {
		List<Handle> list = new ArrayList<Handle>();
		list.add(new GFConnectionEndpointHandle((ConnectionEditPart) getHost(), ConnectionLocator.SOURCE));
		list.add(new GFConnectionEndpointHandle((ConnectionEditPart) getHost(), ConnectionLocator.TARGET));
		return list;
	}
}