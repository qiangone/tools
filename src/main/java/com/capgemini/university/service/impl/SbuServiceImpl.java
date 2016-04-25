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

import com.capgemini.university.dao.SbuDao;
import com.capgemini.university.model.Participant;
import com.capgemini.university.model.Sbu;
import com.capgemini.university.model.SbuCourse;
import com.capgemini.university.service.ICourseService;
import com.capgemini.university.service.ISbuService;

/**
 * 
 * functional description： seller interface
 * 
 * @author zhihuang@capgemini.com
 * @created Dec 18, 2015 10:37:13 AM
 * @date Dec 18, 2015 10:37:13 AM
 */

@Service(value = "sbuService")
public class SbuServiceImpl implements ISbuService {

	private final static Log logger = LogFactory
			.getLog(SbuServiceImpl.class);

	@Autowired
	private SbuDao sbuDao;
	@Autowired
	private ICourseService courseService;

	public Sbu getSbuById(int sbuId){
		Map map = new HashMap();
		map.put("id", sbuId);
		List<Sbu> list = sbuDao.getSbu(map);
		if(list != null && list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	public Sbu getParentSbuById(int parentId){
		Map map = new HashMap();
		map.put("parentId", parentId);
		List<Sbu> list = sbuDao.getSbu(map);
		if(list != null && list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	public List<Sbu> getAllParentSbuList(int sbuId){
		List<Sbu> list = new ArrayList<Sbu>();
		Sbu self = getSbuById(sbuId);
		list.add(self);
		if(self != null){
			while(true){
				int parentId = self.getParentId();
				if(parentId == 0){
					break;
				}
				self = getSbuById(parentId);
				list.add(self);
			}
		}
		return list;
	}
	
	public Sbu getSbuByMail(String mail){
		Map map = new HashMap();
		map.put("email", mail);
		List<Sbu> list = sbuDao.getSbu(map);
		if(list != null && list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	
	public List<Sbu> getSbuByLbps(String lbps){
		Map map = new HashMap();
		map.put("lbps", lbps);
		return sbuDao.getSbu(map);
		
	}
	
//	public List<SbuCourse> getCourseBySbu(int sbuId){
//		Map map = new HashMap();
//		map.put("id", sbuId);
//		return sbuDao.getCourseBySbu(map);
//	}
	
//	public List<Map> countPmdsBySbu(int parentId){
//		Map map = new HashMap();
//		map.put("id", parentId);
//		return sbuDao.countPmdsBySbu(map);
//	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map countSeats(int sbuId, int courseId){
		Map ret = new HashMap();
		
		Map map = new HashMap();
		map.put("courseId", courseId);
		map.put("sbuId", sbuId);
		List<Map> list1 = sbuDao.countSeatsOfOtherSbu(map);
		ret.put("otherCourseList", list1);
		
		//map = new HashMap();
		//map.put("sbuId", sbuId);
		List<Map> list2 = sbuDao.countSeatsOfSubSbu(map);
		ret.put("subSbuCourList", list2);
		
		if(list2 != null && list2.size()>0){
			for(Map map2 : list2){
				Integer sbuId2 = Integer.parseInt(map2.get("id").toString());
				Integer courseId2 = Integer.parseInt(map2.get("courseId").toString());
				List<Participant> parList = courseService.queryParticipantList(sbuId2, courseId2);
				map2.put("participantList", parList);
			}
		}
		
		SbuCourse sc = courseService.getSbuCourse(sbuId, courseId);
		ret.put("subCourse", sc);
		
		return ret;
		
	}
	



}
