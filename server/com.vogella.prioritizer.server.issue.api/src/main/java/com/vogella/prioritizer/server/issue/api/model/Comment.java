package com.vogella.prioritizer.server.issue.api.model;

import java.util.Date;
import java.util.List;

public interface Comment {
	Date getTime();

	String getText();

	long getBugId();

	long getCount();

	long getAttachmentId();

	boolean isPrivate();

	boolean isMarkdown();

	List<String> getTags();

	String getCreator();

	Date getCreationTime();

	long getId();
}
