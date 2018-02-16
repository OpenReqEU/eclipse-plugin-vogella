package com.vogella.prioritizer.service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
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

	public Flux<Bug> findSuitableBugs(String assignee, String product, String component, int limit) {

		Instant oneYearAgo = LocalDateTime.now().minusYears(1).toInstant(ZoneOffset.UTC);
		Flux<Bug> newBugs = issueApi.getBugs(null, limit, product, component, "NEW", Date.from(oneYearAgo), null);

		Mono<List<String>> keywords = getKeywords(assignee, null, null, limit);

		return Flux
				.combineLatest(keywords, newBugs, (BiFunction<List<String>, Bug, Tuple2<List<String>, Bug>>) Tuples::of)
				.sort((o1, o2) -> {
					List<String> kw = o1.getT1();
					Bug bug1 = o1.getT2();
					Bug bug2 = o2.getT2();
					float sum1 = getPrioritySum(bug1, kw);
					float sum2 = getPrioritySum(bug2, kw);

					bug1.setUserPriority(sum1);
					bug2.setUserPriority(sum2);

					return Float.compare(sum2, sum1);
				}).map(Tuple2::getT2);
	}

	private float getPrioritySum(Bug bug, List<String> kw) {
		float sum = 0;

		sum += bug.getComments().size() * 2;
		sum += bug.getCc().size() * 1.8f;
		sum += kw.stream().filter(keyword -> bug.getSummary().toLowerCase().contains(keyword.toLowerCase())).count()
				* 1.6f;
		sum += bug.getAttachments().size() * 1.4f;
		sum += bug.getBlocks().size() * 1.2f;

		return sum;
	}

	public Mono<List<String>> getKeywords(String assignee, String product, String component, int limit) {
		Flux<Bug> resolvedBugs = issueApi.getBugs(assignee, limit, product, component, "RESOLVED", null, null);

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

	public Mono<byte[]> getKeywordImage(String assignee, int width, int height, String product, String component,
			int limit) {
		Mono<List<String>> keywordFlux = getKeywords(assignee, product, component, limit);

		return keywordFlux.map(keywords -> {

			// Create Chart
			PieChart chart = new PieChartBuilder().width(width).height(height)
					.title("Keywords of " + assignee).build();

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
