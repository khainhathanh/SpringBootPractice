package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Pagination;
import com.example.demo.entity.Person;
import com.example.demo.entity.Verhicles;
import com.example.demo.exception.InternalServerException;
import com.example.demo.repository.PersonRepository;

@Service
public class PersonService {
	private static Logger logger = Logger.getLogger(PersonService.class);
	
	@Autowired
	PersonRepository personRepository;

	public Integer insert(List<Person> listPerson) {
		logger.info("START : Lesson 1");
			Integer result = null;
			logger.info("START: Thực thi insert");
			try {
				result = personRepository.insert(listPerson);
			} catch (Exception e) {
				logger.error(e);
				throw new InternalServerException("Can't insert! Systems is error");
			}
			logger.info("END: Thực thi insert");
		logger.info("END : Lesson 1");
		return result;
	}

	// 2.Viết query thêm 1 verhicle mới trong bảng person
	public Long addElement(Verhicles verhicles, ObjectId id) {
		logger.info("START : Lesson 2");
		Long modifiedCount = new Long(-1);
		boolean matchCount = false;// 1 <=> list trung type can update
		logger.info("START : Check ID");
		Document docPer = personRepository.search(id);
		if (docPer != null) {
			logger.info("END : Check ID");
			List<Document> listVerhicles = docPer.getList("verhicles", Document.class);
			// duyet tat ca field List<verhicles> , neu ton tai thuoc tinh type cung loai se
			// bo qua
			logger.info("START : Check Verhicle tồn tại");
			for (Document item : listVerhicles) {
				if (item.getString("type").contentEquals(verhicles.getType())) {
					matchCount = true;
					break;
				}
			}
			logger.info("END : Check Verhicle tồn tại");
			if (matchCount == false) {
				Long result = null;
				logger.info("START : Thực thi thêm Verhicles");
				try {
					result = personRepository.addElement(verhicles, id);
				} catch (Exception e) {
					logger.error(e);
					throw new InternalServerException("Can't add Verhicles! Systems appear a error");
				}
				logger.info("END : Thực thi thêm Verhicles");
				logger.info("START : Kiểm tra kết quả");
				if (result != null) {
					modifiedCount = result;
				}
				logger.info("END : Kiểm tra kết quả");
			}
		}
		logger.info("END : Lesson 2");
		return modifiedCount;
	}

	// 3.Viết query update 1 verhicle trong bảng person thành ko sử dụng
	public Long updateVerhicles(String type, ObjectId id, Integer status) {
		logger.info("START : Lesson 3");
		Long modifiedCount = new Long(-1);
		logger.info("START : Check ID");
		Document docPer = personRepository.search(id);
		Long result = null;		
		if (docPer != null) {
			logger.info("END : Check ID");
			logger.info("START : Thực thi update status");
			try {
				result = personRepository.addElement(type, id, status);
			} catch (Exception e) {
				logger.error(e);
				throw new InternalServerException("Can't update. System is error!");
			}
			logger.info("END : Thực thi update status");
			logger.info("START : Kiểm tra kết quả");
			if (result != null) {
				modifiedCount = result;
			}
			logger.info("END : Kiểm tra kết quả");
		}
		logger.info("END : Lesson 3");
		return modifiedCount;
	}

	// 4.Viết query update toàn bộ person , thêm field fullName = firstName +
	// lastName
	public Long addFullName() {
		logger.info("START : Lesson 4");
		Long modifiedCount = new Long(-1);
		Long result = null;
		logger.info("START : Thực thi update");
		try {
			result = personRepository.updateFullName();
		} catch (Exception e) {
			logger.error(e);
			throw new InternalServerException("Can't update! Systems is error");
		}
		logger.info("END : Thực thi update");
		if (result != null) {
			modifiedCount = result;
		}
		logger.info("END : Lesson 4");
		return modifiedCount;
	}

	/*
	 * 5.Viết query update 1 person, gồm : 3.1 set age = 30 3.2 set 1 verhicle trong
	 * verhicles thành ko sử dụng 3.2 thêm mới 1 language trong languages
	 */
	public Long updateOnePerson(Person personUpdate, ObjectId id) {
		logger.info("START : Lesson 5");
		Long modifiedCount = new Long(-1);
		Document docPer = personRepository.search(id);
		if (docPer != null) {
			Long result = null;
			logger.info("START : Thực thi update");
			try {
				result = personRepository.updateOnePerson(personUpdate, id);
			} catch (Exception e) {
				logger.error(e);
				throw new InternalServerException("Can't update. System is error!");
			}
			logger.info("END : Thực thi update");
			if (result != null) {
				modifiedCount = result;
			}
		}
		logger.info("END : Lesson 5");
		return modifiedCount;
	}

