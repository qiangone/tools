/**
 * CAPGEMINI APPLIANCE CHAINS.
 * Copyright (c) 2015-2015 All Rights Reserved.
 */
package com.capgemini.university.service.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capgemini.university.api.exception.DataAccessDeniedException;
import com.capgemini.university.dao.SbuDao;
import com.capgemini.university.model.Lbps;
import com.capgemini.university.model.Participant;
import com.capgemini.university.model.Sbu;
import com.capgemini.university.model.SbuCourse;
import com.capgemini.university.model.SbuLbps;
import com.capgemini.university.service.ICourseService;
import com.capgemini.university.service.ISbuService;

/**
 * 
 * functional description： seller interface
 * 
 * @author zhihuang@capgemini.com
 * @created Dec 18, 2015 10:37:13 AM
 * @date Dec 18, 2015 10:37:13 AM
 */

@Service(value = "sbuService")
public class SbuServiceImpl implements ISbuService {

	private final static Log logger = LogFactory
			.getLog(SbuServiceImpl.class);

	@Autowired
	private SbuDao sbuDao;
	@Autowired
	private ICourseService courseService;

	public Sbu getSbuById(int sbuId){
		Map map = new HashMap();
		map.put("id", sbuId);
		List<Sbu> list = sbuDao.getSbu(map);
		if(list != null && list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	private Sbu getSbuByName(String sbuName){
		Map map = new HashMap();
		map.put("sbuName", sbuName);
		List<Sbu> list = sbuDao.getSbu(map);
		if(list != null && list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	public Sbu getParentSbuById(int parentId){
		Map map = new HashMap();
		map.put("parentId", parentId);
		List<Sbu> list = sbuDao.getSbu(map);
		if(list != null && list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	public List<Sbu> getAllParentSbuList(int sbuId){
		List<Sbu> list = new ArrayList<Sbu>();
		Sbu self = getSbuById(sbuId);
		list.add(self);
		if(self != null){
			while(true){
				int parentId = self.getParentId();
				if(parentId == 0){
					break;
				}
				self = getSbuById(parentId);
				list.add(self);
			}
		}
		return list;
	}
	
	public List<Sbu> getSbuByMail(String mail){
		Map map = new HashMap();
		map.put("email", mail);
		List<Sbu> list = sbuDao.getSbu(map);
		if(list != null && list.size()>0){
			for(Sbu sbu : list){
				if (sbu != null) {
					int parentId = sbu.getParentId();
					if (parentId != 0) {
						Sbu par = getSbuById(parentId);
						sbu.setParentSbu(par);
					}
				}
			}
		}
		
		return list;
	}
	
	
	public List<Sbu> getSbuByLbps(String lbps){
		Map map = new HashMap();
		map.put("lbps", lbps);
		return sbuDao.getSbu(map);
		
	}
	
//	public List<SbuCourse> getCourseBySbu(int sbuId){
//		Map map = new HashMap();
//		map.put("id", sbuId);
//		return sbuDao.getCourseBySbu(map);
//	}
	
//	public List<Map> countPmdsBySbu(int parentId){
//		Map map = new HashMap();
//		map.put("id", parentId);
//		return sbuDao.countPmdsBySbu(map);
//	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map countSeats(int sbuId, int courseId){
		Map ret = new HashMap();
		
		Map map = new HashMap();
		map.put("courseId", courseId);
		map.put("sbuId", sbuId);
		List<Map> list1 = sbuDao.countSwapSeatsOfOtherSbu(map);
		ret.put("otherSbuSwapList", list1);
		
		//map = new HashMap();
		//map.put("sbuId", sbuId);
		List<Map> list2 = sbuDao.countSeatsOfSubSbu(map);
		if(list2 != null && list2.size()>0){
			for(Map map2 : list2){
				Integer sbuId2 = Integer.parseInt(map2.get("id").toString());
				Integer courseId2 = Integer.parseInt(map2.get("courseId").toString());
				List<Participant> parList = courseService.queryParticipantList(sbuId2, courseId2);
				map2.put("participantList", parList);
			}
		}
		
		ret.put("subSbuList", list2);
		
		SbuCourse sc = courseService.getSbuCourse(sbuId, courseId);
		ret.put("subCourse", sc);
		ret.put("forSwap", sc.getSwapSeats());
		
		return ret;
		
	}
	
	public List<Sbu> getSbuListByParentIdAndParticipant(int parentId, int courseId){
		Map map = new HashMap();
		map.put("parentId", parentId);
		List<Sbu> list = sbuDao.getSbu(map);
		if(list != null && list.size()>0){
			for(Sbu sbu : list){
				List<Participant> parList = courseService.queryParticipantList(sbu.getId(), courseId);
				sbu.setParticipantList(parList);
			}
		}
		return list;
	}
	
	public List<Sbu> getSbuListByParentId(int parentId){
		Map map = new HashMap();
		map.put("parentId", parentId);
		List<Sbu> list = sbuDao.getSbu(map);
		return list;
	}
	
	
	public void updateSbu(Sbu sbu){
		sbuDao.updateSbu(sbu);
	}
	
	public void addSbu(Sbu sbu){
		Sbu tmpSbu = getSbuByName(sbu.getSubName());
		if(tmpSbu != null){
			throw new DataAccessDeniedException("Sbu Name exists!");
		}
		sbuDao.addSbu(sbu);
	}
	
	public void deleteSbu(int sbuId){
		
		Sbu sbu = new Sbu();
		sbu.setId(sbuId);
		sbu.setTag(1);
		sbuDao.updateSbu(sbu);
		
	}
	
	
	public void addSbuLbps(int sbuId, String displayName, String email){
		
		Lbps lbps = getLbpsByEmail(email);
		if(lbps == null){
			lbps = new Lbps();
			lbps.setName(displayName);
			lbps.setEmail(email);
			sbuDao.addLbps(lbps);
		}
		
		SbuLbps sb = new SbuLbps();
		sb.setLbpsId(lbps.getId());
		sb.setSbuId(sbuId);
		sbuDao.addSbuLbps(sb);
	}
	
	public SbuLbps getSbuLbpsById(int id){
		Map map = new HashMap();
		map.put("id",  id);
		
		List<SbuLbps> list = sbuDao.getSbuLbps(map);
		if(list != null && list.size()>0){
			return list.get(0);
		}
		return  null;
	}
	
	public SbuLbps getSbuLbps(int sbuId, int lbpsId){
		Map map = new HashMap();
		map.put("sbuId",  sbuId);
		map.put("lbpsId",  lbpsId);
		
		List<SbuLbps> list = sbuDao.getSbuLbps(map);
		if(list != null && list.size()>0){
			return list.get(0);
		}
		return  null;
	}
	
	public void relateSbuLbps(int sbuId, String sbuLbpsId, String displayName, String email){
		Lbps lbps = getLbpsByEmail(email);
		if(lbps == null){
			lbps = new Lbps();
			lbps.setName(displayName);
			lbps.setEmail(email);
			sbuDao.addLbps(lbps);
		}
		
		if(StringUtils.isNotEmpty(sbuLbpsId)){
			SbuLbps sl = getSbuLbpsById(Integer.parseInt(sbuLbpsId));
			sl.setLbpsId(lbps.getId());
			sbuDao.updateSbuLbps(sl);
		}else{
			SbuLbps sb = new SbuLbps();
			sb.setLbpsId(lbps.getId());
			sb.setSbuId(sbuId);
			sbuDao.addSbuLbps(sb);
		}
		
		
		
	}
	
	public void updateLbps(Lbps lbps){
		sbuDao.updateLbps(lbps);
	}
	
	
	private void setCellValue(HSSFRow row, Object[] values, HSSFCellStyle style){
		if(row==null || values==null||values.length==0){
			return;
		}
        for(int i=0;i<values.length;i++){
        	HSSFCell cell = row.createCell(i); 
        	cell.setCellStyle(style);  
        	String textValue = "";
        	if (null != values[i]) {
	        	if (values[i] instanceof Date)  
	            {  
	                Date date = (Date) values[i];  
	                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
	                textValue = sdf.format(date);  
	            }else{
	            	textValue = values[i].toString();
	            }
	        }                             
        	cell.setCellValue(textValue);
        }
	        
	}
	
	public void exportNomination(List<Map> list, HttpServletResponse response) {
		String[] titles= new String[]{"SBU NAME","COURSE NAME","START_TIME","END_TIME","EMAIL","DISPLAY NAME"};
		
		int index = 0;
		HSSFWorkbook wb = new HSSFWorkbook();  
        HSSFSheet sheet = wb.createSheet("Nomination List"); 
//        sheet.setColumnWidth(0, 10*256);
        for(int i=1;i<8;i++){
        	sheet.setColumnWidth(i, 50*256);
        }
        HSSFFont headFont = wb.createFont();  
        headFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); 
        HSSFCellStyle headStyle = wb.createCellStyle();  
        headStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); 
        headStyle.setFont(headFont);
        HSSFRow headRow = sheet.createRow(index++); 
        setCellValue(headRow,titles,headStyle);
        

        HSSFCellStyle contentStyle = wb.createCellStyle();  
        contentStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT); 
        
        for(int i=0;i<list.size();i++){
        	Map map = list.get(i);
        	Object[] values = new Object[6];
        	values[0] = map.get("sbu_name");
        	values[1] = map.get("course_name");
        	values[2] = map.get("start_time");
        	values[3] = map.get("end_time");
        	values[4] = map.get("email");
        	values[5] = map.get("display_name");
        	HSSFRow contentRow = sheet.createRow(index++); 
        	setCellValue(contentRow,values,contentStyle);
        }
       
        OutputStream os=null;
        try {
            response.reset();
            response.addHeader("Content-Disposition", "attachment;filename=Nomination.xls");
            response.setContentType("application/vnd.ms-excel;charset=utf-8"); 
            os=response.getOutputStream();
            wb.write(os);
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(os!=null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
		
	}
	
	public List<Map> countParticipantsOfSbu(int sbuId, String eventName){
		Map map = new HashMap();
		if(sbuId != 0){
			map.put("sbuId", sbuId);
		}
		if(StringUtils.isNotEmpty(eventName)){
			map.put("eventName", eventName);
		}
		
		return sbuDao.countParticipantsOfSbu(map);
		
	}
	
	public Lbps getLbpsByEmail(String email){
		Map map = new HashMap();
		map.put("email", email);
		List<Lbps> list = sbuDao.getLbps(map);
		if(list != null && list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	public List<Sbu> countParticipantsByCourse(int courseId){
		Map map = new HashMap();
		map.put("courseId", courseId);
		return sbuDao.countParticipantsByCourse(map);
	}
	
	public void convertToFreeSeats(){
		sbuDao.convertToFreeSeats();
	}
	
	
	public Map countParticipantAndPmds(int sbuId){
		Map ret = new HashMap();
		List<Map> list2 = new ArrayList<Map>();
		List<Sbu> list = getSbuListByParentId(sbuId);
		int total_used_seats =0;
		int total_used_pmds =0;
		int total_avaliable_seats =0;
		int total_avaliable_pmds =0;
		int total_lost_seats =0;
		int total_lost_pmds =0;
		
		if(list != null && list.size()>0){
			
			for(Sbu sbu : list){
				Map map = new HashMap();
				map.put("sbuId", sbu.getId());
				Map map3 = sbuDao.countParticipantAndPmds(map);
				if(map3 != null){
					
					int used_seats = (int)map3.get("used_seats");
					int used_pmds = (int)map3.get("used_pmds");
					int avaliable_seats = (int)map3.get("avaliable_seats");
					int avaliable_pmds = (int)map3.get("avaliable_pmds");
					int lost_seats = (int)map3.get("lost_seats");
					int lost_pmds = (int)map3.get("lost_pmds");
					
					total_used_seats += used_seats;
					total_used_pmds += used_pmds;
					total_avaliable_seats += avaliable_seats;
					total_avaliable_pmds += avaliable_pmds;
					total_lost_seats += lost_seats;
					total_lost_pmds += lost_pmds;
						
					
				}
				
				Map map2 = new HashMap();
				map2.put("sbuName", sbu.getSubName());
				map2.put("detailMap", map3);
				list2.add(map2);
			}
		}
		
		ret.put("sbuList", list2);
		ret.put("total_used_seats", total_used_seats);
		ret.put("total_used_pmds", total_used_pmds);
		ret.put("total_avaliable_seats", total_avaliable_seats);
		ret.put("total_avaliable_pmds", total_avaliable_pmds);
		ret.put("total_lost_seats", total_lost_seats);
		ret.put("total_used_seats", total_used_seats);
		ret.put("total_lost_pmds", total_lost_pmds);
		return ret;
		
		
	}


}
