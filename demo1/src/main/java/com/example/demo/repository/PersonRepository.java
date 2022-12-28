package com.example.demo.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Person;
import com.example.demo.entity.Verhicles;
import com.mongodb.BasicDBObject;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;
import com.mongodb.client.result.UpdateResult;

@Repository
public class PersonRepository {

	@Autowired
	MongoDatabase database;

	public Integer insert(List<Person> listPerson) {
		MongoCollection<Document> mongoClient = database.getCollection("Person");
		List<WriteModel<Document>> listWrite = new ArrayList<>();
		for (Person itemPerson : listPerson) {
			Document document = new Document("firstName",itemPerson.getFirstName())
					.append("lastName", itemPerson.getLastName())
					.append("age", itemPerson.getAge())
					.append("sex", itemPerson.getSex())
					.append("languages", itemPerson.getLanguages())
					.append("verhicles",
							Arrays.asList(new Document("type", itemPerson.getVerhicles().get(0).getType())
									.append("status", itemPerson.getVerhicles().get(0).getStatus())))
					.append("dateofbirth", itemPerson.getDateofbirth())
					.append("email", itemPerson.getEmail())
					.append("phone", itemPerson.getPhone()) ;
			listWrite.add(new InsertOneModel<>(document));
		}
		BulkWriteResult result = mongoClient.bulkWrite(listWrite);
		Integer count = result.getInsertedCount();
		return count;
	}

	public Long updateFullName() {
		MongoCollection<Document> mongoClient = database.getCollection("Person");
		List<Bson> update = Arrays.asList(new Document("$set", new Document("fullName",
				new Document("$concat", Stream.of("$firstName", " ", "$lastName").collect(Collectors.toList())))));
		UpdateResult result = mongoClient.updateMany(new BasicDBObject(), update);
		Long modifiedCount = result.getModifiedCount();
		return modifiedCount;
	}

	public Long addElement(String type, ObjectId id, Integer status) {
		MongoCollection<Document> mongoClient = database.getCollection("Person");
		BasicDBObject query = new BasicDBObject("_id", id);
		Bson update = new Document("$set", new Document("verhicles.$[x].status", status));
		List<Bson> filter = Arrays.asList(new Document("x.type", type));
		UpdateOptions options = new UpdateOptions();
		options.arrayFilters(filter);
		UpdateResult result = mongoClient.updateOne(query, update, options);
		Long modifiedCount = result.getModifiedCount();
		return modifiedCount;
	}
	
	public Long updateOnePerson(Person personUpdate, ObjectId id) {
		MongoCollection<Document> mongoClient = database.getCollection("Person");
		BasicDBObject query = new BasicDBObject("_id", id);
		Bson update = new Document("$set",
				new Document("verhicles.$[x].status", personUpdate.getVerhicles().get(0).getStatus()).append("age",
						personUpdate.getAge())).append("$addToSet",
								new Document("languages", personUpdate.getLanguages().get(0)));
		List<Bson> filter = Arrays.asList(new Document("x.type", personUpdate.getVerhicles().get(0).getType()));
		UpdateOptions options = new UpdateOptions();
		options.arrayFilters(filter);
		UpdateResult result = mongoClient.updateOne(query, update, options);
		Long modifiedCount = result.getModifiedCount();
		return modifiedCount;
	}

	public Long addElement(Verhicles verhicles, ObjectId id) {
		MongoCollection<Document> mongoClient = database.getCollection("Person");
		BasicDBObject query = new BasicDBObject("_id", id);
		Bson update = new Document("$$addToSet", new Document("verhicles",
				new BasicDBObject("type", verhicles.getType()).append("status", verhicles.getStatus())));
		UpdateResult result = mongoClient.updateOne(query, update);
		Long modifiedCount = result.getModifiedCount();
		return modifiedCount;
	}

	public Document search(ObjectId id) {
		MongoCollection<Document> mongoClient = database.getCollection("Person");
		BasicDBObject query = new BasicDBObject("_id", id);
		return mongoClient.find(query).first();
	}

	public Document showDB(Integer page, Integer limit) {
		MongoCollection<Document> mongoClient = database.getCollection("Person");
		Bson unwind = new BasicDBObject("$unwind", "$$languages");
		Bson group = new BasicDBObject("$group", new BasicDBObject("_id", "$languages"));
		Bson countLang = new BasicDBObject("$count", "languages");
		Bson sort = new BasicDBObject("$sort", new BasicDBObject("_id", 1));
		Bson project = new BasicDBObject("$project",
				new BasicDBObject("countLang",
						new BasicDBObject("$arrayElemAt",
								Stream.of("$countLang.languages", 0).collect(Collectors.toList()))).append("showLang",
										1));
		Bson skip = new BasicDBObject("$skip", (page - 1) * limit);
		Bson limits = new BasicDBObject("$limit", limit);
		;
		Bson facet = new BasicDBObject("$facet",
				new BasicDBObject("countLang", Stream.of(unwind, group, countLang).collect(Collectors.toList()))
						.append("showLang", Stream.of(unwind, group, sort, skip, limits).collect(Collectors.toList())));
		Document result = mongoClient.aggregate(Stream.of(facet, project).collect(Collectors.toList())).first();
		return result;
	}
	
