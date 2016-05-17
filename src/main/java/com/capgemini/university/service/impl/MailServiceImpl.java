package com.capgemini.university.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.capgemini.university.api.exception.DataAccessDeniedException;
import com.capgemini.university.model.Course;
import com.capgemini.university.model.CourseMail;
import com.capgemini.university.model.Lbps;
import com.capgemini.university.model.Sbu;
import com.capgemini.university.service.ICourseService;
import com.capgemini.university.service.IMailService;
import com.capgemini.university.service.ISbuService;
import com.capgemini.university.util.ConfigUtil;


@Service(value = "mailService")
public class MailServiceImpl implements IMailService {

	private final Log logger = LogFactory.getLog(getClass());
 
	private JavaMailSender mailSender;
    private String from;
    private String mailTemplateOf3week;
    private String mailTemplateOf2week;
    private String mailTemplateOfSwap;
    private VelocityEngine velocityEngine;
    @Autowired
    private ICourseService courseService;
    @Autowired
    private ISbuService sbuService;
    
  
    
    /**
     * @empId - employee id
     * @cerFile - certification doc 
     * @contextUrl  host url
     */
    @Override
	public void notifyLbpsMail() {
	
    	String simulatorTag = ConfigUtil.getPropertyValue("mail.simulator");
    	String subject3week = ConfigUtil.getPropertyValue("mail.3week.subject");
    	String subject2week = ConfigUtil.getPropertyValue("mail.2week.subject");
    	
    	
		boolean simulator = simulatorTag != null && simulatorTag != "" && simulatorTag.equals("true")?true:false;
		
    	List<CourseMail> list = courseService.getAllCourseBeforeStarting(3);//3 weeks 
    	if(list != null && list.size()>0){
    	
    		
    		for(CourseMail cm : list){
    			List<Map> courseList = cm.getCourseList();
    			int totalSeats = 0;
    			int totalPmds = 0;
    			for(Map map : courseList){
    				int remainSeats = Integer.parseInt(map.get("remainning_seats").toString());
    				int remainPmds = Integer.parseInt(map.get("remainning_pmds").toString());
    				totalSeats += remainSeats;
    				totalPmds += remainPmds;
    			}
    			cm.setTotalRemainSeats(totalSeats);
    			cm.setTotalRemainPmds(totalPmds);
    			
    			sendMail(simulator, cm, mailTemplateOf3week, subject3week);
    			break;//for test;
    		}
    	}
    	
    	
    	List<CourseMail> list2 = courseService.getAllCourseBeforeStarting(2);//2 weeks 
    	if(list2 != null && list2.size()>0){
    		for(CourseMail cm : list2){
    			sendMail(simulator, cm, mailTemplateOf2week, subject2week);
    		}
    	}
    	
    	//call sp to convert to free seats
    	sbuService.convertToFreeSeats();
       
	}
    
    public void sendSwapMail(Integer mySbuId , Integer giveoutCourseId, Integer swapSbuId,
	         Integer swapCourseId, Integer swapSeats)throws DataAccessDeniedException{
    	try{
    		String simulatorTag = ConfigUtil.getPropertyValue("mail.simulator");
        	String subject3Swap = ConfigUtil.getPropertyValue("mail.swap.subject");
        	
    		boolean simulator = simulatorTag != null && simulatorTag != "" && simulatorTag.equals("true")?true:false;
    		
    		Sbu sbu = sbuService.getSbuById(swapSbuId);
    		Sbu mySbu = sbuService.getSbuById(mySbuId);
    		Lbps lbps = sbu.getLbps();
    		String toName = lbps.getName();
    		String email = lbps.getEmail();
    		
    		Course cour = courseService.getCourseById(swapCourseId);
    		String courseName = cour.getName();
    		float duration = cour.getDuration();
    		
    		CourseMail cm = new CourseMail();
    		cm.setName(toName);
    		cm.setEmail(email);
    		cm.setCourseName(courseName);
    		cm.setSeats(swapSeats);
    		cm.setPmds((int) Math.ceil(swapSeats * duration));
    		cm.setToSbuName(mySbu.getSubName());
    		
    		//send email
    		sendSwapMail(simulator, cm, mailTemplateOfSwap, subject3Swap);
    	}catch(Exception e){
    		logger.error("sendSwapMail error:",e);
    		throw e;
    	}
    	
    }
    
