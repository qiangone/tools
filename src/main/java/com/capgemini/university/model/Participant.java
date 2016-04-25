/**
 * CAPGEMINI APPLIANCE CHAINS.
 * Copyright (c) 2015-2015 All Rights Reserved.
 */
package com.capgemini.university.model;

import java.util.List;


/**
 * 
 * functional description： Category
 * @author  zhihuang@capgemini.com
 * @created Dec 18, 2015 10:22:27 AM
 * @date Dec 18, 2015 10:22:27 AM
 */

public class Participant {
    
    private Integer id;
    /*
     * login cn name
     */
    private String cnName;
    
    /*
     * email
     */
    private String email;
    
    
    private String displayName;
    
    private Integer  sbuId;
    
    private Integer courseId;
    
    


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
	 * @return the cnName
	 */
	public String getCnName() {
		return cnName;
	}


	/**
	 * @param cnName the cnName to set
	 */
	public void setCnName(String cnName) {
		this.cnName = cnName;
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
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}


	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
    
    
   
   
    
    

}
