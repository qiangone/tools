/**
 * CAPGEMINI APPLIANCE CHAINS.
 * Copyright (c) 2015-2015 All Rights Reserved.
 */
package com.capgemini.university.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.capgemini.university.api.exception.DataAccessDeniedException;
import com.capgemini.university.common.JsonParamObj;
import com.capgemini.university.common.PageResults;
import com.capgemini.university.common.Pagination;
import com.capgemini.university.model.Participant;
import com.capgemini.university.model.Sbu;
import com.capgemini.university.model.SbuCourse;
import com.capgemini.university.response.ResponseUtil;
import com.capgemini.university.response.WDResponse;
import com.capgemini.university.service.ICourseService;
import com.capgemini.university.service.ISbuService;
import com.capgemini.university.service.ISbuSwapHistoryService;

@Controller
@RequestMapping("/api/sbu")
public class SbuController {

	private final Log logger = LogFactory.getLog(SbuController.class);

	@Autowired
	private ICourseService courseService;
	@Autowired
	private ISbuService sbuService;
	@Autowired
	private ISbuSwapHistoryService historyService;

	@RequestMapping(value = "/courseList", method = RequestMethod.POST)
	public @ResponseBody WDResponse courseList(@RequestBody JsonParamObj query) {
		if (query == null) {
			return ResponseUtil.apiError("1001", "Missing parameter!");
		}
		if (query.getSbuId() == null) {
			return ResponseUtil.apiError("1002", "sbuId is null.");
		}

		Map map = new HashMap();
		map.put("id", query.getSbuId());

		Integer currentPage = query.getCurrentPage();
		Integer pageSize = query.getPageSize();

		String courseName = query.getCourseName();

		Pagination page = new Pagination();
		if (currentPage != null) {
			page.setCurrentPage(currentPage);
		}
		if (pageSize != null) {
			page.setPageSize(pageSize);
		}
		if (StringUtils.isNotEmpty(courseName)) {
			map.put("courseName", courseName);
		}if (StringUtils.isNotEmpty(query.getType())) {
			map.put("type", query.getType());
		}


		try {
			PageResults<Map> ret = courseService.getCourseListByPage(map, page);

			return ResponseUtil.apiSuccess(ret, "query participant successful");
		} catch (Exception e) {
			logger.error("query participant exception: ", e);
			return ResponseUtil.apiError("1100", "未知错误");
		}

	}

	@RequestMapping(value = "/swap", method = RequestMethod.POST)
	public @ResponseBody WDResponse swapSeats(@RequestBody JsonParamObj param) {
		if (param == null) {
			return ResponseUtil.apiError("1001", "Missing parameter!");
		}

		if (param.getCourseId1() == null) {
			return ResponseUtil.apiError("1002", "courseId1 is null");
		}
		if (param.getFromSbuId() == null) {
			return ResponseUtil.apiError("1003", "fromSbuId is null");
		}
		if (param.getToSbuId() == null) {
			return ResponseUtil.apiError("1004", "toSbuId is null");
		}
		if (param.getAction() == null) {
			return ResponseUtil.apiError("1005", "action is null");
		}
		if (param.getSeats() == null) {
			return ResponseUtil.apiError("1006", "seats is null");
		}
		if (param.getCourseId2() == null) {
			return ResponseUtil.apiError("1007", "courseId2 is null");
		}

		try {
			courseService.swap(param.getCourseId1(), param.getFromSbuId(),
					param.getToSbuId(), param.getAction(), param.getSeats(),
					param.getCourseId2());

			return ResponseUtil.apiSuccess("swapSeats", "swapSeats successful");
		} catch (DataAccessDeniedException de) {
			return ResponseUtil.apiError("1008", de.getMessage());
		} catch (Exception e) {
			logger.error("swapSeats exception: ", e);
			return ResponseUtil.apiError("1100", "未知错误");
		}
	}

	@RequestMapping(value = "/countSeats", method = RequestMethod.GET)
	public @ResponseBody WDResponse countSeats(@RequestParam String sbuId,
			@RequestParam String courseId) {
		if (sbuId == null) {
			return ResponseUtil.apiError("1001", "sbuId is null.");
		}
		if (courseId == null) {
			return ResponseUtil.apiError("1002", "courseId is null.");
		}
		try {
			Sbu sbu = sbuService.getSbuById(Integer.parseInt(sbuId));
			if(sbu.getParentId() == 0){
				Map map = sbuService.countSeats(Integer.parseInt(sbuId),
						Integer.parseInt(courseId));

				return ResponseUtil.apiSuccess(map, "countSeats successful");
			}else{
				Map map  = new HashMap();
				Integer sbuId2 = Integer.parseInt(sbuId);
				Integer courseId2 = Integer.parseInt(courseId);
				List<Participant> parList = courseService.queryParticipantList(sbuId2, courseId2);
				SbuCourse sc = courseService.getSbuCourse(sbuId2, courseId2);
				map.put("subCourse", sc);
				map.put("sbuName", sbu.getSubName());
				map.put("participantList", parList);
				return ResponseUtil.apiSuccess(map, "countSeats successful");
			}
			
			
			
		} catch (Exception e) {
			logger.error("countSeats exception: ", e);
			return ResponseUtil.apiError("1100", "未知错误");
		}

	}

	@RequestMapping(value = "/getSwapHistory", method = RequestMethod.POST)
	public @ResponseBody WDResponse getSwapHistory(@RequestBody JsonParamObj query) {
		if (query == null) {
			return ResponseUtil.apiError("1001", "Missing parameter!");
		}
		if(query.getSbuId() == null){
			return ResponseUtil.apiError("1002", "sbuId is null.");
		}

		try {
			Map map = new HashMap();
			map.put("sbuId", query.getSbuId());

			Integer currentPage = query.getCurrentPage();
			Integer pageSize = query.getPageSize();

			Pagination page = new Pagination();
			if (currentPage != null) {
				page.setCurrentPage(currentPage);
			}
			if (pageSize != null) {
				page.setPageSize(pageSize);
			}
			

			PageResults<Map> ret = historyService.getSbuSwapHistory(map, page);

			return ResponseUtil.apiSuccess(ret, "getSwapHistory successful");
		} catch (Exception e) {
			logger.error("getSwapHistory exception: ", e);
			return ResponseUtil.apiError("1100", "未知错误");
		}

	}

}