    private void sendSwapMail(boolean simulator, CourseMail cm,  String mailTemplate, String subject){
    	MimeMessagePreparator preparator = new MimeMessagePreparator()
        {
            public void prepare(MimeMessage mimeMessage) throws Exception
            {
            	MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true);
             
            	String email = cm.getEmail();
                
                message.setSubject(subject);
                if(simulator){
                	message.setTo(from);
                }else{
                	message.setTo(email);
                }
                
                message.setFrom(from);
               
                Map<String, Object> model = new HashMap<String, Object>();
                model.put("cm", cm);
               
                
//                model.put("content", getMailContent(cerFile, userFirstName, contextUrl));
                
                String encoding = "UTF-8";
                
                String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, mailTemplate,
            			encoding, model);
                
                message.setText(text,true);
            }
        };
        
        this.mailSender.send(preparator);
    }
    
    
    private void sendMail(boolean simulator, CourseMail cm, String mailTemplate, String subject){
    	MimeMessagePreparator preparator = new MimeMessagePreparator()
        {
            public void prepare(MimeMessage mimeMessage) throws Exception
            {
            	MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true);
             
            	String email = cm.getEmail();
                
                message.setSubject(subject);
                if(simulator){
                	message.setTo(from);
                }else{
                	message.setTo(email);
                }
                
                message.setFrom(from);
               
                Map<String, Object> model = new HashMap<String, Object>();
                model.put("courseList", cm.getCourseList());
                model.put("toName", cm.getName());
                model.put("totalRemainSeats", cm.getTotalRemainSeats());
                model.put("totalRemainPmds", cm.getTotalRemainPmds());
                
//                model.put("content", getMailContent(cerFile, userFirstName, contextUrl));
                
                String encoding = "UTF-8";
                
                String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, mailTemplate,
            			encoding, model);
                
                message.setText(text,true);
            }
        };
        
        this.mailSender.send(preparator);
    }
	
	
//	private String getMailContent(String cerFile, String userName, String contextUrl){
//		
//		String docLink = "";
//
//      	docLink = docLink + " <a href='"+contextUrl + Constants.CERTIFICATION_IMAGE + "/" + cerFile +"'>"+ cerFile +"</a> ";
//        
//        String content = userName + " has submited a certificate request. Please click " + docLink +" to get the document";
//		
//        return content;
//	}
	
    public JavaMailSender getMailSender()
    {
        return mailSender;
    }

    public void setMailSender(JavaMailSender mailSender)
    {
        this.mailSender = mailSender;
    }

    public String getFrom()
    {
        return from;
    }

    public void setFrom(String from)
    {
        this.from = from;
    }

    public VelocityEngine getVelocityEngine()
    {
        return velocityEngine;
    }

    public void setVelocityEngine(VelocityEngine velocityEngine)
    {
        this.velocityEngine = velocityEngine;
    }

	/**
	 * @return the mailTemplateOf3week
	 */
	public String getMailTemplateOf3week() {
		return mailTemplateOf3week;
	}

	/**
	 * @param mailTemplateOf3week the mailTemplateOf3week to set
	 */
	public void setMailTemplateOf3week(String mailTemplateOf3week) {
		this.mailTemplateOf3week = mailTemplateOf3week;
	}

	/**
	 * @return the mailTemplateOf2week
	 */
	public String getMailTemplateOf2week() {
		return mailTemplateOf2week;
	}

	/**
	 * @param mailTemplateOf2week the mailTemplateOf2week to set
	 */
	public void setMailTemplateOf2week(String mailTemplateOf2week) {
		this.mailTemplateOf2week = mailTemplateOf2week;
	}

	/**
	 * @return the mailTemplateOfSwap
	 */
	public String getMailTemplateOfSwap() {
		return mailTemplateOfSwap;
	}

	/**
	 * @param mailTemplateOfSwap the mailTemplateOfSwap to set
	 */
	public void setMailTemplateOfSwap(String mailTemplateOfSwap) {
		this.mailTemplateOfSwap = mailTemplateOfSwap;
	}

   

}
