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
package org.eclipse.nebula.widgets.suggestbox.canvas;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.nebula.widgets.suggestbox.canvas.listener.HoverListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class LinkSuggestBoxCanvas extends SuggestBoxCanvas {

	private ArrayList<Listener> listeners = new ArrayList<>();

	private String hyperlinkText;

	private HyperlinkListener hyperlinkListener;
	private HoverListener hoverListener;

	public LinkSuggestBoxCanvas(Composite parent, int style, Object input, ILabelProvider labelProvider,
			String hyperlinkText) {
		super(parent, style, input, labelProvider);
		this.hyperlinkText = hyperlinkText;

		addListener();
	}

	@Override
	public void addListener(int eventType, Listener listener) {
		if (SWT.Selection == eventType) {
			this.listeners.add(listener);
			return;
		}

		super.addListener(eventType, listener);
		addDisposeListener(d -> {
			removeListener(SWT.MouseDown, hyperlinkListener);
			removeMouseMoveListener(hoverListener);
			removeMouseTrackListener(hoverListener);
		});
	}

	protected void addListener() {
		hyperlinkListener = new HyperlinkListener();
		hoverListener = new HoverListener(this::getRelativePosition);
		addListener(SWT.MouseDown, hyperlinkListener);
		addMouseMoveListener(hoverListener);
		addMouseTrackListener(hoverListener);
	}

	@Override
	protected Point getAfterTextSize(GC gc) {
		Point hyperlinkTextSize = gc.textExtent(hyperlinkText);

		return hyperlinkTextSize;
	}

	@Override
	protected void drawAfterText(GC gc, Point position) {
		gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLUE));
		gc.drawText(hyperlinkText, position.x, position.y, true);
	}

	private class HyperlinkListener implements Listener {

		@Override
		public void handleEvent(Event event) {
			if (hoverListener.isHover()) {
				for (Listener listener : listeners) {
					listener.handleEvent(event);
				}
			}
		}
	}

}
