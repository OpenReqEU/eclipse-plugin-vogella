package com.vogella.tips;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.eclipse.tips.core.IHtmlTip;
import org.eclipse.tips.core.Tip;
import org.eclipse.tips.core.TipAction;
import org.eclipse.tips.core.TipImage;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShortcutTip extends Tip implements IHtmlTip {

	private static final Logger LOG = LoggerFactory.getLogger(ShortcutTip.class);

	private String commandName;
	private String shortcut;

	public ShortcutTip(String providerId, String commandName, String shortcut) {
		super(providerId);
		this.commandName = commandName;
		this.shortcut = shortcut;
		Bundle bundle = FrameworkUtil.getBundle(getClass());
		try {
			TipImage tipImage = new TipImage(bundle.getEntry("icons/16/innosensr-logo.png"));
			TipAction reportBugTipAction = new TipAction("Report Bug", "Create a bug report in InnoSensr.",
					() -> System.out.println("Reporting bug"), tipImage);
			getActions().add(reportBugTipAction);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
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
		return "<html><head><title>Shortcut advice</title></head><body><p>The " + commandName
				+ " command has been used quite often.</p><p>Did you know about the " + shortcut
				+ " shortcut to invoke this command.</p><p>Shortcuts allow you to invoke commands faster and be more efficient.</p></body></html>";
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
