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
 *    mwenz - Bug 323155 - Check usage scenarios for DefaultPrintFeature and
 *            DefaultSaveImageFeature
 *
 * </copyright>
 *
 *******************************************************************************/
package org.eclipse.graphiti.features;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.context.IAddBendpointContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICopyContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IMoveAnchorContext;
import org.eclipse.graphiti.features.context.IMoveBendpointContext;
import org.eclipse.graphiti.features.context.IMoveConnectionDecoratorContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.features.context.IRemoveBendpointContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.features.impl.AbstractFeatureProvider;
import org.eclipse.graphiti.features.impl.DefaultPrintFeature;
import org.eclipse.graphiti.features.impl.DefaultSaveImageFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

/**
 * The Interface IFeatureProvider.
 * 
 * The set of provided features defines the operations, potentially available in
 * a graphical editor. There has been the idea to restrict available operations
 * through tool behavior providers (
 * {@link org.eclipse.graphiti.tb.IToolBehaviorProvider}).
 * 
 * @see org.eclipse.graphiti.features.IFeature
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients,
 *              extend {@link AbstractFeatureProvider} or
 *              {@link DefaultFeatureProvider} instead.
 */
public interface IFeatureProvider extends IMappingProvider {

	/**
	 * Provides all create features. In the graphics framework they will be
	 * visualized in an editor as create tools.
	 * 
	 * @return all create features
	 * 
	 * @see ICreateFeature
	 */
	ICreateFeature[] getCreateFeatures();

	/**
	 * Gets the create connection features.
	 * 
	 * @return all features to create connections
	 * 
	 * @see ICreateConnectionFeature
	 */
	ICreateConnectionFeature[] getCreateConnectionFeatures();

	/**
	 * Add features create graphical representations of domain model objects.
	 * 
	 * @param context
	 *            the context
	 * 
	 * @return add feature according to the given context
	 * 
	 * @see IAddFeature
	 */
	IAddFeature getAddFeature(IAddContext context);

	/**
	 * Delete features remove the grapical representations of domain model
	 * objects as well as the domain model objects itself.
	 * 
	 * @param context
	 *            the context
	 * 
	 * @return delete feature according to the given context
	 * 
	 * @see IDeleteFeature
	 */
	IDeleteFeature getDeleteFeature(IDeleteContext context);

	/**
	 * Copy features fill the clipboard.
	 * 
	 * @param context
	 *            the context
	 * 
	 * @return copy feature according to the given context
	 * 
	 * @see ICopyFeature
	 */
	ICopyFeature getCopyFeature(ICopyContext context);

	/**
	 * Paste features bring clipboard content to the diagram.
	 * 
	 * @param context
	 *            the context
	 * 
	 * @return copy feature according to the given context
	 * 
	 * @see ICopyFeature
	 */
	IPasteFeature getPasteFeature(IPasteContext context);

	/**
	 * Remove features remove the grapical representations of domain model
	 * objects.
	 * 
	 * @param context
	 *            the context
	 * 
	 * @return remove feature according to the given context
	 * 
	 * @see IRemoveFeature
	 */
	IRemoveFeature getRemoveFeature(IRemoveContext context);

	/**
	 * Resize shape features respond to user's resize requests.
	 * 
	 * @param context
	 *            the context
	 * 
	 * @return resize feature according to the given context
	 * 
	 * @see IResizeShapeFeature
	 */
	IResizeShapeFeature getResizeShapeFeature(IResizeShapeContext context);

	/**
	 * Move shape features respond to user's move requests.
	 * 
	 * @param context
	 *            the context
	 * 
	 * @return move feature according to the given context
	 * 
	 * @see IMoveShapeFeature
	 */
	IMoveShapeFeature getMoveShapeFeature(IMoveShapeContext context);

	/**
	 * Move features respond to user's move requests.
	 * 
	 * @param context
	 *            the context
	 * 
	 * @return move feature according to the given context
	 * 
	 * @see IMoveConnectionDecoratorFeature
	 */
	IMoveConnectionDecoratorFeature getMoveConnectionDecoratorFeature(IMoveConnectionDecoratorContext context);

	/**
	 * Move features respond to user's move requests. This one is especially for
	 * fix point anchors (
	 * {@link org.eclipse.graphiti.mm.pictograms.FixPointAnchor}).
	 * 
	 * @param context
	 *            the context
	 * 
	 * @return move feature according to the given context
	 * 
	 * @see IMoveAnchorFeature
	 */
	IMoveAnchorFeature getMoveAnchorFeature(IMoveAnchorContext context);

	/**
	 * Move features respond to user's move requests. This one is especially for
	 * fix point anchors (
	 * {@link org.eclipse.graphiti.mm.pictograms.FixPointAnchor}).
	 * 
	 * @param context
	 *            the context
	 * 
	 * @return move feature according to the given context
	 * 
	 * @see IMoveBendpointFeature
	 */
	IMoveBendpointFeature getMoveBendpointFeature(IMoveBendpointContext context);

	/**
	 * It is planned to use this for printing support. Not yet supported
	 * perfectly. The default implementation in {@link AbstractFeatureProvider}
	 * returns the an instance of {@link DefaultPrintFeature} which could be
	 * overridden to influence the standard behaviour.<br>
	 * Return <code>null</code> to disable printing.
	 * 
	 * @return The print feature to use or <code>null</code> to disable
	 *         printing.
	 * 
	 * @see IPrintFeature
	 */
	IPrintFeature getPrintFeature();

