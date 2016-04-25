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
import com.capgemini.university.common.JsonParamObj;
import com.capgemini.university.model.Participant;
import com.capgemini.university.response.ResponseUtil;
import com.capgemini.university.response.WDResponse;
import com.capgemini.university.service.ICourseService;


@Controller
@RequestMapping("/api/participant")
public class ParticipantController {

	private final Log logger = LogFactory.getLog(ParticipantController.class);

	@Autowired
	private ICourseService courseService;

	@RequestMapping(value = "/removeParticipant", method = RequestMethod.POST)
	public @ResponseBody WDResponse removeParticipant(@RequestBody JsonParamObj param) {
		if (param == null) {
			return ResponseUtil.apiError("1001", "Missing parameter!");
		}
		if (param.getSbuId() == null) {
			return ResponseUtil.apiError("1002", "Missing parameter!");
		}
		
		if (param.getCourseId() == null) {
			return ResponseUtil.apiError("1003", "courseId is null");
		}
		
		
		if (param.getEmail() == null) {
			return ResponseUtil.apiError("1006", "email is null");
		}
		try {
			courseService.removeParticipant(param.getSbuId(), param.getCourseId(), param.getEmail());

			return ResponseUtil.apiSuccess("removeParticipant", "removeParticipant successful");
		} catch (Exception e) {
			logger.error("removeParticipant exception: ", e);
			return ResponseUtil.apiError("1100", "未知错误");
		}

	}
	
	@RequestMapping(value = "/query", method = RequestMethod.GET)
	public @ResponseBody WDResponse query(@RequestParam String name) {
		if (name == null) {
			return ResponseUtil.apiError("1001", "Missing parameter!");
		}
		try {
			List<Participant> list = courseService.queryParticipantByName(name);

			return ResponseUtil.apiSuccess(list, "query participant successful");
		} catch (Exception e) {
			logger.error("query participant exception: ", e);
			return ResponseUtil.apiError("1100", "未知错误");
		}

	}
	
	
	@RequestMapping(value = "/relateParticipant", method = RequestMethod.POST)
	public @ResponseBody WDResponse relateParticipant(@RequestBody JsonParamObj param) {
		if (param == null) {
			return ResponseUtil.apiError("1001", "Missing parameter!");
		}
		
		if (param.getSbuId() == null) {
			return ResponseUtil.apiError("1002", "sbuId is null");
		}
		if (param.getCourseId() == null) {
			return ResponseUtil.apiError("1003", "courseId is null");
		}
		if (param.getParticipantList()== null || param.getParticipantList().size() ==0) {
			return ResponseUtil.apiError("1004", "participantList is null");
		}
	
		
		try {
			List<Participant> list = param.getParticipantList();
			for(Participant p: list){
				p.setSbuId(param.getSbuId());
				p.setCourseId(param.getCourseId());
			}
			courseService.addParticipantList(list);
//			Participant part = new Participant();
//			part.setSbuId(param.getSbuId());
//			part.setCourseId(param.getCourseId());
//			part.setCnName(param.getLoginName());
//			part.setDisplayName(param.getDisplayName());
//			part.setEmail(param.getEmail());
//			courseService.addParticipant(part);

			return ResponseUtil.apiSuccess("relateParticipant", "relateParticipant successful");
		}catch (DataAccessDeniedException ex) {
			logger.error("relateParticipant exception: ", ex);
			return ResponseUtil.apiError("1100", ex.getMessage());
		} 
		catch (Exception e) {
			logger.error("relateParticipant exception: ", e);
			return ResponseUtil.apiError("1100", "未知错误");
		}
	}

}
