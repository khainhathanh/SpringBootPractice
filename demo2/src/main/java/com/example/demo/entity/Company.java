package com.example.demo.entity;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString
@Getter
@Setter
@Document(collection = "Company")
public class Company {
	@Id
	private ObjectId id ;
	private String code;
	private String address;
	private Integer employeeNumb;
	private String categories;
	private String currency;
	private List<NameCompany> names ;
}
