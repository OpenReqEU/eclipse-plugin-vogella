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
package org.eclipse.nebula.widgets.suggestbox.canvas.listener;

import java.util.function.Supplier;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;

public class HoverListener extends MouseTrackAdapter implements MouseMoveListener {

	private Supplier<Point> relativePositionSupplier;

	private boolean hover;

	/**
	 * 
	 * @param canvas
	 * @param relativePositionSupplier , which is relative to the canvas bounds
	 */
	public HoverListener(Supplier<Point> relativePositionSupplier) {
		this.relativePositionSupplier = relativePositionSupplier;
	}

	@Override
	public void mouseExit(MouseEvent e) {
		if (isHover()) {
			Control source = (Control) e.getSource();
			source.setCursor(null);
			hover = false;
			source.redraw();
		}
	}

	@Override
	public void mouseMove(MouseEvent e) {
		Control source = (Control) e.getSource();
		Rectangle bounds = source.getBounds();
		// reset x bounds according to the e.x of this listener
		bounds.x = relativePositionSupplier.get().x + 4;
		bounds.width -= relativePositionSupplier.get().x + 4;
		if (bounds.contains(e.x, e.y)) {
			if (!isHover()) {
				hover = true;
				source.setCursor(new Cursor(source.getDisplay(), SWT.CURSOR_HAND));
				source.redraw();
			}
		} else {
			if (isHover()) {
				hover = false;
				source.setCursor(null);
				source.redraw();
			}
		}
	}

	public boolean isHover() {
		return hover;
	}

}
