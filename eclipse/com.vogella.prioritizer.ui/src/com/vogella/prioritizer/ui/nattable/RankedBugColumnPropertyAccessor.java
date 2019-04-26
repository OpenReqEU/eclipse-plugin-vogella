package com.vogella.prioritizer.ui.nattable;

import java.util.Arrays;
import java.util.List;

import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;

import com.vogella.prioritizer.core.model.RankedBug;

public class RankedBugColumnPropertyAccessor implements IColumnPropertyAccessor<RankedBug> {

	private static final List<String> propertyNames = Arrays.asList("id", "summary", "userpriority", "platform",
			"component", "created", "Not suitable", "Not now", "I like it");

	@Override
	public int getColumnCount() {
		return propertyNames.size();
	}

	@Override
	public Object getDataValue(RankedBug bug, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return bug.getId();
		case 1:
			return bug.getSummary();
		case 2:
			return bug.getPriority() / 100;
		case 3:
			return bug.getProduct();
		case 4:
			return bug.getComponent();
		case 5:
			return convertDate(bug.getCreationTime());
		case 6:
			return "Not suitable";
		case 7:
			return "Not now";
		case 8:
			return "I like it";
		}

		return bug;
	}

	private String convertDate(String date)  {
		if (date == null) {
			return "";
		}
		return date.substring(0, 10);
	}

	@Override
	public void setDataValue(RankedBug rowObject, int columnIndex, Object newValue) {
		// no editing necessary
	}

	@Override
	public String getColumnProperty(int columnIndex) {
		return propertyNames.get(columnIndex);
	}

	@Override
	public int getColumnIndex(String propertyName) {
		return propertyNames.indexOf(propertyName);
	}

}
