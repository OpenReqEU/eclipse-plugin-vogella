 
package com.vogella.prioritizer.ui.handler;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;

import com.vogella.prioritizer.core.events.Events;
import com.vogella.prioritizer.ui.parts.PrioritizerPart;

public class RefreshHandler {
	@Execute
	public void execute(IEventBroker eventBroker, @Named("com.vogella.prioritizer.ui.commandparameter.viewId") String viewId) {
		if("com.vogella.prioritizer.ui.partdescriptor.prioritizerpart".equals(viewId)) {
			eventBroker.post(Events.REFRESH_PRIORITIZER, true);
		} else if ("com.vogella.prioritizer.ui.partdescriptor.mostdiscussedbugs".equals(viewId)) {
			eventBroker.post(Events.REFRESH_MDB, true);
		}
	}
	
	@CanExecute
	public boolean canExecute(@Optional @Named(PrioritizerPart.ALL_DATA_SET) Boolean enabled) {
		if (enabled == null) {
			return false;
		}
		return enabled;
	}

}