package com.vogella.tracing.core.listener;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

public class SelectionDisplayFilter implements Listener {

	private static final Logger LOG = LoggerFactory.getLogger(SelectionDisplayFilter.class);

	private MeterRegistry meterRegistry;

	private Counter menuSelectionCounter;
	private Counter toolbarSelectionCounter;

	public SelectionDisplayFilter(MeterRegistry meterRegistry) {
		this.meterRegistry = meterRegistry;
		menuSelectionCounter = Counter.builder("selection.menu").tags("selection", "menu").register(meterRegistry);
		toolbarSelectionCounter = Counter.builder("selection.toolbar").tags("selection", "toolbar")
				.register(meterRegistry);
	}

	@Override
	public void handleEvent(Event event) {
		Widget widget = event.widget;

		if (widget instanceof MenuItem) {
			MenuItem menuItem = (MenuItem) widget;
			menuSelectionCounter.increment();

			int menuDepth = getMenuDepth(menuItem.getParent(), 0);
			Counter menuDepthCounter = meterRegistry.counter("selection.menu", "menuText", menuItem.getText(),
					"menuDepth", String.valueOf(menuDepth));
			menuDepthCounter.increment();
			LOG.debug(menuItem.getText() + " has a depth of " + menuDepth);
		} else if (widget instanceof ToolItem) {
			toolbarSelectionCounter.increment();
		}
	}

	private int getMenuDepth(Menu parentMenu, int i) {
		if (parentMenu.getParentMenu() != null) {
			getMenuDepth(parentMenu.getParentMenu(), ++i);
		}

		return i;
	}
}
