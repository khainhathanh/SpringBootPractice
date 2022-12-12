package com.demospringboot.entity;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Document(collection  = "Person")
public class Person {
	@Id
	private ObjectId id;
	private String name;
	private Integer age;
	private String sex;
}
