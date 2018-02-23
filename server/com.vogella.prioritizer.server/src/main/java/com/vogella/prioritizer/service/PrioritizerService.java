package com.vogella.prioritizer.service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.BiFunction;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WordlistLoader;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
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

import com.vogella.prioritizer.server.issue.api.IssueService;
import com.vogella.prioritizer.server.issue.api.model.Bug;
import com.vogella.prioritizer.server.issue.api.model.PriorityBug;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@Service
public class PrioritizerService {

	@Autowired
	private IssueService issueApi;

	private CharArraySet stopWordSet;

	public PrioritizerService() throws IOException {
		File file = ResourceUtils.getFile("classpath:stopwords");
		stopWordSet = WordlistLoader.getWordSet(new FileReader(file));
	}

	public Flux<Bug> getMostDiscussedBugsOfTheMonth(List<String> product, List<String> component) {

		LocalDate firstDayOfTheMonth = LocalDate.now().withDayOfMonth(1);
		Date lastModifiedDate = Date.from(firstDayOfTheMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
		Flux<Bug> bugs = issueApi.getBugs(null, 500, product, component, null, null, lastModifiedDate, true);

		return bugs.sort((b1, b2) -> Integer.compare(b2.getComments().size(), b1.getComments().size()));
	}

	public Flux<PriorityBug> findSuitableBugs(String assignee, List<String> product, List<String> component, int limit) {

		Instant oneYearAgo = LocalDateTime.now().minusYears(2).toInstant(ZoneOffset.UTC);
		// TODO store latest bugs as prioritizer bugs in the db
		Flux<Bug> newBugs = issueApi.getBugs(null, limit, product, component, "NEW", null, Date.from(oneYearAgo), false);

		Mono<List<String>> keywords = getKeywords(assignee, null, null, limit);

		return Flux
				.combineLatest(keywords, newBugs, (BiFunction<List<String>, Bug, Tuple2<List<String>, Bug>>) Tuples::of)
				.map(tuple -> {
					List<String> kw = tuple.getT1();
					Bug bug = tuple.getT2();

					PriorityBug priorityBug = new PriorityBug();

					priorityBug.setBug(bug);

					priorityBug.setGerritChangeCount(bug.getSeeAlso().size());
					priorityBug.setCommentCount(bug.getComments().size());
					priorityBug.setCcCount(bug.getCc().size());
					priorityBug.setUserKeywordMatchCount(kw.stream()
							.filter(keyword -> bug.getSummary().toLowerCase().contains(keyword.toLowerCase())).count());
					priorityBug.setBlockingIssuesCount(bug.getBlocks().size());

					return priorityBug;
				}).sort();
	}

	public Mono<List<String>> getKeywords(String assignee, List<String> product, List<String> component, int limit) {
		Flux<Bug> resolvedBugs = issueApi.getBugs(assignee, limit, product, component, "RESOLVED", null, null, false);

		return resolvedBugs.map(Bug::getSummary).flatMapIterable(summariesAsText -> {
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
		}).collectList();
	}

	public Mono<byte[]> getKeywordImage(String assignee, int width, int height, List<String> product, List<String> component,
			int limit) {
		Mono<List<String>> keywordFlux = getKeywords(assignee, product, component, limit);

		return keywordFlux.map(keywords -> {

			// Create Chart
			PieChart chart = new PieChartBuilder().width(width).height(height).title("Keywords of " + assignee).build();

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
}
