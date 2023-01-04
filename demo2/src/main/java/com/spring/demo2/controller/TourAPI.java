package com.spring.demo2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spring.demo2.entity.Pagination;
import com.spring.demo2.service.AvailableTourService;

@RestController
public class TourAPI {

	@Autowired
	private AvailableTourService tourService;
	/*
	 * input: numSlot, lang, date, currency call 3 collection Tour, PriceTour,
	 * DateOpen Từ Collection Tour lấy doc map (lang & numSlot)-> kết quả 1
	 * (listTour) Từ Collection PriceTour lấy ra doc map với từng idTour ở kết quả
	 * 1. Đồng thời so sánh date trong khoảng dateApplyStart & dateApplyEnd trong
	 * PriceTour -> kết quả 2 (listPriceTour) Từ Collection DateOpen lấy ra doc map
	 * với từng idTour ở kết quả 2. Đồng thời so sánh date = dateAvailable trong
	 * DateOpen -> kết quả 3 (listDateOpen) Từ kết quả 3 , lọc trong kết quả 1 có
	 * các idTour map với các idTour ở kết quả 3 -> kết quả 4 (listTour2) Từ kết quả
	 * 4 , lọc trong kết quả 2 các idTour map với các idTour trong kết quả 4 -> kết
	 * quả 5 (listPriceTour2) Từ kết quả 5 , lấy ra currency , xử lý chuyển đổi
	 * 
	 */

	@GetMapping(value = "/tour")
	public ResponseEntity<?> showTour(@RequestParam(value = "numSlot") Integer numSlot,
			@RequestParam(value = "lang") String lang, @RequestParam(value = "date") String date,
			@RequestParam(value = "currency") String currency,
			@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
			@RequestParam(value = "limit", required = false, defaultValue = "5") Integer limit) {
		Pagination pagination = null;
		if (numSlot > 0) {
			if (page <= 0 && limit <= 0) {
				page = 1;
				limit = 5;
			}
			pagination = tourService.showDB(numSlot, lang, date, currency, page, limit);
			if (pagination.getListDoc().isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found record valid!");
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("path error!");
		}
		return ResponseEntity.status(HttpStatus.OK).body(pagination);
	}
	
}
