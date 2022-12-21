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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Employee;
import com.example.demo.entity.Pagination;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ExecutionsException;
import com.example.demo.exception.InterruptedsException;
import com.example.demo.repository.EmployeeRepository;
import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;

@Service
public class EmployeeService {

	@Autowired
	EmployeeRepository employeeRepository;

	@Autowired
	MongoDatabase database;

	public List<String> insert(List<Employee> listEmployee) {
		List<String> listID = new ArrayList<>();
		MongoCollection<Document> mongoClient = database.getCollection("Employee");

		for (Employee itemEmployee : listEmployee) {
			List<Bson> insert = Arrays.asList(new Document("$set",
					new Document().append("personId", itemEmployee.getPersonId())
							.append("companyId", itemEmployee.getCompanyId()).append("salary", itemEmployee.getSalary())
							.append("startDate", itemEmployee.getStartDate())
							.append("currency", itemEmployee.getCurrency())));
			UpdateOptions options = new UpdateOptions().upsert(true);
			UpdateResult result = employeeRepository.insert(mongoClient, itemEmployee.getId(), insert, options);
			listID.add(result.getUpsertedId().asObjectId().getValue().toHexString());
		}
		return listID;
	}

	// 14.
	public Pagination showEmployees(Integer year, Integer page, Integer limit) {
		Pagination pagination = new Pagination();
		MongoCollection<Document> mongoClient = database.getCollection("Employee");
		List<Bson> query = new ArrayList<>();
		Bson project = new BasicDBObject("$project", new BasicDBObject("_id", 0)
				.append("companyId", 1)
				.append("year", new BasicDBObject("$year","$startDate"))
				.append("salary", 1));
		Bson match = new BasicDBObject("$match", new BasicDBObject("year", year));
		Bson group = new BasicDBObject("$group", new BasicDBObject("_id", Stream.of("$companyId","$year").collect(Collectors.toList()))
				.append("salary", new BasicDBObject("$sum","$salary"))
				.append("numberofEmployee", new BasicDBObject("$sum",1)));
		Bson sort = new BasicDBObject("$sort", new BasicDBObject("_id",1));
		Bson skip = null;
		Bson limits = null;
		
		
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
		
		query.add(project);
		query.add(match);
		query.add(group);
		query.add(sort);
		
		Future<Iterator> future = null;
		if (skip != null && limit != null) {

			List<Bson> listTotal = new ArrayList<>();
			listTotal.add(project);
			listTotal.add(match);
			listTotal.add(group);

			ExecutorService executor = Executors.newCachedThreadPool();
			future = executor.submit(new Callable<Iterator>() {

				@Override
				public Iterator call() throws Exception {
					AggregateIterable<Document> record = employeeRepository.showDB(mongoClient, listTotal);
					Iterator iteratorRecord = record.iterator();
					return iteratorRecord;
				}

			});
			executor.shutdown();

			query.add(skip);
			query.add(limits);
		}
		
		AggregateIterable<Document> showEmployees = employeeRepository.showDB(mongoClient, query);

		List<Document> listDoc = null;
		if(showEmployees.first() != null) {
			Iterator iterator = showEmployees.iterator();
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

	// 15.
	public Pagination showEmpl(Integer year, Integer page, Integer limit) {
		Pagination pagination = new Pagination();
		MongoCollection<Document> mongoClient = database.getCollection("Employee");
		List<Bson> query = new ArrayList<>();
		Bson project = new BasicDBObject("$project", new BasicDBObject("_id", 0)
				.append("companyId", 1)
				.append("year", new BasicDBObject("$year","$startDate"))
				.append("salary", 1));
		Bson match = new BasicDBObject("$match", new BasicDBObject("year", year));
		Bson group = new BasicDBObject("$group", new BasicDBObject("_id", Stream.of("$companyId","$year").collect(Collectors.toList()))
				.append("salary", new BasicDBObject("$sum","$salary")));
		Bson sort = new BasicDBObject("$sort", new BasicDBObject("_id",1));
		
		Bson skip = null;
		Bson limits = null;
		
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
		
		query.add(project);
		query.add(match);
		query.add(group);
		query.add(sort);
		
		Future<Iterator> future = null;
		if (skip != null && limit != null) {

			List<Bson> listTotal = new ArrayList<>();
			listTotal.add(project);
			listTotal.add(match);
			listTotal.add(group);

			ExecutorService executor = Executors.newCachedThreadPool();
			future = executor.submit(new Callable<Iterator>() {

				@Override
				public Iterator call() throws Exception {
					AggregateIterable<Document> record = employeeRepository.showDB(mongoClient, listTotal);
					Iterator iteratorRecord = record.iterator();
					return iteratorRecord;
				}

			});
			executor.shutdown();

			query.add(skip);
			query.add(limits);
		}
		
		AggregateIterable<Document> showEmpl = employeeRepository.showDB(mongoClient, query);
		
		List<Document> listDoc = null;
		if(showEmpl.first() != null) {
			Iterator iterator = showEmpl.iterator();
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

	// 16.
	public Pagination showEmployee(String category, Integer startYear, Integer endYear, Integer page, Integer limit) {
		Pagination pagination = new Pagination();
		MongoCollection<Document> mongoClient = database.getCollection("Employee");
		List<Bson> query = new ArrayList<>();
		Bson lookup = new BasicDBObject("$lookup", new BasicDBObject("from", "Company")
						.append("localField", "companyId")
						.append("foreignField", "code")
						.append("as", "companydata"));	
		Bson unwind = new BasicDBObject("$unwind","$companydata");
		Bson project = new BasicDBObject("$project", new BasicDBObject("_id", 0)
				.append("companyId", 1)
				.append("year", new BasicDBObject("$year","$startDate"))
				.append("salary", 1)
				.append("category", "$companydata.categories")
				);
		Bson match = new BasicDBObject("$match", new BasicDBObject("category", category)
							.append("$and", Stream.of(
									new BasicDBObject("year",
											new BasicDBObject("$gte",startYear)), 
									new BasicDBObject("year",
											new BasicDBObject("$lte",endYear))).collect(Collectors.toList())));
		Bson group = new BasicDBObject("$group", new BasicDBObject("_id", Stream.of("$category","$companyId").collect(Collectors.toList()))
				.append("salary", new BasicDBObject("$sum","$salary")));
		Bson sort = new BasicDBObject("$sort", new BasicDBObject("_id",1));
		Bson skip = null;
		Bson limits = null;
		
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
		
		query.add(lookup);
		query.add(unwind);
		query.add(project);
		query.add(match);
		query.add(group);
		query.add(sort);
		
		Future<Iterator> future = null;
		if (skip != null && limit != null) {

			List<Bson> listTotal = new ArrayList<>();
			listTotal.add(lookup);
			listTotal.add(unwind);
			listTotal.add(project);
			listTotal.add(match);
			listTotal.add(group);

			ExecutorService executor = Executors.newCachedThreadPool();
			future = executor.submit(new Callable<Iterator>() {

				@Override
				public Iterator call() throws Exception {
					AggregateIterable<Document> record = employeeRepository.showDB(mongoClient, listTotal);
					Iterator iteratorRecord = record.iterator();
					return iteratorRecord;
				}

			});
			executor.shutdown();

			query.add(skip);
			query.add(limits);
		}
		
		AggregateIterable<Document> listEmployee = employeeRepository.showDB(mongoClient, query);
		List<Document> listDoc = null;
		if(listEmployee.first() != null) {
			Iterator iterator = listEmployee.iterator();
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
