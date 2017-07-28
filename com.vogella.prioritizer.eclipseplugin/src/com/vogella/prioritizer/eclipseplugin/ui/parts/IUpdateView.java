package com.vogella.prioritizer.eclipseplugin.ui.parts;

import java.util.List;

import com.vogella.spring.data.entities.RankedBug;

public interface IUpdateView {
	void updateView(List<RankedBug> bugs);
	void setError();
}
