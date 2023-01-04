package com.spring.demo2.repository;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;
import com.mongodb.client.result.UpdateResult;
import com.spring.demo2.entity.PriceOpen;

@Repository
public class PriceOpenRepository {
	
	@Autowired
	private MongoDatabase database;
	
	public Document insert(List<PriceOpen> listPriceOpen) {
		MongoCollection<Document> mongoClient = database.getCollection("PriceOpen");
		List<WriteModel<Document>> listWrite = new ArrayList<>();
		
		for (PriceOpen itemPriceOpen : listPriceOpen) {
			Bson filter = new BasicDBObject("tourID",itemPriceOpen.getTourID());
			Bson update = new BasicDBObject("$set",
					new BasicDBObject("tourID",itemPriceOpen.getTourID())
					.append("dateOpen", itemPriceOpen.getDateOpen())
					.append("currency", itemPriceOpen.getCurrency())
					.append("price", itemPriceOpen.getPrice()));
			UpdateOptions options = new UpdateOptions();
			options.upsert(true);
			listWrite.add(new UpdateOneModel<>(filter, update, options));
		}
		BulkWriteResult result = mongoClient.bulkWrite(listWrite);
		Document docResult = new Document("InsertCount",result.getUpserts().size())
				.append("ModifiCount", result.getModifiedCount());
		return docResult; 	
	}
	
	public Document update(PriceOpen priceOpen) {
		MongoCollection<Document> mongoClient = database.getCollection("PriceOpen");
		Bson filter = new BasicDBObject("tourID",priceOpen.getTourID());
		BasicDBObject dataUpdate = new BasicDBObject();
		if(!priceOpen.getDateOpen().isEmpty()) {
			dataUpdate.append("dateOpen", priceOpen.getDateOpen());
		}
		if( priceOpen.getCurrency() != null) {
			dataUpdate.append("currency", priceOpen.getCurrency());
		}
		if( priceOpen.getCurrency() != null) {
			dataUpdate.append("price", priceOpen.getPrice());
		}
		Bson update = new BasicDBObject("$set",dataUpdate);
		UpdateOptions option = new UpdateOptions();
		option.upsert(true);
		UpdateResult result = mongoClient.updateOne(filter, update, option);
		Document docResult = null;
		if(result.getUpsertedId() != null) {
			docResult = new Document("InsertId",result.getUpsertedId().toString());
		}else {
			docResult = new Document("ModifiCount",result.getModifiedCount());
		}
		return docResult; 	
	}
}
