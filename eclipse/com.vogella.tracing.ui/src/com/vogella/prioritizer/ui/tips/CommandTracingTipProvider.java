package com.vogella.prioritizer.ui.tips;

import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.tips.core.Tip;
import org.eclipse.tips.core.TipImage;
import org.eclipse.tips.core.TipProvider;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.vogella.tips.ShortcutTip;

@SuppressWarnings("restriction")
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

		ECommandService commandService = PlatformUI.getWorkbench().getService(ECommandService.class);
		EHandlerService handlerService = PlatformUI.getWorkbench().getService(EHandlerService.class);
		UISynchronize uiSync = PlatformUI.getWorkbench().getService(UISynchronize.class);
		ArrayList<Tip> tips = new ArrayList<>();
		tips.add(new CommandInvocationGraphTip(getID()));
		tips.add(new CommandInvocationShortCutTip(getID()));
		tips.add(new MenuDepthGraphTip(getID()));
		tips.add(new ShortcutTip(getID(), "Refresh", "F5", commandService, handlerService, uiSync));
		tips.add(new ShortcutTip(getID(), "Properties", "ALT + ENTER", commandService, handlerService, uiSync));
		tips.add(new ShortcutTip(getID(), "Save", "CTRL + S", commandService, handlerService, uiSync));
		tips.add(new ShortcutTip(getID(), "Save All", "CTRL + SHIFT + S", commandService, handlerService, uiSync));

		setTips(tips);

		return Status.OK_STATUS;
	}

	@Override
	public void dispose() {
	}
}
