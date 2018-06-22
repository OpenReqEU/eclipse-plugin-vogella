package com.vogella.tips;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.tips.core.IHtmlTip;
import org.eclipse.tips.core.Tip;
import org.eclipse.tips.core.TipAction;
import org.eclipse.tips.core.TipImage;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("restriction")
public class ShortcutTip extends Tip implements IHtmlTip {

	private static final Logger LOG = LoggerFactory.getLogger(ShortcutTip.class);

	private String commandName;
	private String shortcut;

	public ShortcutTip(String providerId, String commandName, String shortcut, ECommandService commandService,
			EHandlerService handlerService, UISynchronize uiSync) {
		super(providerId);
		this.commandName = commandName;
		this.shortcut = shortcut;

		TipAction openPreferencesAction = new TipAction("Preferences", "Show shortcuts in preferences", () -> {
			ParameterizedCommand command = commandService.createCommand("org.eclipse.ui.window.preferences",
					Collections.singletonMap("preferencePageId", "org.eclipse.ui.preferencePages.Keys"));
			uiSync.asyncExec(() -> {
				handlerService.executeHandler(command);
			});
		}, null);
		TipAction reportBugTipAction = new TipAction("Report Bug", "Create a bug report in InnoSensr.",
				() -> System.out.println("Reporting bug"), null);

		getActions().add(reportBugTipAction);
		getActions().add(openPreferencesAction);
	}

	@Override
	public Date getCreationDate() {
		return Date.from(LocalDate.of(2018, 6, 21).atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	@Override
	public String getSubject() {
		return "Using shortcuts instead of menus";
	}

	@Override
	public String getHTML() {
		return "<html><head><title>Shortcut advice</title></head><body><p>The <i>" + commandName
				+ "</i> command has been used quite often.</p><p>Did you know about the <i>" + shortcut
				+ "</i> shortcut to invoke this command.</p><p>Shortcuts allow you to invoke commands faster and be more efficient.</p></body></html>";
	}

	@Override
	public TipImage getImage() {
		Bundle bundle = FrameworkUtil.getBundle(getClass());
		try {
			return new TipImage(bundle.getEntry("icons/48/keyboard.png"));
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

}
