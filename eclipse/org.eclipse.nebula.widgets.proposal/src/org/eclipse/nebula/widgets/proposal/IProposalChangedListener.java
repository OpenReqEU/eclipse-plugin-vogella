/*******************************************************************************
 * Copyright (c) 2018, 2019 vogella GmbH and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Simon Scholz <simon.scholz@vogella.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.proposal;

import org.eclipse.jface.viewers.ISelection;

/**
 * This is a listener interface, which can be attached to the
 * {@link ContentProposalAdapter} in order to get notified, if a selection in
 * the {@link ProposalDialog} occured.
 * 
 * @author Simon Scholz
 *
 */
public interface IProposalChangedListener {

	/**
	 * Informs listeners about a new {@link ISelection}
	 * 
	 * @param selection
	 *            {@link ISelection}
	 */
	public void proposalChanged(ISelection selection);
}
