package com.vogella.spring.datacrawler.data.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="Bug")
@Data
public class Bug {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	String id;
	String product;
	String component;
	String description;
	String assignedTo;
	
}
