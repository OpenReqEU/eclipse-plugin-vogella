package com.vogella.prioritizer.ui.nattable;

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

public class CommandStatsHeaderDataProvider implements IDataProvider {

	@Override
	public Object getDataValue(int columnIndex, int rowIndex) {
		switch (columnIndex) {
		case 0:
			return "Command id";
		case 1:
			return "Command name";
		case 2:
			return "Invocations";
		case 3:
			return "Keybinding";
		}
		return "";
	}

	@Override
	public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
		throw new UnsupportedOperationException("Setting data values to the header is not supported");
	}

	@Override
	public int getColumnCount() {
		return 4;
	}

	@Override
	public int getRowCount() {
		return 1;
	}

}
