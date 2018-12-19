package org.eclipse.nebula.widgets.suggestbox;

import org.eclipse.nebula.widgets.suggestbox.listener.SuggestBoxListener;

public interface ClosableSuggestBoxEntry<T> {
	public void addCloseClickListener(SuggestBoxListener<T> listener);

	public void removeCloseClickListener(SuggestBoxListener<T> listener);
}
