package com.example.demo.repository;

import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import com.example.demo.exception.InternalServerException;
import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

@Repository
public class PersonRepository {


	public UpdateResult insert(MongoCollection<Document> mongoClient, ObjectId id, List<Bson> insert,
			UpdateOptions options) {
		UpdateResult result = null;
		try {
			result = mongoClient.updateOne(Filters.lt("_id", id), insert, options);
		} catch (Exception e) {
			throw new InternalServerException("Can't insert! Systems is error");
		}
		return result;
	}

	public UpdateResult update(MongoCollection<Document> mongoClient, List<Bson> update, BasicDBObject query) {
		UpdateResult result = null;
		try {
			result = mongoClient.updateMany(query, update);
		} catch (Exception e) {
			throw new InternalServerException("Can't update! Systems is error");
		}
		return result;
	}
	
	public UpdateResult addElement(MongoCollection<Document> mongoClient, Bson update, BasicDBObject query , UpdateOptions options) {
		UpdateResult result = null;
		try {
			if(options != null) {
				result = mongoClient.updateOne(query, update , options);
			}else {
				result = mongoClient.updateOne(query, update);
			}
		} catch (Exception e) {
			throw new InternalServerException("Can't add Verhicles! Systems appear a error");
		}
		return result;
	}


	public FindIterable<Document> search(MongoCollection<Document> mongoClient, BasicDBObject query, Integer skip,
			Integer limit) {
		return mongoClient.find(query).limit(limit).skip(skip);
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
