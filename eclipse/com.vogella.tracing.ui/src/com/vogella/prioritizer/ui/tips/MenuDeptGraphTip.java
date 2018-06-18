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

public class MenuDeptGraphTip extends Tip implements IUrlTip {

	public MenuDeptGraphTip(String providerId) {
		super(providerId);
	}

	@Override
	public String getURL() {
		Instant now = Instant.now();
		Duration duration = Duration.between(now.minus(7, ChronoUnit.DAYS), now);

		TimeZone timeZone = TimeZone.getDefault();

		return "http://localhost:7101/api/v1/graph?q=name,menu.dept,:eq,(,count,),:by&s=e-"
				+ duration.toMinutes() + "m&l=0&title=Command invocations of the last 7 days&tz=" + timeZone.getID();
	}

	@Override
	public Date getCreationDate() {
		return Date.from(LocalDate.of(2018, 6, 7).atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	@Override
	public String getSubject() {
		return "Compare the menu depts of actions invoked from a menu";
	}

}
