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

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * This is wrapper for a {@link Text} widget in order to have a common API for
 * accessing data from a certain {@link Control}.
 * 
 * @author Simon Scholz
 *
 */
public class TextControlAdapter implements IControlAdapter {

	private Text text;

	/**
	 * Wrap a {@link Text} widget.
	 * 
	 * @param text {@link Text}
	 */
	public TextControlAdapter(Text text) {
		this.text = text;
	}

	@Override
	public Control getControl() {
		return text;
	}

	@Override
	public String getContent() {
		return text.getText();
	}

	@Override
	public void setContent(String content) {
		text.setText(content);
	}

	@Override
	public Rectangle getInsertionBounds() {
		Point caretOrigin = text.getCaretLocation();
		// We fudge the y pixels due to problems with getCaretLocation
		// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=52520
		return new Rectangle(caretOrigin.x + text.getClientArea().x, caretOrigin.y + text.getClientArea().y + 3, 1,
				text.getLineHeight());
	}

	@Override
	public int getCaretOffset() {
		return text.getCaretPosition();
	}

	@Override
	public boolean isEmpty() {
		return getContent().isEmpty();
	}

}
