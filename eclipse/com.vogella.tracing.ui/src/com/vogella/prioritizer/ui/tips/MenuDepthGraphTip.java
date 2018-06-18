package com.vogella.prioritizer.ui.tips;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.TimeZone;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.tips.core.IUrlTip;
import org.eclipse.tips.core.Tip;
import org.eclipse.tips.core.TipAction;
import org.eclipse.tips.core.TipImage;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class MenuDepthGraphTip extends Tip implements IUrlTip {

	public MenuDepthGraphTip(String providerId) {
		super(providerId);
		Bundle bundle = FrameworkUtil.getBundle(getClass());
		try {
			TipImage tipImage = new TipImage(bundle.getEntry("icons/16/innosensr-logo.png"));
			TipAction reportBugTipAction = new TipAction("Report bug", "Create a bug report in InnoSensr.",
					() -> System.out.println("Reporting bug"), tipImage);
			getActions().add(reportBugTipAction);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getURL() {
		Instant now = Instant.now();
		Duration duration = Duration.between(now.minus(7, ChronoUnit.DAYS), now);

		TimeZone timeZone = TimeZone.getDefault();

		return "http://localhost:7101/api/v1/graph?q=name,menu.dept,:eq,(,count,),:by&s=e-" + duration.toMinutes()
				+ "m&l=0&title=Menu depths of the last 7 days&tz=" + timeZone.getID();
	}

	@Override
	public Date getCreationDate() {
		return Date.from(LocalDate.of(2018, 6, 7).atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	@Override
	public String getSubject() {
		return "Compare the menu depths of actions invoked from a menu";
	}

}
