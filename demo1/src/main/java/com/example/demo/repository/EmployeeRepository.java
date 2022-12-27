package com.example.demo.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Employee;
import com.mongodb.BasicDBObject;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;

@Repository
public class EmployeeRepository {
	
	@Autowired
	MongoDatabase database;
	
	public BulkWriteResult insert(List<Employee> listEmployee) {
		MongoCollection<Document> mongoClient = database.getCollection("Employee");
		List<WriteModel<Document>> listWrite = new ArrayList<>();		
		for (Employee itemEmployee : listEmployee) {
			Document document = new Document("personId", itemEmployee.getPersonId())
					.append("companyId", itemEmployee.getCompanyId())
					.append("salary", itemEmployee.getSalary())
					.append("startDate", itemEmployee.getStartDate())
					.append("currency", itemEmployee.getCurrency());
			listWrite.add(new InsertOneModel<>(document));
		}
		BulkWriteResult result = mongoClient.bulkWrite(listWrite);
		return result;
	}
	
	public Document showDB14(Integer year, Integer page, Integer limit) {
		MongoCollection<Document> mongoClient = database.getCollection("Employee");
		Bson project = new BasicDBObject("$project", new BasicDBObject("_id", 0).append("companyId", 1)
				.append("year", new BasicDBObject("$year", "$startDate")).append("salary", 1));
		Bson match = new BasicDBObject("$match", new BasicDBObject("year", year));
		Bson group = new BasicDBObject("$group",
				new BasicDBObject("_id", Stream.of("$companyId", "$year").collect(Collectors.toList()))
						.append("salary", new BasicDBObject("$sum", "$salary"))
						.append("numberofEmployee", new BasicDBObject("$sum", 1)));
		Bson count = new BasicDBObject("$count", "total");
		Bson sort = new BasicDBObject("$sort", new BasicDBObject("_id", 1));
		Bson project2 = new BasicDBObject("$project",
				new BasicDBObject("total",
						new BasicDBObject("$arrayElemAt", Stream.of("$total.total", 0).collect(Collectors.toList())))
								.append("data", 1));
		Bson skip = new BasicDBObject("$skip", (page - 1) * limit);
		Bson limits = new BasicDBObject("$limit", limit);
		Bson facet = new BasicDBObject("$facet",
				new BasicDBObject("total", Stream.of(project, match, group, count).collect(Collectors.toList())).append(
						"data", Stream.of(project, match, group, sort, skip, limits).collect(Collectors.toList())));
		Document result = mongoClient.aggregate(Stream.of(facet, project2).collect(Collectors.toList())).first();
		return result;
	}
	
	public Document showDB15(Integer year, Integer page, Integer limit) {
		MongoCollection<Document> mongoClient = database.getCollection("Employee");
		Bson project = new BasicDBObject("$project", new BasicDBObject("_id", 0).append("companyId", 1)
				.append("year", new BasicDBObject("$year", "$startDate")).append("salary", 1));
		Bson match = new BasicDBObject("$match", new BasicDBObject("year", year));
		Bson group = new BasicDBObject("$group",
				new BasicDBObject("_id", Stream.of("$companyId", "$year").collect(Collectors.toList())).append("salary",
						new BasicDBObject("$sum", "$salary")));
		Bson sort = new BasicDBObject("$sort", new BasicDBObject("_id", 1));

		Bson count = new BasicDBObject("$count", "total");

		Bson project2 = new BasicDBObject("$project",
				new BasicDBObject("total",
						new BasicDBObject("$arrayElemAt", Stream.of("$total.total", 0).collect(Collectors.toList())))
								.append("data", 1));
		Bson skip = new BasicDBObject("$skip", (page - 1) * limit);
		Bson limits = new BasicDBObject("$limit", limit);
		Bson facet = new BasicDBObject("$facet",
				new BasicDBObject("total", Stream.of(project, match, group, count).collect(Collectors.toList())).append(
						"data", Stream.of(project, match, group, sort, skip, limits).collect(Collectors.toList())));
		Document result = mongoClient.aggregate(Stream.of(facet, project2).collect(Collectors.toList())).first();
		return result;
	}
	
	public Document showDB16(String category, Integer startYear, Integer endYear, Integer page, Integer limit) {
		MongoCollection<Document> mongoClient = database.getCollection("Employee");
		Bson lookup = new BasicDBObject("$lookup", new BasicDBObject("from", "Company")
				.append("localField", "companyId").append("foreignField", "code").append("as", "companydata"));
		Bson unwind = new BasicDBObject("$unwind", "$companydata");
		Bson project = new BasicDBObject("$project",
				new BasicDBObject("_id", 0).append("companyId", 1)
						.append("year", new BasicDBObject("$year", "$startDate")).append("salary", 1)
						.append("category", "$companydata.categories"));
		Bson match = new BasicDBObject("$match",
				new BasicDBObject("category", category).append("$and",
						Stream.of(new BasicDBObject("year", new BasicDBObject("$gte", startYear)),
								new BasicDBObject("year", new BasicDBObject("$lte", endYear)))
								.collect(Collectors.toList())));
		Bson group = new BasicDBObject("$group",
				new BasicDBObject("_id", Stream.of("$category", "$companyId").collect(Collectors.toList()))
						.append("salary", new BasicDBObject("$sum", "$salary")));
		Bson sort = new BasicDBObject("$sort", new BasicDBObject("_id", 1));
		Bson count = new BasicDBObject("$count", "total");
		Bson project2 = new BasicDBObject("$project",
				new BasicDBObject("total",
						new BasicDBObject("$arrayElemAt", Stream.of("$total.total", 0).collect(Collectors.toList())))
								.append("data", 1));
		Bson skip = new BasicDBObject("$skip", (page - 1) * limit);
		Bson limits = new BasicDBObject("$limit", limit);
		Bson facet = new BasicDBObject("$facet",
				new BasicDBObject("total",
						Stream.of(lookup, unwind, project, match, group, count).collect(Collectors.toList()))
								.append("data", Stream.of(lookup, unwind, project, match, group, sort, skip, limits)
										.collect(Collectors.toList())));
		Document result = mongoClient.aggregate(Stream.of(facet, project2).collect(Collectors.toList())).first();
		return result;
	}
}
