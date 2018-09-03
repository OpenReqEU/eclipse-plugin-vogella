package com.vogella.tracing.ui.tips;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.IPresentationEngine;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.tips.core.Tip;
import org.eclipse.tips.core.TipAction;
import org.eclipse.tips.core.TipImage;
import org.eclipse.tips.ui.ISwtTip;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vogella.common.ui.dialog.ReportDialog;
import com.vogella.common.ui.dialog.ReportModel;
import com.vogella.services.InnoSensrService;
import com.vogella.services.InnoSensrStatus;
import com.vogella.tracing.ui.domain.CommandStats;
import com.vogella.tracing.ui.parts.CommandStatsPart;

import reactor.core.scheduler.Schedulers;

public class CommandInvocationShortCutTip extends Tip implements ISwtTip {

	private static final Logger LOG = LoggerFactory.getLogger(CommandInvocationShortCutTip.class);
	private InnoSensrService innoSensrService;
	private MPart statsPart;

	public CommandInvocationShortCutTip(String providerId, InnoSensrService innoSensrService, UISynchronize uiSync) {
		super(providerId);
		this.innoSensrService = innoSensrService;
		Bundle bundle = FrameworkUtil.getBundle(getClass());
		try {
			TipImage tipImage = new TipImage(bundle.getEntry("icons/16/innosensr-logo.png"));
			TipAction reportBugTipAction = new TipAction("Report Bug", "Create a bug report in OpenReq Live",
					() -> uiSync.asyncExec(this::reportBug), tipImage);
			getActions().add(reportBugTipAction);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private void reportBug() {
		Shell shell = Display.getDefault().getActiveShell();
		ReportModel reportModel = new ReportModel();
		Object object = statsPart.getContext().get(CommandStatsPart.COMMAND_STATS_SELECTION);
		if (object instanceof List<?>) {
			List<CommandStats> commandStats = (List<CommandStats>) object;
			if (commandStats.size() > 0) {
				CommandStats commandStat = commandStats.get(0);
				if (commandStat.getKeybinding() == null
						|| CommandStats.NO_KEYBINDING_DEFINED.equals(commandStat.getKeybinding())) {
					reportModel.setTitle("No default shortcut for the " + commandStat.getCommandName() + " command");
					reportModel
							.setDescription("The " + commandStat.getCommandName() + " should have a default shortcut.");
				} else {
					reportModel.setTitle("Problem with " + commandStat.getCommandName() + " command");
					reportModel.setDescription(
							"The " + commandStat.getCommandName() + " command has the following problems:\n\n*");
				}
				ReportDialog reportDialog = new ReportDialog(shell, reportModel, "Report bug in InnoSensr",
						"Open a new issue in InnoSensr.");
				if (Window.OK == reportDialog.open()) {
					LOG.debug("Sending bug report to InnoSensr.");
					innoSensrService
							.createRequirement("bLMk11Jc", reportModel.getTitle(), reportModel.getDescription(),
									InnoSensrStatus.NEW)
							.subscribeOn(Schedulers.elastic()).subscribe(System.out::println, e -> {
								Status errorStatus = new Status(IStatus.ERROR, "com.vogella.tracing.ui", e.getMessage(),
										e);
								ErrorDialog.openError(shell, "Error creating issue", e.getMessage(), errorStatus);
							});
				}
			} else {
				MessageDialog.openInformation(shell, "Info", "You have to select an entry to report a bug.");
			}
		} else {
			MessageDialog.openInformation(shell, "Info", "You have to select an entry to report a bug.");
		}
	}

	@Override
	public void createControl(Composite parent) {
		IEclipseContext eclipseContext = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getService(IEclipseContext.class);

		IPresentationEngine presentationEngine = eclipseContext.get(IPresentationEngine.class);

		EPartService partService = eclipseContext.get(EPartService.class);
		statsPart = partService.createPart("com.vogella.tracing.ui.partdescriptor.stats");

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
