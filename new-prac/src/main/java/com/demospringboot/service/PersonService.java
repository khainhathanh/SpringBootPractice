package com.demospringboot.service;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demospringboot.entity.Person;
import com.demospringboot.repository.PersonRepository;
import com.mongodb.client.FindIterable;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

@Service
public class PersonService {
	
	@Autowired
	PersonRepository personRepository;
	
	public List<String> insert(List<Person> listPerson){
		List<String> listID = new ArrayList<>();
		List<UpdateResult> listResult = personRepository.insert(listPerson);
		for(UpdateResult itemResult : listResult) {
			itemResult.getUpsertedId().asObjectId().getValue().toString();
			listID.add(itemResult.getUpsertedId().asObjectId().getValue().toHexString());
		}
		return listID;
	}
	
	public long update(Person personUpdate, Person personFilter){
		long modifiedCount = 0;
		UpdateResult result = personRepository.update(personUpdate,personFilter);
		if(result != null) {
			modifiedCount = result.getModifiedCount();
		}
		return modifiedCount;
	}
	
	public long delete(List<String> ids){
		long modifiedCount = 0;
		List<ObjectId> listObjectid = new ArrayList<>();
		
		// check ids.isEmty nham han che vong for khi ko truyen id vao
		if(!ids.isEmpty()) {
			for(String itemID: ids) {
				listObjectid.add(new ObjectId(itemID));
			}
		}
		DeleteResult result = personRepository.delete(listObjectid);
		if(result != null) {
			modifiedCount = result.getDeletedCount();
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
