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


import org.eclipse.nebula.widgets.suggestbox.listener.SuggestBoxListener;
import org.eclipse.swt.widgets.Composite;

/**
 * Implementations of a {@link SuggestBoxEntry} can be added to the
 * {@link SuggestBox} widget.
 * 
 * @see ClosableSuggestBoxEntry
 * @see SuggestBox
 * 
 * @author Simon Scholz
 *
 * @param <T>
 *            type of the object, which is represented by this
 *            {@link SuggestBoxEntry}
 */
public interface SuggestBoxEntry<T> {

	/**
	 * Get the (model) object, which is represented by this
	 * {@link SuggestBoxEntry}
	 * 
	 * @return (model) object behind this {@link SuggestBoxEntry}.
	 */
	public T getInput();

	/**
	 * This method renders the {@link SuggestBoxEntry}.
	 * 
	 * @param parent
	 *            {@link Composite}
	 */
	public void create(Composite parent);

	/**
	 * Add listener to the underlying widget.
	 * 
	 * @param eventType
	 *            SWT event listener type
	 * @param listener
	 *            {@link SuggestBoxListener}
	 */
	public void addListener(int eventType, SuggestBoxListener<T> listener);

	/**
	 * Removes listener from the underlying widget.
	 * 
	 * @param eventType
	 *            SWT event listener type
	 * @param listener
	 *            {@link SuggestBoxListener}
	 */
	public void removeListener(int eventType, SuggestBoxListener<T> listener);

	/**
	 * Disposes the {@link SuggestBoxEntry}.
	 */
	public void dispose();
}
