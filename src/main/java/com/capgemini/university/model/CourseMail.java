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
    
    private String courseName;
    private int seats;
    private int pmds;
    private String toSbuName;
    
    
    private List<Map> courseList;
    
    private int totalRemainSeats = 0;
	private int totalRemainPmds = 0;


    
	/**
	 * @return the courseName
	 */
	public String getCourseName() {
		return courseName;
	}


	/**
	 * @param courseName the courseName to set
	 */
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}


	/**
	 * @return the seats
	 */
	public int getSeats() {
		return seats;
	}


	/**
	 * @param seats the seats to set
	 */
	public void setSeats(int seats) {
		this.seats = seats;
	}


	/**
	 * @return the pmds
	 */
	public int getPmds() {
		return pmds;
	}


	/**
	 * @param pmds the pmds to set
	 */
	public void setPmds(int pmds) {
		this.pmds = pmds;
	}


	/**
	 * @return the toSbuName
	 */
	public String getToSbuName() {
		return toSbuName;
	}


	/**
	 * @param toSbuName the toSbuName to set
	 */
	public void setToSbuName(String toSbuName) {
		this.toSbuName = toSbuName;
	}


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

