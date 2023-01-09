package com.springboot.hahalolo.service;

import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.springboot.hahalolo.exception.InternalServerException;
import com.springboot.hahalolo.repository.TourRepository;


@Service
public class TourService {
	
	private static Logger logger = Logger.getLogger(TourService.class);
	
	@Autowired
	private TourRepository tourRepository;
	
	public <T> List<Map<String,T>> showList(String nameTour, Integer typeTour, String langTour, String topicTour , Integer page, Integer limit){
		Integer skip = (page-1)* limit;
		List<Map<String,T>> listDocTour = null;
		try {
			listDocTour = tourRepository.showList( nameTour,  typeTour,  langTour,  topicTour, skip, limit);
		} catch (Exception e) {
			logger.error(e);
			throw new InternalServerException("Lỗi truy vấn dữ liệu");
		}
		return listDocTour;
	}
}
