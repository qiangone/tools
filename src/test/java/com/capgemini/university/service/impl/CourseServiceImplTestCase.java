package com.capgemini.university.service.impl;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.capgemini.university.util.JsonUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:spring/spring-application-context.xml",
		"classpath:spring/spring-application-security.xml" })
public class CourseServiceImplTestCase {

	@Autowired
	private ICourseService courseService;

	@Test
	public void testQueryParticipantByName() {

		List<Participant> list = courseService
				.queryParticipantByName("zhihuang");
		if (list != null && list.size() > 0) {
			Participant p = list.get(0);
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
	@Test
	public void testGetAllEvent(){
		List<Course> list = courseService.getAllEvent(2);
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
			InputStream in = new FileInputStream("D:\\Project\\University Tool\\courseTemplate.xlsx");
			courseService.uploadCourse(in, true);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}
	
	


}
