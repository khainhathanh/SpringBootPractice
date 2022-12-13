package com.demospringboot.entity;

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
	private long totalPage;
	private long pageCurrent;
	private List<Document> listDoc = new ArrayList<>();
}
