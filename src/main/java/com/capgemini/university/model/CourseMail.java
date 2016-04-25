/**
 * CAPGEMINI APPLIANCE CHAINS.
 * Copyright (c) 2015-2015 All Rights Reserved.
 */
package com.capgemini.university.model;

import java.util.List;
import java.util.Map;


/**
 * 
 * functional description： Category
 * @author  zhihuang@capgemini.com
 * @created Dec 18, 2015 10:22:27 AM
 * @date Dec 18, 2015 10:22:27 AM
 */

public class CourseMail {
    
    
    
    private String name;
    

    private String email;
    
    
    private List<Map> courseList;


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}


	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}


	/**
	 * @return the courseList
	 */
	public List<Map> getCourseList() {
		return courseList;
	}


	/**
	 * @param courseList the courseList to set
	 */
	public void setCourseList(List<Map> courseList) {
		this.courseList = courseList;
	}
    
    

}

