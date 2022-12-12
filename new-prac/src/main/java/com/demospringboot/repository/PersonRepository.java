package com.demospringboot.repository;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.demospringboot.entity.Person;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

@Repository
public class PersonRepository {

	@Autowired
	MongoDatabase database;

	public List<UpdateResult> insert(List<Person> listPerson, MongoCollection<Document> mongoClient) {
		List<UpdateResult> listResult = new ArrayList<>();
		UpdateResult result = null;
		for (Person itemPerson : listPerson) {
			Bson insert = new Document("$set", new Document().append("name", itemPerson.getName())
					.append("age", itemPerson.getAge()).append("sex", itemPerson.getSex()));
			UpdateOptions options = new UpdateOptions().upsert(true);
			try {
				result = mongoClient.updateOne(Filters.lt("_id", itemPerson.getId()), insert, options);
			} catch (Exception e) {
				e.printStackTrace();
			}
			listResult.add(result);
		}
		return listResult;
	}

	public UpdateResult update(Person personUpdate, Person personFilter, MongoCollection<Document> mongoClient,
			BasicDBObject query) {
		UpdateResult result = null;
		Bson update = new Document("$set", new Document().append("name", personUpdate.getName())
				.append("age", personUpdate.getAge()).append("sex", personUpdate.getSex()));
		try {
			result = mongoClient.updateMany(query, update);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public DeleteResult delete(MongoCollection<Document> mongoClient, BasicDBObject query) {
		DeleteResult result = null;
		try {
			result = mongoClient.deleteMany(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public FindIterable<Document> search(Person personFilter) {
		MongoCollection<Document> mongoClient = database.getCollection("Person");
		BasicDBObject query = new BasicDBObject();
		if (personFilter.getId() != null) {
			query.put("_id", personFilter.getId());
		}
		if (personFilter.getName() != null) {
			query.put("name", personFilter.getName());
		}
		if (personFilter.getAge() != null) {
			query.put("age", personFilter.getAge());
		}
		if (personFilter.getSex() != null) {
			query.put("sex", personFilter.getSex());
		}
//		FindIterable<Document> list = mongoClient.find(query);
		return mongoClient.find(query);
	}

}
