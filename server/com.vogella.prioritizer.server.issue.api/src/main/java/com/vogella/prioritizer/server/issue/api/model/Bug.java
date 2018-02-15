package com.vogella.prioritizer.server.issue.api.model;

import java.util.Date;
import java.util.List;

public interface Bug {

	int getId();

	void setId(int id);

	String getResolution();

	void setResolution(String resolution);

	boolean isOpen();

	void setOpen(boolean isOpen);

	String getStatus();

	void setStatus(String status);

	String getCreator();

	void setCreator(String creator);

	String getSummary();

	void setSummary(String summary);

	String getPlatform();

	void setPlatform(String platform);

	String getProduct();

	void setProduct(String product);

	String getComponent();

	void setComponent(String component);

	String getSeverity();

	void setSeverity(String severity);

	String getPriority();

	void setPriority(String priority);

	String getVersion();

	void setVersion(String version);

	Date getCreationTime();

	void setCreationTime(Date creationTime);

	List<String> getCc();

	void setCc(List<String> cc);

	List<Integer> getBlocks();

	void setBlocks(List<Integer> blocks);

	List<String> getKeywords();

	void setKeywords(List<String> keywords);

	List<Comment> getComments();

	void setComments(List<Comment> comments);

	List<Attachment> getAttachments();

	void setAttachments(List<Attachment> attachments);

}