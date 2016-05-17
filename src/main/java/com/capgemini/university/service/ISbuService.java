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
import com.capgemini.university.model.SbuLbps;



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
	
	public List<Sbu> getSbuListByParentIdAndParticipant(int parentId, int courseId);
	
	public void updateSbu(Sbu sbu);
	
	public void deleteSbu(int sbuId);
	
	public void addSbu(Sbu sbu);
	
	public void addSbuLbps(int sbuId,String displayName, String email);
	
	public void relateSbuLbps(int sbuId,  String sbuLbpsId, String displayName, String email);
	
	public SbuLbps getSbuLbps(int sbuId, int lbpsId);
	
	public SbuLbps getSbuLbpsById(int id);
	
	
	public void updateLbps(Lbps lbps);
	
	public void exportNomination(List<Map> list, HttpServletResponse response);
	
	public List<Map> countParticipantsOfSbu(int sbuId, String eventName);
	
	public Lbps getLbpsByEmail(String email);
	
	
	public List<Sbu> countParticipantsByCourse(int courseId);
	
	
	public Map countParticipantAndPmds(int sbuId);
	
	public void convertToFreeSeats();
	

}
