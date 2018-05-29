
package com.vogella.tracing.core.addon;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;

import com.vogella.tracing.core.listener.SelectionDisplayFilter;

import io.micrometer.core.instrument.MeterRegistry;

public class SelectionDisplayFilterAddon {

	private Listener selectionListener;

	@Inject
	public void initSelectionDisplayFilter(Display display, MeterRegistry meterRegistry) {
		selectionListener = new SelectionDisplayFilter(meterRegistry);
		display.addFilter(SWT.Selection, selectionListener);
	}

	@PreDestroy
	public void dispose(Display display) {
		if (display != null && display.isDisposed()) {
			display.removeFilter(SWT.Selection, selectionListener);
		}
	}
}
