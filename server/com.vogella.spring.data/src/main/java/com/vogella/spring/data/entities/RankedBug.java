package com.vogella.spring.data.entities;

import lombok.Data;

@Data
public class RankedBug {

	private int bugIdBugzilla;
	private String title;
	private String component;
	private String reporter;
	private String assignedTo;
	private String status;
	private String priority;
	private String severity;
	private String created;
	private String lastChanged;
	private int votes;
	private int countCC;
	private int countAttachments;
	private int countBlocks;
	private int countDependsOn;
	private int countDuplicates;
}
