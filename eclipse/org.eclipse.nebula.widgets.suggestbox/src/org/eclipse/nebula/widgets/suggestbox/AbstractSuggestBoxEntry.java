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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.nebula.widgets.suggestbox.listener.SuggestBoxListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public abstract class AbstractSuggestBoxEntry<T> implements SuggestBoxEntry<T> {

	private Map<SuggestBoxListener<T>, Listener> listenerList = new HashMap<SuggestBoxListener<T>, Listener>();

	private T input;
	private ILabelProvider labelProvider;

	public AbstractSuggestBoxEntry(T input, ILabelProvider labelProvider) {
		this.input = input;
		this.labelProvider = labelProvider;
	}

	@Override
	public T getInput() {
		return input;
	}

	public abstract Control getCreatedControl();

	@Override
	public void addListener(int eventType, final SuggestBoxListener<T> listener) {
		SuggestBoxEntryDelegatingListener suggestBoxEntryDelegatingListener = new SuggestBoxEntryDelegatingListener(
				listener);
		listenerList.put(listener, suggestBoxEntryDelegatingListener);
		getCreatedControl().addListener(eventType,
				suggestBoxEntryDelegatingListener);
	}

	@Override
	public void removeListener(int eventType, final SuggestBoxListener<T> listener) {
		Listener suggestBoxEntryDelegatingListener = listenerList.get(listener);
		getCreatedControl().removeListener(eventType,
				suggestBoxEntryDelegatingListener);
	}

	protected ILabelProvider getLabelProvider() {
		return labelProvider;
	}

	protected class SuggestBoxEntryDelegatingListener implements Listener {

		private SuggestBoxListener<T> listener;

		public SuggestBoxEntryDelegatingListener(SuggestBoxListener<T> listener) {
			this.listener = listener;
		}

		@Override
		public void handleEvent(Event event) {
			listener.handleEvent(AbstractSuggestBoxEntry.this, event);
		}
	}

}
