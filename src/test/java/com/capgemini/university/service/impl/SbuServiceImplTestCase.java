package com.capgemini.university.service.impl;


import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.capgemini.university.model.Sbu;
import com.capgemini.university.service.ISbuService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/spring-application-context.xml","classpath:spring/spring-application-security.xml" })
public class SbuServiceImplTestCase {

	@Autowired
	private ISbuService sbuService;
	
	
	

	
	@Test
	public void testGetSbuByMail() {

		String mail = "zhiqiang.huang@capgemini.com";
		List<Sbu> sbu = sbuService.getSbuByMail(mail);
		Assert.assertNotNull(sbu);
//		String name = sbu.getSubName();
//		Assert.assertEquals("Assert value", name, "Apps1");
	}
	
	@Test
	public void testGetSbuListByParentId() {

		
		List<Sbu> sbu = sbuService.getSbuListByParentId(0);
		Assert.assertNotNull(sbu);
//		String name = sbu.getSubName();
//		Assert.assertEquals("Assert value", name, "Apps1");
	}
	@Test
	public void testDeleteSbu(){
		sbuService.deleteSbu(40);
	}
	
	@Test
	public void testAddSbu(){
		Sbu sbu = new Sbu();
		sbu.setSubName("Qiang");
		sbu.setParentId(0);
		sbuService.addSbu(sbu);
	}
	
	@Test
	public void testAddSbuLbps(){
		String name="Huang, Zhiqiang222";
		String email="zhiqiang.222huang@capgemini.com";
		String logo = "image/logo/kay.jpg";
		int sbuId=75;
		sbuService.addSbuLbps(sbuId, name, email);
	}
	
	@Test
	public void testCountParticipantsByCourse(){
		List<Sbu> list = sbuService.countParticipantsByCourse(2);
		list.size();
	}
	
	@Test
	public void testCountParticipantAndPmds(){
		Map map = sbuService.countParticipantAndPmds(1);
		System.out.print("");
	}
	
	
	
}
