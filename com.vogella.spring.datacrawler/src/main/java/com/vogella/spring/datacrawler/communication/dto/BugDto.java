package com.vogella.spring.datacrawler.communication.dto;

import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import com.vogella.spring.datacrawler.data.entities.Bug;

@Root(strict=false)
public class BugDto {

	@Element(name="bug_id")
	private String id;
	
	@Element(name="product")
	String product;
	
	@Element(name="component")
	String component;
	
	@Element(name="reporter")
	String reporter;
	
	@Element(name="assigned_to")
	String assignedTo;
	
	@ElementList(entry="see_also", inline = true, required=false)
	List<String> gerritChanges;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public String getReporter() {
		return reporter;
	}

	public void setReporter(String reporter) {
		this.reporter = reporter;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}

	public List<String> getGerritChanges() {
		return gerritChanges;
	}

	public void setGerritChanges(List<String> gerritChanges) {
		this.gerritChanges = gerritChanges;
	}

	public Bug getBugFromBugDto() {
		Bug bugDetails = new Bug();
		bugDetails.setId(this.id);
		bugDetails.setProduct(this.product);
		bugDetails.setComponent(this.component);
		bugDetails.setReporter(this.reporter);
		bugDetails.setAssignedTo(this.assignedTo);
		return bugDetails;
	}
}
