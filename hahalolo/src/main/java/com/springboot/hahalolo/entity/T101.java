package com.springboot.hahalolo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Document(collection = "T101")
public class T101 {
	@Id
	private String id;

	private String lang;
	
	private String tv151;
	
	private String tv152;
	
	private String tv153;

	private String tv154;

	private String tv159;
}
