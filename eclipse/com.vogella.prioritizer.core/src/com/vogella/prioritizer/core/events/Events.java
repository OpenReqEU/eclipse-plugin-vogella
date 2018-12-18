package com.vogella.prioritizer.core.events;

public interface Events {
	static final String TOGGLE_VIEW = "vogella/prioritizer/view/toggle/*";
	static final String REFRESH_PRIORITIZER = "vogella/prioritizer/view/refresh";
	static final String REFRESH_MDB = "vogella/mdb/view/refresh";
	static final String TOGGLE_VIEW_PRIORITIZERPART = "vogella/prioritizer/view/toggle/PrioritizerPart";
	static final String TOGGLE_VIEW_MOSTDISCUSSEDBUGSOFTHEMONTHPART = "vogella/prioritizer/view/toggle/MostDiscussedBugsOfTheMonthPart";
}
