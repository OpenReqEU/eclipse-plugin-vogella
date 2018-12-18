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

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerColumn;

import reactor.core.publisher.Mono;

/**
 * This interface is used to configure the {@link ProposalDialog} and to deliver
 * its input.
 * 
 * @author Simon Scholz
 *
 */
public interface IProposalConfigurator<T> {

	/**
	 * This method may be used to initialize the given {@link Viewer} with
	 * certain {@link ViewerColumn}s or {@link CellLabelProvider}.
	 * 
	 * @param viewer
	 *            {@link Viewer}
	 */
	public void configureViewer(Viewer viewer);

	/**
	 * <p>
	 * As receiving the input may be a long running process a {@link Mono}
	 * object is returned, which can inform about incoming input.
	 * </p>
	 * <p>
	 * For Example:<br/>
	 * 
	 * <pre>
	 * Mono<T> input = proposalConfigurator.getInput(filterContent);
	 * input.doOnSubscribe(s -> proposalDialog.showLoading()).subscribe(proposalDialog::setInput,
	 * this::handleError);
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param filterContent
	 *            a String, which should filter the proposals. May be
	 *            <code>null</code> in order to aviod filtering
	 * @return {@link Mono} object, which can be used to receive the input of
	 *         the proposal viewer asyc.
	 */
	public Mono<T> getInput(String filterContent);
}
