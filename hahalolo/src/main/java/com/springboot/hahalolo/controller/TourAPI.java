package com.springboot.hahalolo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.hahalolo.entity.ResponseOutput;
import com.springboot.hahalolo.service.TourService;

@RestController
public class TourAPI {
	
	@Autowired
	private TourService tourService;
	
	@GetMapping(value = "/hahalolo")
	public <T> ResponseOutput<List<Map<String,T>>> showList(@RequestParam(value = "nameTour", required = false) String nameTour,
			@RequestParam(value = "typeTour", required = false) Integer typeTour,
			@RequestParam(value = "langTour", required = false) String langTour,
			@RequestParam(value = "topicTour", required = false) String topicTour,
			@RequestParam(value = "ofset", required = false , defaultValue = "1") Integer page,
			@RequestParam(value = "limit", required = false , defaultValue = "5") Integer limit){
		if(page <= 0 && limit <=0) {
			page = 1;
			limit = 5;
		}
		List<Map<String,T>> listDocTour = tourService.showList(nameTour, typeTour, langTour, topicTour, page, limit);
		ResponseOutput<List<Map<String,T>>> responseOutput = new ResponseOutput<>();
		if(!listDocTour.isEmpty()) {
			responseOutput.setCode(200);
			responseOutput.setSuccess(true);
			responseOutput.setData(listDocTour);
		}else {
			responseOutput.setCode(2004);
			responseOutput.setMessage("No Content");
			responseOutput.setSuccess(false);
		}
		return responseOutput;
	}
}
