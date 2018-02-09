package com.vogella.prioritizer.service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WordlistLoader;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.vogella.prioritizer.bugzilla.BugzillaApi;
import com.vogella.prioritizer.bugzilla.model.Bug;
import com.vogella.prioritizer.bugzilla.model.BugResponse;
import com.vogella.prioritizer.priority.PriorityBug;

import io.reactivex.Single;
import okhttp3.ResponseBody;

@Service
public class PrioritizerService {

	@Autowired
	private BugzillaApi bugzillaApi;

	private CharArraySet stopWordSet;

	public PrioritizerService() throws IOException {
		File file = ResourceUtils.getFile("classpath:stopwords");
		stopWordSet = WordlistLoader.getWordSet(new FileReader(file));
	}

	public Collection<Bug> findSuitableBugs(String assignee, int limit) throws JSONException, IOException {
		Single<BugResponse> bugResponse = bugzillaApi.getRecentOpenBugs(limit);
		
		List<String> keywords = getKeywords(assignee, 50);

		List<Bug> bugs = bugResponse.blockingGet().getBugs();

		TreeMap<PriorityBug, Bug> priorityBugs = new TreeMap<>();

		for (Bug bug : bugs) {
			PriorityBug priorityBug = new PriorityBug();

			int commentCount = getCommentCount(bug.getId());
			priorityBug.setCommentCount(commentCount);

			priorityBug.setCcCount(bug.getCc().size());
			
			priorityBug.setSeverity(bug.getSeverity());
			
			priorityBug.setBlockingIssuesCount(bug.getBlocks().size());
			
			long foundKeyWords = keywords.stream().filter(keyword -> bug.getSummary().contains(keyword)).count();
			priorityBug.setUserKeywordMatchCount(foundKeyWords);
			
			priorityBugs.put(priorityBug, bug);
		}

		return priorityBugs.values();
	}

	public List<String> getKeywords(String assignee, int limit) throws IOException {
		List<Bug> suitableBugs = bugzillaApi.getBugsOfAssignee(assignee, limit, "RESOLVED").blockingGet().getBugs();
		StringBuilder sb = new StringBuilder();

		for (Bug bug : suitableBugs) {
			String summary = bug.getSummary();
			sb.append(summary);
			sb.append(" ");
		}

		try (Analyzer analyzer = new StandardAnalyzer(stopWordSet)) {
			try (TokenStream tokenStream = analyzer.tokenStream("contents", sb.toString())) {

				CharTermAttribute term = tokenStream.addAttribute(CharTermAttribute.class);

				tokenStream.reset();

				List<String> result = new ArrayList<String>();
				while (tokenStream.incrementToken()) {
					result.add(term.toString());
				}
				tokenStream.end();
				tokenStream.close();

				return result;
			}
		}
	}

	public byte[] getKeywordImage(String assignee, int limit) throws IOException {
		List<String> keywords = getKeywords(assignee, limit);

		// Create Chart
		PieChart chart = new PieChartBuilder().width(800).height(600).build();

		// Series
		keywords.stream().sorted((o1, o2) -> {
			int f1 = Collections.frequency(keywords, o1);
			int f2 = Collections.frequency(keywords, o2);
			if (f1 > f2) {
				return -1;
			} else if (f1 == f2) {
				return 0;
			}
			return 1;
		}).distinct().limit(15).forEach(keyword -> {
			int frequency = Collections.frequency(keywords, keyword);
			chart.addSeries(keyword, frequency);
		});

		chart.getStyler().setCircular(false);
		chart.getStyler().setPlotBorderVisible(false);
		chart.getStyler().setChartTitleBoxVisible(false);
		chart.getStyler().setChartTitleVisible(false);

		return BitmapEncoder.getBitmapBytes(chart, BitmapFormat.PNG);
	}

	public int getCommentCount(int bugId) throws JSONException, IOException {
		Single<ResponseBody> comments = bugzillaApi.getComments(bugId);

		String jsonString = comments.blockingGet().string();

		JSONObject rootJsonObject = new JSONObject(jsonString);

		JSONObject bugsJsonObject = rootJsonObject.getJSONObject("bugs");

		JSONObject commentIdJsonObject = bugsJsonObject.getJSONObject(String.valueOf(bugId));

		JSONArray jsonArray = commentIdJsonObject.getJSONArray("comments");

		return jsonArray.length();
	}
}
