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
			/* insert vào collection PriceTour 
			 * 		input: List<PriceTour>
			 * 		output: Document{số lượng record insert, data ChangeStream}
			 */			
			result = priceTourRepository.insert(listPriceTour);
			List<Document> listDocPrice = result.getList("listDocPrice", Document.class);
			Integer insertCount = result.getInteger("InsertCount");
			
			// thêm kết quả số lượng record insert vào docResult (kết quả cuối cùng)
			docResult = new Document("PriceTour", new BasicDBObject("InsertCount", insertCount));
			
			/* Lấy kết quả data ChangeStream insert vào collection PriceOpen
			 * 		input: List<PriceOpen> = dataChangeStream
			 * 		output: Document{số lượng update/ insert record PriceOpen}
			 */			
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
					// thêm kết quả số lượng record insert vào docResult
					docResult.put("PriceOpen", docpriceOpen);
				}
			} else {
				// trường hợp không không tìm thấy kết quả data ChangStream
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
			/*	Update vào PriceTour
			 * 		input : PriceTour
			 * 		output: Long - số lượng record update, data ChangeStream
			 */
			result = priceTourRepository.update(priceTour);
			Document docPrice = result.get("docPrice", Document.class);
			Long modifiedCount = result.getLong("ModifiedCount");
			
			// thêm kết quả số lượng record update vào docResult (kết quả cuối cùng)
			docResult = new Document("PriceTour", new BasicDBObject("ModifiedCount", modifiedCount));
			
			/* update vào collection PriceOpen
			 * 		input: PriceOpen = dataChangeStream
			 * 		output: Document{số lượng record insert/update}
			 */
			if (docPrice != null) {
				PriceOpen pO = new PriceOpen();
				pO.setTourID(docPrice.getString("idTour"));
				pO.setPrice(docPrice.getInteger("price"));
				pO.setCurrency(docPrice.getString("currency"));
				Document docpriceOpen = priceOpen.update(pO);
				
				//thêm kết quả vào docResult
				docResult.put("PriceOpen", docpriceOpen);
			} else {
				// trường hợp không không tìm thấy kết quả data ChangStream
				docResult.put("PriceOpen", null);
			}

		} catch (Exception e) {
			logger.error(e);
			throw new InternalServerException("Can't insert! Systems is error");
		}
		return docResult;
	}

}
