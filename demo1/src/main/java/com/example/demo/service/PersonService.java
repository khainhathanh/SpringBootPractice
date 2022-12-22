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

import com.example.demo.entity.Pagination;
import com.example.demo.entity.Person;
import com.example.demo.entity.Verhicles;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ExecutionsException;
import com.example.demo.exception.InterruptedsException;
import com.example.demo.repository.PersonRepository;
import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;

@Service
public class PersonService {

	@Autowired
	PersonRepository personRepository;

	@Autowired
	MongoDatabase database;

	public List<String> insert(List<Person> listPerson) {
		List<String> listID = new ArrayList<>();
		MongoCollection<Document> mongoClient = database.getCollection("Person");

		for (Person itemPerson : listPerson) {
			List<Bson> insert = Arrays.asList(new Document("$set",
					new Document().append("firstName", itemPerson.getFirstName())
							.append("lastName", itemPerson.getLastName()).append("age", itemPerson.getAge())
							.append("sex", itemPerson.getSex()).append("languages", itemPerson.getLanguages())
							.append("verhicles",
									Arrays.asList(new Document("type", itemPerson.getVerhicles().get(0).getType())
											.append("status", itemPerson.getVerhicles().get(0).getStatus())))
							.append("dateofbirth", itemPerson.getDateofbirth()).append("email", itemPerson.getEmail())
							.append("phone", itemPerson.getPhone()).append("fullName",
									new Document("$concat",
											Stream.of(itemPerson.getFirstName(), " ", itemPerson.getLastName())
													.collect(Collectors.toList())))));
			UpdateOptions options = new UpdateOptions().upsert(true);
			UpdateResult result = personRepository.insert(mongoClient, itemPerson.getId(), insert, options);
			listID.add(result.getUpsertedId().asObjectId().getValue().toHexString());
		}
		return listID;
	}

	// 2.Viết query thêm 1 verhicle mới trong bảng person
	public Long addElement(Verhicles verhicles, ObjectId id) {
		MongoCollection<Document> mongoClient = database.getCollection("Person");
		BasicDBObject query = new BasicDBObject("_id", id);
		Long modifiedCount = new Long(-1);
		boolean matchCount = false;// 1 <=> list trung type can update
		Document docPer = mongoClient.find(query).first();
		if (docPer != null) {
			Bson update = null;
			List<Document> listVerhicles = docPer.getList("verhicles", Document.class);
			// duyet tat ca field List<verhicles> , neu ton tai thuoc tinh type cung loai se
			// bo qua
			for (Document item : listVerhicles) {
				if (item.getString("type").contentEquals(verhicles.getType())) {
					matchCount = true;
					break;
				}
			}
			if (matchCount == false) {
				update = new Document("$addToSet", new Document("verhicles",
						new BasicDBObject("type", verhicles.getType()).append("status", verhicles.getStatus())));
				UpdateResult result = personRepository.addElement(mongoClient, update, query, null);
				if (result != null) {
					modifiedCount = result.getModifiedCount();
				}
			}
		}
		return modifiedCount;
	}

	// 3.Viết query update 1 verhicle trong bảng person thành ko sử dụng
	public Long updateVerhicles(String type, ObjectId id, Integer status) {
		MongoCollection<Document> mongoClient = database.getCollection("Person");
		BasicDBObject query = new BasicDBObject("_id", id);
		Long modifiedCount = new Long(-1);
		Document docPer = mongoClient.find(query).first();
		if (docPer != null) {
			Bson update = new Document("$set", new Document("verhicles.$[x].status", status));
			UpdateOptions options = new UpdateOptions();
			options.arrayFilters(Arrays.asList(new Document("x.type", type)));
			UpdateResult result = personRepository.addElement(mongoClient, update, query, options);
			if (result != null) {
				modifiedCount = result.getModifiedCount();
			}
		}
		return modifiedCount;
	}

	// 4.Viết query update toàn bộ person , thêm field fullName = firstName + lastName
	public Long addFullName() {
		MongoCollection<Document> mongoClient = database.getCollection("Person");
		Long modifiedCount = new Long(-1);
		List<Bson> update = Arrays.asList(new Document("$set", new Document("fullName",
				new Document("$concat", Stream.of("$firstName", " ", "$lastName").collect(Collectors.toList())))));
		UpdateResult result = personRepository.update(mongoClient, update, new BasicDBObject());
		if (result != null) {
			modifiedCount = result.getModifiedCount();
		}
		return modifiedCount;
	}

