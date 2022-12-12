package com.demospringboot.repository;

import java.util.ArrayList;
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

	public List<UpdateResult> insert(List<Person> listPerson) {
		MongoCollection<Document> mongoClient = database.getCollection("Person");
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
//		Person per = new Person();	
//		System.out.print(result.toString());
//		ObjectId objId = result.getUpsertedId().asObjectId().getValue();
//		Document docPer = mongoClient.find(Filters.eq("_id", objId)).first();
//		if (docPer != null) {
//			per.setAge(docPer.getInteger("age"));
//			per.setName(docPer.getString("name"));
//			per.setSex(docPer.getString("sex"));
//		}
		return listResult;
	}

	public UpdateResult update(Person personUpdate, Person personFilter) {

		MongoCollection<Document> mongoClient = database.getCollection("Person");
		BasicDBObject query = new BasicDBObject();
		if(personFilter.getId() != null) {
			query.put("_id", personFilter.getId());
		}
		if(personFilter.getName() != null) {
			query.put("name", personFilter.getName());
		}
		if(personFilter.getAge() != null) {
			query.put("age", personFilter.getAge());
		}
		if(personFilter.getSex() != null) {
			query.put("sex", personFilter.getSex());
		}
		Document docPer = mongoClient.find(query).first();
		UpdateResult result = null;
		if (docPer != null) {
			Bson update = new Document("$set", new Document().append("name", personUpdate.getName())
					.append("age", personUpdate.getAge()).append("sex", personUpdate.getSex()));
			result = mongoClient.updateMany(query, update);
		}
		return result;
	}

	public DeleteResult delete(List<ObjectId> ids) {
		MongoCollection<Document> mongoClient = database.getCollection("Person");
		DeleteResult result = null;
		BasicDBObject query = new BasicDBObject();
		if(!ids.isEmpty()) {
			query.append("_id", new BasicDBObject("$in",ids));
		}//Filters.eq("_id", item)
		result = mongoClient.deleteMany(query);
		return result;
	}
	
	public FindIterable<Document> search(Person personFilter){
		MongoCollection<Document> mongoClient = database.getCollection("Person");
		BasicDBObject query = new BasicDBObject();
		if(personFilter.getId() != null) {
			query.put("_id", personFilter.getId());
		}
		if(personFilter.getName() != null) {
			query.put("name", personFilter.getName());
		}
		if(personFilter.getAge() != null) {
			query.put("age", personFilter.getAge());
		}
		if(personFilter.getSex() != null) {
			query.put("sex", personFilter.getSex());
		}
//		FindIterable<Document> list = mongoClient.find(query);
		return mongoClient.find(query);
	}

}
