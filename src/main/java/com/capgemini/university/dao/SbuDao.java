package com.capgemini.university.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.capgemini.university.model.Sbu;




@Repository
public interface SbuDao {
	
//	public List<SbuCourse> getCourseBySbu(Map map);

	public List<Sbu> getSbu(Map map);
	
	public List<Map> countSeatsOfOtherSbu(Map map);
	
	public List<Map> countSeatsOfSubSbu(Map map);
	

}
