package com.vogella.prioritizer.ui.nattable;

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

public class RankedBugHeaderDataProvider implements IDataProvider {

	@Override
	public Object getDataValue(int columnIndex, int rowIndex) {
		switch (columnIndex) {
		case 0:
			return "Id";
		case 1:
			return "Summary";
		case 2:
			return "Priority";
		case 3:
			return "Product";
		case 4:
			return "Component";
		case 5:
			return "Creation Date";
		case 6:
			return "Dislike";
		case 7:
			return "Snooze";
		case 8:
			return "Like";
		default:
			return "";
		}
	}

	@Override
	public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
		throw new UnsupportedOperationException("Setting data values to the header is not supported");
	}

	@Override
	public int getColumnCount() {
		return 9;
	}

	@Override
	public int getRowCount() {
		return 1;
	}

}
