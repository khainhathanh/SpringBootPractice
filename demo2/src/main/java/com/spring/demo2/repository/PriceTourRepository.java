package com.spring.demo2.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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
import com.spring.demo2.entity.PriceTour;

@Repository
public class PriceTourRepository {
	@Autowired
	private MongoDatabase database;

	@Autowired
	private ChangeStream changStream;

	public Iterator<Document> showDBPriceTour(Set<ObjectId> listID, String date) {
		MongoCollection<Document> mongoClient2 = database.getCollection("PriceTour");

		Bson match_2 = new BasicDBObject("$match",
				new BasicDBObject("tourID",
						new BasicDBObject("$in", listID))
								.append("$expr",
										new BasicDBObject("$and",
												Stream.of(
														new BasicDBObject("$lte", Stream
																.of("$dateApplyStart",
																		new BasicDBObject("$dateFromString",
																				new BasicDBObject("dateString", date)
																						.append("format", "%Y-%m-%d")))
																.collect(Collectors.toList())),
														new BasicDBObject("$gte", Stream
																.of("$dateApplyEnd",
																		new BasicDBObject("$dateFromString",
																				new BasicDBObject("dateString", date)
																						.append("format", "%Y-%m-%d")))
																.collect(Collectors.toList())))
														.collect(Collectors.toList()))));
		Iterator<Document> result = mongoClient2.aggregate(Arrays.asList(match_2)).iterator();
		return result;
	}

	public Document insert(List<PriceTour> listPriceTour) {
		MongoCollection<Document> mongoClient = database.getCollection("PriceTour");
		List<WriteModel<Document>> listWrite = new ArrayList<>();
		for (PriceTour itemPriceTour : listPriceTour) {
			Document document = new Document("tourID", itemPriceTour.getTourID())
					.append("price", itemPriceTour.getPrice()).append("currency", itemPriceTour.getCurrency())
					.append("dateApplyStart", itemPriceTour.getDateApplyStart())
					.append("dateApplyEnd", itemPriceTour.getDateApplyEnd());
			listWrite.add(new InsertOneModel<>(document));
		}
		
		// ChangeStream collection PriceTour
		MongoCursor<ChangeStreamDocument<Document>> changStreams = changStream.changeStreamData(mongoClient);
		
		BulkWriteResult result = mongoClient.bulkWrite(listWrite);
		
		//Get data ChangeStream
		ChangeStreamDocument<Document> event = changStreams.tryNext();
		List<Document> listDocPrice = new ArrayList<>();
		while (event != null) {
			Document docPriceTour = event.getFullDocument();
			listDocPrice.add(docPriceTour);
			event = changStreams.tryNext();
		}
		Document docResult = new Document("InsertCount", result.getInsertedCount()).append("listDocPrice",
				listDocPrice);
		return docResult;
	}

	public Document update(PriceTour priceTour) {
		MongoCollection<Document> mongoClient = database.getCollection("PriceTour");
		BasicDBObject query = new BasicDBObject("_id", priceTour.getId());
		BasicDBObject dataUpdate = new BasicDBObject();
		if (priceTour.getPrice() != null) {
			dataUpdate.append("price", priceTour.getPrice());
		}
		if (priceTour.getCurrency() != null) {
			dataUpdate.append("currency", priceTour.getCurrency());
		}
		if (priceTour.getDateApplyStart() != null) {
			dataUpdate.append("dateApplyStart", priceTour.getDateApplyStart());
		}
		if (priceTour.getDateApplyEnd() != null) {
			dataUpdate.append("dateApplyEnd", priceTour.getDateApplyEnd());
		}
		Bson update = new BasicDBObject("$set",dataUpdate);
		
		// ChangeStream collection PriceTour
		MongoCursor<ChangeStreamDocument<Document>> changStreams = changStream.changeStreamData(mongoClient);
		UpdateResult result = mongoClient.updateOne(query, update);
		
		//Get data ChangeStream
		ChangeStreamDocument<Document> event = changStreams.tryNext();
		Document docPriceTour = null;
		if (event != null) {
			docPriceTour = event.getFullDocument();
		}
		Document docResult = new Document("ModifiedCount", result.getModifiedCount()).append("docPrice",
				docPriceTour);
		return docResult;
	}

	public Document find(String tourID) {
		MongoCollection<Document> mongoClient = database.getCollection("PriceTour");
		Bson query = new BasicDBObject("tourID", tourID);
		Document doc = mongoClient.find(query).first();
		return doc;
	}

	public Document find(Object id) {
		MongoCollection<Document> mongoClient = database.getCollection("PriceTour");
		Bson query = new BasicDBObject("_id", id);
		Document doc = mongoClient.find(query).first();
		return doc;
	}

}
