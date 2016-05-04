/**
 * CAPGEMINI APPLIANCE CHAINS.
 * Copyright (c) 2015-2015 All Rights Reserved.
 */
package com.capgemini.university.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capgemini.university.common.PageResults;
import com.capgemini.university.common.Pagination;
import com.capgemini.university.dao.SbuSwapHistoryDao;
import com.capgemini.university.service.ISbuSwapHistoryService;

/**
 * 
 * functional description： seller interface
 * 
 * @author zhihuang@capgemini.com
 * @created Dec 18, 2015 10:37:13 AM
 * @date Dec 18, 2015 10:37:13 AM
 */
@Service(value = "swaphistoryService")
public class SbuSwapHistoryServiceImpl implements ISbuSwapHistoryService {

	private final static Log logger = LogFactory
			.getLog(SbuSwapHistoryServiceImpl.class);

	
	@Autowired
	private SbuSwapHistoryDao historyDao;
	
	
//	public List<SbuSwapHistory> getSbuSwapHistory(int fromSbuId){
//		Map map = new HashMap();
//		map.put("fromSbuId", fromSbuId);
//		return historyDao.getSbuSwapHistory(map);
//	}
	
	public PageResults<Map> getSbuSwapHistory(Map map, Pagination page){
		if (null == page || !page.isValidPage()) {
			return null;
		}
		
		Integer sbuId = Integer.parseInt(map.get("sbuId").toString());

		// get counts
		List<Map> list = historyDao.getSbuSwapHistory(map);
		if (list == null || list.size() == 0) {
			return null;
		}

		int totalCount = list.size();
		page.setTotalCount(totalCount);

		map.put("showpage", 1);
		map.put(Pagination.STARTINDEX, page.getStartIndex());
		map.put(Pagination.PAGESIZE, page.getPageSize());
		List list3 = new ArrayList();
		List<Map> list2 = historyDao.getSbuSwapHistory(map);
		if(list2 != null && list2.size()>0){
			for(Map map2 : list2){
				int id = Integer.parseInt(map2.get("id").toString());
				int fromSbuId = Integer.parseInt(map2.get("from_sbu_id").toString());
				String fromSbuName = map2.get("from_sbu_name").toString();
				int toSbuId = Integer.parseInt(map2.get("to_sbu_id").toString());
				String toSbuName = map2.get("to_sbu_name").toString();
				int giveOutCourseId = Integer.parseInt(map2.get("giveout_course_id").toString());
				String giveoutCourseName = map2.get("giveout_course_name").toString();
				int takeinCourseId = Integer.parseInt(map2.get("takein_course_id").toString());
				String takeinCourseName = map2.get("takein_course_name").toString();
				float giveoutDuration = Float.parseFloat(map2.get("giveout_duration").toString());
				float takeinDuration = Float.parseFloat(map2.get("takein_duration").toString());
				int seats = Integer.parseInt(map2.get("seats").toString());
				String actionBy = map2.get("action_by").toString();
				String actionTime = map2.get("action_time").toString();
				
				if(sbuId == toSbuId){
					
					Map map3 = new HashMap();
					map3.put("id", id);
					map3.put("from_sbu_id", toSbuId);
					map3.put("to_sbu_id", fromSbuId);
					map3.put("to_sbu_name", fromSbuName);
					map3.put("giveout_course_name", takeinCourseName);
					map3.put("takein_course_name", giveoutCourseName);
					map3.put("giveout_duration", takeinDuration);
					map3.put("takein_duration", giveoutDuration);
					map3.put("seats", seats);
					map3.put("action_by", actionBy);
					map3.put("action_time", actionTime);
					list3.add(map3);
					
				}else{
					list3.add(map2);
				}
			}
		}

		PageResults<Map> result = new PageResults<Map>(page, list3);

		return result;
	}
	
	public PageResults<Map> getAllSbuSwapHistory(Map map, Pagination page){
		
		if (null == page || !page.isValidPage()) {
			return null;
		}
		List<Map> list = historyDao.getSbuSwapHistory(map);
		if (list == null || list.size() == 0) {
			return null;
		}
		
		int totalCount = list.size();
		page.setTotalCount(totalCount);

		map.put("showpage", 1);
		map.put(Pagination.STARTINDEX, page.getStartIndex());
		map.put(Pagination.PAGESIZE, page.getPageSize());
		List<Map> list2 = historyDao.getSbuSwapHistory(map);
		PageResults<Map> result = new PageResults<Map>(page, list2);

		return result;
	}
	  

}
