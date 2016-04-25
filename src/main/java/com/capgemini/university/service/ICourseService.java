/**
 * CAPGEMINI APPLIANCE CHAINS.
 * Copyright (c) 2015-2015 All Rights Reserved.
 */
package com.capgemini.university.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.capgemini.university.api.exception.DataAccessDeniedException;
import com.capgemini.university.common.PageResults;
import com.capgemini.university.common.Pagination;
import com.capgemini.university.model.Course;
import com.capgemini.university.model.CourseMail;
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
  
  public int addParticipant(Participant part);
  
  public List<Participant> queryParticipantList(Integer sbuId, Integer courseId);
  
  public void removeParticipant(Integer sbuId, Integer courseId, String email);
  
  public int saveOrUpdateSbuCourse(SbuCourse sc);
  
  
  
  public int updateSbuCourse(SbuCourse sc);
  
  public Course getCourseById(Integer id);
  
  public List<Course> getCourseListByEvent(String eventName);
  
  public List<Course> getAllEvent(int type);
  
  public SbuCourse getSbuCourse(Integer sbuId, Integer courseId);
  
  
  public void swap(Integer course1, Integer fromSbu, Integer toSbu, Integer action, Integer seats, Integer course2) throws DataAccessDeniedException;
  
  
  public PageResults<Map> getCourseListByPage(Map map, Pagination page) ;
  
  
  public List<CourseMail> getAllCourseToBegin();
  
  
  public void uploadCourse(InputStream in, boolean append) throws DataAccessDeniedException;
  
  public void saveCourse(List<Course> list);
	

}
