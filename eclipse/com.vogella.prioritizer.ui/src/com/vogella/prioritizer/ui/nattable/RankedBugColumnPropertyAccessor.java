package com.vogella.prioritizer.ui.nattable;

import java.util.Arrays;
import java.util.List;

import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;

import com.vogella.prioritizer.core.model.RankedBug;

public class RankedBugColumnPropertyAccessor implements IColumnPropertyAccessor<RankedBug> {

	private static final List<String> propertyNames = Arrays.asList("id", "summary", "userpriority", "platform",
			"component");
	private double max;
	private double min;

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
			return normalize(min, max, bug.getPriority());
		case 3:
			return bug.getProduct();
		case 4:
			return bug.getComponent();
		}

		return bug;
	}

	private static double normalize(double min, double max, double sum) {
		return (sum - min) / (max - min);
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

	public void setMinAndMax(double min, double max) {
		this.min = min;
		this.max = max;
	}
}
