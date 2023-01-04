package com.spring.demo2.controller;

import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spring.demo2.entity.DateOpen;
import com.spring.demo2.service.DateOpenService;

@RestController
public class DateOpenAPI {
	
	@Autowired
	private DateOpenService dateOpenService;
	
	
	@PostMapping(value = "/dateopen")
	public ResponseEntity<?> insertPriceTour(@RequestBody List<DateOpen> listDateOpen) {
		Document result = null;
		if (!listDateOpen.isEmpty()) {
			result = dateOpenService.insert(listDateOpen);
			if(result.get("PriceOpen") == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found data of event");
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("request error .Please check again!");
		}
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}
	
	@PutMapping(value = "/dateopen")
	public ResponseEntity<?> updatePriceTour(@RequestBody DateOpen dateOpen, @RequestParam(value = "id") String id) {
		Document result = null;
		if (id != "") {
			dateOpen.setId(new ObjectId(id));
			result = dateOpenService.update(dateOpen);
			if(result.get("PriceOpen") == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found data of event");
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("request error .Please check again!");
		}
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}
}
