package com.example.demo.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Company;
import com.example.demo.entity.NameCompany;
import com.mongodb.BasicDBObject;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;
import com.mongodb.client.result.UpdateResult;

@Repository
public class CompanyRepository {

	@Autowired
	MongoDatabase database;

	public BulkWriteResult insert(List<Company> listCompany) {
		MongoCollection<Document> mongoClient = database.getCollection("Company");
		List<WriteModel<Document>> listWrite = new ArrayList<>();
		for (Company itemCompany : listCompany) {
			Document document = new Document("names",
							Arrays.asList(new BasicDBObject("lang", itemCompany.getNames().get(0).getLang()).append("name",
									itemCompany.getNames().get(0).getName())))
					.append("code", itemCompany.getCode()).append("address", itemCompany.getAddress())
					.append("employeeNumb", itemCompany.getEmployeeNumb()).append("categories", itemCompany.getCategories())
					.append("currency", itemCompany.getCurrency());
			listWrite.add(new InsertOneModel<>(document));
		}
		BulkWriteResult result = mongoClient.bulkWrite(listWrite);
		return result;
	}

	public UpdateResult addElement(NameCompany names, ObjectId id) {
		MongoCollection<Document> mongoClient = database.getCollection("Company");
		BasicDBObject query = new BasicDBObject("_id", id);
		Bson update = new Document("$addToSet",
				new Document("names", new BasicDBObject("lang", names.getLang()).append("name", names.getName())));
		UpdateResult result = mongoClient.updateOne(query, update);
		return result;
	}

	public UpdateResult addElement7(String lang, ObjectId id) {
		MongoCollection<Document> mongoClient = database.getCollection("Company");
		BasicDBObject query = new BasicDBObject("_id", id);
		Bson update = new Document("$pull", new Document("names", new BasicDBObject("lang", lang)));
		UpdateResult result = mongoClient.updateOne(query, update);
		return result;
	}

	public Document search(ObjectId id) {
		MongoCollection<Document> mongoClient = database.getCollection("Company");
		BasicDBObject query = new BasicDBObject("_id", id);
		Document result = mongoClient.find(query).first();
		return result;
	}

	public Document showDB(Integer page, Integer limit) {
		MongoCollection<Document> mongoClient = database.getCollection("Company");
		Bson project = new BasicDBObject("$project", new BasicDBObject("_id", 0).append("code", 1).append("names", 1)
				.append("address", 1).append("categories", 1).append("employeeNumb", 1).append("currency", 1));
		Bson sort = new BasicDBObject("$sort", new BasicDBObject("code", 1));
		Bson count = new BasicDBObject("$count", "total");
		Bson project2 = new BasicDBObject("$project",
				new BasicDBObject("total",
						new BasicDBObject("$arrayElemAt", Stream.of("$total.total", 0).collect(Collectors.toList())))
								.append("data", 1));
		Bson skip = new BasicDBObject("$skip", (page - 1) * limit);
		Bson limits = new BasicDBObject("$limit", limit);
		Bson facet = new BasicDBObject("$facet",
				new BasicDBObject("total", Stream.of(project, count).collect(Collectors.toList())).append("data",
						Stream.of(project, sort, skip, limits).collect(Collectors.toList())));
		Document result = mongoClient.aggregate(Stream.of(facet, project2).collect(Collectors.toList())).first();
		return result;
	}
}
