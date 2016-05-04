package com.capgemini.university.service.impl;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.capgemini.university.common.PageResults;
import com.capgemini.university.common.Pagination;
import com.capgemini.university.model.Course;
import com.capgemini.university.model.Participant;
import com.capgemini.university.response.ResponseUtil;
import com.capgemini.university.response.WDResponse;
import com.capgemini.university.service.ISbuSwapHistoryService;
import com.capgemini.university.util.DateUtil;
import com.capgemini.university.util.JsonUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:spring/spring-application-context.xml",
		"classpath:spring/spring-application-security.xml" })
public class SbuSwapHistoryServiceImplTestCase {

	@Autowired
	private ISbuSwapHistoryService hisService;

	@Test
	public void testGetAllSbuSwapHistory() throws Exception{

		Map map = new HashMap();
		

		Integer currentPage = 1;
		Integer pageSize = 20;

		Pagination page = new Pagination();
		if (currentPage != null) {
			page.setCurrentPage(currentPage);
		}
		if (pageSize != null) {
			page.setPageSize(pageSize);
		}
		

		PageResults<Map> ret = hisService.getAllSbuSwapHistory(map, page);
		
		ResponseUtil.apiSuccess(ret, "getSwapHistory successful");

	}

	
	
	
	


}
