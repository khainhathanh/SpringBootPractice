package com.example.demo.repository;

import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import com.example.demo.exception.InternalServerException;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;

@Repository
public class EmployeeRepository {
	public UpdateResult insert(MongoCollection<Document> mongoClient, ObjectId id, List<Bson> insert,
			UpdateOptions options) {
		UpdateResult result = null;
		try {
			result = mongoClient.updateOne(Filters.lt("_id", id), insert, options);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public AggregateIterable<Document> showDB(MongoCollection<Document> mongoClient, List<Bson> query) {
		AggregateIterable<Document> result = null;
		try {
			result = mongoClient.aggregate(query);
		} catch (Exception e) {
			throw new InternalServerException("Error retrive!");
		}
		return result;
	}
}
