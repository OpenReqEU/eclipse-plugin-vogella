package com.vogella.prioritizer.server.issue.api.model;

import lombok.Data;

@Data
public class PriorityBug implements Comparable<PriorityBug> {
	
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
			return 1;
		} else if (sumPriority > otherSumPriority) {
			return -1;
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
}
