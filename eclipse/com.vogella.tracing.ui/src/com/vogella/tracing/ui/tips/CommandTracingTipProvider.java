package com.vogella.tracing.ui.tips;

import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.tips.core.Tip;
import org.eclipse.tips.core.TipImage;
import org.eclipse.tips.core.TipProvider;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.vogella.common.core.domain.CommandStats;
import com.vogella.common.core.service.CommandStatsPersistenceService;
import com.vogella.services.innosensr.InnoSensrService;
import com.vogella.tips.ShortcutTip;

public class CommandTracingTipProvider extends TipProvider {

	private TipImage tipImage;

	@Override
	public String getDescription() {
		return "Provider for tips based on user interaction with the Eclipse IDE";
	}

	@Override
	public String getID() {
		return getClass().getName();
	}

	@Override
	public TipImage getImage() {
		if (tipImage == null) {
			Bundle bundle = FrameworkUtil.getBundle(getClass());

			try {
				tipImage = new TipImage(bundle.getEntry("icons/48/openreqlogo.png"));
			} catch (IOException ex) {
				getManager().log(new Status(IStatus.ERROR, bundle.getSymbolicName(), ex.getMessage(), ex));
			}
		}
		return tipImage;
	}

	@Override
	public IStatus loadNewTips(IProgressMonitor monitor) {
		CommandStatsPersistenceService persistenceService = PlatformUI.getWorkbench().getService(CommandStatsPersistenceService.class);
		UISynchronize uiSync = PlatformUI.getWorkbench().getService(UISynchronize.class);
		InnoSensrService innoSensrService = PlatformUI.getWorkbench().getService(InnoSensrService.class);
		ArrayList<Tip> tips = new ArrayList<>();
		tips.add(new CommandInvocationShortCutTip(getID(), innoSensrService, uiSync));

		for (CommandStats commandStats : persistenceService.get()) {
			double invocations = commandStats.getInvocations();
			if (invocations > 3) {
				tips.add(new ShortcutTip(getID(), commandStats.getCommandName(), commandStats.getKeybinding(),
						uiSync, innoSensrService));
			}
		}

		setTips(tips);

		return Status.OK_STATUS;
	}

	@Override
	public void dispose() {
	}
}
