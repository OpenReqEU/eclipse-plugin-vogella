package com.vogella.tracing.core.listener;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

public class SelectionDisplayFilter implements Listener {

	private Counter menuSelectionCounter;
	private Counter toolbarSelectionCounter;

	public SelectionDisplayFilter(MeterRegistry meterRegistry) {
		menuSelectionCounter = Counter.builder("selection.menu").tags("selection", "menu").register(meterRegistry);
		toolbarSelectionCounter = Counter.builder("selection.toolbar").tags("selection", "toolbar")
				.register(meterRegistry);
	}

	@Override
	public void handleEvent(Event event) {
		Widget widget = event.widget;

		if (widget instanceof MenuItem) {
			menuSelectionCounter.increment();
		} else if (widget instanceof ToolItem) {
			toolbarSelectionCounter.increment();
		}
	}
}
