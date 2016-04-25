package com.capgemini.university.service.impl;


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
		Sbu sbu = sbuService.getSbuByMail(mail);
		Assert.assertNotNull(sbu);
		String name = sbu.getSubName();
		Assert.assertEquals("Assert value", name, "Apps1");
	}
	
	

	
}
