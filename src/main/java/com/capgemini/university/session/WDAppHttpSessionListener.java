package com.capgemini.university.session;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class WDAppHttpSessionListener implements HttpSessionListener{
	
	
	private final Log logger = LogFactory.getLog(getClass());

	@Override
	public void sessionCreated(HttpSessionEvent arg0) {

		
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent arg0) {
		
		HttpSession session = arg0.getSession();
		Object obj = session.getAttribute("SPRING_SECURITY_CONTEXT");
		if(obj != null){
//			SecurityContextImpl securityContextImpl = (SecurityContextImpl)obj;
//			if(securityContextImpl.getAuthentication() != null){
//				String userName = securityContextImpl.getAuthentication().getName();
//				logger.info("begin to delete certification! username:"+ userName);
//				
//				CommonUtil.deleteCertification(userName);
//			}
			
			
		}
		
	}

}
