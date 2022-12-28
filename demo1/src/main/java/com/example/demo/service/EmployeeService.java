package com.example.demo.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Employee;
import com.example.demo.entity.Pagination;
import com.example.demo.exception.InternalServerException;
import com.example.demo.repository.EmployeeRepository;

@Service
public class EmployeeService {

	private static Logger logger = Logger.getLogger(PersonService.class);

	@Autowired
	EmployeeRepository employeeRepository;

	public Integer insert(List<Employee> listEmployee) {
		logger.info("START : Lesson 1");
		Integer result = null;
		logger.info("START : Thực thi insert");
		try {
			result = employeeRepository.insert(listEmployee);
		} catch (Exception e) {
			logger.error(e);
			throw new InternalServerException("Can't insert! Systems is error");
		}
		logger.info("END : Thực thi insert");
		logger.info("END : Lesson 1");
		return result;
	}

	// 14.Thống kê công ty A , vào năm 2022 có bao nhiêu nhân viên vào làm, tổng mức
	// lương phải trả cho những nhân viên đó là bao nhiêu
	public Pagination showEmployees(Integer year, Integer page, Integer limit) {
		logger.info("START : Lesson 14");
		Pagination pagination = new Pagination();
		pagination.setPageCurrent(page);
		Document doc = null;
		logger.info("START : Thực thi truy vấn");
		try {
			doc = employeeRepository.showDB14(year, page, limit);
		} catch (Exception e) {
			logger.error(e);
			throw new InternalServerException("Error retrive!");
		}
		logger.info("END : Thực thi truy vấn");
		List<Document> listDoc = null;
		if (doc != null) {
			logger.info("START : Xử lý kết quả");
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
			logger.info("END : Xử lý kết quả");
		}
		logger.info("END : Lesson 14");
		return pagination;
	}

	// 15.Thống kê tổng số tiền các công ty phải trả cho những người đăng ký vào làm
	// trong năm 2022
	public Pagination showEmpl(Integer year, Integer page, Integer limit) {
		logger.info("START : Lesson 15");
		Pagination pagination = new Pagination();
		pagination.setPageCurrent(page);
		Document doc = null;
		logger.info("START : Thực thi truy vấn");
		try {
			doc = employeeRepository.showDB15(year, page, limit);
		} catch (Exception e) {
			logger.error(e);
			throw new InternalServerException("Error retrive!");
		}
		logger.info("START : Thực thi truy vấn");
		List<Document> listDoc = null;
		if (doc != null) {
			logger.info("START : Xử lý kết quả");
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
			logger.info("END : Xử lý kết quả");
		}
		logger.info("END : Lesson 15");
		return pagination;
	}

	// 16.Thống kê tổng số tiền các công ty IT phải trả cho những người đăng ký vào
	// làm trong các năm từ 2020 ~ 2022
	public Pagination showEmployee(String category, Integer startYear, Integer endYear, Integer page, Integer limit) {
		logger.info("START : Lesson 16");
		Pagination pagination = new Pagination();
		pagination.setPageCurrent(page);
		Document doc = null;
		logger.info("START : Thực thi truy vấn");
		try {
			doc = employeeRepository.showDB16(category, startYear, endYear, page, limit);
		} catch (Exception e) {
			logger.error(e);
			throw new InternalServerException("Error retrive!");
		}
		logger.info("END : Thực thi truy vấn");
		List<Document> listDoc = null;
		if (doc != null) {
			logger.info("START : Xử lý kết quả");
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
			logger.info("END : Thực thi truy vấn");
		}
		logger.info("END : Lesson 16");
		return pagination;
	}
}
