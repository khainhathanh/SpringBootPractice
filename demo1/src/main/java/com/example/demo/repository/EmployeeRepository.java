package com.example.demo.repository;

import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;

@Repository
public class EmployeeRepository {
	
	@Autowired
	MongoDatabase database;
	
	public UpdateResult insert(ObjectId id, List<Bson> insert) {
		MongoCollection<Document> mongoClient = database.getCollection("Employee");
		UpdateOptions options = new UpdateOptions().upsert(true);
		UpdateResult result = mongoClient.updateOne(Filters.lt("_id", id), insert, options);
		return result;
	}
	
	public Document showDB(List<Bson> query) {
		MongoCollection<Document> mongoClient = database.getCollection("Employee");
		Document result = mongoClient.aggregate(query).first();
		return result;
	}
}
