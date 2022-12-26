package com.example.demo.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Employee;
import com.example.demo.entity.Pagination;
import com.example.demo.exception.InternalServerException;
import com.example.demo.repository.EmployeeRepository;
import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.result.UpdateResult;

@Service
public class EmployeeService {

	@Autowired
	EmployeeRepository employeeRepository;

	public List<String> insert(List<Employee> listEmployee) {

		List<String> listID = new ArrayList<>();
		UpdateResult result = null;
		for (Employee itemEmployee : listEmployee) {
			List<Bson> insert = Arrays.asList(new Document("$set",
					new Document().append("personId", itemEmployee.getPersonId())
							.append("companyId", itemEmployee.getCompanyId()).append("salary", itemEmployee.getSalary())
							.append("startDate", itemEmployee.getStartDate())
							.append("currency", itemEmployee.getCurrency())));
			try {
				result = employeeRepository.insert(itemEmployee.getId(), insert);
			} catch (Exception e) {
				throw new InternalServerException("Can't insert! Systems is error");
			}
			listID.add(result.getUpsertedId().asObjectId().getValue().toHexString());
		}
		return listID;
	}

	// 14.Thống kê công ty A , vào năm 2022 có bao nhiêu nhân viên vào làm, tổng mức
	// lương phải trả cho những nhân viên đó là bao nhiêu
	public Pagination showEmployees(Integer year, Integer page, Integer limit) {

		Pagination pagination = new Pagination();
		Bson project = new BasicDBObject("$project", new BasicDBObject("_id", 0).append("companyId", 1)
				.append("year", new BasicDBObject("$year", "$startDate")).append("salary", 1));
		Bson match = new BasicDBObject("$match", new BasicDBObject("year", year));
		Bson group = new BasicDBObject("$group",
				new BasicDBObject("_id", Stream.of("$companyId", "$year").collect(Collectors.toList()))
						.append("salary", new BasicDBObject("$sum", "$salary"))
						.append("numberofEmployee", new BasicDBObject("$sum", 1)));
		Bson count = new BasicDBObject("$count", "total");
		Bson sort = new BasicDBObject("$sort", new BasicDBObject("_id", 1));
		Bson project2 = new BasicDBObject("$project",
				new BasicDBObject("total",
						new BasicDBObject("$arrayElemAt", Stream.of("$total.total", 0).collect(Collectors.toList())))
								.append("data", 1));
		Bson skip = new BasicDBObject("$skip", (page - 1) * limit);
		Bson limits = new BasicDBObject("$limit", limit);
		Bson facet = new BasicDBObject("$facet",
				new BasicDBObject("total", Stream.of(project, match, group, count).collect(Collectors.toList())).append(
						"data", Stream.of(project, match, group, sort, skip, limits).collect(Collectors.toList())));
		pagination.setPageCurrent(page);

		List<Bson> query = new ArrayList<>();
		query.add(facet);
		query.add(project2);
		Document doc = null;
		try {
			doc = employeeRepository.showDB(query);
		} catch (Exception e) {
			throw new InternalServerException("Error retrive!");
		}
		List<Document> listDoc = null;
		if (doc != null) {
			listDoc = doc.getList("data", Document.class);
			pagination.setListDoc(listDoc);

			Integer totalRecord = doc.getInteger("total");
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
		return pagination;
	}

	// 15.Thống kê tổng số tiền các công ty phải trả cho những người đăng ký vào làm
	// trong năm 2022
	public Pagination showEmpl(Integer year, Integer page, Integer limit) {

		Pagination pagination = new Pagination();
		Bson project = new BasicDBObject("$project", new BasicDBObject("_id", 0).append("companyId", 1)
				.append("year", new BasicDBObject("$year", "$startDate")).append("salary", 1));
		Bson match = new BasicDBObject("$match", new BasicDBObject("year", year));
		Bson group = new BasicDBObject("$group",
				new BasicDBObject("_id", Stream.of("$companyId", "$year").collect(Collectors.toList())).append("salary",
						new BasicDBObject("$sum", "$salary")));
		Bson sort = new BasicDBObject("$sort", new BasicDBObject("_id", 1));

		Bson count = new BasicDBObject("$count", "total");

		Bson project2 = new BasicDBObject("$project",
				new BasicDBObject("total",
						new BasicDBObject("$arrayElemAt", Stream.of("$total.total", 0).collect(Collectors.toList())))
								.append("data", 1));
		Bson skip = new BasicDBObject("$skip", (page - 1) * limit);
		Bson limits = new BasicDBObject("$limit", limit);
		Bson facet = new BasicDBObject("$facet",
				new BasicDBObject("total", Stream.of(project, match, group, count).collect(Collectors.toList())).append(
						"data", Stream.of(project, match, group, sort, skip, limits).collect(Collectors.toList())));
		pagination.setPageCurrent(page);

		List<Bson> query = new ArrayList<>();
		query.add(facet);
		query.add(project2);
		Document doc = null;
		try {
			doc = employeeRepository.showDB(query);
		} catch (Exception e) {
			throw new InternalServerException("Error retrive!");
		}
		List<Document> listDoc = null;
		if (doc != null) {
			listDoc = doc.getList("data", Document.class);
			pagination.setListDoc(listDoc);

			Integer totalRecord = doc.getInteger("total");
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
		return pagination;
	}

	// 16.Thống kê tổng số tiền các công ty IT phải trả cho những người đăng ký vào
	// làm trong các năm từ 2020 ~ 2022
	public Pagination showEmployee(String category, Integer startYear, Integer endYear, Integer page, Integer limit) {
		Pagination pagination = new Pagination();
		Bson lookup = new BasicDBObject("$lookup", new BasicDBObject("from", "Company")
				.append("localField", "companyId").append("foreignField", "code").append("as", "companydata"));
		Bson unwind = new BasicDBObject("$unwind", "$companydata");
		Bson project = new BasicDBObject("$project",
				new BasicDBObject("_id", 0).append("companyId", 1)
						.append("year", new BasicDBObject("$year", "$startDate")).append("salary", 1)
						.append("category", "$companydata.categories"));
		Bson match = new BasicDBObject("$match",
				new BasicDBObject("category", category).append("$and",
						Stream.of(new BasicDBObject("year", new BasicDBObject("$gte", startYear)),
								new BasicDBObject("year", new BasicDBObject("$lte", endYear)))
								.collect(Collectors.toList())));
		Bson group = new BasicDBObject("$group",
				new BasicDBObject("_id", Stream.of("$category", "$companyId").collect(Collectors.toList()))
						.append("salary", new BasicDBObject("$sum", "$salary")));
		Bson sort = new BasicDBObject("$sort", new BasicDBObject("_id", 1));
		Bson count = new BasicDBObject("$count", "total");
		Bson project2 = new BasicDBObject("$project",
				new BasicDBObject("total",
						new BasicDBObject("$arrayElemAt", Stream.of("$total.total", 0).collect(Collectors.toList())))
								.append("data", 1));
		Bson skip = new BasicDBObject("$skip", (page - 1) * limit);
		Bson limits = new BasicDBObject("$limit", limit);
		Bson facet = new BasicDBObject("$facet",
				new BasicDBObject("total",
						Stream.of(lookup, unwind, project, match, group, count).collect(Collectors.toList()))
								.append("data", Stream.of(lookup, unwind, project, match, group, sort, skip, limits)
										.collect(Collectors.toList())));
		pagination.setPageCurrent(page);

		List<Bson> query = new ArrayList<>();
		query.add(facet);
		query.add(project2);
		Document doc = null;
		try {
			doc = employeeRepository.showDB(query);
		} catch (Exception e) {
			throw new InternalServerException("Error retrive!");
		}
		List<Document> listDoc = null;
		if (doc != null) {
			listDoc = doc.getList("data", Document.class);
			pagination.setListDoc(listDoc);

			Integer totalRecord = doc.getInteger("total");

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
		return pagination;
	}
}
