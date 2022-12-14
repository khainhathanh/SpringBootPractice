package com.example.demo.controller;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Employee;
import com.example.demo.entity.Pagination;
import com.example.demo.service.EmployeeService;

@RestController
public class EmployeeAPI {

	@Autowired
	EmployeeService employeeService;

	@PostMapping(value = "/employee")
	public ResponseEntity<?> createPerson(@RequestBody List<Employee> listEmployee) {
		HttpStatus stt = HttpStatus.OK;
		List<String> listIDPerson = employeeService.insert(listEmployee);
		// truong hop list truyen vao rong
		if (listIDPerson.isEmpty()) {
			stt = HttpStatus.BAD_REQUEST;
		}
		return ResponseEntity.status(stt).body(listIDPerson);
	}

	// 14.
	@GetMapping(value = "/employee/14")
	public ResponseEntity<?> showEmployee(@RequestParam Integer year,
			@RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer limit) {
		
		Pagination showEmpl = employeeService.showEmployees(year,page,limit);
		if (showEmpl.getListDoc() == null || showEmpl.getListDoc().isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found record to retrive");
		}
		return ResponseEntity.status(HttpStatus.OK).body(showEmpl);

	}

	// 15.
	@GetMapping(value = "/employee/15")
	public ResponseEntity<?> showCompany(@RequestParam Integer year,
			@RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer limit) {
		Pagination showEmpl =  employeeService.showEmpl(year, page, limit);
		if (showEmpl.getListDoc() == null || showEmpl.getListDoc().isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found record to retrive");
		}
		return ResponseEntity.status(HttpStatus.OK).body(showEmpl);

	}

	// 16.
	@GetMapping(value = "/employee/16")
	public ResponseEntity<?> showCompany(@RequestParam String category, @RequestParam Integer startYear,
			@RequestParam Integer endYear,@RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer limit) {
		Pagination showEmpl =  employeeService.showEmployee(category, startYear, endYear, page, limit);
		if (showEmpl.getListDoc() == null || showEmpl.getListDoc().isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found record to retrive");
		}
		return ResponseEntity.status(HttpStatus.OK).body(showEmpl);

	}
}