	/**
	 * It is planned to use this for save as image support. Not yet supported
	 * perfectly. The default implementation in {@link AbstractFeatureProvider}
	 * returns the an instance of {@link DefaultSaveImageFeature} which could be
	 * overridden to influence the standard behaviour.<br>
	 * Return <code>null</code> to disable save as image.
	 * 
	 * @return The save feature to use or <code>null</code> to disable save as
	 *         image.
	 */
	ISaveImageFeature getSaveImageFeature();

	/**
	 * Update features do the synchronization work and transport data from
	 * domain model to pictograms model elements.
	 * 
	 * @param context
	 *            the context
	 * 
	 * @return update feature according to the given context
	 * 
	 * @see IUpdateFeature
	 */
	IUpdateFeature getUpdateFeature(IUpdateContext context);

	/**
	 * Layout features do the layouting work (sizes and dimensions) inside
	 * (and/or) outside a pictogram element.
	 * 
	 * @param context
	 *            the context
	 * 
	 * @return layout feature according to the given context
	 * 
	 * @see ILayoutFeature
	 */
	ILayoutFeature getLayoutFeature(ILayoutContext context);

	/**
	 * Reconnection features handle the change of a connection's start or end
	 * anchor.
	 * 
	 * @param context
	 *            the context
	 * 
	 * @return reconnection feature according to the given context
	 * 
	 * @see IReconnectionFeature
	 */
	IReconnectionFeature getReconnectionFeature(IReconnectionContext context);

	/**
	 * Custom features can do anything. Their functionality can not be planned
	 * by the graphics framework (team).
	 * 
	 * @param context
	 *            the context
	 * 
	 * @return custom feature according to the given context
	 * 
	 * @see ICustomFeature
	 */
	ICustomFeature[] getCustomFeatures(ICustomContext context);

	/**
	 * Returns the diagram type provider.
	 * 
	 * @return the diagram type provider
	 * 
	 * @see IDiagramTypeProvider
	 */
	IDiagramTypeProvider getDiagramTypeProvider();

	/**
	 * Add bendpoint features handle the user's requst to have more connection
	 * bendpoints.
	 * 
	 * @param context
	 *            the context
	 * 
	 * @return add feature according to the given context
	 * 
	 * @see IAddBendpointFeature
	 * @see IAddBendpointContext
	 */
	IAddBendpointFeature getAddBendpointFeature(IAddBendpointContext context);

	/**
	 * Add bendpoint features handle the user's requst to remove connection
	 * bendpoints.
	 * 
	 * @param context
	 *            the context
	 * 
	 * @return remove feature according to the given context
	 * 
	 * @see IRemoveBendpointFeature
	 */
	IRemoveBendpointFeature getRemoveBendpointFeature(IRemoveBendpointContext context);

	/**
	 * Direct editing features handle direct editing functionality (including
	 * drop down lists and text completion).
	 * 
	 * @param context
	 *            the context
	 * 
	 * @return direct editing feature according to the given context
	 * 
	 * @see IDirectEditingFeature
	 */
	IDirectEditingFeature getDirectEditingFeature(IDirectEditingContext context);

	/**
	 * If a creation feature wants to switch directly into the direct editing
	 * mode (after the object creation),<br>
	 * it must provide the necessary information via this object.
	 * 
	 * @return {@link IDirectEditingInfo}
	 */
	IDirectEditingInfo getDirectEditingInfo();

	/**
	 * if the user should be able to create connections directly from a
	 * pictogram element without using the connection tool you can provide an
	 * array of features. By providing one feature this feature will be executed
	 * directly on drop. By providing 2 or more features a popup menu will let
	 * the user select the feature.
	 * 
	 * @param context
	 *            - the pictogram element the drag and drop should be initiated
	 *            on
	 * 
	 * @return an array of features or null.
	 */
	IFeature[] getDragAndDropFeatures(IPictogramElementContext context);

	/**
	 * Adds the if possible.
	 * 
	 * @param context
	 *            the context
	 * 
	 * @return added pictogram elements
	 */
	PictogramElement addIfPossible(IAddContext context);

	/**
	 * Can add.
	 * 
	 * @param context
	 *            the context
	 * 
	 * @return status and reason
	 */
	IReason canAdd(IAddContext context);

	/**
	 * Checks if an layout process can be processed. Usually implementers have
	 * to check the context.
	 * 
	 * @param context
	 *            the context
	 * 
	 * @return status and reason
	 */
	IReason canLayout(ILayoutContext context);

	/**
	 * Checks if an update process can be processed. Usually implementers have
	 * to check the context.
	 * 
	 * @param context
	 *            the context
	 * 
	 * @return status and reason
	 */
	IReason canUpdate(IUpdateContext context);

	/**
	 * Process the layout process.
	 * 
	 * @param context
	 *            the context
	 * 
	 * @return status and reason
	 */
	IReason layoutIfPossible(ILayoutContext context);

	/**
	 * Process the update process. Usually reads businees data and modifies
	 * pictograms model.
	 * 
	 * @param context
	 *            the context
	 * 
	 * @return status and reason
	 */
	IReason updateIfPossible(IUpdateContext context);

	/**
	 * Update if possible and needed.
	 * 
	 * @param context
	 *            the context
	 * 
	 * @return status and reason
	 */
	IReason updateIfPossibleAndNeeded(IUpdateContext context);

	/**
	 * Checks if an update process is needed and has be processed. Usually
	 * implementers have to compare pictograms and business data.
	 * 
	 * @param context
	 *            the context
	 * 
	 * @return status and reason
	 */
	IReason updateNeeded(IUpdateContext context);

	//	/**
	//	 * Gets the transactional editing domain.
	//	 * 
	//	 * @return transactional editing domain which is linked to the editor
	//	 */
	//	TransactionalEditingDomain getTransactionalEditingDomain();

	/**
	 * This is called to dispose the object.
	 */
	void dispose();
}