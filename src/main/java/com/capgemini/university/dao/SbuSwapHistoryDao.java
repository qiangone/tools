package com.capgemini.university.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.capgemini.university.model.SbuSwapHistory;




@Repository
public interface SbuSwapHistoryDao {
	public int addSbuSwapHistory(SbuSwapHistory ssh);
	
//	public int addOrderAddress(OrderAddress addr);
//	
//	public int updateAddress(Address addr);
//	
	public List<Map> getSbuSwapHistory(Map map);
//	
//	public List<SbuCourse> getCourseBySbu(Map map);
//	
//	public List<Map> countPmdsBySbu(Map map);
//	
//	public List<Area> getAreaList(Map map);
//	
//	
//	public List<ExpressCompany> getExpressCompList();
//	
//	
//	public ExpressCompany getExpressCompByName(String name);
//	
}
