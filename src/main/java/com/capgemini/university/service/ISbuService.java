/**
 * CAPGEMINI APPLIANCE CHAINS.
 * Copyright (c) 2015-2015 All Rights Reserved.
 */
package com.capgemini.university.service;

import java.util.List;
import java.util.Map;

import com.capgemini.university.model.Sbu;
import com.capgemini.university.model.SbuCourse;



/**
 * 
 * functional description： seller interface
 * @author  zhihuang@capgemini.com
 * @created Dec 18, 2015 10:37:13 AM
 * @date Dec 18, 2015 10:37:13 AM
 */

public interface ISbuService {
	
	
	public List<Sbu> getSbuByLbps(String lbps);
	
	public Sbu getSbuByMail(String mail);
	
//	public List<SbuCourse> getCourseBySbu(int sbuId);
	
//	public List<Map> countPmdsBySbu(int parentId);
	
	public Map countSeats(int sbuId, int courseId);
	
	public Sbu getSbuById(int sbuId);
	
	public Sbu getParentSbuById(int parentId);
	
	public List<Sbu> getAllParentSbuList(int sbuId);
	
	
	

}
