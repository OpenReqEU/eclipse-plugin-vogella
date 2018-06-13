package com.vogella.prioritizer.ui.tips;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.tips.core.Tip;
import org.eclipse.tips.ui.ISwtTip;

public class CommandTracingTip extends Tip implements ISwtTip {

	public CommandTracingTip(String providerId) {
		super(providerId);
	}

	@Override
	public void createControl(Composite parent) {

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
