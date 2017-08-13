package com.vogella.prioritizer.eclipseplugin;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.vogella.prioritizer.eclipseplugin.views.PrioritizerWorkbenchView;

public class RefreshHandler extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow activeWorkbenchWindow = HandlerUtil.getActiveWorkbenchWindow(
				event);
		PrioritizerWorkbenchView findView = (PrioritizerWorkbenchView) activeWorkbenchWindow.getActivePage()
				.findView("com.vogella.prioritizer.eclipseplugin.views.WorkbenchView");
		findView.refresh();
		return null;
	}

}
