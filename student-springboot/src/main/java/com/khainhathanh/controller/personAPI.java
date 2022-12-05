package com.khainhathanh.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.khainhathanh.dto.NewPersonDTO;
import com.khainhathanh.service.INewPersonService;

@RestController
public class personAPI {
	
	@Autowired
	INewPersonService newPersonService;
	
	@PostMapping(value = "/person")
	public NewPersonDTO createStudent(@RequestBody NewPersonDTO personDTO) {
		return newPersonService.save(personDTO);
	}
	
	@PutMapping(value = "/person/{id}")
	public NewPersonDTO updateStudent(@RequestBody NewPersonDTO personDTO,@PathVariable("id") long id) {
		personDTO.setId(id);
		return newPersonService.update(personDTO);
	}
	
	@DeleteMapping(value = "/person")
	public void deleteStudent(@RequestBody long[] ids) {
		newPersonService.delete(ids);
	}
	
	@GetMapping(value = "/person")
	public List<NewPersonDTO> showPersonByName(@RequestParam("nameper") String name) {	
		return newPersonService.listPerson(name);
	}
}
