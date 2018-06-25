package com.vogella.common.ui.dialog;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class ReportModel {

	private final WritableValue<String> title = new WritableValue<>();
	
	private final WritableValue<String> description = new WritableValue<>();

	public String getTitle() {
		return title.getValue();
	}

	public void setTitle(String title) {
		this.title.setValue(title);
	}

	public String getDescription() {
		return description.getValue();
	}

	public void setDescription(String description) {
		this.description.setValue(description);
	}
	
	public IObservableValue<String> getTitleObservable() {
		return title;
	}

	public IObservableValue<String> getDescriptionObservable() {
		return description;
	}
}
