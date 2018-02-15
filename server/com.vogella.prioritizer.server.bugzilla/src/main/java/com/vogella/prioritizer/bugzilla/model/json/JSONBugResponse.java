package com.vogella.prioritizer.bugzilla.model.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JSONBugResponse {
	List<JSONBugzillaBug> bugs;
}
