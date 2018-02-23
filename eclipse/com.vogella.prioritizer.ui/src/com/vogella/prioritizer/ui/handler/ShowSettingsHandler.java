
package com.vogella.prioritizer.ui.handler;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;

import com.vogella.prioritizer.core.events.Events;
import com.vogella.prioritizer.ui.parts.ViewType;

public class ShowSettingsHandler {
	@Execute
	public void execute(IEventBroker eventBroker, @Named("PART_ID") String partId,
			@Named("PAGE_TYPE") String pageType) {
		eventBroker.post(Events.TOGGLE_VIEW.substring(0, Events.TOGGLE_VIEW.length() - 1) + partId,
				ViewType.valueOf(pageType));
	}

}