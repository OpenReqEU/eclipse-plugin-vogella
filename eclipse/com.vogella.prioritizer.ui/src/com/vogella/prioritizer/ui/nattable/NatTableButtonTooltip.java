package com.vogella.prioritizer.ui.nattable;

import java.util.function.Function;
import java.util.function.Supplier;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.tooltip.NatTableContentTooltip;
import org.eclipse.swt.widgets.Event;

public class NatTableButtonTooltip extends NatTableContentTooltip {

	private Function<Integer, Boolean> isBugLikedFunction;

	public NatTableButtonTooltip(NatTable natTable, Function<Integer, Boolean> isBugLikedFunction,String... tooltipRegions) {
		super(natTable, tooltipRegions);
		this.isBugLikedFunction = isBugLikedFunction;
		setPopupDelay(300);
	}

	@Override
	protected String getText(Event event) {
		int col = this.natTable.getColumnPositionByX(event.x);
		switch (col) {
		case 6:
			return "Bug is not suitable";
		case 7:
			return "Snooze bug and show again later\n(See settings panel)";
		case 8:
			int rowPositionByY = natTable.getRowPositionByY(event.y);
			if(isBugLikedFunction.apply(Integer.valueOf(rowPositionByY))) {
				return "Unlike the bug";
			} else {
				return "Like the bug";
			}
		default:
			return super.getText(event); 
		}
	}
}
