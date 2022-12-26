package com.example.demo.controller;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Pagination;
import com.example.demo.entity.Person;
import com.example.demo.entity.Verhicles;
import com.example.demo.service.PersonService;

@RestController
public class PersonAPI {
	@Autowired
	PersonService personService;

	@PostMapping(value = "/person")
	public ResponseEntity<?> createPerson(@RequestBody List<Person> listPerson) {
		List<String> listIDPerson = null;
		if (!listPerson.isEmpty()) {
			listIDPerson = personService.insert(listPerson);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("request error .Please check again!");
		}
		return ResponseEntity.status(HttpStatus.OK).body(listIDPerson);
	}

	// 2.Viết query thêm 1 verhicle mới trong bảng person
	/*
	 * List<Verhicles> -> Output : UpdateCount 2 van de : -update trung type nhung
	 * khac status (mong muon khi them moi nhung type trung nhau se bo qua) --> da
	 * xu ly -update muiltyply verhicles
	 * 
	 */
	@PutMapping(value = "/person/2")
	public ResponseEntity<?> updateAddVerhicles(@RequestBody Verhicles verhicles,
			@RequestParam(value = "id") String id) {
		Long upsertID = null;
		if (id != "") {
			upsertID = personService.addElement(verhicles, new ObjectId(id));
		}
		// truong hop khong co chinh sua gi
		if (upsertID == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request is error. Please try again!");
		}
		// truong hop khong tim thay record de update
		else if (upsertID == -1) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found record to update");
		}
		return ResponseEntity.status(HttpStatus.OK).body(upsertID);
	}

	// 3.Viết query update 1 verhicle trong bảng person thành ko sử dụng
	@PutMapping(value = "/person/3")
	public ResponseEntity<?> updateVerhicles(@RequestBody Integer status, @RequestParam(value = "id") String id,
			@RequestParam(value = "type") String type) {
		Long upsertID = null;
		if (id != "") {
			upsertID = personService.updateVerhicles(type, new ObjectId(id), status);
		}
		// truong hop khong co chinh sua gi
		if (upsertID == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(upsertID);
		}
		// truong hop khong tim thay record de update
		else if (upsertID == -1) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(upsertID);
		}
		return ResponseEntity.status(HttpStatus.OK).body(upsertID);
	}

	// 4.Viết query update toàn bộ person , thêm field fullName = firstName +
	// lastName
	@PutMapping(value = "/person/4")
	public ResponseEntity<?> updateAddFieldPerson(@RequestParam(value = "fullName") String fullName) {
		Long upsertID = personService.addFullName();
		// truong hop loi update
		if (upsertID == -1) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Query is wrong");
		}
		return ResponseEntity.status(HttpStatus.OK).body(upsertID);
	}

	// 5.Viết query update 1 person, gồm :
	/*
	 * 3.1 set age = 30 3.2 set 1 verhicle trong verhicles thành ko sử dụng 3.2 thêm
	 * mới 1 language trong languages
	 */
	@PutMapping(value = "/person/5")
	public ResponseEntity<?> updateOnePerson(@RequestBody Person personUpdate, @RequestParam(value = "id") String id) {
		Long upsertID = null;
		if (id != "") {
			upsertID = personService.updateOnePerson(personUpdate, new ObjectId(id));
		}
		// truong hop khong co chinh sua gi
		if (upsertID == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request is error!. Please try again");
		}
		// truong hop khong tim thay record de update
		else if (upsertID == -1) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found record to update");
		}
		return ResponseEntity.status(HttpStatus.OK).body(upsertID);
	}

	// 8.Viết query đếm trong collection person có bao nhiêu language
	// 9.Viết query get toàn bộ language hiện có trong collection person (kết quả ko
	// được trùng nhau)
	@GetMapping(value = "/person/8")
	public ResponseEntity<?> count(@RequestParam(value = "count") String languages,
			@RequestParam(required = false, defaultValue = "1") Integer page,
			@RequestParam(required = false, defaultValue = "5") Integer limit) {
		Pagination count = null;
		if (languages.contentEquals("languages") == true) {
			count = personService.countLang(page, limit);
			if (count.getListDoc() == null || count.getListDoc().isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found record to retrive");
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request is error!. Please try again");
		}
		return ResponseEntity.status(HttpStatus.OK).body(count);
	}

	// 10.Viết query get những person có họ hoặc tên chứa "Nguyễn" và ngày sinh
	// trong khoảng tháng 2~ tháng 10
	@GetMapping(value = "/person/10")
	public ResponseEntity<?> showPerson(@RequestParam(value = "fullName") String fullName,
			@RequestParam(value = "monthStart") Integer monthStart, @RequestParam(value = "monthEnd") Integer monthEnd,
			@RequestParam(required = false, defaultValue = "1") Integer page,
			@RequestParam(required = false, defaultValue = "5") Integer limit) {
		Pagination showPer = null;
		if (monthStart > 0 && monthStart < 13 && monthEnd > 0 && monthEnd < 13) {
			showPer = personService.showPerson(fullName, monthStart, monthEnd, page, limit);
			if (showPer.getListDoc() == null || showPer.getListDoc().isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found record to retrive");
			}
		}else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request is error!. Please try again");
		}

		return ResponseEntity.status(HttpStatus.OK).body(showPer);
	}

	// 11.
	/*
	 * .Viết query get thông tin của toàn bộ person có giới tính là nam + language
	 * là tiếng việt, yêu cầu: - Group theo fullname (họ + tên) - Kết quả trả về bao
	 * gồm: + fullname (họ + tên) + sdt + language (chỉ hiển thị language
	 * "Tiếng Việt") + email (chỉ hiển thị những email có đuôi là @gmail.com)
	 */
	@GetMapping(value = "/person/11")
	public ResponseEntity<?> showPerson(@RequestParam(value = "mailRegex") String mailRegex,
			@RequestParam(value = "sex") String sex, @RequestParam(value = "languages") String languages,
			@RequestParam(required = false, defaultValue = "1") Integer page,
			@RequestParam(required = false, defaultValue = "5") Integer limit) {
		Pagination showPer = personService.showPerson11(mailRegex, sex, languages, page, limit);
		if (showPer.getListDoc() == null || showPer.getListDoc().isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found record to retrive");
		}
		return ResponseEntity.status(HttpStatus.OK).body(showPer);
	}

	// 12.
	@GetMapping(value = "/person/12")
	public ResponseEntity<?> showPerson12(@RequestParam(value = "mailRegex") String mailRegex,
			@RequestParam(value = "sex") String sex, @RequestParam(value = "languages") String languages,
			@RequestParam(required = false, defaultValue = "1") Integer page,
			@RequestParam(required = false, defaultValue = "5") Integer limit) {
		Pagination showPer = personService.showPerson12(mailRegex, sex, languages, page, limit);
		if (showPer.getListDoc() == null || showPer.getListDoc().isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found record to retrive");
		}

		return ResponseEntity.status(HttpStatus.OK).body(showPer);
	}

}
