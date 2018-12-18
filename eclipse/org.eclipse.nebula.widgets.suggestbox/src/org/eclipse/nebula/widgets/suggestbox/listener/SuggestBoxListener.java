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
package org.eclipse.nebula.widgets.suggestbox.listener;


import org.eclipse.nebula.widgets.suggestbox.SuggestBoxEntry;
import org.eclipse.swt.widgets.Event;

/**
 * This listener can be attached to to a {@link SuggestBoxEntry} in order to
 * listen to events of its underlying widget.
 * 
 * @see SuggestBoxEntry
 * @author Simon Scholz
 *
 */
@FunctionalInterface
public interface SuggestBoxListener<T> {

	/**
	 * Handles the event, which is fired by the {@link SuggestBoxEntry} 's
	 * underlying widget.
	 * 
	 * @param suggestBoxEntry
	 *            container of the widget, which fires this event
	 * @param event
	 *            swt event, which is fired by the widget of a
	 *            {@link SuggestBoxEntry}
	 */
	public void handleEvent(SuggestBoxEntry<T> suggestBoxEntry, Event event);
}
