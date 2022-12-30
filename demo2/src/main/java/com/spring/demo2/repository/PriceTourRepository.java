package com.spring.demo2.repository;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@Repository
public class PriceTourRepository  {
	@Autowired
	private MongoDatabase database;
	
	public Iterator<Document> showDBPriceTour(Set<ObjectId> listID, String date) {
		MongoCollection<Document> mongoClient2 = database.getCollection("PriceTour");
		
		Bson match_2 = new BasicDBObject("$match", 
				new BasicDBObject("tourID",
						new BasicDBObject("$in",listID))
				.append("$expr", 
						new BasicDBObject("$and",Stream.of(
								new BasicDBObject("$lte",Stream.of("$dateApplyStart", 
										new BasicDBObject("$dateFromString",
												new BasicDBObject("dateString",date)
												.append("format", "%Y-%m-%d"))).collect(Collectors.toList())),
								new BasicDBObject("$gte",Stream.of("$dateApplyEnd", 
										new BasicDBObject("$dateFromString",
												new BasicDBObject("dateString",date)
												.append("format", "%Y-%m-%d"))).collect(Collectors.toList()))
								).collect(Collectors.toList()))));
		Iterator<Document> result = mongoClient2.aggregate(Arrays.asList(match_2)).iterator();
		return result;
	}

	
}
