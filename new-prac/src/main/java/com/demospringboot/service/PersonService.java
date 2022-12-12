package com.demospringboot.service;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demospringboot.entity.Person;
import com.demospringboot.repository.PersonRepository;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

@Service
public class PersonService {
	
	@Autowired
	PersonRepository personRepository;
	
	@Autowired
	MongoDatabase database;
	
	public List<String> insert(List<Person> listPerson){
		List<String> listID = new ArrayList<>();
		MongoCollection<Document> mongoClient = database.getCollection("Person");
		List<UpdateResult> listResult = personRepository.insert(listPerson,mongoClient);
		for(UpdateResult itemResult : listResult) {
			itemResult.getUpsertedId().asObjectId().getValue().toString();
			listID.add(itemResult.getUpsertedId().asObjectId().getValue().toHexString());
		}
		return listID;
	}
	
	public Long update(Person personUpdate, Person personFilter){
		Long modifiedCount = new Long(-1);
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
		if (docPer != null) {
			UpdateResult result = personRepository.update(personUpdate, personFilter, mongoClient, query);
			if(result != null) {
				modifiedCount = result.getModifiedCount();
			}
		}
		return modifiedCount;
	}
	
	public Long delete(List<String> ids){
		Long modifiedCount = null;
		List<ObjectId> listObjectid = new ArrayList<>();
		MongoCollection<Document> mongoClient = database.getCollection("Person");
		BasicDBObject query = new BasicDBObject();
		// check ids.isEmty nham han che vong for khi ko truyen id vao
		if(!ids.isEmpty()) {
			for(String itemID: ids) {
				listObjectid.add(new ObjectId(itemID));
			}
			query.append("_id", new BasicDBObject("$in", listObjectid));
		}
		DeleteResult result = personRepository.delete(mongoClient,query);
		if(result != null) {
			modifiedCount = result.getDeletedCount();
		}else {
			modifiedCount = new Long(-1);
		}
		return modifiedCount;
	}
	
	public List<Document> search(Person personFilter){
		List<Document> listDoc = new ArrayList<>();
		FindIterable<Document> results =  personRepository.search(personFilter);
		if(results != null) {
			for(Document itemDoc : results) {
				listDoc.add(itemDoc);
			}
		}
		return listDoc;
	}
}
