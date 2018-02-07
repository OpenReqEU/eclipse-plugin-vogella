package com.vogella.prioritizer.service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WordlistLoader;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.vogella.prioritizer.bugzilla.BugzillaApi;
import com.vogella.prioritizer.bugzilla.model.Bug;

@Service
public class PrioritizerService {

	@Autowired
	private BugzillaApi bugzillaApi;
	private CharArraySet stopWordSet;
	
	public PrioritizerService() throws IOException {
		File file = ResourceUtils.getFile("classpath:stopwords");
		stopWordSet = WordlistLoader.getWordSet(new FileReader(file));
	}
	
	public List<Bug> findSuitableBugs(String assignee, int limit) {
		return bugzillaApi.getBugsOfAssignee(assignee).blockingGet().getBugs();
	}

	public List<String> getKeywords(String assignee, int limit) throws IOException {
		List<Bug> suitableBugs = findSuitableBugs(assignee, limit);
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
}
