package com.vogella.prioritizer.bugzilla.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.vogella.prioritizer.bugzilla.BugzillaApi;
import com.vogella.prioritizer.bugzilla.model.BugzillaBug;
import com.vogella.prioritizer.bugzilla.model.BugzillaComment;
import com.vogella.prioritizer.bugzilla.model.json.JSONBugResponse;
import com.vogella.prioritizer.bugzilla.model.json.JSONBugzillaBug;
import com.vogella.prioritizer.server.issue.api.IssueService;
import com.vogella.prioritizer.server.issue.api.model.Bug;
import com.vogella.prioritizer.server.issue.api.model.Comment;

import okhttp3.ResponseBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class BugzillaIssueService implements IssueService {

	private BugzillaApi bugzillaApi;

	public BugzillaIssueService(BugzillaApi bugzillaApi) {
		this.bugzillaApi = bugzillaApi;
	}

	@Override
	public Flux<Bug> getBugs(String assignee, long limit, List<String> product, List<String> component, String status,
			Date creationTime, Date lastChangeTime, boolean withComments) {

		Mono<JSONBugResponse> bugzillaBugs = bugzillaApi.getBugs(assignee, product, component, limit, status,
				creationTime, lastChangeTime);

		return bugzillaBugs.map(JSONBugResponse::getBugs).flatMapIterable(jsonBugs -> {
			List<Bug> bugs = new ArrayList<>();
			for (JSONBugzillaBug jsonBugzillaBug : jsonBugs) {
				Flux<Comment> commentsFlux = getComments(jsonBugzillaBug.getId());
				// TODO implement getAttachments
				Bug bug = BugzillaBug.of(jsonBugzillaBug);
				// FIXME make this non blocking
				if (withComments) {
					List<Comment> comments = commentsFlux.collectList().block();
					bug.setComments(comments);
				}
				bugs.add(bug);
			}

			return bugs;
		});
	}

	@Override
	public Mono<Bug> getBugById(int id) {
		Mono<JSONBugResponse> bugById = bugzillaApi.getBugById(id);

		return bugById.map(bugResp -> bugResp.getBugs().stream().findFirst()).map(jsonBugOptional -> {
			if (jsonBugOptional.isPresent()) {
				return null;
			}
			return null;
		});
	}

	private Flux<Comment> getComments(long bugId) {
		Mono<ResponseBody> comments = bugzillaApi.getComments(bugId);
		return comments.map(t -> {
			try {
				return t.string();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}).flatMapIterable((jsonString -> {

			JSONObject rootJsonObject = new JSONObject(jsonString);

			JSONObject bugsJsonObject = rootJsonObject.getJSONObject("bugs");

			JSONObject commentIdJsonObject = bugsJsonObject.getJSONObject(String.valueOf(bugId));

			JSONArray jsonArray = commentIdJsonObject.getJSONArray("comments");

			ObjectMapper mapper = new ObjectMapper();

			CollectionType javaType = mapper.getTypeFactory().constructCollectionType(List.class,
					BugzillaComment.class);
			try {
				return mapper.readValue(jsonArray.toString(), javaType);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}));
	}

}
