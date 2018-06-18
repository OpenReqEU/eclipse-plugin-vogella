package com.vogella.prioritizer.ui.tips;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.TimeZone;

import org.eclipse.tips.core.IUrlTip;
import org.eclipse.tips.core.Tip;
import org.eclipse.tips.core.TipAction;

public class CommandInvocationGraphTip extends Tip implements IUrlTip {

	public CommandInvocationGraphTip(String providerId) {
		super(providerId);
		TipAction reportBugTipAction = new TipAction("Report bug", "Create a bug report in InnoSensr.",
				() -> System.out.println("Reporting bug"), null);
		getActions().add(reportBugTipAction);
	}

	@Override
	public String getURL() {
		Instant now = Instant.now();
		Duration duration = Duration.between(now.minus(7, ChronoUnit.DAYS), now);

		TimeZone timeZone = TimeZone.getDefault();

		return "http://localhost:7101/api/v1/graph?q=name,command.calls,:eq,(,commandId,),:by&s=e-"
				+ duration.toMinutes() + "m&l=0&title=Command invocations of the last 7 days&tz=" + timeZone.getID();
	}

	@Override
	public Date getCreationDate() {
		return Date.from(LocalDate.of(2018, 6, 7).atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	@Override
	public String getSubject() {
		return "Show the Command invocation graph";
	}

}
