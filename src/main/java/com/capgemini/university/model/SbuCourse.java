/**
 * CAPGEMINI APPLIANCE CHAINS.
 * Copyright (c) 2015-2015 All Rights Reserved.
 */
package com.capgemini.university.model;

import java.util.Date;
import java.util.List;


/**
 * 
 * functional description： Category
 * @author  zhihuang@capgemini.com
 * @created Dec 18, 2015 10:22:27 AM
 * @date Dec 18, 2015 10:22:27 AM
 */

public class SbuCourse {
    
    private Integer id;
    
    private Integer sbuId;
    
    private Integer courseId;
    
    private Float duration;
    
//    private String courseName;
    
//    private Date startTime;
    
    
    private Integer seats;
    
    private Integer pmds;
    
    private Integer assignSeats;
    
    private Integer assignPmds;

    
    
	

	/**
	 * @return the duration
	 */
	public Float getDuration() {
		return duration;
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(Float duration) {
		this.duration = duration;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the sbuId
	 */
	public Integer getSbuId() {
		return sbuId;
	}

	/**
	 * @param sbuId the sbuId to set
	 */
	public void setSbuId(Integer sbuId) {
		this.sbuId = sbuId;
	}

	/**
	 * @return the courseId
	 */
	public Integer getCourseId() {
		return courseId;
	}

	/**
	 * @param courseId the courseId to set
	 */
	public void setCourseId(Integer courseId) {
		this.courseId = courseId;
	}

	/**
	 * @return the seats
	 */
	public Integer getSeats() {
		return seats;
	}

	/**
	 * @param seats the seats to set
	 */
	public void setSeats(Integer seats) {
		this.seats = seats;
	}

	/**
	 * @return the pmds
	 */
	public Integer getPmds() {
		return pmds;
	}

	/**
	 * @param pmds the pmds to set
	 */
	public void setPmds(Integer pmds) {
		this.pmds = pmds;
	}

	/**
	 * @return the assignSeats
	 */
	public Integer getAssignSeats() {
		return assignSeats;
	}

	/**
	 * @param assignSeats the assignSeats to set
	 */
	public void setAssignSeats(Integer assignSeats) {
		this.assignSeats = assignSeats;
	}

	/**
	 * @return the assignPmds
	 */
	public Integer getAssignPmds() {
		return assignPmds;
	}

	/**
	 * @param assignPmds the assignPmds to set
	 */
	public void setAssignPmds(Integer assignPmds) {
		this.assignPmds = assignPmds;
	}
    
    

	
	
   
    
    

}