	// 8.Viết query đếm trong collection person có bao nhiêu language
	// 9.Viết query get toàn bộ language hiện có trong collection person (kết quả ko
	// được trùng nhau)
	public Pagination countLang(Integer page, Integer limit) {
		logger.info("START: Lesson 8");
		Pagination pagination = new Pagination();
		/*
		 * Trường hợp page, limit đều thêm vào và đều > 0 -> set pageCurrent Trường hợp
		 * page, limit đều ko thêm vào -> set pageCurrent & totalPage = null , list<Doc>
		 * = tất cả Doc match Ngược lại throw Exception
		 */
		pagination.setPageCurrent(page);
		// thực thi hàm lấy ra số record theo limit và skip
		Document doc = null;
		logger.info("START : Thực thi truy vấn");
		try {
			doc = personRepository.showDB(page, limit);
		} catch (Exception e) {
			logger.error(e);
			throw new InternalServerException("Error retrive!");
		}
		logger.info("END : Thực thi truy vấn");
		List<Document> listDoc = null;
		if (doc != null) {
			logger.info("START : Xử lý kết quả truy vấn");
			listDoc = new ArrayList<>();
			listDoc.add(doc);
			// set thuộc tính List<Doc> của Pagination -> được tính toán ở count(dòng 211)
			pagination.setListDoc(listDoc);
			Integer totalRecord = doc.getInteger("countLang");
			// set totalPage thông qua tổng số record trả về và limit
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
			logger.info("END : Xử lý kết quả truy vấn");
		}
		logger.info("END: Lesson 8");
		return pagination;
	}

	// 10.Viết query get những person có họ hoặc tên chứa "Nguyễn" và ngày sinh
	// trong khoảng tháng 2~ tháng 10
	public Pagination showPerson(String fullName, Integer monthStart, Integer monthEnd, Integer page, Integer limit) {
		logger.info("START : Lesson 10");
		Pagination pagination = new Pagination();
		pagination.setPageCurrent(page);
		Document doc = null;
		logger.info("START : Thực thi truy vấn");
		try {
			doc = personRepository.showDB10(fullName, monthStart, monthEnd, page, limit);
		} catch (Exception e) {
			logger.error(e);
			throw new InternalServerException("Error retrive!");
		}
		logger.info("END : Thực thi truy vấn");
		List<Document> listDoc = null;
		if (doc != null) {
			logger.info("START : Xử lý kết quả truy vấn");
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
			logger.info("END : Xử lý kết quả truy vấn");
		}
		logger.info("END : Lesson 10");
		return pagination;
	}

	/*
	 * 11. Viết query get thông tin của toàn bộ person có giới tính là nam +
	 * language là tiếng việt, yêu cầu: - Group theo fullname (họ + tên) - Kết quả
	 * trả về bao gồm: + fullname (họ + tên) + sdt + language (chỉ hiển thị language
	 * "Tiếng Việt") + email (chỉ hiển thị những email có đuôi là @gmail.com)
	 */
	public Pagination showPerson11(String mailRegex, String sex, String languages, Integer page, Integer limit) {
		logger.info("START : Lesson 11");
		Pagination pagination = new Pagination();
		pagination.setPageCurrent(page);
		Document doc = null;
		logger.info("START : Thực thi truy vấn");
		try {
			doc = personRepository.showDB11(mailRegex, sex, languages, page, limit);
		} catch (Exception e) {
			logger.error(e);
			throw new InternalServerException("Error retrive!");
		}
		logger.info("END : Thực thi truy vấn");
		List<Document> listDoc = null;
		if (doc != null) {
			logger.info("START : Xử lý truy vấn");
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
			logger.info("END : Xử lý truy vấn");
		}
		logger.info("END : Lesson 11");
		return pagination;
	}

	// 12.Tương tự số 11, nhưng trả về thêm tổng số record thoả yêu cầu + tổng số
	// record hiện có trong collection person
	public Pagination showPerson12(String mailRegex, String sex, String languages, Integer page, Integer limit) {
		logger.info("START : Lesson 12");
		Pagination pagination = new Pagination();
		pagination.setPageCurrent(page);
		Document doc = null;
		logger.info("START : Thực thi truy vấn");
		try {
			doc = personRepository.showDB12(mailRegex, sex, languages, page, limit);
		} catch (Exception e) {
			logger.error(e);
			throw new InternalServerException("Error retrive!");
		}
		logger.info("END : Thực thi truy vấn");
		List<Document> listDoc = null;
		if (doc != null) {
			logger.info("START : Xử lý kết quả truy vấn");
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
			logger.info("START : Xử lý kết quả truy vấn");
		}
		logger.info("END : Lesson 12");
		return pagination;
	}

}
