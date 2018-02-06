package com.vogella.spring.datacrawler.fileexporter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vogella.spring.datacrawler.entities.Bug;
import com.vogella.spring.datacrawler.repositories.BugRepository;
import com.vogella.spring.datacrawler.services.BugService;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Class to export database files into an arff file. This file can be used to
 * import into Weka, a data mining tool. This allows to easy "play" with the
 * data (try different algorithms, feature sets, etc) without doing much
 * programming work.
 * 
 * @author david
 *
 */
@Component
public class ArffFileExporter {

	private static final Logger logger = Logger.getLogger(ArffFileExporter.class.getName());

	@Autowired
	BugRepository bugRepository;
	@Autowired
	BugService bugService;

	private String user = "none";
	private Map<String, Integer> allKeywordsForUser;

	public ArffFileExporter() {
	}

	public void exportData() {
		logger.log(Level.INFO, "Start to create file");
		allKeywordsForUser = bugService.findAllKeywordsForUser(user);
		Instances instances = new Instances("MyRelation", getAttributes(), 0);
		bugRepository.findAll().forEach(bug -> instances.add(getInstance(bug, instances)));
		writeToFile(instances);
		logger.log(Level.INFO, "File created");
	}

	private void writeToFile(Instances instances) {
		File file = new File("/home/david/Desktop/exportedBugData.arff");
		try {
			FileWriter fw = new FileWriter(file);
			PrintWriter pw = new PrintWriter(fw);
			pw.println(instances);
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private ArrayList<Attribute> getAttributes() {
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();

		// attributes.add(new Attribute("bug_id"));

		// // string
		attributes.add(new Attribute("bug_title", true));

		// nominal
		attributes.add(new Attribute("bug_component", ((ArrayList<String>) bugRepository.findAllDistinctComponents())));
		//
		attributes.add(new Attribute("bug_reporter", ((ArrayList<String>) bugRepository.findAllDistinctReporters())));
		//
		// attributes.add(new Attribute("assignedTo", ((ArrayList<String>)
		// bugRepository.findAllDistinctAssignedTo())));

		ArrayList<String> posValues = new ArrayList<String>();
		posValues.add("true");
		posValues.add("false");
		attributes.add(new Attribute("assignedTo", posValues));

		// numeric
		attributes.add(new Attribute("countCc"));

		attributes.add(new Attribute("countAdditionalLinks"));

		attributes.add(new Attribute("countAttachments"));

		attributes.add(new Attribute("countComments"));

		attributes.add(new Attribute("countBlocks"));

		attributes.add(new Attribute("countDependsOn"));

		attributes.add(new Attribute("countDuplicates"));

		attributes.add(new Attribute("bug_priority", ((ArrayList<String>) bugRepository.findAllDistinctPriorities())));

		attributes.add(new Attribute("bug_severity", ((ArrayList<String>) bugRepository.findAllDistinctSeverities())));

		attributes.add(new Attribute("bug_votes"));

		for (String key : allKeywordsForUser.keySet()) {
			attributes.add(new Attribute(key));
		}

		return attributes;
	}

	private Instance getInstance(Bug bug, Instances instances) {
		Instance instance = new DenseInstance(instances.numAttributes());
		instance.setDataset(instances);

		// instance.setValue(instances.attribute("bug_id"), bug.getBugIdBugzilla());
		instance.setValue(instances.attribute("bug_title"), bug.getTitle());

		instance.setValue(instances.attribute("bug_component"), bug.getComponent());
		instance.setValue(instances.attribute("bug_reporter"), bug.getReporter());
		instance.setValue(instances.attribute("assignedTo"), bug.getAssignedTo().equals(user) ? "true" : "false");

		instance.setValue(instances.attribute("bug_priority"), bug.getPriority());
		instance.setValue(instances.attribute("bug_severity"), bug.getSeverity());

		instance.setValue(instances.attribute("countCc"), bug.getCountCC());
		instance.setValue(instances.attribute("countAdditionalLinks"), bug.getCountAdditionalLinks());
		instance.setValue(instances.attribute("countAttachments"), bug.getCountAttachments());
		instance.setValue(instances.attribute("countDuplicates"), bug.getCountDuplicates());
		instance.setValue(instances.attribute("countComments"), bug.getCountComments());
		instance.setValue(instances.attribute("countBlocks"), bug.getCountBlocks());
		instance.setValue(instances.attribute("countDependsOn"), bug.getCountDependsOn());
		instance.setValue(instances.attribute("bug_votes"), bug.getVotes());

		Map<String, Integer> keywordMap = bug.getKeywords();
		for (String key : allKeywordsForUser.keySet()) {
			int frequency = 0;
			if (keywordMap.containsKey(key)) {
				frequency = keywordMap.get(key);
			}
			instance.setValue(instances.attribute(key), frequency);
		}
		return instance;
	}
}
