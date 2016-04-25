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

import com.capgemini.university.model.CourseMail;
import com.capgemini.university.service.ICourseService;
import com.capgemini.university.service.IMailService;
import com.capgemini.university.util.ConfigUtil;


@Service(value = "mailService")
public class MailServiceImpl implements IMailService {

	private final Log logger = LogFactory.getLog(getClass());
 
	private JavaMailSender mailSender;
    private String from;
    private String mailTemplate;
    private VelocityEngine velocityEngine;
    @Autowired
    private ICourseService courseService;
    
  
    
    /**
     * @empId - employee id
     * @cerFile - certification doc 
     * @contextUrl  host url
     */
    @Override
	public void notifyLbpsMail() {
	
    	List<CourseMail> list = courseService.getAllCourseToBegin();
    	if(list != null && list.size()>0){
    		String simulatorTag = ConfigUtil.getPropertyValue("mail.simulator");
    		boolean simulator = simulatorTag != null && simulatorTag != "" && simulatorTag.equals("true")?true:false;
    		for(CourseMail cm : list){
    			sendMail(simulator, cm);
    		}
    	}
		
		
		
        
       
	}
    
    private void sendMail(boolean simulator, CourseMail cm){
    	MimeMessagePreparator preparator = new MimeMessagePreparator()
        {
            public void prepare(MimeMessage mimeMessage) throws Exception
            {
            	MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true);
             
            	String email = cm.getEmail();
                
                message.setSubject("you have below seats not signed");
                if(simulator){
                	message.setTo(from);
                }else{
                	message.setTo(email);
                }
                
                message.setFrom(from);
               
                Map<String, Object> model = new HashMap<String, Object>();
                model.put("courseList", cm.getCourseList());
                model.put("toName", cm.getName());
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

    public String getMailTemplate()
    {
        return mailTemplate;
    }

    public void setMailTemplate(String mailTemplate)
    {
        this.mailTemplate = mailTemplate;
    }

}
