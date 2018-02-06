package com.vogella.prioritizer.eclipseplugin;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.vogella.spring.data.entities.RankedBug;

public class BugFilter
		extends ViewerFilter {

	private String searchString;

	public void setSearchText(String s) {
		// ensure that the value can be used for matching
		this.searchString = ".*" + s + ".*";
		this.searchString = searchString.toLowerCase();
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (searchString == null || searchString.isEmpty()) {
			return true;
		}
		RankedBug rankedBug = (RankedBug) element;
		if (rankedBug.getAssignedTo().toLowerCase().matches(searchString)) {
			return true;
		}
		if (String.valueOf(rankedBug.getBugIdBugzilla()).matches(searchString)) {
			return true;
		}
		if (rankedBug.getComponent().toLowerCase().matches(searchString)) {
			return true;
		}
		if (rankedBug.getCreated().toLowerCase().matches(searchString)) {
			return true;
		}
		if (rankedBug.getLastChanged().toLowerCase().matches(searchString)) {
			return true;
		}
		if (rankedBug.getPriority().toLowerCase().matches(searchString)) {
			return true;
		}
		if (rankedBug.getReporter().toLowerCase().matches(searchString)) {
			return true;
		}
		if (rankedBug.getTitle().toLowerCase().matches(searchString)) {
			return true;
		}
		return false;
	}
}
