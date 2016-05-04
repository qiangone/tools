/**
 * CAPGEMINI APPLIANCE CHAINS.
 * Copyright (c) 2015-2015 All Rights Reserved.
 */
package com.capgemini.university.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.capgemini.university.model.Lbps;
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
	
	public List<Sbu> getSbuByMail(String mail);
	
//	public List<SbuCourse> getCourseBySbu(int sbuId);
	
//	public List<Map> countPmdsBySbu(int parentId);
	
	public Map countSeats(int sbuId, int courseId);
	
	public Sbu getSbuById(int sbuId);
	
	public Sbu getParentSbuById(int parentId);
	
	public List<Sbu> getAllParentSbuList(int sbuId);
	
	public List<Sbu> getSbuListByParentId(int parentId);
	
	public void updateSbu(Sbu sbu);
	
	public void deleteSbu(int sbuId);
	
	public void addSbu(Sbu sbu);
	
	public void addSbuLbps(int sbuId, String displayName, String email);
	
	public void updateLbps(Lbps lbps);
	
	public void exportNomination(List<Map> list, HttpServletResponse response);
	
	public List<Map> countParticipantsOfSbu(int sbuId);
	
	public Lbps getLbpsByEmail(String email);
	

}
