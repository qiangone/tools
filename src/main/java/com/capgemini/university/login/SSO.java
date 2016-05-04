package com.capgemini.university.login;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.capgemini.university.model.Lbps;
import com.capgemini.university.model.Participant;
import com.capgemini.university.model.Sbu;
import com.capgemini.university.response.ResponseUtil;
import com.capgemini.university.response.WDResponse;
import com.capgemini.university.service.ICourseService;
import com.capgemini.university.service.ISbuService;

@Controller
public class SSO {
	
	@Autowired
	private ISbuService sbuService;
	
	@Autowired
	private ICourseService participantService;
	private final Log logger = LogFactory.getLog(SSO.class);
	

	@RequestMapping("/getUserInfo")
	public @ResponseBody WDResponse getUserInfo() {
		try {
			UserDetails detail = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			
			String loginName = detail.getUsername();
			List<Participant> list = participantService.queryParticipantByName(loginName);
			if(list != null && list.size()>0){
				Participant part = list.get(0);
				//map.put("loginInfo", part);
//				List<Sbu> list2 = sbuService.getSbuByLbps(part.getDisplayName());
//				if(list2 == null || list2.size()==0){
//					return ResponseUtil.apiSuccess("you have no persmisson to login.", "you have no persmisson to login.");
//				}
				Map map = new HashMap();
				Lbps lbps = sbuService.getLbpsByEmail(part.getEmail());
				if(lbps == null){
					return ResponseUtil.apiSuccess("you have no persmisson to login.", "you have no persmisson to login.");
				}
				
				byte[] logoByte = part.getThumbnailPhoto();
				if(logoByte != null){
					String logoImg = new String(Base64.encodeBase64(logoByte));  
//					map.put("logoImg", logoImg);
					lbps.setLogo(logoImg);
				}
				
				int role = lbps.getRole();
				List<Sbu> sbuList = null;
				
				if(role == 1){//admin role
					sbuList = sbuService.getSbuListByParentId(0);
					map.put("role", "admin");
				}else{
					sbuList = sbuService.getSbuByMail(part.getEmail());
					map.put("role", "lbps");
				}
				
				map.put("sbuList", sbuList);
				 
				map.put("lbps", lbps);
				return ResponseUtil.apiSuccess(map, "login successful");
			}

			return ResponseUtil.apiError("search people exception by ldap", "search people exception by ldap");
		} catch (Exception e) {
			logger.error("login exception: ", e);
			return ResponseUtil.apiError("1100", "未知错误");
		}
	}
	

	@RequestMapping("/sso")
	public @ResponseBody ModelAndView login() {

		ModelAndView mav = new ModelAndView();


		mav.setView(new RedirectView("main.html"));
		
		
		

		//redirect to different page based on the role
//		if(hasRole(detail, "ROLE_LOCALHR")||hasRole(detail, "ROLE_OPERATOR_FAQ")||hasRole(detail, "ROLE_APPROVER_FAQ"))
//		{
//			mav.setView(new RedirectView("LocalHR/index.html"));
//		}
//		else if(hasRole(detail, "ROLE_HELPDESK"))
//		{
//			mav.setView(new RedirectView("Helpdesk_support/index.html"));
//		}
//        else if(hasRole(detail, "ROLE_DBA"))
//        {
//            mav.setView(new RedirectView("management.html"));
//        }
//        else if(hasRole(detail, "ROLE_ROLEMANAGER"))
//        {
//            mav.setView(new RedirectView("Super_User/index.html"));
//        }
//		else
//		{
//			mav.setView(new RedirectView("Employee/index.html"));
//		}
//		mav.setView(new RedirectView("main.html"));
//		
		return mav;
	}

	/**
	 * check whethere this user has role "roleName"
	 * @param user
	 * @param roleName
	 * @return
	 */
	private boolean hasRole(UserDetails user, String roleName) {

		for (GrantedAuthority au : user.getAuthorities()) {

			String currentRole = au.getAuthority();

			if (roleName.equals(currentRole)) {
				return true;
			}
		}
		return false;
	}

}