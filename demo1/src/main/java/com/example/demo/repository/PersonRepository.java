package com.example.demo.repository;

import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;

@Repository
public class PersonRepository {

	@Autowired
	MongoDatabase database;

	public UpdateResult insert(ObjectId id, List<Bson> insert) {
		MongoCollection<Document> mongoClient = database.getCollection("Person");
		UpdateOptions options = new UpdateOptions().upsert(true);
		UpdateResult result = mongoClient.updateOne(Filters.lt("_id", id), insert, options);
		return result;
	}

	public UpdateResult update(List<Bson> update, BasicDBObject query) {
		MongoCollection<Document> mongoClient = database.getCollection("Person");
		UpdateResult result = mongoClient.updateMany(query, update);
		return result;
	}

	public UpdateResult addElement(Bson update, BasicDBObject query, List<Bson> filter) {
		MongoCollection<Document> mongoClient = database.getCollection("Person");
		UpdateOptions options = new UpdateOptions();
		options.arrayFilters(filter);
		UpdateResult result = mongoClient.updateOne(query, update, options);
		return result;
	}

	public UpdateResult addElement(Bson update, BasicDBObject query) {
		MongoCollection<Document> mongoClient = database.getCollection("Person");
		UpdateResult result = mongoClient.updateOne(query, update);
		return result;
	}

	public Document search(BasicDBObject query) {
		MongoCollection<Document> mongoClient = database.getCollection("Person");
		return mongoClient.find(query).first();
	}

	public Document showDB(List<Bson> query) {
		MongoCollection<Document> mongoClient = database.getCollection("Person");
		Document result = mongoClient.aggregate(query).first();
		return result;
	}

}
