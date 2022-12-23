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
	public ResponseEntity<?> createPerson(@RequestBody List<Company> listCompany) {
		HttpStatus stt = HttpStatus.OK;
		List<String> listIDPerson = companyService.insert(listCompany);
		// truong hop list truyen vao rong
		if (listIDPerson.isEmpty()) {
			stt = HttpStatus.BAD_REQUEST;
		}
		return ResponseEntity.status(stt).body(listIDPerson);
	}
	
	//6. Viết query update 1 company, thêm 1 name mới theo ngôn ngữ trong names
	/*
	 * nhuoc diem chua xu ly viec update trung lang, update List<NameCompany>--> da xu ly
	 * update multiply names
	 */
	@PutMapping(value = "/company/6")
	public ResponseEntity<?> updateName(@RequestBody NameCompany names, @RequestParam(value = "id") String id){
		Long upsertID = companyService.updateName(names, new ObjectId(id));
		// truong hop loi update
		if (upsertID == -1) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found record to update");
		}
		return ResponseEntity.status(HttpStatus.OK).body(upsertID);
		
	}
	
	//7.Viết query xoá 1 name trong names của 1 company theo ngôn ngữ
	@PutMapping(value = "/company/7")
	public ResponseEntity<?> updateName(@RequestParam String lang, @RequestParam(value = "id") String id){
		Long upsertID = companyService.updateName(lang, new ObjectId(id));
		// truong hop loi update
		if (upsertID == -1) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found record to update");
		}
		return ResponseEntity.status(HttpStatus.OK).body(upsertID);
		
	}
	
	//13. Thống kê có bao nhiêu công ty, số lượng nhân viên của công ty
		@GetMapping(value = "/company/13")
		public ResponseEntity<?> showCompany(@RequestParam(required = false) Integer page,
				@RequestParam(required = false) Integer limit){	
			Pagination showCompany = companyService.showCompany(page,limit);
			if(showCompany.getListDoc() == null || showCompany.getListDoc().isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found record to retrive");
			}			
			return ResponseEntity.status(HttpStatus.OK).body(showCompany);
			
		}
}
