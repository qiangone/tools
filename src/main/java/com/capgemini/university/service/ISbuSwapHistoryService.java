/**
 * CAPGEMINI APPLIANCE CHAINS.
 * Copyright (c) 2015-2015 All Rights Reserved.
 */
package com.capgemini.university.service;

import java.util.List;
import java.util.Map;

import com.capgemini.university.api.exception.DataAccessDeniedException;
import com.capgemini.university.common.PageResults;
import com.capgemini.university.common.Pagination;
import com.capgemini.university.model.Course;
import com.capgemini.university.model.Participant;
import com.capgemini.university.model.SbuCourse;
import com.capgemini.university.model.SbuSwapHistory;



/**
 * 
 * functional description： seller interface
 * @author  zhihuang@capgemini.com
 * @created Dec 18, 2015 10:37:13 AM
 * @date Dec 18, 2015 10:37:13 AM
 */

public interface ISbuSwapHistoryService {
	

	public PageResults<Map> getSbuSwapHistory(Map map, Pagination page);
  

}
