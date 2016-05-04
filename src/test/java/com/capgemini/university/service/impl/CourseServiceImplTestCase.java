package com.capgemini.university.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.capgemini.university.common.PageResults;
import com.capgemini.university.common.Pagination;
import com.capgemini.university.model.Course;
import com.capgemini.university.model.Participant;
import com.capgemini.university.response.ResponseUtil;
import com.capgemini.university.response.WDResponse;
import com.capgemini.university.service.ICourseService;
import com.capgemini.university.util.DateUtil;
import com.capgemini.university.util.JsonUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:spring/spring-application-context.xml",
		"classpath:spring/spring-application-security.xml" })
public class CourseServiceImplTestCase {

	@Autowired
	private ICourseService courseService;

	@Test
	public void testQueryParticipantByName() throws Exception{

		List<Participant> list = courseService
				.queryParticipantByName("Ye, Calvin");
		if (list != null && list.size() > 0) {
			Participant p = list.get(0);
			System.out.print(p);
			
			byte[] logoByte = p.getThumbnailPhoto();
			String logoImg = new String(Base64.encodeBase64(logoByte));  
			System.out.print(logoImg);
			if(logoImg != null){
				String URL="D:/ak26.bmp";//创建本地图片URL
//
//				File file=new File(URL);
//
//				FileOutputStream fos=new FileOutputStream(file);
//
//				fos.write(logoImg,0,logoImg.length);
//
//				fos.flush();
//
//				fos.close();
			}
		}

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetCourseListByPage() {
		@SuppressWarnings("rawtypes")
		Map map = new HashMap();
		map.put("id", "1");
		map.put("type", "2");
		Pagination page = new Pagination();
		page.setCurrentPage(2);
		page.setPageSize(20);

		PageResults<Map> ret = courseService.getCourseListByPage(map, page);
		@SuppressWarnings("unused")
		WDResponse oa = ResponseUtil.apiSuccess(ret, "query participant successful");
		System.out.println(JsonUtil.toJson(oa));
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetAdminCourseListByPage() {
		@SuppressWarnings("rawtypes")
		Map map = new HashMap();
		map.put("eventName", "BPW3");
//		map.put("id", "23");
		Pagination page = new Pagination();
		page.setCurrentPage(1);
		page.setPageSize(20);

		PageResults<Course> ret = courseService.getAdminCourseListByPage(map, page);
		@SuppressWarnings("unused")
		WDResponse oa = ResponseUtil.apiSuccess(ret, "query participant successful");
		System.out.println(JsonUtil.toJson(oa));
		
	}
	
	@Test
	public void testGetCourseInfo(){
		Course c = courseService.getAdminCourse(22);
		WDResponse oa = ResponseUtil.apiSuccess(c, "query participant successful");
		System.out.println(JsonUtil.toJson(oa));
	}
	
	@Test
	public void testUpdateCourse() throws Exception{
		Course c = courseService.getAdminCourse(22);
//		c.setName("testddddd");
		c.setStartTime(DateUtil.formatDate("03/16/2016"));
		c.setEndTime(DateUtil.formatDate("03/17/2016"));
		c.setDuration(2f);
//		c.setSbuList(null);
		courseService.updateCourse(c);
		WDResponse oa = ResponseUtil.apiSuccess(c, "query participant successful");
		System.out.println(JsonUtil.toJson(oa));
	}
	
	
	
	
	@Test
	public void testGetAllEvent(){
		List<Map> list = courseService.getEventList("1");
		WDResponse oa = ResponseUtil.apiSuccess(list, "query participant successful");
		System.out.println(JsonUtil.toJson(oa));
	}
	
	@Test
	public void testGetCourseListByEvent(){
		String eventName="Community (architects/EM)";
		List<Course> list = courseService.getCourseListByEvent(eventName);
		WDResponse oa = ResponseUtil.apiSuccess(list, "query participant successful");
		System.out.println(JsonUtil.toJson(oa));
	}
	
	@Test
	public void testUploadCourse(){
		
		try{
			InputStream in = new FileInputStream("D:\\Project\\University Tool\\courseTemplate_data.xlsx");
			courseService.uploadCourse(in, true);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}
	
	@Test
	public void testSwap(){
		
		courseService.swap(1, 3, 2, 2, 3);
	}
	
	
	
	


}
