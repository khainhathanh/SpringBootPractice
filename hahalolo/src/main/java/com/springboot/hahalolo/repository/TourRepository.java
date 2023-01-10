package com.springboot.hahalolo.repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
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
		BasicDBObject match = new BasicDBObject();	
		BasicDBObject project = null;
		if(nameTour != null) {
			match.append("t101.tv151", new BasicDBObject("$regex",nameTour).append("$options", "i"));
		}
		if(typeTour != null) {
			match.append("tn120", typeTour);
		}
		if(langTour != null) {
			match.append("ft101.lang", langTour);
			project = new BasicDBObject("$project", 
					new BasicDBObject("id", 1)
					.append("ft101",
							new BasicDBObject("$filter", 
									new BasicDBObject("input","$ft101")
									.append("as", "item")
									.append("cond", new BasicDBObject("$eq",Stream.of("$$item.lang",langTour).collect(Collectors.toList())))
									)
							)
					.append("currency", 1)
					.append("dl146", 1)
					.append("dl147", 1)
					.append("dl148", 1)
					.append("dl149", 1)
					.append("lang", 1)
					.append("pt550", 1)
					.append("t101", 1)
					.append("t102", 1)
					.append("t550", 1)
					.append("tn120", 1)
					.append("tn123", 1)
					.append("tn127", 1)
					.append("tn130", 1)
					.append("tn131", 1)
					.append("tn133", 1)
					.append("tn134", 1)
					.append("tn135", 1)
					);
			
		}
		if(topicTour != null) {
			match.append("pt550", topicTour);
		}
		Bson matchs = new BasicDBObject("$match",match);
		Bson limits = new BasicDBObject("$limit",limit);
		Bson skips = new BasicDBObject("$skip",skip);
		List<Bson> query = new ArrayList<>();
		query.add(matchs);
		if(project !=null) {
			query.add(project);
		}
		query.add(limits);
		query.add(skips);
		Iterator<Document> iterator = mongoCollection.aggregate(query).iterator();
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
