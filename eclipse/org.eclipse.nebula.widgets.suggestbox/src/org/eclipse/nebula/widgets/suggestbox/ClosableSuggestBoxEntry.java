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
package org.eclipse.nebula.widgets.suggestbox;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.nebula.widgets.suggestbox.canvas.ClosableSuggestBoxCanvas;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Default implementation of a {@link SuggestBoxEntry}, which internally creates
 * a {@link ClosableSuggestBoxCanvas} in order to render the given <T> object.
 * 
 * @author Simon Scholz
 *
 * @param <T> type of the object, which is represented by this
 *        {@link SuggestBoxEntry}
 */
public class ClosableSuggestBoxEntry<T> extends AbstractSuggestBoxEntry<T> {

	private Canvas suggestBoxCanvas;

	/**
	 * Constructs a {@link ClosableSuggestBoxEntry}, which uses a
	 * {@link ClosableSuggestBoxCanvas} to render the input.
	 * 
	 * @param input         object to be visualized
	 */
	public ClosableSuggestBoxEntry(T input) {
		super(input, new LabelProvider());
	}
	
	/**
	 * Constructs a {@link ClosableSuggestBoxEntry}, which uses a
	 * {@link ClosableSuggestBoxCanvas} to render the input.
	 * 
	 * @param input         object to be visualized
	 * @param labelProvider to determine the label and image of the input object
	 */
	public ClosableSuggestBoxEntry(T input, ILabelProvider labelProvider) {
		super(input, labelProvider);
	}

	public Control getCreatedControl() {
		return suggestBoxCanvas;
	}

	@Override
	public void create(Composite parent) {
		suggestBoxCanvas = createCanvas(parent, SWT.NONE, getInput(), getLabelProvider());
	}

	protected Canvas createCanvas(Composite parent, int style, Object input, ILabelProvider labelProvider) {
		return new ClosableSuggestBoxCanvas(parent, style, input, labelProvider);
	}

	@Override
	public void dispose() {
		suggestBoxCanvas.dispose();
	}
}
