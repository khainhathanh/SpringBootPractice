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
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

	public List<String> insert(List<Company> listCompany) {
		List<String> listID = new ArrayList<>();

		for (Company itemCompany : listCompany) {
			List<Bson> insert = Arrays.asList(new Document("$set", new Document()
					.append("names",
							Arrays.asList(new BasicDBObject("lang", itemCompany.getNames().get(0).getLang())
									.append("name", itemCompany.getNames().get(0).getName())))
					.append("code", itemCompany.getCode()).append("address", itemCompany.getAddress())
					.append("employeeNumb", itemCompany.getEmployeeNumb())
					.append("categories", itemCompany.getCategories()).append("currency", itemCompany.getCurrency())));
			UpdateOptions options = new UpdateOptions().upsert(true);
			UpdateResult result = companyRepository.insert(itemCompany.getId(), insert, options);
			listID.add(result.getUpsertedId().asObjectId().getValue().toHexString());
		}
		return listID;
	}

	// 6.Viết query update 1 company, thêm 1 name mới theo ngôn ngữ trong names ....
	public Long updateName(NameCompany names, ObjectId id) {
		BasicDBObject query = new BasicDBObject("_id", id);
		Long modifiedCount = new Long(-1);
		boolean matchCount = false;
		Bson update = null;
		Document docPer = companyRepository.search(query).first();
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
				UpdateResult result = companyRepository.addElement(update, query, null);
				if (result != null) {
					modifiedCount = result.getModifiedCount();
				}
			}
		}
		return modifiedCount;
	}

	// 7.Viết query xoá 1 name trong names của 1 company theo ngôn ngữ
	public Long updateName(String lang, ObjectId id) {
		BasicDBObject query = new BasicDBObject("_id", id);
		Long modifiedCount = new Long(-1);
		Document docPer = companyRepository.search(query).first();
		if (docPer != null) {
			Bson update = new Document("$pull", new Document("names", new BasicDBObject("lang", lang)));
			UpdateResult result = companyRepository.addElement(update, query, null);
			if (result != null) {
				modifiedCount = result.getModifiedCount();
			}
		}
		return modifiedCount;
	}

	// 13.Thống kê có bao nhiêu công ty, số lượng nhân viên của công ty
	public Pagination showCompany(Integer page, Integer limit) {
		Pagination pagination = new Pagination();
		Bson project = new BasicDBObject("$project", new BasicDBObject("_id", 0).append("code", 1).append("names", 1)
				.append("address", 1).append("categories", 1).append("employeeNumb", 1).append("currency", 1));
		Bson sort = new BasicDBObject("$sort", new BasicDBObject("code",1));
		Bson count = new BasicDBObject("$count","total");
		Bson facet = new BasicDBObject("$facet",
				new BasicDBObject("total",Stream.of(project, count).collect(Collectors.toList()))
				.append("data", Stream.of(project,sort).collect(Collectors.toList())));
		Bson project2 = new BasicDBObject("$project",
				new BasicDBObject("total",
						new BasicDBObject("$arrayElemAt",Stream.of("$total.total",0).collect(Collectors.toList())))
				.append("data", 1));
		Bson skip = null;
		Bson limits = null;
		Bson facet2 = null;
		
		if (page != null && limit != null) {
			if (page > 0 && limit > 0) {
				skip = new BasicDBObject("$skip", (page - 1) * limit);
				limits = new BasicDBObject("$limit", limit);
				facet2 = new BasicDBObject("$facet",
						new BasicDBObject("total",Stream.of(project, count).collect(Collectors.toList()))
						.append("data", Stream.of(project, sort, skip, limits).collect(Collectors.toList())));
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
		
		List<Bson> query = new ArrayList<>();
		if(facet2 != null) {
			query.add(facet2);
		}else {
			query.add(facet);
		}
		query.add(project2);
		
		
		
		
		AggregateIterable<Document> showCompany = companyRepository.showDB(query);
		Document doc = showCompany.first();
		List<Document> listDoc = null;
		if(showCompany.first() != null) {
			listDoc = doc.getList("data", Document.class);			
			pagination.setListDoc(listDoc);
			if (limit != null) {
				Integer totalRecord = doc.getInteger("total");;

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
