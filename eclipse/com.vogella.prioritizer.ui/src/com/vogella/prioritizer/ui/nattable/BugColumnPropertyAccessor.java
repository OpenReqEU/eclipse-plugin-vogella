package com.vogella.prioritizer.ui.nattable;

import java.util.Arrays;
import java.util.List;

import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;

import com.vogella.prioritizer.core.model.Bug;

public class BugColumnPropertyAccessor implements IColumnPropertyAccessor<Bug> {

	private static final List<String> propertyNames = Arrays.asList("id", "summary", "platform", "component");

	@Override
	public int getColumnCount() {
		return propertyNames.size();
	}

	@Override
	public Object getDataValue(Bug bug, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return bug.getId();
		case 1:
			return bug.getSummary();
		case 2:
			return bug.getProduct();
		case 3:
			return bug.getComponent();
		}

		return bug;
	}

	@Override
	public void setDataValue(Bug rowObject, int columnIndex, Object newValue) {
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
