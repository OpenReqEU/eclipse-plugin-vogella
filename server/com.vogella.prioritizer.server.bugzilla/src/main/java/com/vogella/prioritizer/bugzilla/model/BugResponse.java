package com.vogella.prioritizer.bugzilla.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BugResponse {
	List<Bug> bugs;
}
