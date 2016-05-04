package com.capgemini.university.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.capgemini.university.model.Course;
import com.capgemini.university.model.CourseMail;
import com.capgemini.university.model.Participant;
import com.capgemini.university.model.SbuCourse;




@Repository
public interface CourseDao {

	public int addParticipant(Participant part);
	
	public int updateParticipant(Participant part);
	
	public int addSbuCourse(SbuCourse sc);
	
	public int addSbuCourseList(List<SbuCourse> list);
	
	public List<SbuCourse> getSbuCourse(Map map);
	
	public List<Participant> queryParticipantList(Map map);
	
	public int removeParticipant(Map map);
	
	public int updateSbuCourse(SbuCourse sc);
	
	
	public List<Course> getCourse(Map map);
	
	public List<Map> getEventList(Map map);
	
	public List<Map> getCourseListByPage(Map map);
	
	public List<Course> getAdminCourseListByPage(Map map);
	
	
	public List<CourseMail> getAllCourseToBegin(Map map);
	
	public int addCourse(Course c);
	
	public int updateCourse(Course c);
	
	public List<Map> getCourseDetail(Map map);

}
