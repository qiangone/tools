/**
 * CAPGEMINI APPLIANCE CHAINS.
 * Copyright (c) 2015-2015 All Rights Reserved.
 */
package com.capgemini.university.controller;

import java.util.List;

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
import com.capgemini.university.model.Course;
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
	
	
	@RequestMapping(value = "/testSendMail", method = RequestMethod.POST)
	public @ResponseBody WDResponse testSendMail() {
		
		
		
		try {
			mailService.notifyLbpsMail();

			return ResponseUtil.apiSuccess("testSendMail", "testSendMail successful");
		} catch(DataAccessDeniedException de){
			return ResponseUtil.apiError("1008", de.getMessage());
		}catch (Exception e) {
			logger.error("testSendMail exception: ", e);
			return ResponseUtil.apiError("1100", "未知错误");
		}
	}
	
	@RequestMapping(value = "/getAllEvent", method = RequestMethod.POST)
	public @ResponseBody WDResponse getAllEvent() {
		
		try {
			List<Course> list = courseService.getAllEvent(2);

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
	
	
	
	
	

}
