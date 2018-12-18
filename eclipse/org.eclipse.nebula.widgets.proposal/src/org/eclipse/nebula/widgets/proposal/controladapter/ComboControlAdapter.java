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
package org.eclipse.nebula.widgets.proposal.controladapter;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;

public class ComboControlAdapter implements
		IControlAdapter {

	private Combo combo;

	public ComboControlAdapter(Combo combo) {
		this.combo = combo;
	}

	@Override
	public Control getControl() {
		return combo;
	}

	@Override
	public String getContent() {
		return combo.getText();
	}

	@Override
	public void setContent(String content) {
		combo.setText(content);
	}

	@Override
	public Rectangle getInsertionBounds() {
		// This doesn't take horizontal scrolling into affect.
		// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=204599
		int position = combo.getSelection().y;
		String contents = combo.getText();
		GC gc = new GC(combo);
		gc.setFont(combo.getFont());
		Point extent = gc.textExtent(contents.substring(0,
				Math.min(position, contents.length())));
		gc.dispose();

		return new Rectangle(extent.x, 0, 1, combo.getSize().y);
	}

	@Override
	public int getCaretOffset() {
		return combo.getCaretPosition();
	}

	@Override
	public boolean isEmpty() {
		return getContent().isEmpty();
	}

}
