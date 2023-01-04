package com.spring.demo2.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.spring.demo2.entity.DateOpen;
import com.spring.demo2.entity.PriceOpen;
import com.spring.demo2.exception.InternalServerException;
import com.spring.demo2.repository.DateOpenRepository;

@Service
public class DateOpenService {
	
	private static Logger logger = Logger.getLogger(AvailableTourService.class);
	
	@Autowired
	private DateOpenRepository dateOpenRepository;

	@Autowired
	private PriceOpenService priceOpen;

	public Document insert(List<DateOpen> listDateOpen) {

		Document result = null;
		Document docResult = null;
		try {
			result = dateOpenRepository.insert(listDateOpen);
			List<Document> listDocDate = result.getList("listDocDate", Document.class);
			Integer insertCount = result.getInteger("InsertCount");
			
			docResult = new Document("DateOpen", new BasicDBObject("InsertCount", insertCount));
			List<PriceOpen> listPriceOpen = new ArrayList<>();
			if (!listDocDate.isEmpty()) {
				for (Document doc : listDocDate) {
					PriceOpen pO = new PriceOpen();
					pO.setTourID(doc.getString("tourID"));
					pO.setDateOpen(doc.getList("dateAvailable",String.class));
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
	
	public Document update(DateOpen dateOpen) {
		Document result = null;
		Document docResult = null;
		try {
			result = dateOpenRepository.update(dateOpen);
			Document docDate = result.get("docDate", Document.class);
			Long modifiedCount = result.getLong("ModifiedCount");
			docResult = new Document("DateOpen", new BasicDBObject("ModifiedCount", modifiedCount));
			if (docDate != null) {
				PriceOpen pO = new PriceOpen();
				pO.setDateOpen(docDate.getList("dateAvailable", String.class));
				pO.setTourID(docDate.getString("tourID"));
				Document docpriceOpen = priceOpen.update(pO);
				docResult.put("PriceOpen", docpriceOpen);
			} else {
				docResult.put("PriceOpen", null);
			}

		} catch (Exception e) {
			throw new InternalServerException("Can't insert! Systems is error");
		}
		return docResult;
	}
}
