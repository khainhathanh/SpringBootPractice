package com.demospringboot.controller;

import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
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
	public List<String> createPerson(@RequestBody List<Person> listPerson) {
		return personService.insert(listPerson);
	}

	/*
	 * client truyen thuoc tinh filter va 1 person thuoc tinh can chinh sua (ngay ca
	 * thuoc tinh filter neu can) client nhan lai so luong da record da update
	 */
	@PutMapping(value = "/person")
	public long updatePerson(@RequestBody Person personUpdate, @RequestParam(required = false) String id,
			@RequestParam(required = false) String name, @RequestParam(required = false) Integer age,
			@RequestParam(required = false) String sex) {
		Person personFilter = new Person();
		if (id != null) {
			personFilter.setId(new ObjectId());
		} else {
			personFilter.setId(null);
		}
		personFilter.setName(name);
		personFilter.setAge(age);
		personFilter.setSex(sex);
		return personService.update(personUpdate, personFilter);
	}

	@DeleteMapping(value = "/person")
	public long delete(@RequestBody List<String> ids) {
		return personService.delete(ids);
	}

	@GetMapping(value = "/person")
	public List<Document> search(@RequestParam(required = false) String id,
								@RequestParam(required = false) String name,
								@RequestParam(required = false) Integer age, 
								@RequestParam(required = false) String sex) {
		Person personFilter = new Person();
		if (id != null) {
			personFilter.setId(new ObjectId());
		} else {
			personFilter.setId(null);
		}
		personFilter.setName(name);
		personFilter.setAge(age);
		personFilter.setSex(sex);
		return personService.search(personFilter);
	}
}
