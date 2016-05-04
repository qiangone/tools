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

public class Sbu {
    
    private Integer id;
    
    
    private String subName;
    
    private Integer parentId;
    
    private Sbu parentSbu;
    
    private Lbps lbps;
    
    private Integer tag;
    
    
    
    

	/**
	 * @return the tag
	 */
	public Integer getTag() {
		return tag;
	}

	/**
	 * @param tag the tag to set
	 */
	public void setTag(Integer tag) {
		this.tag = tag;
	}

	/**
	 * @return the lbps
	 */
	public Lbps getLbps() {
		return lbps;
	}

	/**
	 * @param lbps the lbps to set
	 */
	public void setLbps(Lbps lbps) {
		this.lbps = lbps;
	}

	/**
	 * @return the parentSbu
	 */
	public Sbu getParentSbu() {
		return parentSbu;
	}

	/**
	 * @param parentSbu the parentSbu to set
	 */
	public void setParentSbu(Sbu parentSbu) {
		this.parentSbu = parentSbu;
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
	 * @return the subName
	 */
	public String getSubName() {
		return subName;
	}

	/**
	 * @param subName the subName to set
	 */
	public void setSubName(String subName) {
		this.subName = subName;
	}

	/**
	 * @return the parentId
	 */
	public Integer getParentId() {
		return parentId;
	}

	/**
	 * @param parentId the parentId to set
	 */
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
    
    
    
	
    
    

}
