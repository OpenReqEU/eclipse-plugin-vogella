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

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;

/**
 * This is wrapper for widgets in order to have a common API for accessing data
 * from a certain {@link Control}.
 * 
 * @author Simon Scholz
 *
 */
public interface IControlAdapter {

	public int getCaretOffset();

	/**
	 * Returns the wrapped control.
	 * 
	 * @return {@link Control}
	 */
	public Control getControl();

	/**
	 * Indicated whether the control's content is empty or not.
	 * 
	 * @return <code>true</code> if control's content is empty and otherwise
	 *         <code>false</code>
	 */
	public boolean isEmpty();

	/**
	 * Returns the content of the Widget.
	 * 
	 * @return {@link String}
	 */
	public String getContent();

	/**
	 * Sets the content of the Widget
	 * 
	 * @param content
	 *            {@link String}
	 */
	public void setContent(String content);

	/**
	 * Get the bounds where a pop for the given widget can be placed.
	 * 
	 * @return {@link Rectangle}
	 */
	public Rectangle getInsertionBounds();
}
