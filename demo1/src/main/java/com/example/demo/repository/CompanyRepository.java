package com.example.demo.repository;

import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.demo.exception.InternalServerException;
import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;

@Repository
public class CompanyRepository {
	
	@Autowired
	MongoDatabase database;
	
	public UpdateResult insert(ObjectId id, List<Bson> insert,
			UpdateOptions options) {
		MongoCollection<Document> mongoClient = database.getCollection("Company");
		UpdateResult result = null;
		try {
			result = mongoClient.updateOne(Filters.lt("_id", id), insert, options);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public UpdateResult addElement(Bson update, BasicDBObject query , UpdateOptions options) {
		MongoCollection<Document> mongoClient = database.getCollection("Company");
		UpdateResult result = null;
		try {
			if(options != null) {
				result = mongoClient.updateOne(query, update , options);
			}else {
				result = mongoClient.updateOne(query, update);
			}
		} catch (Exception e) {
			throw new InternalServerException("Can't update. System is error!");
		}
		return result;
	}
	public FindIterable<Document> search(BasicDBObject query) {
		MongoCollection<Document> mongoClient = database.getCollection("Company");
		return mongoClient.find(query);
	}
	public AggregateIterable<Document> showDB(List<Bson> query) {
		MongoCollection<Document> mongoClient = database.getCollection("Company");
		AggregateIterable<Document> result = null;
		try {
			result = mongoClient.aggregate(query);
		} catch (Exception e) {
			throw new InternalServerException("Can't retrive. System is error!");
		}
		return result;
	}
}
