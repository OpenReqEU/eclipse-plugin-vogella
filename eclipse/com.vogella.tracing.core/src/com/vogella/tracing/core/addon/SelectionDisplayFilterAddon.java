
package com.vogella.tracing.core.addon;

import java.io.IOException;

import javax.inject.Inject;

import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.bindings.EBindingService;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.osgi.service.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vogella.common.core.service.CommandStatsPersistenceService;
import com.vogella.tracing.core.listener.SelectionDisplayFilter;

@SuppressWarnings("restriction")
public class SelectionDisplayFilterAddon {

	private static final Logger LOG = LoggerFactory.getLogger(SelectionDisplayFilterAddon.class);

	private Listener selectionListener;
	private CommandStatsPersistenceService persistenceService;
	private ECommandService commandService;
	private EBindingService bindingService;
	private MApplication app;

	@Inject
	public SelectionDisplayFilterAddon(ECommandService commandService, EBindingService bindingService,
			CommandStatsPersistenceService persistenceService, MApplication app) {
		this.commandService = commandService;
		this.bindingService = bindingService;
		this.persistenceService = persistenceService;
		this.app = app;
	}

	@Inject
	public void initSelectionDisplayFilter(Display display) {
		selectionListener = new SelectionDisplayFilter(persistenceService, commandService, bindingService, app);
		display.addFilter(SWT.Selection, selectionListener);
	}

	@Inject
	@Optional
	public void dispose(@UIEventTopic(UIEvents.UILifeCycle.APP_SHUTDOWN_STARTED) Event event, Display display) {
		try {
			persistenceService.persist();
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		if (display != null && !display.isDisposed()) {
			display.removeFilter(SWT.Selection, selectionListener);
		}
	}
}
