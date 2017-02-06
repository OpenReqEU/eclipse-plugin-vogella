package com.vogella.spring.datacrawler.communication.dto;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import com.vogella.spring.datacrawler.data.entities.Bug;

@Root(name="bug", strict=false)
public class BugDto {

	@Element(name="id")
	@Path("bug")
	private String id;

	@Element(name="product")
	@Path("bug")
	private String product;
	
	@Element(name="component")
	@Path("bug")
	private String component;
	
	@Element(name="short_desc")
	@Path("bug")
	private String description;
	
	@Element(name="assigned_to")
	@Path("bug")
	private String assignedTo;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}
	
	public Bug getBugFromBugDto() {
		Bug bug = new Bug();
		bug.setId(this.id);
		bug.setDescription(this.description);
		bug.setAssignedTo(this.assignedTo);
		bug.setProduct(this.product);
		bug.setComponent(this.component);
		return bug;
	}
}
