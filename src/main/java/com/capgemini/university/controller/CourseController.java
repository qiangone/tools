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
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.capgemini.university.api.exception.DataAccessDeniedException;
import com.capgemini.university.common.JsonParamObj;
import com.capgemini.university.common.PageResults;
import com.capgemini.university.common.Pagination;
import com.capgemini.university.model.Course;
import com.capgemini.university.model.Participant;
import com.capgemini.university.model.SbuCourse;
import com.capgemini.university.response.ResponseUtil;
import com.capgemini.university.response.WDResponse;
import com.capgemini.university.service.ICourseService;
import com.capgemini.university.service.IMailService;


@Controller
@RequestMapping("/api/course")
public class CourseController {

	private final Log logger = LogFactory.getLog(CourseController.class);

	@Autowired
	private ICourseService participantService;
	@Autowired
	private ICourseService courseService;
	@Autowired
	private IMailService mailService;

	
	@RequestMapping(value = "/assignSeats", method = RequestMethod.POST)
	public @ResponseBody WDResponse assignSeatsToCourse(@RequestBody SbuCourse param) {
		if (param == null) {
			return ResponseUtil.apiError("1001", "Missing parameter!");
		}
		
		if (param.getSbuId() == null) {
			return ResponseUtil.apiError("1002", "sbuId is null");
		}
		if (param.getCourseId() == null) {
			return ResponseUtil.apiError("1003", "courseId is null");
		}
		if (param.getSeats() == null) {
			return ResponseUtil.apiError("1004", "seats is null");
		}
		
		
		try {
			courseService.saveOrUpdateSbuCourse(param);

			return ResponseUtil.apiSuccess("addSbuCourse", "addSbuCourse successful");
		} catch(DataAccessDeniedException de){
			return ResponseUtil.apiError("1008", de.getMessage());
		}catch (Exception e) {
			logger.error("addSbuCourse exception: ", e);
			return ResponseUtil.apiError("1100", "未知错误");
		}
	}
	
	
	@RequestMapping(value = "/getAllEvent", method = RequestMethod.GET)
	public @ResponseBody WDResponse getAllEvent(@RequestParam String type) {
		
		try {
			List<Map> list = courseService.getEventList(type);

			return ResponseUtil.apiSuccess(list, "getAllEvent");
		} catch(DataAccessDeniedException de){
			return ResponseUtil.apiError("1008", de.getMessage());
		}catch (Exception e) {
			logger.error("getAllEvent exception: ", e);
			return ResponseUtil.apiError("1100", "未知错误");
		}
	}
	
	@RequestMapping(value = "/getCourseListByEvent", method = RequestMethod.GET)
	public @ResponseBody WDResponse getCourseListByEvent(@RequestParam String eventName) {
		
		try {
			List<Course> list = courseService.getCourseListByEvent(eventName);

			return ResponseUtil.apiSuccess(list, "getCourseListByEvent");
		} catch(DataAccessDeniedException de){
			return ResponseUtil.apiError("1008", de.getMessage());
		}catch (Exception e) {
			logger.error("getCourseListByEvent exception: ", e);
			return ResponseUtil.apiError("1100", "未知错误");
		}
	}
	
	
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody WDResponse uploadCourse(
			@RequestParam(required = false) String append,
			@RequestParam(value = "file", required = false) CommonsMultipartFile course
			) {
		
		try {
			if(StringUtils.isNotEmpty(append) && "0".equalsIgnoreCase(append)){
				courseService.uploadCourse(course.getInputStream(), false);
			}else{
				courseService.uploadCourse(course.getInputStream(), true);
			}
			return ResponseUtil.apiSuccess("upload course Successfully.", "upload course Successfully.");
		} catch(DataAccessDeniedException de){
			return ResponseUtil.apiError("1008", de.getMessage());
		}catch (Exception e) {
			logger.error("upload course exception: ", e);
			return ResponseUtil.apiError("1100", "未知错误");
		}
		
	}
	
	@RequestMapping(value = "/adminCourseList", method = RequestMethod.POST)
	public @ResponseBody WDResponse adminCourseList(@RequestBody JsonParamObj query) {
		if (query == null) {
			return ResponseUtil.apiError("1001", "Missing parameter!");
		}
		

		Map map = new HashMap();
		
		Integer currentPage = query.getCurrentPage();
		Integer pageSize = query.getPageSize();

		String courseName = query.getCourseName();
		Integer sbuId = query.getSbuId();
		String eventName = query.getEventName();

		Pagination page = new Pagination();
		if (currentPage != null) {
			page.setCurrentPage(currentPage);
		}
		if (pageSize != null) {
			page.setPageSize(pageSize);
		}
		
		
		if (StringUtils.isNotEmpty(eventName)) {
			map.put("eventName", eventName);
		}


		try {
			PageResults<Course> ret = courseService.getAdminCourseListByPage(map, page);

			return ResponseUtil.apiSuccess(ret, "query adminCourseList successful");
		} catch (Exception e) {
			logger.error("query adminCourseList exception: ", e);
			return ResponseUtil.apiError("1100", "未知错误");
		}

	}
	
	@RequestMapping(value = "/getCourseInfo", method = RequestMethod.GET)
	public @ResponseBody WDResponse getCourseInfo(@RequestParam String id) {
		if (id == null) {
			return ResponseUtil.apiError("1001", "id is null");
		}
		
		try {
			Course ret = courseService.getAdminCourse(Integer.parseInt(id));

			return ResponseUtil.apiSuccess(ret, "query getCourseInfo successful");
		} catch (Exception e) {
			logger.error("query getCourseInfo exception: ", e);
			return ResponseUtil.apiError("1100", "未知错误");
		}

	}
	
	@RequestMapping(value = "/updateCourse", method = RequestMethod.POST)
	public @ResponseBody WDResponse updateCourse(@RequestBody Course course) {
		if (course == null) {
			return ResponseUtil.apiError("1001", "Missing parameter!");
		}
		
		if(course.getId() ==0){
			return ResponseUtil.apiError("1002", "id is null");
		}
		
		try {
			 courseService.updateCourse(course);

			return ResponseUtil.apiSuccess("updateCourse", "updateCourse successful");
		} catch (Exception e) {
			logger.error("updateCourse exception: ", e);
			return ResponseUtil.apiError("1100", "未知错误");
		}

	}
	
	
	@RequestMapping(value = "/recordAttend", method = RequestMethod.POST)
	public @ResponseBody WDResponse recordAttend(@RequestBody Participant pa) {
		if (pa == null) {
			return ResponseUtil.apiError("1001", "Missing parameter!");
		}
		
		if(pa.getId() ==0){
			return ResponseUtil.apiError("1002", "id is null");
		}
		
		if(pa.getAttend() ==0){
			return ResponseUtil.apiError("1003", "attend is null");
		}
		
		try {
			 courseService.updatedAttend(pa.getId(), pa.getAttend());

			return ResponseUtil.apiSuccess("recordAttend", "recordAttend successful");
		} catch (Exception e) {
			logger.error("recordAttend exception: ", e);
			return ResponseUtil.apiError("1100", "未知错误");
		}

	}
	
	
	

}
