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

import com.spring.demo2.entity.PriceTour;
import com.spring.demo2.service.PriceTourService;

@RestController
public class PriceTourAPI {
	
	@Autowired
	private PriceTourService priceTourService;
	
	@PostMapping(value = "/pricetour")
	public ResponseEntity<?> insertPriceTour(@RequestBody List<PriceTour> listPriceTour) {
		Document result = null;
		if (!listPriceTour.isEmpty()) {
			result = priceTourService.insert(listPriceTour);
			if(result.get("PriceOpen") == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found data of event");
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("request error .Please check again!");
		}
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}
	
	@PutMapping(value = "/pricetour")
	public ResponseEntity<?> updatePriceTour(@RequestBody PriceTour priceTour, @RequestParam(value = "id") String id) {
		Document result = null;
		if (id != "") {
			priceTour.setId(new ObjectId(id));
			result = priceTourService.update(priceTour);
			if(result.get("PriceOpen") == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found data of event");
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("request error .Please check again!");
		}
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}
}
