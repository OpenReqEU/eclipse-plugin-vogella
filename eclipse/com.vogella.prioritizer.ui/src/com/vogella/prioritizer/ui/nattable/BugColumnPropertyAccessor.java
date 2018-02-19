package com.vogella.prioritizer.ui.nattable;

import java.util.Arrays;
import java.util.List;

import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;

import com.vogella.prioritizer.core.model.PriorityBug;

public class BugColumnPropertyAccessor implements IColumnPropertyAccessor<PriorityBug> {

	private static final List<String> propertyNames = Arrays.asList("id", "summary", "userpriority", "platform",
			"component");

	@Override
	public int getColumnCount() {
		return propertyNames.size();
	}

	@Override
	public Object getDataValue(PriorityBug bug, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return bug.getBug().getId();
		case 1:
			return bug.getBug().getSummary();
		case 2:
			return calcUserPrio(bug);
		case 3:
			return bug.getBug().getProduct();
		case 4:
			return bug.getBug().getComponent();
		}

		return bug;
	}

	private float calcUserPrio(PriorityBug priorityBug) {
		float sum = 0;

		sum += priorityBug.getGerritChangeCount() * 2.2f;
		sum += priorityBug.getCommentCount() * 1.9f;
		sum += priorityBug.getCcCount() * 1.7f;
		sum += priorityBug.getUserKeywordMatchCount() * 1.5f;
		sum += priorityBug.getBlockingIssuesCount() * 1.4f;

		return sum;
	}

	@Override
	public void setDataValue(PriorityBug rowObject, int columnIndex, Object newValue) {
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
