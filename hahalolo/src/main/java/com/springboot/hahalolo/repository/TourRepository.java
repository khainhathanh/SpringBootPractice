package com.springboot.hahalolo.repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.json.JsonWriterSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.springboot.hahalolo.exception.InternalServerException;


@Repository
public class TourRepository {
	
	private static Logger logger = Logger.getLogger(TourRepository.class);
	
	@Autowired
	private MongoDatabase database;
	
	@Autowired
	private JsonWriterSettings settings;
	
	@SuppressWarnings("unchecked")
	public <T> List<Map<String,T>> showList(String nameTour, Integer typeTour, String langTour, String topicTour, Integer skip, Integer limit) {
		MongoCollection<Document> mongoCollection = database.getCollection("T100");
		BasicDBObject query = new BasicDBObject();
		if(nameTour != null) {
			query.append("t101.tv151", new BasicDBObject("$regex",nameTour).append("$options", "i"));
		}
		if(typeTour != null) {
			query.append("tn120", typeTour);
		}
		if(langTour != null) {
			query.append("ft101.lang", langTour);
		}
		if(topicTour != null) {
			query.append("pt550", topicTour);
		}
		Iterator<Document> iterator = mongoCollection.find(query).skip(skip).limit(limit).iterator();
		List<Map<String,T>> listMap = new ArrayList<>();
		Map<String,T> mapper = null;
		while(iterator.hasNext()) {
			Document docTour = iterator.next();
			ObjectMapper objectMapper =  new ObjectMapper();
			try {
				mapper = objectMapper.readValue(docTour.toJson(settings), Map.class);
			} catch (JsonMappingException e) {
				logger.error(e);
				throw new InternalServerException("lỗi chuyển đổi dữ liệu");
			} catch (JsonProcessingException e) {
				logger.error(e);
				throw new InternalServerException("lỗi chuyển đổi dữ liệu");
			}			
			listMap.add(mapper);			
		}
		return listMap;
	}
}
