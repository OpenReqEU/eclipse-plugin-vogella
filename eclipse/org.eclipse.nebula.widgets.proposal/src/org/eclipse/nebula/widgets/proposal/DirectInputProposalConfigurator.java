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
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * This is a default implementation of the {@link IProposalConfigurator}
 * interface, which adds one column to the given viewer, attaches the given
 * {@link CellLabelProvider} and directly returns a {@link Mono} with the given
 * input object.
 * 
 * @author Simon Scholz
 *
 */
public class DirectInputProposalConfigurator<T> implements IProposalConfigurator<T> {

	private Flux<T> inputMono;
	private CellLabelProvider cellLabelProvider;

	/**
	 * Constructor.
	 * 
	 * @param input object, which will be passed to the {@link ProposalDialog}
	 */
	public DirectInputProposalConfigurator(T input) {
		this(input, new ColumnLabelProvider());
	}

	/**
	 * Constructor.
	 * 
	 * @param input object, which will be passed to the {@link ProposalDialog}
	 */
	public DirectInputProposalConfigurator(Flux<T> input) {
		this(input, new ColumnLabelProvider());
	}

	/**
	 * Constructor.
	 * 
	 * @param input             object, which will be passed to the
	 *                          {@link ProposalDialog}
	 * @param cellLabelProvider {@link CellLabelProvider} object, which is attached
	 *                          to the viewer.
	 */
	public DirectInputProposalConfigurator(T input, CellLabelProvider cellLabelProvider) {
		this.inputMono = Flux.just(input);
		this.cellLabelProvider = cellLabelProvider;
	}

	/**
	 * Constructor.
	 * 
	 * @param input             object, which will be passed to the
	 *                          {@link ProposalDialog}
	 * @param cellLabelProvider {@link CellLabelProvider} object, which is attached
	 *                          to the viewer.
	 */
	public DirectInputProposalConfigurator(Flux<T> input, CellLabelProvider cellLabelProvider) {
		this.inputMono = input;
		this.cellLabelProvider = cellLabelProvider;
	}

	@Override
	public void configureViewer(Viewer viewer) {
		if (viewer instanceof TableViewer) {
			TableViewer tableViewer = (TableViewer) viewer;
			TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.LEFT);
			column.getColumn().setWidth(300);
			column.setLabelProvider(cellLabelProvider);
		}
	}

	@Override
	public Flux<T> getInput(String filterContent) {
		if(cellLabelProvider instanceof ILabelProvider && filterContent != null) {
			return inputMono.filter(element -> ((ILabelProvider) cellLabelProvider).getText(element).toLowerCase().startsWith(filterContent.toLowerCase()));
		}
		return inputMono;
	}
}
