package com.spring.demo2.repository;

import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@Repository
public class TourRepository {

	@Autowired
	private MongoDatabase database;
	
	public Iterator<Document> showDBTour(Integer numSlot, String lang) {
		MongoCollection<Document> mongoClient1 = database.getCollection("Tour");

		Bson match_1 = new BasicDBObject("$match",
				new BasicDBObject("info.lang", lang).append("slot", new BasicDBObject("$gte", numSlot)));
		Bson project_1 = new BasicDBObject("$project", 
				new BasicDBObject("info",
						new BasicDBObject("$filter", 
								new BasicDBObject("input", "$info")
								.append("as", "item").append("cond",
										new BasicDBObject("$eq", 
												Stream.of("$$item.lang", lang).collect(Collectors.toList())
												))))
				.append("slot", 1)
				);
		Iterator<Document> result = mongoClient1.aggregate(Stream.of(match_1,project_1).collect(Collectors.toList())).iterator();
		return result;
	}
	
}
