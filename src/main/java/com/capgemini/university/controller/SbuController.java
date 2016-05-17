/**
 * CAPGEMINI APPLIANCE CHAINS.
 * Copyright (c) 2015-2015 All Rights Reserved.
 */
package com.capgemini.university.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import com.capgemini.university.model.Lbps;
import com.capgemini.university.model.Participant;
import com.capgemini.university.model.Sbu;
import com.capgemini.university.model.SbuCourse;
import com.capgemini.university.response.ResponseUtil;
import com.capgemini.university.response.WDResponse;
import com.capgemini.university.service.ICourseService;
import com.capgemini.university.service.IMailService;
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
	
	@Autowired
	private IMailService mailServcie;

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

		if (param.getMySbuId() == null) {
			return ResponseUtil.apiError("1002", "mySbuId is null");
		}
		if (param.getGiveoutCourseId() == null) {
			return ResponseUtil.apiError("1003", "giveoutCourseId is null");
		}
		if (param.getSwapSbuId() == null) {
			return ResponseUtil.apiError("1004", "swapSbuId is null");
		}
		if (param.getSwapCourseId() == null) {
			return ResponseUtil.apiError("1005", "swapCourseId is null");
		}
		if (param.getSwapSeats() == null) {
			return ResponseUtil.apiError("1006", "swapSeats is null");
		}
		

		try {
			courseService.swap(param.getMySbuId(),param.getGiveoutCourseId(),param.getSwapSbuId(),param.getSwapCourseId(),param.getSwapSeats());
			
			mailServcie.sendSwapMail(param.getMySbuId(),param.getGiveoutCourseId(),param.getSwapSbuId(),param.getSwapCourseId(),param.getSwapSeats());

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
	
	@RequestMapping(value = "/getSwapHistoryForAdmin", method = RequestMethod.POST)
	public @ResponseBody WDResponse getSwapHistoryForAdmin(@RequestBody JsonParamObj query) {
		if (query == null) {
			return ResponseUtil.apiError("1001", "Missing parameter!");
		}
		

		try {
			Map map = new HashMap();
			
			Integer currentPage = query.getCurrentPage();
			Integer pageSize = query.getPageSize();

			Pagination page = new Pagination();
			if (currentPage != null) {
				page.setCurrentPage(currentPage);
			}
			if (pageSize != null) {
				page.setPageSize(pageSize);
			}

			PageResults<Map> ret = historyService.getAllSbuSwapHistory(map, page);

			return ResponseUtil.apiSuccess(ret, "getSwapHistoryForAdmin successful");
		} catch (Exception e) {
			logger.error("getSwapHistoryForAdmin exception: ", e);
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
	
	
	@RequestMapping(value = "/updateSwapSeats", method = RequestMethod.POST)
	public @ResponseBody WDResponse updateSwapSeats(@RequestBody SbuCourse param) {
		if (param == null) {
			return ResponseUtil.apiError("1001", "Missing parameter!");
		}
		
		if(param.getSbuId()==null){
			return ResponseUtil.apiError("1002", "sbuId is null");
		}
		if(param.getCourseId() == null){
			return ResponseUtil.apiError("1003", "courseId is null");
		}
		if(param.getSwapSeats() == null){
			return ResponseUtil.apiError("1004", "swapSeats is null");
		}
		
		try {
			 SbuCourse sc = courseService.getSbuCourse(param.getSbuId(), param.getCourseId());
			 if(sc != null){
				 int leftSeat = sc.getSeats()-sc.getAssignSeats();
				 int swapSeat = param.getSwapSeats();
				 if(leftSeat < swapSeat){
					 return ResponseUtil.apiSuccess("no left seats to swap.", "no left seats to swap.");
				 }
				 sc.setSwapSeats(swapSeat);
			 }
			 
			 courseService.updateSbuCourse(sc);

			return ResponseUtil.apiSuccess("updateSwapSeats", "updateSwapSeats successful");
		} catch (Exception e) {
			logger.error("updateSwapSeats exception: ", e);
			return ResponseUtil.apiError("1100", "未知错误");
		}

	}
	
	
	@RequestMapping(value = "/sbuList", method = RequestMethod.POST)
	public @ResponseBody WDResponse sbuList(@RequestBody JsonParamObj param) {
		try {
			
			if (param == null) {
				return ResponseUtil.apiError("1001", "Missing parameter!");
			}
			
			if(param.getParentSbuId()==null){
				return ResponseUtil.apiError("1002", "parentSbuId is null");
			}
			
			List<Sbu> list = null;
			if(param.getCourseId()!=null){
				list = sbuService.getSbuListByParentIdAndParticipant(param.getParentSbuId(), param.getCourseId());
			}else{
				list = sbuService.getSbuListByParentId(param.getParentSbuId());
			}
			
			return ResponseUtil.apiSuccess(list, "sbuList successful");
		} catch (Exception e) {
			logger.error("sbuList exception: ", e);
			return ResponseUtil.apiError("1100", "未知错误");
		}

	}
	
	@RequestMapping(value = "/updateSbu", method = RequestMethod.POST)
	public @ResponseBody WDResponse updateSbu(@RequestBody Sbu sbu) {
		try {
			
			if (sbu == null) {
				return ResponseUtil.apiError("1001", "Missing parameter!");
			}
			
			if(sbu.getId()==null){
				return ResponseUtil.apiError("1002", "id is null");
			}
			
			if(sbu.getSubName()==null){
				return ResponseUtil.apiError("1003", "sbuName is null");
			}
			
			

			sbuService.updateSbu(sbu);

			return ResponseUtil.apiSuccess("updateSbu", "updateSbu successful");
		} catch (Exception e) {
			logger.error("updateSbu exception: ", e);
			return ResponseUtil.apiError("1100", "未知错误");
		}

	}
	
	@RequestMapping(value = "/deleteSbu", method = RequestMethod.POST)
	public @ResponseBody WDResponse deleteSbu(@RequestBody Sbu sbu) {
		try {
			
			if (sbu == null) {
				return ResponseUtil.apiError("1001", "Missing parameter!");
			}
			
			if(sbu.getId()==null){
				return ResponseUtil.apiError("1002", "id is null");
			}
			
			sbuService.deleteSbu(sbu.getId());

			return ResponseUtil.apiSuccess("deleteSbu", "deleteSbu successful");
		} catch (Exception e) {
			logger.error("deleteSbu exception: ", e);
			return ResponseUtil.apiError("1100", "未知错误");
		}

	}
	
	@RequestMapping(value = "/addSbu", method = RequestMethod.POST)
	public @ResponseBody WDResponse addSbu(@RequestBody Sbu sbu) {
		try {
			
			if (sbu == null) {
				return ResponseUtil.apiError("1001", "Missing parameter!");
			}
			
			if(sbu.getParentId()==null){
				return ResponseUtil.apiError("1002", "parentId is null");
			}
			
			if(sbu.getSubName()==null){
				return ResponseUtil.apiError("1003", "sbuName is null");
			}
			sbuService.addSbu(sbu);

			return ResponseUtil.apiSuccess("addSbu", "addSbu successful");
		} catch (Exception e) {
			logger.error("addSbu exception: ", e);
			return ResponseUtil.apiError("1100", "未知错误");
		}

	}
	
	
	@RequestMapping(value = "/relateSbuLbps", method = RequestMethod.POST)
	public @ResponseBody WDResponse relateSbuLbps(@RequestBody JsonParamObj param) {
		try {
			
			if (param == null) {
				return ResponseUtil.apiError("1001", "Missing parameter!");
			}
			
			if(param.getSbuId()==null){
				return ResponseUtil.apiError("1002", "sbuId is null");
			}
			
			if(param.getDisplayName()==null){
				return ResponseUtil.apiError("1003", "displayName is null");
			}
			if(param.getEmail()==null){
				return ResponseUtil.apiError("1004", "email is null");
			}
			
			
			sbuService.relateSbuLbps(param.getSbuId(), param.getSbuLbpsId(), param.getDisplayName(), param.getEmail());

			return ResponseUtil.apiSuccess("addSbuLbps", "addSbuLbps successful");
		} catch (Exception e) {
			logger.error("addSbuLbps exception: ", e);
			return ResponseUtil.apiError("1100", "未知错误");
		}

	}
	
	@RequestMapping(value = "/addSbuLbps", method = RequestMethod.POST)
	public @ResponseBody WDResponse addSbuLbps(@RequestBody JsonParamObj param) {
		try {
			
			if (param == null) {
				return ResponseUtil.apiError("1001", "Missing parameter!");
			}
			
			if(param.getSbuId()==null){
				return ResponseUtil.apiError("1002", "sbuId is null");
			}
			
			if(param.getDisplayName()==null){
				return ResponseUtil.apiError("1003", "displayName is null");
			}
			if(param.getEmail()==null){
				return ResponseUtil.apiError("1004", "email is null");
			}
			
			
			sbuService.addSbuLbps(param.getSbuId(), param.getDisplayName(), param.getEmail());

			return ResponseUtil.apiSuccess("addSbuLbps", "addSbuLbps successful");
		} catch (Exception e) {
			logger.error("addSbuLbps exception: ", e);
			return ResponseUtil.apiError("1100", "未知错误");
		}

	}
	
	
	@RequestMapping(value = "/updateSbuLbps", method = RequestMethod.POST)
	public @ResponseBody WDResponse updateSbuLbps(@RequestBody Lbps lbps) {
		try {
			
			if (lbps == null) {
				return ResponseUtil.apiError("1001", "Missing parameter!");
			}
		
			if(lbps.getId()==null){
				return ResponseUtil.apiError("1002", "id is null");
			}
			
			if(lbps.getName()==null){
				return ResponseUtil.apiError("1003", "name is null");
			}
			if(lbps.getEmail()==null){
				return ResponseUtil.apiError("1004", "email is null");
			}
			
			sbuService.updateLbps(lbps);

			return ResponseUtil.apiSuccess("updateSbuLbps", "updateSbuLbps successful");
		} catch (Exception e) {
			logger.error("updateSbuLbps exception: ", e);
			return ResponseUtil.apiError("1100", "未知错误");
		}

	}
	
	@RequestMapping(value="/exportNomination",method = RequestMethod.GET)
    public void exportNomination(@RequestParam(required=false) String sbuId, @RequestParam(required=false) String eventName, HttpServletResponse response){
        try {
        	int i=0;
        	if(!StringUtils.isEmpty(sbuId)){
        		i = Integer.parseInt(sbuId);
        	}
		
        	List<Map> list = sbuService.countParticipantsOfSbu(i, eventName);
        	sbuService.exportNomination(list, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	@RequestMapping(value="/countParticipantsByCourse",method = RequestMethod.GET)
    public @ResponseBody WDResponse  countParticipantsByCourse(@RequestParam(required=false) String courseId){
        try {
        	if (courseId == null) {
				return ResponseUtil.apiError("1001", "courseId is null");
			}
        	List<Sbu> list = sbuService.countParticipantsByCourse(Integer.parseInt(courseId));
        	return ResponseUtil.apiSuccess(list, "countParticipantsByCourse successful");
        } catch (Exception e) {
        	logger.error("addSbuLbps exception: ", e);
			return ResponseUtil.apiError("1100", "未知错误");
        }
    }
	
	@RequestMapping(value="/dashboard",method = RequestMethod.GET)
    public @ResponseBody WDResponse  dashboard(@RequestParam(required=false) String sbuId){
        try {
        	if (sbuId == null) {
				return ResponseUtil.apiError("1001", "sbuId is null");
			}
        	Map map = sbuService.countParticipantAndPmds(Integer.parseInt(sbuId));
        	return ResponseUtil.apiSuccess(map, "dashboard successful");
        } catch (Exception e) {
        	logger.error("dashboard exception: ", e);
			return ResponseUtil.apiError("1100", "未知错误");
        }
    }
	
	

}
