package com.spring.demo2.entity;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class Pagination {
	private Integer totalPage;
	private Integer pageCurrent;
	private List<Document> listDoc = new ArrayList<>();
}