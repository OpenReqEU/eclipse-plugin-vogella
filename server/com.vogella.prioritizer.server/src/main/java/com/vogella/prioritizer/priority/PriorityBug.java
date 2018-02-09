package com.vogella.prioritizer.priority;

import lombok.Data;

@Data
public class PriorityBug implements Comparable<PriorityBug> {

	private int commentCount;

	private int ccCount;

	private int votesCount;

	private int attachmentsCount;

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

		sum += priorityBug.commentCount * 2;
		sum += priorityBug.ccCount * 1.8f;
		sum += priorityBug.votesCount * 1.6f;
		sum += priorityBug.attachmentsCount * 1.4f;
		sum += priorityBug.blockingIssuesCount * 1.2f;

		return sum;
	}
}
