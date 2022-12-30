package com.spring.demo2.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.demo2.entity.Pagination;
import com.spring.demo2.exception.InternalServerException;
import com.spring.demo2.repository.DateOpenRepository;
import com.spring.demo2.repository.PriceTourRepository;
import com.spring.demo2.repository.TourRepository;

@Service
public class AvailableTourService {

	private static Logger logger = Logger.getLogger(AvailableTourService.class);

	@Autowired
	private TourRepository tourRepository;

	@Autowired
	private PriceTourRepository priceTourReposiotry;

	@Autowired
	private DateOpenRepository dateOpenRepository;

	public Pagination showDB(Integer numSlot, String lang, String date, String currency, Integer page, Integer limit) {
		Pagination pagination = new Pagination();
		Integer skip = (page - 1) * limit;
		Iterator<Document> aggregate1 = tourRepository.showDBTour(numSlot, lang);
		Map<ObjectId, Document> mapDoc1 = new HashMap<>();
		while (aggregate1.hasNext()) {
			Document doc1 = aggregate1.next();
			mapDoc1.put(doc1.getObjectId("_id"), doc1);
		}
		List<Document> result = new ArrayList<Document>();
		CompletableFuture<Object> completeResult = null;
		if (!mapDoc1.isEmpty()) {
			Set<ObjectId> keySet1 = mapDoc1.keySet();
			Map<ObjectId, Document> mapDoc2 = new HashMap<>();
			Document doc3 = new Document();
			CompletableFuture<Map<ObjectId, Document>> futurePriceTour = CompletableFuture
					.supplyAsync(() -> priceTourReposiotry.showDBPriceTour(keySet1, date)).thenApply(fn -> {
						Document doc2 = new Document();
						while (fn.hasNext()) {
							doc2 = fn.next();
							mapDoc2.put(doc2.getObjectId("tourID"), doc2);
						}
						System.out.println("1");
						return mapDoc2;
					});
			CompletableFuture<Document> futureDateOpen = CompletableFuture
					.supplyAsync(() -> dateOpenRepository.showDBDateOpen(keySet1, date, skip, limit)).thenApply(fn -> {
						doc3.put("pagination", fn);
						System.out.println("2");
						return doc3;
					});
			CompletableFuture<Void> completeAllof = CompletableFuture.allOf(futurePriceTour, futureDateOpen);
			completeResult = completeAllof.handle((res, ex) -> {
				if (ex != null) {
					logger.error(ex);
					throw new InternalServerException("Thread is error!");
				} else {
					Document docPagination = doc3.get("pagination", Document.class);
					List<Document> listDoc3 = docPagination.getList("listDoc", Document.class);
					if (!listDoc3.isEmpty()) {
						Integer recordTotal = docPagination.getInteger("recordTotal");
						Integer totalPage = null;
						if (recordTotal <= limit) {
							recordTotal = limit;
							totalPage = recordTotal / limit;
						} else {
							if (recordTotal % limit > 0) {
								totalPage = (recordTotal / limit) + 1;
							} else {
								totalPage = recordTotal / limit;
							}
						}
					
						for (Document itemDoc : listDoc3) {
							ObjectId id = itemDoc.getObjectId("tourID");
							if (mapDoc2.containsKey(id) && mapDoc1.containsKey(id)) {
								String icurrency = mapDoc2.get(id).getString("currency");
								Integer priceTotal = null;
								if (icurrency.contentEquals(currency) == false) {
									Integer price = mapDoc2.get(id).getInteger("price");
									if (currency.contentEquals("Vietnamese")) {
										priceTotal = price * 20000 * numSlot;
									} else if (currency.contentEquals("USD")) {
										priceTotal = price / 20000 * numSlot;
									}
								}
								Document doc = new Document("dateOpend", itemDoc);
								doc.append("priceTour", mapDoc2.get(id));
								doc.append("tourInfo", mapDoc1.get(id));
								doc.append("totalPriceTour", new Document("currency", currency)
										.append("numSlot", numSlot).append("totalPrice", priceTotal));
								result.add(doc);
							}
						}
						if(!result.isEmpty()) {
							pagination.setTotalPage(totalPage);
							pagination.setPageCurrent(page);
							pagination.setListDoc(result);
						}
					}
				}
				return null;
			});
		}
		if (completeResult != null) {
			while (!completeResult.isDone()) {
				completeResult.join();
			}
		}
		return pagination;
	}
}
