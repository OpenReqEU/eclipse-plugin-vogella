package com.vogella.prioritizer.core.model;

public class PriorityBug implements Comparable<PriorityBug> {

	public static final PriorityBug LOADING_DATA_FAKE_BUG;

	static {
		LOADING_DATA_FAKE_BUG = new PriorityBug();
		LOADING_DATA_FAKE_BUG.setBug(new Bug("Loading data..."));
	}

	private Bug bug;

	private int gerritChangeCount;

	private int commentCount;

	private int ccCount;

	private int blockingIssuesCount;

	private String severity;

	private long userKeywordMatchCount;

	@Override
	public int compareTo(PriorityBug priorityBug) {
		float sumPriority = sumPriority(this);
		float otherSumPriority = sumPriority(priorityBug);

		if (sumPriority < otherSumPriority) {
			return -1;
		} else if (sumPriority > otherSumPriority) {
			return 1;
		}

		return 0;
	}

	private float sumPriority(PriorityBug priorityBug) {

		float sum = 0;

		sum += priorityBug.gerritChangeCount * 2.2f;
		sum += priorityBug.commentCount * 1.9f;
		sum += priorityBug.ccCount * 1.7f;
		sum += priorityBug.userKeywordMatchCount * 1.5f;
		sum += priorityBug.blockingIssuesCount * 1.4f;

		return sum;
	}

	public Bug getBug() {
		return bug;
	}

	public void setBug(Bug bug) {
		this.bug = bug;
	}

	public int getGerritChangeCount() {
		return gerritChangeCount;
	}

	public void setGerritChangeCount(int gerritChangeCount) {
		this.gerritChangeCount = gerritChangeCount;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public int getCcCount() {
		return ccCount;
	}

	public void setCcCount(int ccCount) {
		this.ccCount = ccCount;
	}

	public int getBlockingIssuesCount() {
		return blockingIssuesCount;
	}

	public void setBlockingIssuesCount(int blockingIssuesCount) {
		this.blockingIssuesCount = blockingIssuesCount;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public long getUserKeywordMatchCount() {
		return userKeywordMatchCount;
	}

	public void setUserKeywordMatchCount(long userKeywordMatchCount) {
		this.userKeywordMatchCount = userKeywordMatchCount;
	}
}
