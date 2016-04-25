/**
 * CAPGEMINI APPLIANCE CHAINS.
 * Copyright (c) 2015-2015 All Rights Reserved.
 */
package com.capgemini.university.model;

import java.util.Date;


/**
 * 
 * functional description： Category
 * @author  zhihuang@capgemini.com
 * @created Dec 18, 2015 10:22:27 AM
 * @date Dec 18, 2015 10:22:27 AM
 */

public class SbuSwapHistory {
    
    private Integer id;
    private Integer fromSbuId;
    private Integer toSbuId;
    private Integer giveoutCourseId;
    private Integer takeinCourseId;
    private Float giveoutDuration;
    private Float takeinDuration;
    private Integer seats;
    private String actionBy;
    private Date actionTime;
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
	 * @return the fromSbuId
	 */
	public Integer getFromSbuId() {
		return fromSbuId;
	}
	/**
	 * @param fromSbuId the fromSbuId to set
	 */
	public void setFromSbuId(Integer fromSbuId) {
		this.fromSbuId = fromSbuId;
	}
	/**
	 * @return the toSbuId
	 */
	public Integer getToSbuId() {
		return toSbuId;
	}
	/**
	 * @param toSbuId the toSbuId to set
	 */
	public void setToSbuId(Integer toSbuId) {
		this.toSbuId = toSbuId;
	}
	/**
	 * @return the giveoutCourseId
	 */
	public Integer getGiveoutCourseId() {
		return giveoutCourseId;
	}
	/**
	 * @param giveoutCourseId the giveoutCourseId to set
	 */
	public void setGiveoutCourseId(Integer giveoutCourseId) {
		this.giveoutCourseId = giveoutCourseId;
	}
	/**
	 * @return the takeinCourseId
	 */
	public Integer getTakeinCourseId() {
		return takeinCourseId;
	}
	/**
	 * @param takeinCourseId the takeinCourseId to set
	 */
	public void setTakeinCourseId(Integer takeinCourseId) {
		this.takeinCourseId = takeinCourseId;
	}
	/**
	 * @return the giveoutDuration
	 */
	public Float getGiveoutDuration() {
		return giveoutDuration;
	}
	/**
	 * @param giveoutDuration the giveoutDuration to set
	 */
	public void setGiveoutDuration(Float giveoutDuration) {
		this.giveoutDuration = giveoutDuration;
	}
	/**
	 * @return the takeinDuration
	 */
	public Float getTakeinDuration() {
		return takeinDuration;
	}
	/**
	 * @param takeinDuration the takeinDuration to set
	 */
	public void setTakeinDuration(Float takeinDuration) {
		this.takeinDuration = takeinDuration;
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
	 * @return the actionBy
	 */
	public String getActionBy() {
		return actionBy;
	}
	/**
	 * @param actionBy the actionBy to set
	 */
	public void setActionBy(String actionBy) {
		this.actionBy = actionBy;
	}
	/**
	 * @return the actionTime
	 */
	public Date getActionTime() {
		return actionTime;
	}
	/**
	 * @param actionTime the actionTime to set
	 */
	public void setActionTime(Date actionTime) {
		this.actionTime = actionTime;
	}
    
    
    
	
	
    
    

}
