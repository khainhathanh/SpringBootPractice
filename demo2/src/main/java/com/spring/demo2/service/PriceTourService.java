package com.spring.demo2.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.spring.demo2.entity.PriceOpen;
import com.spring.demo2.entity.PriceTour;
import com.spring.demo2.exception.InternalServerException;
import com.spring.demo2.repository.PriceTourRepository;

@Service
public class PriceTourService {
	
	private static Logger logger = Logger.getLogger(AvailableTourService.class);
	
	@Autowired
	private PriceTourRepository priceTourRepository;

	@Autowired
	private PriceOpenService priceOpen;

	public Document insert(List<PriceTour> listPriceTour) {

		Document result = null;
		Document docResult = null;
		try {
			result = priceTourRepository.insert(listPriceTour);
			List<Document> listDocPrice = result.getList("listDocPrice", Document.class);
			Integer insertCount = result.getInteger("InsertCount");
			
			docResult = new Document("PriceTour", new BasicDBObject("InsertCount", insertCount));
			List<PriceOpen> listPriceOpen = new ArrayList<>();
			if (!listDocPrice.isEmpty()) {
				for (Document doc : listDocPrice) {
					PriceOpen pO = new PriceOpen();
					pO.setTourID(doc.getString("tourID"));
					pO.setPrice(doc.getInteger("price"));
					pO.setCurrency(doc.getString("currency"));
					listPriceOpen.add(pO);
				}
				if (!listPriceOpen.isEmpty()) {
					Document docpriceOpen = priceOpen.insert(listPriceOpen);
					docResult.put("PriceOpen", docpriceOpen);
				}
			} else {
				docResult.put("PriceOpen", null);
			}
		} catch (Exception e) {
			logger.error(e);
			throw new InternalServerException("Can't insert! Systems is error");
		}
		return docResult;
	}

	public Document update(PriceTour priceTour) {
		Document result = null;
		Document docResult = null;
		try {
			result = priceTourRepository.update(priceTour);
			Document docPrice = result.get("docPrice", Document.class);
			Long modifiedCount = result.getLong("ModifiedCount");
			
			docResult = new Document("PriceTour", new BasicDBObject("ModifiedCount", modifiedCount));
			
			if (docPrice != null) {
				PriceOpen pO = new PriceOpen();
				pO.setTourID(docPrice.getString("idTour"));
				pO.setPrice(docPrice.getInteger("price"));
				pO.setCurrency(docPrice.getString("currency"));
				Document docpriceOpen = priceOpen.update(pO);
				docResult.put("PriceOpen", docpriceOpen);
			} else {
				docResult.put("PriceOpen", null);
			}

		} catch (Exception e) {
			logger.error(e);
			throw new InternalServerException("Can't insert! Systems is error");
		}
		return docResult;
	}

}
