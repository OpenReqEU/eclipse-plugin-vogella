package com.vogella.prioritizer.service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
import org.json.JSONObject;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.PieSeries.PieSeriesRenderStyle;
import org.knowm.xchart.style.PieStyler.AnnotationType;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.colors.BaseSeriesColors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.vogella.prioritizer.bugzilla.BugzillaApi;
import com.vogella.prioritizer.bugzilla.model.Bug;
import com.vogella.prioritizer.bugzilla.model.BugResponse;
import com.vogella.prioritizer.priority.PriorityBug;

import okhttp3.ResponseBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PrioritizerService {

	@Autowired
	private BugzillaApi bugzillaApi;

	private CharArraySet stopWordSet;

	public PrioritizerService() throws IOException {
		File file = ResourceUtils.getFile("classpath:stopwords");
		stopWordSet = WordlistLoader.getWordSet(new FileReader(file));
	}

	public Flux<Bug> findSuitableBugs(String assignee, int limit) {
		Mono<BugResponse> bugResponse = bugzillaApi.getRecentOpenBugs(limit);

		return bugResponse.flatMapIterable(bR -> {
			List<Bug> bugs = bR.getBugs();

			Flux<String> keywords = getKeywords(assignee, 50);

			TreeMap<PriorityBug, Bug> priorityBugs = new TreeMap<>();

			for (Bug bug : bugs) {
				PriorityBug priorityBug = new PriorityBug();

				Mono<Integer> commentCount = getCommentCount(bug.getId());
				priorityBug.setCommentCount(commentCount.block());

				priorityBug.setCcCount(bug.getCc().size());

				priorityBug.setSeverity(bug.getSeverity());

				priorityBug.setBlockingIssuesCount(bug.getBlocks().size());

				long foundKeyWords = keywords.toStream().filter(keyword -> bug.getSummary().contains(keyword)).count();
				priorityBug.setUserKeywordMatchCount(foundKeyWords);

				priorityBugs.put(priorityBug, bug);
			}

			return priorityBugs.values();
		});
	}

	public Flux<String> getKeywords(String assignee, int limit) {
		Mono<BugResponse> bugsOfAssignee = bugzillaApi.getBugsOfAssignee(assignee, limit, "RESOLVED");

		return bugsOfAssignee.map(bR -> bR.getBugs()).map(bugList -> {
			StringBuilder sb = new StringBuilder();

			for (Bug bug : bugList) {
				String summary = bug.getSummary();
				sb.append(summary);
				sb.append(" ");
			}
			return sb.toString();
		}).flatMapIterable(summariesAsText -> {
			try (Analyzer analyzer = new StandardAnalyzer(stopWordSet)) {
				try (TokenStream tokenStream = analyzer.tokenStream("contents", summariesAsText)) {

					CharTermAttribute term = tokenStream.addAttribute(CharTermAttribute.class);

					tokenStream.reset();

					List<String> result = new ArrayList<String>();
					while (tokenStream.incrementToken()) {
						result.add(term.toString());
					}
					tokenStream.end();
					tokenStream.close();

					return result;
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

	public Mono<byte[]> getKeywordImage(String assignee, int limit) {
		Flux<String> keywordFlux = getKeywords(assignee, limit);

		return keywordFlux.collectList().map(keywords -> {

			// Create Chart
			PieChart chart = new PieChartBuilder().width(800).height(600)
					.title("Keywords of already fixed bugs by " + assignee).build();

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

			chart.getStyler().setLegendVisible(true);
			chart.getStyler().setLegendPosition(Styler.LegendPosition.OutsideE);
			chart.getStyler().setLegendLayout(Styler.LegendLayout.Vertical);

			chart.getStyler().setAnnotationType(AnnotationType.LabelAndPercentage);
			chart.getStyler().setAnnotationDistance(.82);

			chart.getStyler().setPlotContentSize(.89);

			chart.getStyler().setDefaultSeriesRenderStyle(PieSeriesRenderStyle.Pie);

			chart.getStyler().setDecimalPattern("#");

			chart.getStyler().setSeriesColors(new BaseSeriesColors().getSeriesColors());

			chart.getStyler().setSumVisible(true);
			chart.getStyler().setSumFontSize(18f);

			try {
				return BitmapEncoder.getBitmapBytes(chart, BitmapFormat.PNG);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	public Mono<Integer> getCommentCount(int bugId) {
		Mono<ResponseBody> comments = bugzillaApi.getComments(bugId);
		return comments.map(t -> {
			try {
				return t.string();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}).map(jsonString -> {

			JSONObject rootJsonObject = new JSONObject(jsonString);

			JSONObject bugsJsonObject = rootJsonObject.getJSONObject("bugs");

			JSONObject commentIdJsonObject = bugsJsonObject.getJSONObject(String.valueOf(bugId));

			JSONArray jsonArray = commentIdJsonObject.getJSONArray("comments");

			return jsonArray.length();
		});

	}
}
