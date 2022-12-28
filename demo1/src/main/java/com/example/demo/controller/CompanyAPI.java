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

import com.example.demo.entity.Company;
import com.example.demo.entity.NameCompany;
import com.example.demo.entity.Pagination;
import com.example.demo.service.CompanyService;

@RestController
public class CompanyAPI {

	@Autowired
	CompanyService companyService;

	// nhuoc diem chua xu ly getName() insert multilply names
	@PostMapping(value = "/company")
	public ResponseEntity<?> createCompany(@RequestBody List<Company> listCompany) {
		Integer countInsert = null;
		if (!listCompany.isEmpty()) {
			countInsert = companyService.insert(listCompany);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("request error. Please check again!");
		}
		return ResponseEntity.status(HttpStatus.OK).body(countInsert);
	}

	// 6. Viết query update 1 company, thêm 1 name mới theo ngôn ngữ trong names
	/*
	 * nhuoc diem chua xu ly viec update trung lang, update List<NameCompany>--> da
	 * xu ly update multiply names
	 */
	@PutMapping(value = "/company/6")
	public ResponseEntity<?> updateName(@RequestBody NameCompany names, @RequestParam(value = "id") String id) {
		Long upsertID = companyService.updateName(names, new ObjectId(id));
		// truong hop loi update
		if (upsertID == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found record to update");
		}else if(upsertID == -1) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Record is exist");
		}
		return ResponseEntity.status(HttpStatus.OK).body(upsertID);

	}

	// 7.Viết query xoá 1 name trong names của 1 company theo ngôn ngữ
	@PutMapping(value = "/company/7")
	public ResponseEntity<?> updateName(@RequestParam String lang, @RequestParam(value = "id") String id) {
		Long upsertID = companyService.updateName(lang, new ObjectId(id));
		// truong hop loi update
		if (upsertID == -1) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found record to update");
		}
		return ResponseEntity.status(HttpStatus.OK).body(upsertID);

	}

	// 13. Thống kê có bao nhiêu công ty, số lượng nhân viên của công ty
	@GetMapping(value = "/company/13")
	public ResponseEntity<?> showCompany(@RequestParam(required = false, defaultValue = "1") Integer page,
			@RequestParam(required = false, defaultValue = "5") Integer limit) {
		Pagination showCompany = companyService.showCompany(page, limit);
		if (showCompany.getListDoc() == null || showCompany.getListDoc().isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found record to retrive");
		}
		return ResponseEntity.status(HttpStatus.OK).body(showCompany);

	}
}
