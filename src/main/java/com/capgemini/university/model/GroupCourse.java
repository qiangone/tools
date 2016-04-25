package com.capgemini.university.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

public class GroupCourse {
	
	private Object eventName;
	private Object eventLogo;
	private String startTime;
	private String endTime;
	
	private List<Map> results;

	
	
	
	/**
	 * @return the eventName
	 */
	
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
	 * @return the eventName
	 */
	public Object getEventName() {
		return eventName;
	}


	/**
	 * @param eventName the eventName to set
	 */
	public void setEventName(Object eventName) {
		this.eventName = eventName;
	}


	/**
	 * @return the eventLogo
	 */
	public Object getEventLogo() {
		if(StringUtils.isEmpty(eventLogo)){
			return "";
		}
		return eventLogo;
	}


	/**
	 * @param eventLogo the eventLogo to set
	 */
	public void setEventLogo(Object eventLogo) {
		this.eventLogo = eventLogo;
	}



	





	/**
	 * @return the results
	 */
	public List<Map> getResults() {
		return results;
	}

	/**
	 * @param results the results to set
	 */
	public void setResults(List<Map> results) {
		this.results = results;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		GroupCourse gc = (GroupCourse)obj;
		return eventName.equals(gc.getEventName());
	}
	
	

}
