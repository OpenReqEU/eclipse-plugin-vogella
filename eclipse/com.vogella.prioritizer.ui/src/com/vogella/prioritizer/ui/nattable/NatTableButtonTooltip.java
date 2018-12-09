package com.vogella.prioritizer.ui.nattable;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.tooltip.NatTableContentTooltip;
import org.eclipse.swt.widgets.Event;

public class NatTableButtonTooltip extends NatTableContentTooltip {

	public NatTableButtonTooltip(NatTable natTable, String... tooltipRegions) {
		super(natTable, tooltipRegions);
		setPopupDelay(300);
	}

	@Override
	protected String getText(Event event) {
		int col = this.natTable.getColumnPositionByX(event.x);
		switch (col) {
		case 5:
			return "Bug is not suitable";
		case 6:
			return "Snooze bug aka Ask me later\n(See settings panel)";
		default:
			return super.getText(event); 
		}
	}
}
