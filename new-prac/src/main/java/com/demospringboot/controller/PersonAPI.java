package com.demospringboot.controller;

import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demospringboot.entity.Person;
import com.demospringboot.service.PersonService;

@RestController
public class PersonAPI {

	@Autowired
	PersonService personService;

	@PostMapping(value = "/person")
	public ResponseEntity<?> createPerson(@RequestBody List<Person> listPerson) {
		HttpStatus stt = HttpStatus.OK;
		List<String> listIDPerson = personService.insert(listPerson);
		// truong hop list truyen vao rong
		if (listIDPerson.isEmpty()) {
			stt = HttpStatus.BAD_REQUEST;
		}
		return ResponseEntity.status(stt).body(listIDPerson);
	}

	/*
	 * client truyen thuoc tinh filter va 1 person thuoc tinh can chinh sua (ngay ca
	 * thuoc tinh filter neu can) client nhan lai so luong da record da update
	 */
	@PutMapping(value = "/person")
	public ResponseEntity<?> updatePerson(@RequestBody Person personUpdate, @RequestParam(required = false) String id,
			@RequestParam(required = false) String name, @RequestParam(required = false) Integer age,
			@RequestParam(required = false) String sex) {
		HttpStatus stt = HttpStatus.OK;
		Person personFilter = new Person();
		if (id != null) {
			personFilter.setId(new ObjectId());
		} else {
			personFilter.setId(null);
		}
		personFilter.setName(name);
		personFilter.setAge(age);
		personFilter.setSex(sex);
		Long upsertID = personService.update(personUpdate, personFilter);
		// truong hop khong tim thay record tuong ung
		if (upsertID == null) {
			stt = HttpStatus.NOT_FOUND;
		}
		// truong hop tim thay nhung khong co chinh sua gi
		else if (upsertID == 0) {
			stt = HttpStatus.BAD_REQUEST;
		}
		// truong hop he thong loi khong the update
		else if (upsertID == -1) {
			stt = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return ResponseEntity.status(stt).body(upsertID);
	}

	@DeleteMapping(value = "/person")
	public ResponseEntity<?> delete(@RequestBody List<String> ids) throws NotFoundException {
		HttpStatus stt = HttpStatus.OK;
		Long deleteID = personService.delete(ids);
		//truong hop khong tim thay id can xoa
		if (deleteID == 0) {
//			stt = HttpStatus.NOT_FOUND;
			throw new NotFoundException();
		} 
		//truong hop tim thay nhung khong xoa duoc do loi server
		else if (deleteID == -1) {
			stt = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return ResponseEntity.status(stt).body(deleteID);
	}

	@GetMapping(value = "/person")
	public ResponseEntity<?> search(@RequestParam(required = false) String id, @RequestParam(required = false) String name,
			@RequestParam(required = false) Integer age, @RequestParam(required = false) String sex) {
		Person personFilter = new Person();
		HttpStatus stt = HttpStatus.OK;
		if (id != null) {
			personFilter.setId(new ObjectId());
		} else {
			personFilter.setId(null);
		}
		personFilter.setName(name);
		personFilter.setAge(age);
		personFilter.setSex(sex);
		List<Document> listDoc =  personService.search(personFilter);
		if(listDoc.isEmpty()) {
			stt = HttpStatus.NOT_FOUND;
		}
		return ResponseEntity.status(stt).body(listDoc);
	}
}
