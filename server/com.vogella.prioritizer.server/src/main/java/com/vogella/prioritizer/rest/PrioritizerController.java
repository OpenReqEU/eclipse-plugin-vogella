package com.vogella.prioritizer.rest;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.vogella.prioritizer.bugzilla.model.Bug;
import com.vogella.prioritizer.service.PrioritizerService;

@RestController
class PrioritizerController {

	@Autowired
	private PrioritizerService prioritizerService;

	@GetMapping("/findSuitableBugs")
	public List<Bug> findSuitableBugs(@RequestParam("assignee") String assignee,
			@RequestParam(name = "limit", required = false, defaultValue = "50") int limit) throws IOException {
		return prioritizerService.findSuitableBugs(assignee, limit);
	}

	@GetMapping(value = "/getChart", produces = MediaType.IMAGE_PNG_VALUE)
	public @ResponseBody byte[] getImageWithMediaType(@RequestParam("assignee") String assignee,
			@RequestParam(name = "limit", required = false, defaultValue = "50") int limit) throws IOException {

		List<String> keywords = prioritizerService.getKeywords(assignee, 50);
		
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
		}).distinct().limit(10).forEach(keyword -> {
			int frequency = Collections.frequency(keywords, keyword);
			chart.addSeries(keyword, frequency);
		});

		chart.getStyler().setCircular(false);
		chart.getStyler().setPlotBorderVisible(false);
		chart.getStyler().setChartTitleBoxVisible(false);
		chart.getStyler().setChartTitleVisible(false);

		return BitmapEncoder.getBitmapBytes(chart, BitmapFormat.PNG);
	}
}
