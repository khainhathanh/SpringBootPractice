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
			/* insert vào collection DateOpen 
			 * 		input: List<DateOpen>
			 * 		output: Document{số lượng record insert, data ChangeStream}
			 */	
			result = dateOpenRepository.insert(listDateOpen);
			List<Document> listDocDate = result.getList("listDocDate", Document.class);
			Integer insertCount = result.getInteger("InsertCount");
			
			// thêm kết quả số lượng record insert vào docResult (kết quả cuối cùng)
			docResult = new Document("DateOpen", new BasicDBObject("InsertCount", insertCount));
			
			/* Lấy kết quả data ChangeStream insert vào collection PriceOpen
			 * 		input: List<PriceOpen> = dataChangeStream
			 * 		output: Document{số lượng update/ insert record PriceOpen}
			 */
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
	
	public Document update(DateOpen dateOpen) {
		Document result = null;
		Document docResult = null;
		try {
			/*	Update vào PriceTour
			 * 		input : PriceTour
			 * 		output: Long - số lượng record update, data ChangeStream
			 */
			result = dateOpenRepository.update(dateOpen);
			Document docDate = result.get("docDate", Document.class);
			Long modifiedCount = result.getLong("ModifiedCount");
			
			// thêm kết quả số lượng record update vào docResult (kết quả cuối cùng)
			docResult = new Document("DateOpen", new BasicDBObject("ModifiedCount", modifiedCount));
			
			/* update vào collection PriceOpen
			 * 		input: PriceOpen = dataChangeStream
			 * 		output: Document{số lượng record insert/update}
			 */
			if (docDate != null) {
				PriceOpen pO = new PriceOpen();
				pO.setDateOpen(docDate.getList("dateAvailable", String.class));
				pO.setTourID(docDate.getString("tourID"));
				Document docpriceOpen = priceOpen.update(pO);
				
				//thêm kết quả vào docResult
				docResult.put("PriceOpen", docpriceOpen);
			} else {
				docResult.put("PriceOpen", null);
			}

		} catch (Exception e) {
			// trường hợp không không tìm thấy kết quả data ChangStream
			throw new InternalServerException("Can't insert! Systems is error");
		}
		return docResult;
	}
}
