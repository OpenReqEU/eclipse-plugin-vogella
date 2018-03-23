 
package com.vogella.prioritizer.ui.handler;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;

import com.vogella.prioritizer.core.events.Events;

public class RefreshHandler {
	@Execute
	public void execute(IEventBroker eventBroker) {
		eventBroker.post(Events.REFRESH, true);
	}
		
}