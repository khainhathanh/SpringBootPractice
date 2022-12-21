package com.example.demo.entity;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString
@Getter
@Setter
@Document(collection = "Person")
public class Person {
	@Id
	private ObjectId Id;
	private String firstName;
	private String lastName;
	private Integer age;
	private String sex;
	private List<String> languages;
	
	private List<Verhicles> verhicles;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date dateofbirth;
	private String email;
	private String phone;
	private String fullName;
}
