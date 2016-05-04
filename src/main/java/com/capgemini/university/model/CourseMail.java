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
    
    private String sbuName;
    
    
    private List<Map> courseList;
    
    private int totalRemainSeats = 0;
	private int totalRemainPmds = 0;


    
	/**
	 * @return the totalRemainSeats
	 */
	public int getTotalRemainSeats() {
		return totalRemainSeats;
	}


	/**
	 * @param totalRemainSeats the totalRemainSeats to set
	 */
	public void setTotalRemainSeats(int totalRemainSeats) {
		this.totalRemainSeats = totalRemainSeats;
	}


	/**
	 * @return the totalRemainPmds
	 */
	public int getTotalRemainPmds() {
		return totalRemainPmds;
	}


	/**
	 * @param totalRemainPmds the totalRemainPmds to set
	 */
	public void setTotalRemainPmds(int totalRemainPmds) {
		this.totalRemainPmds = totalRemainPmds;
	}


	/**
	 * @return the sbuName
	 */
	public String getSbuName() {
		return sbuName;
	}


	/**
	 * @param sbuName the sbuName to set
	 */
	public void setSbuName(String sbuName) {
		this.sbuName = sbuName;
	}


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

