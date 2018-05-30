
package com.vogella.prioritizer.ui.parts;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;

public class CommandCallsPart {

	private Browser browser;

	@PostConstruct
	public void postConstruct(Composite parent) {
		browser = new Browser(parent, SWT.NONE);
		Instant oneWeekBefore = ZonedDateTime.now(ZoneOffset.UTC).minusMinutes(20).toInstant();
		updateGraph(oneWeekBefore);
	}

	@Inject
	@Optional
	public void updateGraph(@UIEventTopic("INSTANT") Instant instant) {
		Instant now = Instant.now();
		Duration duration = Duration.between(instant, now);
		
		TimeZone timeZone = TimeZone.getDefault();
		
		browser.setUrl("http://localhost:7101/api/v1/graph?q=name,command.calls,:eq,(,commandId,),:by&s=e-"
				+ duration.toMinutes() + "m&l=0&tz=" + timeZone.getID());
	}

}