package com.capgemini.university.common;

import java.util.List;

import com.capgemini.university.model.Participant;
import com.capgemini.university.model.SbuCourse;



public class JsonParamObj {
	
	private Integer id;
	
	private Integer sbuId;
	
	private Integer lbpsId;
	
	private Integer parentSbuId;
	
	private Integer courseId;
	
	private String sbuName;
	
	private String sbuLbpsId;
	
	private String loginName;
	
	private String displayName;
	
	private String email;
	
	private String courseName;
	
	private String eventName;
	
	private String type;
	
	private String name;
	
	private String logo;
	
	private String url;
	
	private String startTime;
	private String endTime;
	
	private Float duration;
	
	
	private Integer mySbuId;
	private Integer giveoutCourseId;
	private Integer swapSbuId;
	private Integer swapCourseId;
	private Integer swapSeats;
	private Integer seats;
	
	private Integer currentPage;
	private Integer pageSize;
	
	private List<Participant> participantList;
	
	private List<SbuCourse> sbuList;
	
	
	
	
	

	

	/**
	 * @return the sbuLbpsId
	 */
	public String getSbuLbpsId() {
		return sbuLbpsId;
	}

	/**
	 * @param sbuLbpsId the sbuLbpsId to set
	 */
	public void setSbuLbpsId(String sbuLbpsId) {
		this.sbuLbpsId = sbuLbpsId;
	}

	/**
	 * @return the lbpsId
	 */
	public Integer getLbpsId() {
		return lbpsId;
	}

	/**
	 * @param lbpsId the lbpsId to set
	 */
	public void setLbpsId(Integer lbpsId) {
		this.lbpsId = lbpsId;
	}

	/**
	 * @return the logo
	 */
	public String getLogo() {
		return logo;
	}

	/**
	 * @param logo the logo to set
	 */
	public void setLogo(String logo) {
		this.logo = logo;
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
	 * @return the parentSbuId
	 */
	public Integer getParentSbuId() {
		return parentSbuId;
	}

	/**
	 * @param parentSbuId the parentSbuId to set
	 */
	public void setParentSbuId(Integer parentSbuId) {
		this.parentSbuId = parentSbuId;
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
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the startTime
	 */
	public String getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the endTime
	 */
	public String getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

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
	 * @return the sbuList
	 */
	public List<SbuCourse> getSbuList() {
		return sbuList;
	}

	/**
	 * @param sbuList the sbuList to set
	 */
	public void setSbuList(List<SbuCourse> sbuList) {
		this.sbuList = sbuList;
	}

	/**
	 * @return the eventName
	 */
	public String getEventName() {
		return eventName;
	}

	/**
	 * @param eventName the eventName to set
	 */
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the participantList
	 */
	public List<Participant> getParticipantList() {
		return participantList;
	}

	/**
	 * @param participantList the participantList to set
	 */
	public void setParticipantList(List<Participant> participantList) {
		this.participantList = participantList;
	}

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
	 * @return the currentPage
	 */
	public Integer getCurrentPage() {
		return currentPage;
	}

	/**
	 * @param currentPage the currentPage to set
	 */
	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	/**
	 * @return the pageSize
	 */
	public Integer getPageSize() {
		return pageSize;
	}

	/**
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	

	/**
	 * @return the mySbuId
	 */
	public Integer getMySbuId() {
		return mySbuId;
	}

	/**
	 * @param mySbuId the mySbuId to set
	 */
	public void setMySbuId(Integer mySbuId) {
		this.mySbuId = mySbuId;
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
	 * @return the swapSbuId
	 */
	public Integer getSwapSbuId() {
		return swapSbuId;
	}

	/**
	 * @param swapSbuId the swapSbuId to set
	 */
	public void setSwapSbuId(Integer swapSbuId) {
		this.swapSbuId = swapSbuId;
	}

	/**
	 * @return the swapCourseId
	 */
	public Integer getSwapCourseId() {
		return swapCourseId;
	}

	/**
	 * @param swapCourseId the swapCourseId to set
	 */
	public void setSwapCourseId(Integer swapCourseId) {
		this.swapCourseId = swapCourseId;
	}

	/**
	 * @return the swapSeats
	 */
	public Integer getSwapSeats() {
		return swapSeats;
	}

	/**
	 * @param swapSeats the swapSeats to set
	 */
	public void setSwapSeats(Integer swapSeats) {
		this.swapSeats = swapSeats;
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
	 * @return the loginName
	 */
	public String getLoginName() {
		return loginName;
	}

	/**
	 * @param loginName the loginName to set
	 */
	public void setLoginName(String loginName) {
		this.loginName = loginName;
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
	
	



	
}