	public Document showDB10(String fullName, Integer monthStart, Integer monthEnd, Integer page, Integer limit) {
		MongoCollection<Document> mongoClient = database.getCollection("Person");
		Bson project = new BasicDBObject("$project",
				new BasicDBObject("month", new BasicDBObject("$month", "$dateofbirth")).append("fullName", 1)
						.append("age", 1).append("sex", 1).append("dateofbirth", 1).append("languages", 1)
						.append("verhicles", 1).append("verhicles", 1).append("phone", 1));
		Bson match = new BasicDBObject("$match",
				new BasicDBObject("$and",
						Arrays.asList(new BasicDBObject("fullName", new BasicDBObject("$regex", fullName))
								.append("month", new BasicDBObject("$gte", monthStart))
								.append("month", new BasicDBObject("$lte", monthEnd)))));
		Bson sort = new BasicDBObject("$sort", new BasicDBObject("_id", 1));
		Bson count = new BasicDBObject("$count", "total");
		Bson project2 = new BasicDBObject("$project",
				new BasicDBObject("total",
						new BasicDBObject("$arrayElemAt", Stream.of("$total.total", 0).collect(Collectors.toList())))
								.append("data", 1));
		Bson skip = new BasicDBObject("$skip", (page - 1) * limit);
		Bson limits = new BasicDBObject("$limit", limit);
		Bson facet = new BasicDBObject("$facet",
				new BasicDBObject("total", Stream.of(project, match, count).collect(Collectors.toList())).append("data",
						Stream.of(project, match, sort, skip, limits).collect(Collectors.toList())));
		Document result = mongoClient.aggregate(Stream.of(facet, project2).collect(Collectors.toList())).first();
		return result;
	}
	
	public Document showDB11(String mailRegex, String sex, String languages, Integer page, Integer limit) {
		MongoCollection<Document> mongoClient = database.getCollection("Person");
		Bson project = new BasicDBObject("$project", new BasicDBObject("items.fullName", 1).append("items.phone", 1)
				.append("items.languages", 1).append("items.email", 1));
		Bson match = new BasicDBObject("$match", new BasicDBObject("email", new BasicDBObject("$regex", mailRegex))
				.append("sex", sex).append("languages", languages));
		Bson group = new BasicDBObject("$group",
				new BasicDBObject("_id", "$age").append("items", new BasicDBObject("$push", "$$ROOT")));
		Bson count = new BasicDBObject("$count", "total");
		Bson sort = new BasicDBObject("$sort", new BasicDBObject("_id", 1));
		Bson project2 = new BasicDBObject("$project",
				new BasicDBObject("total",
						new BasicDBObject("$arrayElemAt", Stream.of("$total.total", 0).collect(Collectors.toList())))
								.append("data", 1));
		Bson skip = new BasicDBObject("$skip", (page - 1) * limit);
		Bson limits = new BasicDBObject("$limit", limit);
		Bson facet = new BasicDBObject("$facet",
				new BasicDBObject("total", Stream.of(match, group, count).collect(Collectors.toList())).append("data",
						Stream.of(match, group, project, sort, skip, limits).collect(Collectors.toList())));
		Document result = mongoClient.aggregate(Stream.of(facet, project2).collect(Collectors.toList())).first();
		return result;
	}
	
	public Document showDB12(String mailRegex, String sex, String languages, Integer page, Integer limit) {
		MongoCollection<Document> mongoClient = database.getCollection("Person");
		BasicDBObject project1 = new BasicDBObject("$project", new BasicDBObject("items.fullName", 1)
				.append("items.phone", 1).append("items.languages", 1).append("items.email", 1));
		BasicDBObject match = new BasicDBObject("$match",
				new BasicDBObject("email", new BasicDBObject("$regex", mailRegex)).append("sex", sex)
						.append("languages", languages));
		BasicDBObject group = new BasicDBObject("$group",
				new BasicDBObject("_id", "$age").append("items", new BasicDBObject("$push", "$$ROOT")));
		Bson count = new BasicDBObject("$count", "total");
		Bson sort = new BasicDBObject("$sort", new BasicDBObject("_id", 1));
		Bson project2 = new BasicDBObject("$project",
				new BasicDBObject("totalrecord",
						new BasicDBObject("$arrayElemAt",
								Stream.of("$totalrecord.totalrecord", 0).collect(Collectors.toList())))
										.append("totalperson",
												new BasicDBObject("$arrayElemAt",
														Stream.of("$totalperson.totalperson", 0)
																.collect(Collectors.toList())))
										.append("data", 1).append("total", new BasicDBObject("$arrayElemAt",
												Stream.of("$totalgroup.total", 0).collect(Collectors.toList()))));
		BasicDBObject skip = new BasicDBObject("$skip", (page - 1) * limit);
		
		BasicDBObject limits = new BasicDBObject("$limit", limit);
		Bson facet = new BasicDBObject("$facet",
				new BasicDBObject("totalrecord", Arrays.asList(new BasicDBObject("$count", "totalrecord")))
						.append("totalperson",
								Stream.of(match, new BasicDBObject("$count", "totalperson"))
										.collect(Collectors.toList()))
						.append("data",
								Stream.of(match, group, project1, sort, skip, limits).collect(Collectors.toList()))
						.append("totalgroup", Stream.of(match, group, count).collect(Collectors.toList())));
		Document result = mongoClient.aggregate(Stream.of(facet, project2).collect(Collectors.toList())).first();
		return result;
	}

}
