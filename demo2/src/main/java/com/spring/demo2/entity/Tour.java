package com.spring.demo2.entity;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Document(collection = "Tour")
public class Tour {
	private ObjectId id;
	private List<TourDetail> tourDetail;
	private Integer slot;
}
