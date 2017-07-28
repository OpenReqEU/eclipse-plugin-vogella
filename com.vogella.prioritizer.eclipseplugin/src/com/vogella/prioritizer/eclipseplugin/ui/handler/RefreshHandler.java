package com.vogella.prioritizer.eclipseplugin.ui.handler;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import com.vogella.prioritizer.eclipseplugin.ui.parts.PrioritizerView;

public class RefreshHandler {

	@Execute
	public static void execute(EPartService partService) {
		MPart part = partService.findPart("com.vogella.prioritizer.prioritizerviewid");
		PrioritizerView prioritizerView = (PrioritizerView) part.getObject();
		prioritizerView.refresh();
	}
}
