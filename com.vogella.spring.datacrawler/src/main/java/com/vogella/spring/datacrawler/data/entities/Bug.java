package com.vogella.spring.datacrawler.data.entities;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="Bug")
@Data
public class Bug {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	String id;	
	String bugId;
	String assignedTo;
	String component;
	String product;
	String reporter;
	String version;
	String status;
	String priority;
	String severity;
	int votes;
	String creationTimestamp;
	String lastChangeTimestamp;
	
	@ElementCollection(fetch=FetchType.EAGER)
	Set<String> ccList;
	
	@ElementCollection(fetch=FetchType.EAGER)
	Set<String> additionalLinks;
	
	@OneToMany(mappedBy="bug", fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	@OrderColumn
	List<Comment> comments;
	 
}