	/* 5.Viết query update 1 person, gồm :
	3.1 set age = 30
	3.2 set 1 verhicle trong verhicles  thành ko sử dụng 
	3.2 thêm mới 1 language trong languages
	*/
	public Long updateOnePerson(Person personUpdate, ObjectId id) {
		MongoCollection<Document> mongoClient = database.getCollection("Person");
		BasicDBObject query = new BasicDBObject("_id", id);
		Long modifiedCount = new Long(-1);
		Document docPer = mongoClient.find(query).first();
		if (docPer != null) {
			Bson update = new Document("$set",
					new Document("verhicles.$[x].status", personUpdate.getVerhicles().get(0).getStatus()).append("age",
							personUpdate.getAge())).append("$addToSet",
									new Document("languages", personUpdate.getLanguages().get(0)));
			UpdateOptions options = new UpdateOptions();
			options.arrayFilters(Arrays.asList(new Document("x.type", personUpdate.getVerhicles().get(0).getType())));
			UpdateResult result = personRepository.addElement(mongoClient, update, query, options);
			if (result != null) {
				modifiedCount = result.getModifiedCount();
			}
		}
		return modifiedCount;
	}

	// 8.Viết query đếm trong collection person có bao nhiêu language
	public Pagination countLang(Integer page, Integer limit) {
		Pagination pagination = new Pagination();
		MongoCollection<Document> mongoClient = database.getCollection("Person");
		Bson unwind = new BasicDBObject("$unwind", "$languages");
		Bson group = new BasicDBObject("$group", new BasicDBObject("_id", "$languages"));
		Bson countLang = new BasicDBObject("$count", "languages");
		Bson skip = null;
		Bson limits = null;
		
		/* Trường hợp page, limit đều thêm vào và đều > 0 -> set pageCurrent
		 * Trường hợp page, limit đều ko thêm vào  -> set pageCurrent & totalPage = null , list<Doc> = tất cả Doc match
		 * Ngược lại throw Exception		
		 * */		
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

		List<Bson> query = new ArrayList<>(); // query lay record match (kèm limit & skip nếu có)
		query.add(unwind);
		query.add(group);
		query.add(countLang);
		
		Future<Iterator> future = null;
		if (skip != null && limit != null) {

			List<Bson> listTotal = new ArrayList<>(); // query lay tong so record (ko có limit & skip)
			listTotal.add(unwind);
			listTotal.add(group);
			listTotal.add(countLang);
			
			// Tao luong chay song song lay recordData(query) va totalRecord(listTotal)
			ExecutorService executor = Executors.newCachedThreadPool();
			future = executor.submit(new Callable<Iterator>() {

				@Override
				public Iterator call() throws Exception {
				//  thuc thi ham lấy ra listRecord
					AggregateIterable<Document> record = personRepository.showDB(mongoClient, listTotal); 
					Iterator iteratorRecord = record.iterator();
					return iteratorRecord;
				}

			});
			executor.shutdown();
			// add thêm skip va limit nếu được thêm vào
			query.add(skip);
			query.add(limits);
		}
		// thực thi hàm lấy ra số record theo limit và skip
		AggregateIterable<Document> count = personRepository.showDB(mongoClient, query); 
		List<Document> listDoc = null;
		if (count.first() != null) {
			
			Iterator iterator = count.iterator();
			listDoc = new ArrayList<>();
			while (iterator.hasNext()) {
				listDoc.add((Document) iterator.next());
			}
			// set thuộc tính List<Doc> của Pagination -> được tính toán ở count(dòng 211)
			pagination.setListDoc(listDoc);
			if (limit != null) {
				Integer totalRecord = 0;

				try {
					// lay ra List tổng số record (khi khong co limit, skip) từ luồng chạy song song và tính tổng số record trả về
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
				
					//set totalPage thông qua tổng số record trả về và limit
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

	// 9.Viết query get toàn bộ language hiện có trong collection person (kết quả ko được trùng nhau)
	public Pagination showLangs(Integer page, Integer limit) {
		Pagination pagination = new Pagination();
		MongoCollection<Document> mongoClient = database.getCollection("Person");
		Bson unwind = new BasicDBObject("$unwind", "$languages");
		Bson group = new BasicDBObject("$group", new BasicDBObject("_id", "$languages"));
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

		List<Bson> query = new ArrayList<>();
		query.add(unwind);
		query.add(group);
		query.add(sort);
		Future<Iterator> future = null;
		if (skip != null && limit != null) {

			List<Bson> queryTotal = new ArrayList<>();
			queryTotal.add(unwind);
			queryTotal.add(group);
			ExecutorService executor = Executors.newCachedThreadPool();
			future = executor.submit(new Callable<Iterator>() {
				
				@Override
				public Iterator call() throws Exception {
					AggregateIterable<Document> record = personRepository.showDB(mongoClient, queryTotal);
					Iterator iteratorRecord = record.iterator();
					return iteratorRecord;
				}

			});
			executor.shutdown();

			query.add(skip);
			query.add(limits);
		}
		AggregateIterable<Document> showLangs = personRepository.showDB(mongoClient, query);
		List<Document> listDoc = null;
		if (showLangs.first() != null) {
			Iterator iterator = showLangs.iterator();
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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

	// 10.Viết query get những person có họ hoặc tên chứa "Nguyễn" và ngày sinh trong khoảng tháng 2~ tháng 10 
	public Pagination showPerson(String fullName, Integer monthStart, Integer monthEnd, Integer page, Integer limit) {
		Pagination pagination = new Pagination();
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

		List<Bson> query = new ArrayList<>();
		query.add(project);
		query.add(match);
		query.add(sort);
		Future<Iterator> future = null;
		if (skip != null && limit != null) {

			List<Bson> queryTotal = new ArrayList<>();
			queryTotal.add(project);
			queryTotal.add(match);
			ExecutorService executor = Executors.newCachedThreadPool();
			future = executor.submit(new Callable<Iterator>() {

				@Override
				public Iterator call() throws Exception {
					AggregateIterable<Document> record = personRepository.showDB(mongoClient, queryTotal);
					Iterator iteratorRecord = record.iterator();
					return iteratorRecord;
				}

			});
			executor.shutdown();

			query.add(skip);
			query.add(limits);
		}

		AggregateIterable<Document> showPerson = personRepository.showDB(mongoClient, query);

		List<Document> listDoc = null;
		if (showPerson.first() != null) {
			Iterator iterator = showPerson.iterator();
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

	// 12.Tương tự số 11, nhưng trả về thêm tổng số record thoả yêu cầu + tổng số record hiện có trong collection person
	public Pagination showPerson12(String mailRegex, String sex, String languages, Integer page, Integer limit) {
		Pagination pagination = new Pagination();
		MongoCollection<Document> mongoClient = database.getCollection("Person");
		BasicDBObject project1 = new BasicDBObject("$project", new BasicDBObject("items.fullName", 1)
				.append("items.phone", 1).append("items.languages", 1).append("items.email", 1));
		Bson project2 = new BasicDBObject("$project",
				new BasicDBObject("totalrecord",
						new BasicDBObject("$arrayElemAt",
								Stream.of("$totalrecord.totalrecord", 0).collect(Collectors.toList())))
										.append("totalperson", new BasicDBObject("$arrayElemAt",
												Stream.of("$totalperson.totalperson", 0).collect(Collectors.toList())))
										.append("data", 1));
		BasicDBObject match = new BasicDBObject("$match",
				new BasicDBObject("email", new BasicDBObject("$regex", mailRegex)).append("sex", sex)
						.append("languages", languages));
		BasicDBObject group = new BasicDBObject("$group",
				new BasicDBObject("_id", "$age").append("items", new BasicDBObject("$push", "$$ROOT")));
		Bson sort = new BasicDBObject("$sort", new BasicDBObject("_id",1));
		Bson facet = new BasicDBObject("$facet",
				new BasicDBObject("totalrecord", Arrays.asList(new BasicDBObject("$count", "totalrecord")))
						.append("totalperson",
								Stream.of(match, new BasicDBObject("$count", "totalperson"))
										.collect(Collectors.toList()))
						.append("data", Stream.of(match, group, project1,sort).collect(Collectors.toList())));

		BasicDBObject skip = null;
		BasicDBObject limits = null;
		Bson facet2 = null;
		
		if (page != null && limit != null) {
			if (page > 0 && limit > 0) {
				skip = new BasicDBObject("$skip", (page - 1) * limit);
				limits = new BasicDBObject("$limit", limit);
				// trường hợp page và limit > 0 -> facet2 được định nghĩa chèn thêm skip và limit vào "data" trả về
				facet2 = new BasicDBObject("$facet",
						new BasicDBObject("totalrecord", Arrays.asList(new BasicDBObject("$count", "totalrecord")))
								.append("totalperson",
										Stream.of(match, new BasicDBObject("$count", "totalperson"))
												.collect(Collectors.toList()))
								.append("data", Stream.of(match, group, project1,sort, skip, limits).collect(Collectors.toList())));
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
		
		Future<List<Document>> future = null;
		if (skip != null && limit != null) {

			List<Bson> queryTotal = new ArrayList<>();
			queryTotal.add(facet);
			queryTotal.add(project2);
			
			ExecutorService executor = Executors.newCachedThreadPool();
			future = executor.submit(new Callable<List<Document>>() {

				@Override
				public List<Document> call() throws Exception {
					AggregateIterable<Document> record = personRepository.showDB(mongoClient, queryTotal);
					List<Document> listTotalRecord = record.first().getList("data", Document.class);
					return listTotalRecord;
				}

			});
			executor.shutdown();
		}
		
		AggregateIterable<Document> showPerson = personRepository.showDB(mongoClient, query);
		List<Document> listDoc = null;
		if (!showPerson.first().getList("data", Document.class).isEmpty()) {
			listDoc = new ArrayList<>();
			listDoc.add(showPerson.first());
			pagination.setListDoc(listDoc);
			if (limit != null) {
				Integer totalRecord = 0;
				Document docTotal = null;
				List<Document> listDocTotal = null;
				
				try {
					listDocTotal = future.get();
					for(Document itemDoc : listDocTotal) {
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

	/* 11.
	 * Viết query get thông tin của toàn bộ person có giới tính là nam + language là tiếng việt, yêu cầu:
    - Group theo fullname (họ + tên)
    - Kết quả trả về bao gồm: 
        + fullname (họ + tên)
        + sdt
        + language (chỉ hiển thị language "Tiếng Việt")
        + email (chỉ hiển thị những email có đuôi là @gmail.com)
	 */
	public Pagination showPerson11(String mailRegex, String sex, String languages, Integer page, Integer limit) {
		Pagination pagination = new Pagination();
		MongoCollection<Document> mongoClient = database.getCollection("Person");
		Bson project = new BasicDBObject("$project", new BasicDBObject("items.fullName", 1).append("items.phone", 1)
				.append("items.languages", 1).append("items.email", 1));
		Bson match = new BasicDBObject("$match", new BasicDBObject("email", new BasicDBObject("$regex", mailRegex))
				.append("sex", sex).append("languages", languages));
		Bson group = new BasicDBObject("$group",
				new BasicDBObject("_id", "$age").append("items", new BasicDBObject("$push", "$$ROOT")));
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

		List<Bson> query = new ArrayList<>();
		query.add(match);
		query.add(group);
		query.add(project);
		query.add(sort);
		Future<Iterator> future = null;
		if (skip != null && limit != null) {

			List<Bson> listTotal = new ArrayList<>();
			listTotal.add(match);
			listTotal.add(group);
			listTotal.add(project);

			ExecutorService executor = Executors.newCachedThreadPool();
			future = executor.submit(new Callable<Iterator>() {

				@Override
				public Iterator call() throws Exception {
					AggregateIterable<Document> record = personRepository.showDB(mongoClient, listTotal);
					Iterator iteratorRecord = record.iterator();
					return iteratorRecord;
				}

			});
			executor.shutdown();

			query.add(skip);
			query.add(limits);
		}

		AggregateIterable<Document> showPerson = personRepository.showDB(mongoClient, query);

		List<Document> listDoc = null;
		if (showPerson.first() != null) {
			Iterator iterator = showPerson.iterator();
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
