package com.vogella.spring.datacrawler.fileexporter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vogella.spring.datacrawler.entities.Bug;
import com.vogella.spring.datacrawler.repositories.BugRepository;

@Component
public class CSVExporter {

	private static final Logger logger = Logger.getLogger(CSVExporter.class.getName());

	@Autowired
	BugRepository bugRepository;

	public CSVExporter() {

	}

	public void exportBugData() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				logger.log(Level.INFO, "Start to create file");
				StringBuilder builder = new StringBuilder();
				addColumns(builder);
				bugRepository.findAll().forEach(bug -> addBugData(builder, bug));
				writeToFile(builder);
				logger.log(Level.INFO, "File created");
			}

		});
		t.run();
	}

	private void addColumns(StringBuilder builder) {
		builder.append("id");
		builder.append(",");
//		builder.append("title");
//		builder.append(",");
//		builder.append("description");
//		builder.append(",");
		builder.append("component");
		builder.append(",");
		builder.append("reporter");
		builder.append(",");
		builder.append("assignedTo");
		builder.append(",");
		builder.append("creationTimestamp");
		builder.append(",");
		builder.append("lastChangeTimestamp");
		builder.append(",");
		builder.append("version");
		builder.append(",");
		builder.append("reportedPlatform");
		builder.append(",");
		builder.append("operationSystem");
		builder.append(",");
		builder.append("status");
		builder.append(",");
		builder.append("resolution");
		builder.append(",");
		builder.append("milestone");
		builder.append(",");
		builder.append("countCc");
		builder.append(",");
		builder.append("countAdditionalLinks");
		builder.append(",");
		builder.append("countAttachments");
		builder.append(",");
		builder.append("countComments");
		builder.append(",");
		builder.append("countBlocks");
		builder.append(",");
		builder.append("countDependsOn");
		builder.append(",");
		builder.append("countDuplicates");
		builder.append(",");
		builder.append("priority");
		builder.append(",");
		builder.append("severity");
		builder.append(",");
		builder.append("votes");
		builder.append("\n");
	}

	private void addBugData(StringBuilder builder, Bug bug) {
		builder.append(bug.getBugIdBugzilla());
		builder.append(",");
//		builder.append(bug.getTitle().replace(",", ".").replace("\"", ""));
//		builder.append(",");
//		builder.append(bug.getDescription().replace(",", ".").replace("\"", ""));
//		builder.append(",");
		builder.append(bug.getComponent());
		builder.append(",");
		builder.append(bug.getReporter());
		builder.append(",");
		builder.append(bug.getAssignedTo());
		builder.append(",");
		builder.append(convertTimestamp(bug.getCreationTimestamp()));
		builder.append(",");
		builder.append(convertTimestamp(bug.getLastChangeTimestamp()));
		builder.append(",");
		builder.append(bug.getVersion());
		builder.append(",");
		builder.append(bug.getReportedPlatform());
		builder.append(",");
		builder.append(bug.getOperationSystem());
		builder.append(",");
		builder.append(bug.getStatus());
		builder.append(",");
		builder.append(bug.getResolution());
		builder.append(",");
		builder.append(bug.getMilestone());
		builder.append(",");

		builder.append(bug.getCountCC());
		builder.append(",");
		builder.append(bug.getCountAdditionalLinks());
		builder.append(",");
		builder.append(bug.getCountAttachments());
		builder.append(",");
		builder.append(bug.getCountComments());
		builder.append(",");
		builder.append(bug.getCountBlocks());
		builder.append(",");
		builder.append(bug.getCountDependsOn());
		builder.append(",");
		builder.append(bug.getCountDuplicates());
		builder.append(",");
		builder.append(bug.getPriority());
		builder.append(",");
		builder.append(bug.getSeverity());
		builder.append(",");
		builder.append(bug.getVotes());
		builder.append("\n");
	}

	private String convertTimestamp(long timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp);
		return sdf.format(new Date(calendar.getTimeInMillis()));
	}

	private void writeToFile(StringBuilder builder) {
		File file = new File("exportedBugData.csv");
		try {
			FileWriter fw = new FileWriter(file);
			PrintWriter pw = new PrintWriter(fw);
			pw.println(builder.toString());
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
