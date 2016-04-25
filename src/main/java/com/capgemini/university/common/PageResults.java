package com.capgemini.university.common;

import java.io.Serializable;
import java.util.List;


public class PageResults<T> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Pagination page;
	
	private List<T> result;
	
	public PageResults(Pagination page, List<T> result) {
		super();
		this.page = page;
		this.result = result;
	}
	
	public List<T> getResult() {
		return result;
	}

	public void setResult(List<T> result) {
		this.result = result;
	}

	public Pagination getPage() {
		return page;
	}

	public void setPage(Pagination page) {
		this.page = page;
	}

}
