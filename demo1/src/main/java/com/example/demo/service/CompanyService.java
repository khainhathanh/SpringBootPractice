package com.example.demo.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Company;
import com.example.demo.entity.NameCompany;
import com.example.demo.entity.Pagination;
import com.example.demo.exception.InternalServerException;
import com.example.demo.repository.CompanyRepository;

@Service
public class CompanyService {
	
	private static Logger logger = Logger.getLogger(PersonService.class);
	
	@Autowired
	CompanyRepository companyRepository;

	public Integer insert(List<Company> listCompany) {
		logger.info("START : Lession 1");
		Integer result = null;
		logger.info("START : Thực thi insert");
		try {
			result = companyRepository.insert(listCompany);
		} catch (Exception e) {
			logger.error(e);
			throw new InternalServerException("Can't insert! Systems is error");
		}
		logger.info("END : Thực thi insert");
		logger.info("END : Lession 1");
		return result;
	}

	// 6.Viết query update 1 company, thêm 1 name mới theo ngôn ngữ trong names ....
	public Long updateName(NameCompany names, ObjectId id) {
		logger.info("START : Lesson 6");
		Long modifiedCount = null;
		boolean matchCount = false;
		logger.info("START : Check ID");
		Document docPer = companyRepository.search(id);
		if (docPer != null) {
			logger.info("END : Check ID");
			Document docLang = companyRepository.checkLangNames(names, id);
			// duyet tat ca field List<verhicles> , neu ton tai thuoc tinh type cung loai se
			// bo qua
			logger.info("START : Check languages tồn tại");
			if (!docLang.getList("lang", Document.class).isEmpty()) {
				matchCount = true;
			}
			
			logger.info("END : Check languages tồn tại");
			if (matchCount == false) {
				Long result = null;
				logger.info("START : Thực thi update");
				try {
					result = companyRepository.addElement(names, id);
				} catch (Exception e) {
					logger.error(e);
					throw new InternalServerException("Can't update. System is error!");
				}
				logger.info("END : Thực thi update");
				logger.info("START : Kiểm tra kết quả");
				if (result != null) {
					modifiedCount = result;
				}
				logger.info("END : Kiểm tra kết quả");
			}else {
				modifiedCount = (long) -1;
			}
		}
		logger.info("END : Lesson 6");
		return modifiedCount;
	}

	// 7.Viết query xoá 1 name trong names của 1 company theo ngôn ngữ
	public Long updateName(String lang, ObjectId id) {
		logger.info("START : Lesson 7");
		Long modifiedCount = new Long(-1);
		logger.info("START : Check ID");
		Document docPer = companyRepository.search(id);
		if (docPer != null) {
			logger.info("END : Check ID");
			Long result = null;
			logger.info("START : Thực thi delete");
			try {
				result = companyRepository.addElement7(lang, id);
			} catch (Exception e) {
				logger.error(e);
				throw new InternalServerException("Can't update. System is error!");
			}
			logger.info("START : Thực thi delete");
			logger.info("START : Kiểm tra kết quả");
			if (result != null) {
				modifiedCount = result;
			}
			logger.info("END : Kiểm tra kết quả");
		}
		logger.info("END : Lesson 7");
		return modifiedCount;
	}

	// 13.Thống kê có bao nhiêu công ty, số lượng nhân viên của công ty
	public Pagination showCompany(Integer page, Integer limit) {
		logger.info("START : Lesson 13");
		Pagination pagination = new Pagination();
		pagination.setPageCurrent(page);
		Document doc = null;
		logger.info("START : Thực thi truy vấn");
		try {
			doc = companyRepository.showDB(page, limit);
		} catch (Exception e) {
			logger.error(e);
			throw new InternalServerException("Can't retrive. System is error!");
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
		logger.info("END : Lesson 13");
		return pagination;
	}
}
