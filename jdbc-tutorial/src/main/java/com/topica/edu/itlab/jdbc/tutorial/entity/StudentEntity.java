package com.topica.edu.itlab.jdbc.tutorial.entity;

import com.topica.edu.itlab.jdbc.tutorial.annotation.Column;
import com.topica.edu.itlab.jdbc.tutorial.annotation.Table;

@Table(name = "student")
public class StudentEntity {
	@Column(name = "id")
	private Long id;
	
	@Column(name = "name")
	private String name;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
