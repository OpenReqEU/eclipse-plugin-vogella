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
import org.eclipse.nebula.widgets.suggestbox.canvas.LinkSuggestBoxCanvas;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class LinkSuggestBoxEntry<T> extends ClosableSuggestBoxEntryImpl<T> {

	private String hyperlinkText;

	public LinkSuggestBoxEntry(T input, ILabelProvider labelProvider, String hyperlinkText) {
		super(input, labelProvider);
		this.hyperlinkText = hyperlinkText;
	}

	@Override
	protected Canvas createCanvas(Composite parent, int style, Object input, ILabelProvider labelProvider) {
		return new LinkSuggestBoxCanvas(parent, style, input, labelProvider, hyperlinkText);
	}
}
