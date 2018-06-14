package com.vogella.prioritizer.ui.tips;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.IPresentationEngine;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.tips.core.Tip;
import org.eclipse.tips.ui.ISwtTip;
import org.eclipse.ui.PlatformUI;

public class CommandInvocationShortCutTip extends Tip implements ISwtTip {

	public CommandInvocationShortCutTip(String providerId) {
		super(providerId);
	}

	@Override
	public void createControl(Composite parent) {
		IEclipseContext eclipseContext = PlatformUI.getWorkbench().getService(IEclipseContext.class);
		IPresentationEngine presentationEngine = eclipseContext.get(IPresentationEngine.class);
		
		EPartService partService = eclipseContext.get(EPartService.class);
		MPart statsPart = partService.createPart("com.vogella.tracing.ui.partdescriptor.stats");
		
		presentationEngine.createGui(statsPart, parent, eclipseContext);
	}

	@Override
	public Date getCreationDate() {
		return Date.from(LocalDate.of(2018, 6, 7).atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	@Override
	public String getSubject() {
		return "Recently used Commands and it's shortcuts";
	}

}
