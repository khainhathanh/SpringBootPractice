package com.example.demo.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Company;
import com.example.demo.entity.NameCompany;
import com.example.demo.entity.Pagination;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ExecutionsException;
import com.example.demo.exception.InterruptedsException;
import com.example.demo.repository.CompanyRepository;
import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;

@Service
public class CompanyService {

	@Autowired
	CompanyRepository companyRepository;

	@Autowired
	MongoDatabase database;

	public List<String> insert(List<Company> listCompany) {
		List<String> listID = new ArrayList<>();
		MongoCollection<Document> mongoClient = database.getCollection("Company");

		for (Company itemCompany : listCompany) {
			List<Bson> insert = Arrays.asList(new Document("$set", new Document()
					.append("names",
							Arrays.asList(new BasicDBObject("lang", itemCompany.getNames().get(0).getLang())
									.append("name", itemCompany.getNames().get(0).getName())))
					.append("code", itemCompany.getCode()).append("address", itemCompany.getAddress())
					.append("employeeNumb", itemCompany.getEmployeeNumb())
					.append("categories", itemCompany.getCategories()).append("currency", itemCompany.getCurrency())));
			UpdateOptions options = new UpdateOptions().upsert(true);
			UpdateResult result = companyRepository.insert(mongoClient, itemCompany.getId(), insert, options);
			listID.add(result.getUpsertedId().asObjectId().getValue().toHexString());
		}
		return listID;
	}

	// 6.
	public Long updateName(NameCompany names, ObjectId id) {
		MongoCollection<Document> mongoClient = database.getCollection("Company");
		BasicDBObject query = new BasicDBObject("_id", id);
		Long modifiedCount = new Long(-1);
		boolean matchCount = false;
		Bson update = null;
		Document docPer = mongoClient.find(query).first();
		if (docPer != null) {

			List<Document> listNames = docPer.getList("names", Document.class);
			// duyet tat ca field List<verhicles> , neu ton tai thuoc tinh type cung loai se
			// bo qua
			for (Document item : listNames) {
				if (item.getString("lang").contentEquals(names.getLang())) {
					matchCount = true;
					break;
				}
			}

			if (matchCount == false) {
				update = new Document("$addToSet", new Document("names",
						new BasicDBObject("lang", names.getLang()).append("name", names.getName())));
				UpdateResult result = companyRepository.addElement(mongoClient, update, query, null);
				if (result != null) {
					modifiedCount = result.getModifiedCount();
				}
			}
		}
		return modifiedCount;
	}

	// 7.
	public Long updateName(String lang, ObjectId id) {
		MongoCollection<Document> mongoClient = database.getCollection("Company");
		BasicDBObject query = new BasicDBObject("_id", id);
		Long modifiedCount = new Long(-1);
		Document docPer = mongoClient.find(query).first();
		if (docPer != null) {
			Bson update = new Document("$pull", new Document("names", new BasicDBObject("lang", lang)));
			UpdateResult result = companyRepository.addElement(mongoClient, update, query, null);
			if (result != null) {
				modifiedCount = result.getModifiedCount();
			}
		}
		return modifiedCount;
	}

	// 13.
	public Pagination showCompany(Integer page, Integer limit) {
		Pagination pagination = new Pagination();
		MongoCollection<Document> mongoClient = database.getCollection("Company");
		List<Bson> query = new ArrayList<>();
		Bson project = new BasicDBObject("$project", new BasicDBObject("_id", 0).append("code", 1).append("names", 1)
				.append("address", 1).append("categories", 1).append("employeeNumb", 1).append("currency", 1));
		Bson sort = new BasicDBObject("$sort", new BasicDBObject("code",1));
		Bson skip = null;
		Bson limits = null;
		query.add(project);
		query.add(sort);
		if (page != null && limit != null) {
			if (page > 0 && limit > 0) {
				skip = new BasicDBObject("$skip", (page - 1) * limit);
				limits = new BasicDBObject("$limit", limit);
				pagination.setPageCurrent(page);
			} else {
				throw new BadRequestException("path page appear a error!");
			}
		} else if ((page != null && limit == null) || (limit != null & page == null)) {
			throw new BadRequestException("path page appear a error!");
		} else {
			pagination.setPageCurrent(null);
			pagination.setTotalPage(null);
		}
		
		Future<Iterator> future = null;
		if (skip != null && limit != null) {

			List<Bson> listTotal = new ArrayList<>();
			listTotal.add(project);

			ExecutorService executor = Executors.newCachedThreadPool();
			future = executor.submit(new Callable<Iterator>() {

				@Override
				public Iterator call() throws Exception {
					AggregateIterable<Document> record = companyRepository.showDB(mongoClient, listTotal);
					Iterator iteratorRecord = record.iterator();
					return iteratorRecord;
				}

			});
			executor.shutdown();

			query.add(skip);
			query.add(limits);
		}
		
		
		AggregateIterable<Document> showCompany = companyRepository.showDB(mongoClient, query);
		
		List<Document> listDoc = null;
		if(showCompany.first() != null) {
			Iterator iterator = showCompany.iterator();
			listDoc = new ArrayList<>();
			while (iterator.hasNext()) {
				listDoc.add((Document) iterator.next());
			}
			
			pagination.setListDoc(listDoc);
			if (limit != null) {
				Integer totalRecord = 0;

				try {
					Iterator iteratorTotalRecord = future.get();
					while (iteratorTotalRecord.hasNext()) {
						iteratorTotalRecord.next();
						totalRecord++;
					}
				} catch (InterruptedException e) {
					throw new InterruptedsException("a thread of a task is interrupted");
				} catch (ExecutionException e) {
					throw new ExecutionsException("Attempting to retrieve the result of a task that aborted");
				}

				if (totalRecord <= limit) {
					totalRecord = limit;
					pagination.setTotalPage(totalRecord / limit);
				} else {
					if (totalRecord % limit > 0) {
						pagination.setTotalPage((totalRecord / limit) + 1);
					} else {
						pagination.setTotalPage(totalRecord / limit);
					}
				}
			}
		}
		
		return pagination;
	}

}
