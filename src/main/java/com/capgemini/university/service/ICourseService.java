/**
 * CAPGEMINI APPLIANCE CHAINS.
 * Copyright (c) 2015-2015 All Rights Reserved.
 */
package com.capgemini.university.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.capgemini.university.api.exception.DataAccessDeniedException;
import com.capgemini.university.common.PageResults;
import com.capgemini.university.common.Pagination;
import com.capgemini.university.model.Course;
import com.capgemini.university.model.CourseMail;
import com.capgemini.university.model.FreeSeatPool;
import com.capgemini.university.model.Participant;
import com.capgemini.university.model.SbuCourse;



/**
 * 
 * functional description： seller interface
 * @author  zhihuang@capgemini.com
 * @created Dec 18, 2015 10:37:13 AM
 * @date Dec 18, 2015 10:37:13 AM
 */

public interface ICourseService {
	

  public List<Participant> queryParticipantByName(String name);
  
  public int addParticipantList(List<Participant> list);
  
  public void takeFreeParticipantList(List<Participant> list);
  
  public int addParticipant(Participant part);
  
  public List<Participant> queryParticipantList(Integer sbuId, Integer courseId);
  
  public void removeParticipant(Integer sbuId, Integer courseId, String email);
  
  public int saveOrUpdateSbuCourse(SbuCourse sc);
  
  
  
  public int updateSbuCourse(SbuCourse sc);
  
  public Course getCourseById(Integer id);
  
  public List<Course> getCourseListByEvent(String eventName);
  
  public List<Map> getSbuCourseListByEvent(String type,String eventName, String sbuId);
  
  public List<Map> getEventList(String type, String sbuId);
  
  public SbuCourse getSbuCourse(Integer sbuId, Integer courseId);
  
  
  public void swap_bak(Integer course1, Integer fromSbu, Integer toSbu, Integer action, Integer seats, Integer course2) throws DataAccessDeniedException;
  
  public void swap(Integer mySbuId , Integer giveoutCourseId, Integer swapSbuId,
	         Integer swapCourseId, Integer swapSeats);
  
  public PageResults<Map> getCourseListByPage(Map map, Pagination page) ;
  
  public PageResults<Course> getAdminCourseListByPage(Map map, Pagination page) ;
  
  public Course getAdminCourse(Integer id);
  
  
  public List<CourseMail> getAllCourseBeforeStarting(int week);
  
  
  public void uploadCourse(InputStream in, boolean append) throws DataAccessDeniedException;
  
  public void saveCourse(List<Course> list);
  
  public void updateCourse(Course c);
  
  public void updatedAttend(int participantId, int attend);
  
  public List<Map> getAllOtherSwapCourseList(int sbuId) ;
  
  public void deleteCourse(int courseId);
  
  public FreeSeatPool getFreeSeatPoolById(int courseId);
  
  public List<FreeSeatPool> getFreeSeatPoolList();
	

}
