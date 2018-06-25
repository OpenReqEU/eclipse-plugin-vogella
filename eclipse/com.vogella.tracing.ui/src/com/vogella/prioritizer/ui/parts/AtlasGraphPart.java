
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

public class AtlasGraphPart {

	private Browser browserCommandCalls;

	private Browser browserMenuDepths;

	@PostConstruct
	public void postConstruct(Composite parent) {
		browserCommandCalls = new Browser(parent, SWT.NONE);
		browserMenuDepths = new Browser(parent, SWT.NONE);
		Instant oneWeekBefore = ZonedDateTime.now(ZoneOffset.UTC).minusMinutes(20).toInstant();
		updateGraphs(oneWeekBefore);
	}

	@Inject
	@Optional
	public void updateGraphs(@UIEventTopic("INSTANT") Instant instant) {
		Instant now = Instant.now();
		Duration duration = Duration.between(instant, now);
		
		TimeZone timeZone = TimeZone.getDefault();
		
		browserCommandCalls.setUrl("http://localhost:7101/api/v1/graph?q=name,command.calls,:eq,(,commandId,commandName,),:by&s=e-"
				+ duration.toMinutes() + "m&l=0&title=Recent command invocations&tz=" + timeZone.getID());
		
		browserMenuDepths.setUrl("http://localhost:7101/api/v1/graph?q=name,selection.menu,:eq,(,menuText,menuDepth,),:by&s=e-"
				+ duration.toMinutes() + "m&l=0&title=Recent command invocations&tz=" + timeZone.getID());
	}

}