/*******************************************************************************
 * <copyright>
 *
 * Copyright (c) 2005, 2011 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP AG - initial API, implementation and documentation
 *    mwenz - Bug 324859 - Need Undo/Redo support for Non-EMF based domain objects
 *
 * </copyright>
 *
 *******************************************************************************/
package org.eclipse.graphiti.pattern;

import org.eclipse.graphiti.features.ICustomUndoableFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReconnectionFeature;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.features.impl.AbstractFeature;
import org.eclipse.graphiti.func.IReconnection;
import org.eclipse.graphiti.internal.Messages;

/**
 * The Class ReconnectionFeatureForPattern.
 */
public class ReconnectionFeatureForPattern extends AbstractFeature implements IReconnectionFeature, ICustomUndoableFeature {

	private static final String NAME = Messages.ReconnectionFeatureForPattern_0_xfld;
	private IReconnection deletegate;

	/**
	 * Creates a new {@link ReconnectionFeatureForPattern}.
	 * 
	 * @param fp
	 *            the feature provider
	 * @param pattern
	 *            the pattern
	 */
	public ReconnectionFeatureForPattern(IFeatureProvider fp, IReconnection pattern) {
		super(fp);
		this.deletegate = pattern;
	}

	public boolean canReconnect(IReconnectionContext context) {
		return deletegate.canReconnect(context);
	}

	public void postReconnect(IReconnectionContext context) {
		deletegate.postReconnect(context);
	}

	public void preReconnect(IReconnectionContext context) {
		deletegate.preReconnect(context);
	}

	public void reconnect(IReconnectionContext context) {
		deletegate.reconnect(context);
	}

	public boolean canExecute(IContext context) {
		boolean ret = false;
		if (context instanceof IReconnectionContext) {
			ret = canReconnect((IReconnectionContext) context);
		}
		return ret;
	}

	public void execute(IContext context) {
		if (context instanceof IReconnectionContext) {
			reconnect((IReconnectionContext) context);
		}
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public boolean canUndo(IContext context) {
		if (deletegate instanceof ICustomUndoablePattern) {
			return ((ICustomUndoablePattern) deletegate).canUndo(this, context);
		}
		return super.canUndo(context);
	}

	/**
	 * @since 0.8
	 */
	@Override
	public void undo(IContext context) {
		if (deletegate instanceof ICustomUndoablePattern) {
			((ICustomUndoablePattern) deletegate).undo(this, context);
		}
	}

	/**
	 * @since 0.8
	 */
	@Override
	public boolean canRedo(IContext context) {
		if (deletegate instanceof ICustomUndoablePattern) {
			return ((ICustomUndoablePattern) deletegate).canRedo(this, context);
		}
		return true;
	}

	/**
	 * @since 0.8
	 */
	@Override
	public void redo(IContext context) {
		if (deletegate instanceof ICustomUndoablePattern) {
			((ICustomUndoablePattern) deletegate).redo(this, context);
		}
	}
}
