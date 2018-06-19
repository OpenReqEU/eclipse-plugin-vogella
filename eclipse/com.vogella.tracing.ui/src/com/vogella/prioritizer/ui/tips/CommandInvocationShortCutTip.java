package com.vogella.prioritizer.ui.tips;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.IPresentationEngine;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.tips.core.Tip;
import org.eclipse.tips.core.TipAction;
import org.eclipse.tips.core.TipImage;
import org.eclipse.tips.ui.ISwtTip;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class CommandInvocationShortCutTip extends Tip implements ISwtTip {

	public CommandInvocationShortCutTip(String providerId) {
		super(providerId);
		Bundle bundle = FrameworkUtil.getBundle(getClass());
		try {
			TipImage tipImage = new TipImage(bundle.getEntry("icons/16/innosensr-logo.png"));
			TipAction reportBugTipAction = new TipAction("Report Bug", "Create a bug report in InnoSensr.",
					() -> System.out.println("Reporting bug"), tipImage);
			getActions().add(reportBugTipAction);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void createControl(Composite parent) {
		IEclipseContext eclipseContext = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getService(IEclipseContext.class);

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
