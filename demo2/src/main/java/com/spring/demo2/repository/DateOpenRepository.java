package com.spring.demo2.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.result.UpdateResult;
import com.spring.demo2.entity.DateOpen;

@Repository
public class DateOpenRepository {
	@Autowired
	private MongoDatabase database;
	
	@Autowired
	private ChangeStream changStream;

	public Document showDBDateOpen(Set<ObjectId> listID, String date, Integer skip, Integer limit) {
		MongoCollection<Document> mongoClient3 = database.getCollection("DateOpen");
		Bson match_3 = new BasicDBObject("$match", 
				new BasicDBObject("tourID", new BasicDBObject("$in",listID))
				.append("dateAvailable", date));
		Bson sort = new BasicDBObject("$sort",
				new BasicDBObject("_id",1));
		Bson skips = new BasicDBObject("$skip",skip);
		Bson limits = new BasicDBObject("$limit",limit);
		Bson count = new BasicDBObject("$count","recordTotal");
		Bson project = new BasicDBObject("$project", 
				new BasicDBObject("listDoc",1)
				.append("recordTotal", 
						new BasicDBObject("$arrayElemAt", Stream.of("$recordTotal.recordTotal",0).collect(Collectors.toList()))));
		Bson facet = new BasicDBObject("$facet",
				new BasicDBObject("listDoc",Stream.of(match_3, sort, skips, limits).collect(Collectors.toList()))
				.append("recordTotal", Stream.of(match_3,count).collect(Collectors.toList()))
				);
		Document result = mongoClient3.aggregate(Stream.of(facet, project).collect(Collectors.toList())).first();
		return result;
	}
	
	public Document insert(List<DateOpen> listDateOpen) {
		MongoCollection<Document> mongoClient = database.getCollection("DateOpen");
		List<WriteModel<Document>> listWrite = new ArrayList<>();
		for (DateOpen itemDateOpen : listDateOpen) {
			Document document = new Document("tourID", itemDateOpen.getTourID())
					.append("dateAvailable", itemDateOpen.getDateAvailable())
					.append("status", itemDateOpen.getStatus());
			listWrite.add(new InsertOneModel<>(document));
		}
		MongoCursor<ChangeStreamDocument<Document>> changStreams = changStream.changeStreamData(mongoClient);
		List<Document> listDocDateOpen = new ArrayList<>();
		BulkWriteResult result = mongoClient.bulkWrite(listWrite);
		ChangeStreamDocument<Document> event = changStreams.tryNext();
		while (event != null) {
			Document docDateOpen = event.getFullDocument();
			listDocDateOpen.add(docDateOpen);
			event = changStreams.tryNext();
		}
		Document docResult = new Document("InsertCount", result.getInsertedCount()).append("listDocDate",
				listDocDateOpen);
		return docResult;
	}
	
	public Document update(DateOpen dateOpen) {
		MongoCollection<Document> mongoClient = database.getCollection("DateOpen");
		BasicDBObject query = new BasicDBObject("_id", dateOpen.getId());
		BasicDBObject dataUpdate = new BasicDBObject();
		if (!dateOpen.getDateAvailable().isEmpty()) {
			dataUpdate.append("dateAvailable", dateOpen.getDateAvailable());
		}
		if (dateOpen.getStatus() != null) {
			dataUpdate.append("status", dateOpen.getStatus());
		}
		Bson update = new BasicDBObject("$set",dataUpdate);

		MongoCursor<ChangeStreamDocument<Document>> changStreams = changStream.changeStreamData(mongoClient);
		UpdateResult result = mongoClient.updateOne(query, update);
		ChangeStreamDocument<Document> event = changStreams.tryNext();
		Document docPriceTour = null;
		if (event != null) {
			docPriceTour = event.getFullDocument();
		}
		Document docResult = new Document("ModifiedCount", result.getModifiedCount()).append("docDate",
				docPriceTour);
		return docResult;
	}
}
