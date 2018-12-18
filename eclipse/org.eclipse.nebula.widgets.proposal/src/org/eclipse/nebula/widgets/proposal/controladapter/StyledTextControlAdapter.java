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

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;

public class StyledTextControlAdapter implements
		IControlAdapter {

	private StyledText styledText;

	public StyledTextControlAdapter(StyledText styledText) {
		this.styledText = styledText;
	}

	@Override
	public Control getControl() {
		return styledText;
	}

	@Override
	public String getContent() {
		return styledText.getText();
	}

	@Override
	public void setContent(String content) {
		styledText.setText(content);
	}

	@Override
	public Rectangle getInsertionBounds() {
		Point caretOrigin = styledText.getCaret().getLocation();
		// We fudge the y pixels due to problems with getCaretLocation
		// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=52520
		return new Rectangle(caretOrigin.x + styledText.getClientArea().x,
				caretOrigin.y + styledText.getClientArea().y + 3, 1,
				styledText.getLineHeight());
	}

	@Override
	public int getCaretOffset() {
		return styledText.getCaretOffset();
	}

	@Override
	public boolean isEmpty() {
		return getContent().isEmpty();
	}
}
