package com.spring.demo2.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.spring.demo2.entity.PriceOpen;
import com.spring.demo2.exception.InternalServerException;
import com.spring.demo2.repository.PriceOpenRepository;

@Component
public class PriceOpenService {
	
	private static Logger logger = Logger.getLogger(AvailableTourService.class);
	
	@Autowired
	private PriceOpenRepository priceOpenRepository;

	public Document insert(List<PriceOpen> listPriceOpen) {
		Document docResult = null;
		try {
			List<PriceOpen> listInsertPriceOpen = new ArrayList<>();
			List<PriceOpen> listUpdatePriceOpen = new ArrayList<>();
			for (PriceOpen itemPriceOpen : listPriceOpen) {
				Document docPriceOpen = priceOpenRepository.find(itemPriceOpen.getTourID());
				if (docPriceOpen != null) {
					listUpdatePriceOpen.add(itemPriceOpen);
				} else {
					listInsertPriceOpen.add(itemPriceOpen);
				}
			}
			if (!listInsertPriceOpen.isEmpty() || !listUpdatePriceOpen.isEmpty()) {
				docResult = priceOpenRepository.insert(listInsertPriceOpen, listUpdatePriceOpen);
			}
		} catch (Exception e) {
			logger.error(e);
			throw new InternalServerException("Can't insert! Systems is error");
		}
		return docResult;
	}
	
	public Document update(PriceOpen priceOpen) {
		Document docResult = null;
		try {
			docResult = priceOpenRepository.update(priceOpen);
		} catch (Exception e) {
			logger.error(e);
			throw new InternalServerException("Can't insert! Systems is error");
		}
		return docResult;
	}
}
