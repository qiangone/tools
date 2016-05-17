package com.capgemini.university.service.impl;


import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.capgemini.university.model.Participant;
import com.capgemini.university.service.IMailService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/spring-application-context.xml","classpath:spring/spring-application-security.xml" })
public class MailServiceImplTestCase {

	@Autowired
	private IMailService mailService;
	
	
	
	@Test
	public void testNotifyLbpsMail() {

		mailService.notifyLbpsMail();
	}
	@Test
	public void testSendSwapMail(){
		mailService.sendSwapMail(1, 3, 2, 61, 3);
	}
	
	

	
}
