package com.spring.demo2.repository;

import org.bson.Document;
import org.springframework.stereotype.Component;

import com.mongodb.client.ChangeStreamIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.FullDocument;

@Component
public class ChangeStream {
	
	public MongoCursor<ChangeStreamDocument<Document>> changeStreamData(MongoCollection<Document> mongoClient) {
		ChangeStreamIterable<Document> changStream = mongoClient.watch().fullDocument(FullDocument.UPDATE_LOOKUP);
		MongoCursor<ChangeStreamDocument<Document>> cursor = changStream.iterator();
		return cursor;
	}
}
