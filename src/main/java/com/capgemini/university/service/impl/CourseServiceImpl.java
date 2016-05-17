/**
 * CAPGEMINI APPLIANCE CHAINS.
 * Copyright (c) 2015-2015 All Rights Reserved.
 */
package com.capgemini.university.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.LikeFilter;
import org.springframework.ldap.filter.OrFilter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import com.capgemini.university.api.exception.DataAccessDeniedException;
import com.capgemini.university.common.PageResults;
import com.capgemini.university.common.Pagination;
import com.capgemini.university.common.UTConstant;
import com.capgemini.university.dao.CourseDao;
import com.capgemini.university.dao.SbuSwapHistoryDao;
import com.capgemini.university.ldap.ParticipantMapper;
import com.capgemini.university.model.Course;
import com.capgemini.university.model.CourseMail;
import com.capgemini.university.model.FreeSeatPool;
import com.capgemini.university.model.GroupCourse;
import com.capgemini.university.model.Participant;
import com.capgemini.university.model.Sbu;
import com.capgemini.university.model.SbuCourse;
import com.capgemini.university.model.SbuSwapHistory;
import com.capgemini.university.service.ICourseService;
import com.capgemini.university.service.ISbuService;
import com.capgemini.university.util.DateUtil;

/**
 * 
 * functional description： seller interface
 * 
 * @author zhihuang@capgemini.com
 * @created Dec 18, 2015 10:37:13 AM
 * @date Dec 18, 2015 10:37:13 AM
 */

public class CourseServiceImpl implements ICourseService {

	private final static Log logger = LogFactory
			.getLog(CourseServiceImpl.class);

	private LdapTemplate ldapTemplate;

	@Autowired
	private CourseDao cpDao;

	@Autowired
	private SbuSwapHistoryDao historyDao;
	
	@Autowired
	private ISbuService sbuService;


	public void setLdapTemplate(LdapTemplate ldapTemplate) {
		this.ldapTemplate = ldapTemplate;
	}

	@Override
	public List<Participant> queryParticipantByName(String name) {
		// DistinguishedName dn = new DistinguishedName();
		// dn.append("OU", "Resources").append("OU", "Conference Rooms");
		// String baseDN = "dc=corp,dc=capgemini,dc=com";
		// AndFilter filter = new AndFilter();
		OrFilter filter2 = new OrFilter();
		// filter.and(new EqualsFilter("objectClass", "user"));
		// filter.and(new EqualsFilter("cn", name));

		filter2.append((new LikeFilter("mail", name)));

		filter2.append((new EqualsFilter("cn", name)));
//		filter2.append((new EqualsFilter("displayName", name)));
		
		System.out.println(filter2.encode());

		// String filter2 = "(cn=zhihuang)";

		ParticipantMapper mapper = new ParticipantMapper();

		return ldapTemplate.search("", filter2.encode(), mapper);
	}

	public List<Participant> queryParticipantList(Integer sbuId,
			Integer courseId) {
		Map map = new HashMap();
		map.put("courseId", courseId);
		map.put("sbuId", sbuId);
		return cpDao.queryParticipantList(map);
	}

	public void removeParticipant(Integer sbuId, Integer courseId, String email) {

		Map map = new HashMap();
		map.put("courseId", courseId);
		map.put("sbuId", sbuId);
		map.put("email", email);
		List<Participant> list = cpDao.queryParticipantList(map);
		if(list != null && list.size()>0){
			Participant p = list.get(0);
			int source = p.getSource();
			
			// remove participant
			cpDao.removeParticipant(map);
			
			if(source == 1){//free seats
				SbuCourse self = getSbuCourse(sbuId, courseId);
				
				// update sbu course
				int freeSeats = self.getGetFreeSeats();
				self.setGetFreeSeats(freeSeats - 1);
				cpDao.updateSbuCourse(self);
				
				//update free seat pool
				FreeSeatPool fsp = getFreeSeatPoolById(courseId);
			    fsp.setSeats(fsp.getSeats() + 1);
			    cpDao.updateFreeSeatPool(fsp);
				
				
			}else{
				SbuCourse self = getSbuCourse(sbuId, courseId);
				Float duration = self.getDuration();
				int assignSeats = self.getAssignSeats();
				int assignPmds = self.getAssignPmds();
				self.setAssignSeats(assignSeats - 1);
				self.setAssignPmds((int)Math.ceil((assignPmds - duration)));
				cpDao.updateSbuCourse(self);
			}
		}
		
	}
	
	public void takeFreeParticipantList(List<Participant> list){
		int sbuId=0;
		int courseId=0;
		for(Participant p : list){
			sbuId = p.getSbuId();
			courseId = p.getCourseId();
			p.setSource(1);//from free seat
			cpDao.addParticipant(p);
		}
		
		SbuCourse sc = getSbuCourse(sbuId, courseId);
		if(sc == null){
			SbuCourse sco = new SbuCourse();
			sco.setSbuId(sbuId);
			sco.setCourseId(courseId);
			Course c = getCourseById(courseId);
			sco.setDuration(c.getDuration());
			sco.setSeats(0);
			sco.setPmds(0);
			sco.setGetFreeSeats(list.size());
			cpDao.addSbuCourse(sco);
			
		}else{
			sc.setGetFreeSeats(sc.getGetFreeSeats() + list.size());
			cpDao.updateSbuCourse(sc);
		}
		
		
		FreeSeatPool fsp = getFreeSeatPoolById(courseId);
		if(fsp != null){
			fsp.setSeats(fsp.getSeats() - list.size());
			cpDao.updateFreeSeatPool(fsp);
		}else{
			throw new DataAccessDeniedException("takeFreeParticipantList Date exception.");
		}
		
		
		
		
		
	}
	
	 public List<FreeSeatPool> getFreeSeatPoolList(){
		Map map = new HashMap();
		map.put("freeSeat", "true");
		List<FreeSeatPool> list = cpDao.getFreeSeatPoolList(map);
		return list;
		 
	 }
	
	public FreeSeatPool getFreeSeatPoolById(int courseId){
		Map map = new HashMap();
		map.put("courseId", courseId);
		List<FreeSeatPool> list = cpDao.getFreeSeatPoolList(map);
		if(list != null && list.size()>0){
			return list.get(0);
		}
		return null;
	}

	public int addParticipantList(List<Participant> list) {
		if (list != null && list.size() > 0) {
			for (Participant p : list) {
				addParticipant(p);
			}
		}
		return 0;
	}

	public int addParticipant(Participant part) {

		int sbuId = part.getSbuId();
		int courseId = part.getCourseId();

		SbuCourse self = getSbuCourse(sbuId, courseId);
		if (self != null) {
			int leftSeats = self.getSeats() - self.getAssignSeats();
			if (leftSeats <= 0) {
				throw new DataAccessDeniedException("seats not enough.");
			}
		}

		Float duration = self.getDuration();
		int assignSeats = self.getAssignSeats();
		int assignPmds = self.getAssignPmds();
		self.setAssignSeats(assignSeats + 1);
		self.setAssignPmds((int)Math.ceil( (assignPmds + duration)));
		cpDao.updateSbuCourse(self);

		// List<Sbu> list = sbuService.getAllParentSbuList(sbuId);
		// if(list != null && list.size()>0){
		// for(Sbu sbu : list){
		// SbuCourse course = getSbuCourse(sbu.getId(), courseId);
		// if(course != null){
		// Float duration = course.getDuration();
		// int assignSeats = course.getAssignSeats();
		// int assignPmds = course.getAssignPmds();
		// course.setAssignSeats(assignSeats+1);
		// course.setAssignPmds((int)(assignPmds + duration));
		// cpDao.updateSbuCourse(course);
		// }
		// }
		// }

		// record ..
		cpDao.addParticipant(part);

		return 0;
	}

	public Course getAdminCourse(Integer id) {
		Map map = new HashMap();
		map.put("id", id);
		List<Course> list = cpDao.getAdminCourseListByPage(map);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	public PageResults<Course> getAdminCourseListByPage(Map map, Pagination page) {
		if (null == page || !page.isValidPage()) {
			return null;
		}

		// get counts
		List<Course> list = cpDao.getAdminCourseListByPage(map);
		if (list == null || list.size() == 0) {
			return null;
		}

		int totalCount = list.size();
		page.setTotalCount(totalCount);

		map.put("showpage", 1);
		map.put(Pagination.STARTINDEX, page.getStartIndex());
		map.put(Pagination.PAGESIZE, page.getPageSize());

		List<Course> list2 = cpDao.getAdminCourseListByPage(map);
		PageResults<Course> result = new PageResults<Course>(page, list2);

		return result;
	}
	
	public List<Map> getAllOtherSwapCourseList(int sbuId){
		Map map = new HashMap();
		map.put("swapSeats", "true");
		map.put("sbuId", sbuId);
		List<Map> list = cpDao.getCourseListByPage(map);
		return list;
	}

	public PageResults<Map> getCourseListByPage(Map map, Pagination page) {
		if (null == page || !page.isValidPage()) {
			return null;
		}

		// get counts
		List<Map> list = cpDao.getCourseListByPage(map);
		if (list == null || list.size() == 0) {
			return null;
		}

		int totalCount = list.size();
		page.setTotalCount(totalCount);

		map.put("showpage", 1);
		map.put(Pagination.STARTINDEX, page.getStartIndex());
		map.put(Pagination.PAGESIZE, page.getPageSize());

		List<Map> retList = new ArrayList<Map>();
		List<Map> list2 = cpDao.getCourseListByPage(map);

		Map groupMap = groupEvent(list2);
		retList.add(groupMap);

		PageResults<Map> result = new PageResults<Map>(page, retList);

		return result;
	}

	private String getEventRangeTime(String eventName) {
		List<Course> list = getCourseListByEvent(eventName);
		Date compStart = null;
		;
		Date compEnd = null;
		if (list != null && list.size() > 0) {
			int i = 0;
			for (Course c : list) {
				Date start = c.getStartTime();
				Date end = c.getEndTime();
				if (i == 0) {
					compStart = start;
					compEnd = end;
				}

				if (compStart.after(start)) {
					compStart = start;
				}
				if (compEnd.before(end)) {
					compEnd = end;
				}

				i++;
			}

			return DateUtil.dateFormat(compStart) + "|"
					+ DateUtil.dateFormat(compEnd);

		}

		return "";

	}

	private Map groupEvent(List<Map> map) {
		Map map3 = new LinkedHashMap();
		List<Map> retList = new ArrayList<Map>();
		if (map != null && map.size() > 0) {
			for (Map map2 : map) {
				Object eventName = map2.get("event_name");
				Object eventLogo = map2.get("event_logo");

				GroupCourse gc = new GroupCourse();
				gc.setEventName(eventName);

				gc.setEventLogo(eventLogo);

				String rangTime = getEventRangeTime(eventName.toString());
				if (rangTime != "") {
					String[] str = rangTime.split("\\|");
					gc.setStartTime(str[0]);
					gc.setEndTime(str[1]);
				}

				if (map3.get(eventName) == null) {// not exist
					List list = new ArrayList();
					list.add(map2);
					gc.setResults(list);

					map3.put(eventName, gc);

				} else {
					GroupCourse group = (GroupCourse) map3.get(eventName);
					List list4 = group.getResults();
					list4.add(map2);
					map3.put(eventName, group);
				}

			}
		}

		return map3;
	}

	public SbuCourse getSbuCourse(Integer sbuId, Integer courseId) {
		Map map = new HashMap();
		map.put("sbuId", sbuId);
		map.put("courseId", courseId);
		List<SbuCourse> list = cpDao.getSbuCourse(map);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	public int saveOrUpdateSbuCourse(SbuCourse sc) {

		int courseId = sc.getCourseId();
		int sbuId = sc.getSbuId();
		int seats = sc.getSeats();// to do seats

		Sbu self = sbuService.getSbuById(sbuId);
		SbuCourse parentSc = getSbuCourse(self.getParentId(), courseId);
		Course cou = getCourseById(courseId);
		float duration = cou.getDuration();

		SbuCourse scour = getSbuCourse(sbuId, courseId);
		if (scour != null) {// update
			int origiSeats = scour.getSeats();
			if (seats < origiSeats) {// delete
				if (parentSc != null) {
					List<Participant> list = queryParticipantList(sbuId,
							courseId);
					if (list != null && list.size() > 0) {
						if (list.size() > seats) {
							throw new DataAccessDeniedException(
									"please remove participant firstly.");
						}
					}

					int assignSeat = parentSc.getAssignSeats()
							- (origiSeats - seats);
					int assignPmd = (int) Math.ceil((assignSeat * duration));
					parentSc.setAssignSeats(assignSeat);
					parentSc.setAssignPmds(assignPmd);
					cpDao.updateSbuCourse(parentSc);
				}
			} else if (seats > origiSeats) {// add
				if (parentSc != null) {
					int addSeats = seats - origiSeats;
					int leftSeats = parentSc.getSeats()
							- parentSc.getAssignSeats();
					if (addSeats > leftSeats) {
						throw new DataAccessDeniedException("seats not enough.");
					}

					int assignSeat = parentSc.getAssignSeats() + addSeats;
					int assignPmd = (int) Math.ceil((assignSeat * duration));
					parentSc.setAssignSeats(assignSeat);
					parentSc.setAssignPmds(assignPmd);
					cpDao.updateSbuCourse(parentSc);

				}
			}

			scour.setSeats(sc.getSeats());
			scour.setPmds((int) Math.ceil((scour.getSeats() * scour.getDuration())));
			cpDao.updateSbuCourse(scour);// update

		} else {// insert

			if (parentSc != null) {
				int avaiSeats = parentSc.getSeats() - parentSc.getAssignSeats();
				if (sc.getSeats() > avaiSeats) {
					throw new DataAccessDeniedException("seats not enough.");
				}

				parentSc.setAssignSeats(parentSc.getAssignSeats()
						+ sc.getSeats());
				parentSc.setAssignPmds((int)Math.ceil( (parentSc.getAssignPmds() + sc
						.getSeats() * parentSc.getDuration())));
				cpDao.updateSbuCourse(parentSc);
			}

			sc.setDuration(duration);
			sc.setPmds((int) Math.ceil((sc.getSeats() * duration)));
			cpDao.addSbuCourse(sc);

		}

		// Course cou = getCourseById(courseId);
		// float duration = cou.getDuration();
		// if(scour != null){//update
		// scour.setSeats(sc.getSeats());
		// scour.setPmds((int)(sc.getSeats() * duration));
		// cpDao.updateSbuCourse(scour);
		//
		// }else{//insert
		//
		// sc.setDuration(duration);
		// sc.setPmds((int)(sc.getSeats() * duration));
		// cpDao.addSbuCourse(sc);
		// }
		//

		return 0;
	}

	public int updateSbuCourse(SbuCourse sc) {

		return cpDao.updateSbuCourse(sc);
	}

	public List<Map> getEventList(String type, String sbuId) {
		Map map = new HashMap();
		map.put("type", type);
		map.put("sbuId", sbuId);

		List<Map> list = cpDao.getEventList(map);
		return list;
	}

	public List<Course> getCourseListByEvent(String eventName) {
		Map map = new HashMap();
		map.put("eventName", eventName);

		List<Course> list = cpDao.getCourse(map);
		return list;
	}
	
	public List<Map> getSbuCourseListByEvent(String type,String eventName, String sbuId){
		Map map = new HashMap();
		map.put("eventName", eventName);
		map.put("sbuId", sbuId);
		map.put("type", type);
		return cpDao.getCourseListByEvent(map);
	}

	public Course getCourseById(Integer id) {
		Map map = new HashMap();
		map.put("id", id);

		List<Course> list = cpDao.getCourse(map);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	public void swap_bak(Integer course1, Integer fromSbuId, Integer toSbuId,
			Integer action, Integer seats, Integer course2)
			throws DataAccessDeniedException {
		UserDetails detail = (UserDetails) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();

		String loginName = detail.getUsername();

		if (action == 0) {// lend-
			// lend
			SbuCourse fromSbu1 = getSbuCourse(fromSbuId, course1);
			SbuCourse toSbu1 = getSbuCourse(toSbuId, course1);

			// return
			SbuCourse fromSbu2 = getSbuCourse(toSbuId, course2);
			SbuCourse toSbu2 = getSbuCourse(fromSbuId, course2);

			lendCourse(fromSbu1, toSbu1, seats);
			lendCourse(fromSbu2, toSbu2, seats);

			SbuSwapHistory his = new SbuSwapHistory();
			his.setFromSbuId(fromSbuId);
			his.setToSbuId(toSbuId);
			his.setGiveoutCourseId(course1);
			his.setTakeinCourseId(course2);
			his.setGiveoutDuration(fromSbu1.getDuration());
			his.setTakeinDuration(fromSbu2.getDuration());
			his.setSeats(seats);

			his.setActionBy(loginName);
			his.setActionTime(new Date());

			historyDao.addSbuSwapHistory(his);

		} else {// return +
				// lend
			SbuCourse fromSbu1 = getSbuCourse(toSbuId, course1);
			SbuCourse toSbu1 = getSbuCourse(fromSbuId, course1);

			// return
			SbuCourse fromSbu2 = getSbuCourse(fromSbuId, course2);
			SbuCourse toSbu2 = getSbuCourse(toSbuId, course2);

			lendCourse(fromSbu1, toSbu1, seats);
			lendCourse(fromSbu2, toSbu2, seats);

			SbuSwapHistory his = new SbuSwapHistory();
			his.setFromSbuId(fromSbuId);
			his.setToSbuId(toSbuId);
			his.setGiveoutCourseId(course2);
			his.setTakeinCourseId(course1);
			his.setGiveoutDuration(fromSbu2.getDuration());
			his.setTakeinDuration(fromSbu1.getDuration());
			his.setSeats(seats);

			his.setActionBy(loginName);
			his.setActionTime(new Date());

			historyDao.addSbuSwapHistory(his);

		}

	}
	
	
	public void swap(Integer mySbuId , Integer giveoutCourseId, Integer swapSbuId,
			         Integer swapCourseId, Integer swapSeats)
			throws DataAccessDeniedException {
			UserDetails detail = (UserDetails) SecurityContextHolder.getContext()
					.getAuthentication().getPrincipal();
	
			String loginName = detail.getUsername();
		
		
			// lend
			SbuCourse fromSbu1 = getSbuCourse(mySbuId, giveoutCourseId);
			SbuCourse toSbu1 = getSbuCourse(swapSbuId, giveoutCourseId);
			lendNormalSeats(fromSbu1, toSbu1, swapSbuId, swapSeats);
			
			
			// return
			SbuCourse fromSbu2 = getSbuCourse(swapSbuId, swapCourseId);
			SbuCourse toSbu2 = getSbuCourse(mySbuId, swapCourseId);
			lendSwapSeats(fromSbu2, toSbu2, mySbuId, swapSeats);
			

			SbuSwapHistory his = new SbuSwapHistory();
			his.setFromSbuId(mySbuId);
			his.setToSbuId(swapSbuId);
			his.setGiveoutCourseId(giveoutCourseId);
			his.setTakeinCourseId(swapCourseId);
			his.setGiveoutDuration(fromSbu1.getDuration());
			his.setTakeinDuration(fromSbu2.getDuration());
			his.setSeats(swapSeats);
			his.setActionBy(loginName);
			his.setActionTime(new Date());

			historyDao.addSbuSwapHistory(his);
			
			

	}
	
	private void lendNormalSeats(SbuCourse fromSbu, SbuCourse toSbu, int toSbuId, int seats) {

		int leftSeats = fromSbu.getSeats() - fromSbu.getAssignSeats() - fromSbu.getSwapSeats();
		if (leftSeats < seats) {

			// Course c1 = getCourseById(fromSbu.getSbuId());
			throw new DataAccessDeniedException("sbu" + fromSbu.getSbuId()
					+ "for course" + fromSbu.getCourseId()
					+ " avaliable seats is not enough.");
		}
		
		//lend
		int swapSeat = fromSbu.getSwapSeats();
		if(seats<=swapSeat){
			fromSbu.setSwapSeats(swapSeat-seats); //give out seat from swap firstly
		}else{
			fromSbu.setSwapSeats(0);//clear swap seat
			fromSbu.setSeats(fromSbu.getSeats() -(seats -swapSeat));//if seat not enough,then take from remain seat
			fromSbu.setPmds((int) Math.ceil((fromSbu.getSeats() * fromSbu.getDuration())));
		}
		
		
		cpDao.updateSbuCourse(fromSbu);
		
		
		//to
		if(toSbu == null){//no have this course
			SbuCourse sc = new SbuCourse();
			sc.setSbuId(toSbuId);
			sc.setCourseId(fromSbu.getCourseId());
			sc.setDuration(fromSbu.getDuration());
			sc.setSeats(seats);
			sc.setPmds((int) Math.ceil((sc.getSeats() * sc.getDuration())));
			cpDao.addSbuCourse(sc);
		}else{
			toSbu.setSeats(toSbu.getSeats() + seats);
			toSbu.setPmds((int) Math.ceil((toSbu.getSeats() * toSbu.getDuration())));
			cpDao.updateSbuCourse(toSbu);
		}
		
		
	}
	
	private void lendSwapSeats(SbuCourse fromSbu, SbuCourse toSbu, int toSbuId, int swapSeats) {

		int leftSeats = fromSbu.getSwapSeats();
		if (leftSeats < swapSeats) {

			// Course c1 = getCourseById(fromSbu.getSbuId());
			throw new DataAccessDeniedException("sbu:" + fromSbu.getSbuId()
					+ "for course:" + fromSbu.getCourseId()
					+ " swap seats is not enough.");
		}
		
		//lend
		fromSbu.setSwapSeats(fromSbu.getSwapSeats() - swapSeats);
		cpDao.updateSbuCourse(fromSbu);
		
		//to
		if(toSbu == null){//no have this course
			SbuCourse sc = new SbuCourse();
			sc.setSbuId(toSbuId);
			sc.setCourseId(fromSbu.getCourseId());
			sc.setDuration(fromSbu.getDuration());
			sc.setSeats(swapSeats);
			sc.setPmds((int) Math.ceil((sc.getSeats() * sc.getDuration())));
			cpDao.addSbuCourse(sc);
		}else{
			toSbu.setSeats(toSbu.getSeats() + swapSeats);
			toSbu.setPmds((int) Math.ceil((toSbu.getSeats() * toSbu.getDuration())));
			cpDao.updateSbuCourse(toSbu);
		}
		
		
	}

	private void lendCourse(SbuCourse fromSbu, SbuCourse toSbu, int seats) {

		int leftSeats = fromSbu.getSeats() - fromSbu.getAssignSeats();
		int leftSeats2 = toSbu.getSeats() - toSbu.getAssignSeats();
		if (leftSeats < seats) {

			// Course c1 = getCourseById(fromSbu.getSbuId());
			throw new DataAccessDeniedException("sbu" + fromSbu.getSbuId()
					+ "for course" + fromSbu.getCourseId()
					+ " avaliable seats is not enough.");
		}
		// if(leftSeats2<seats){
		// throw new DataAccessDeniedException("sbu" + toSbu.getSbuId()+
		// "for course" +toSbu.getCourseId()+
		// " avaliable seats is not enough.");
		// }

		// fromSbu.setSeats(fromSbu.getSeats() - seats);
		// fromSbu.setPmds(fromSbu.getPmds() - fromSbu.getDuration() * seats);
		// cpDao.updateSbuCourse(fromSbu);

		// update parent sbu_course seats and pmds
		List<Sbu> list = sbuService.getAllParentSbuList(fromSbu.getSbuId());
		if (list != null && list.size() > 0) {
			for (Sbu sbu : list) {
				SbuCourse course = getSbuCourse(sbu.getId(),
						fromSbu.getCourseId());
				if (course != null) {
					course.setSeats(course.getSeats() - seats);
					course.setPmds((int) Math.ceil((course.getPmds() - course
							.getDuration() * seats)));
					cpDao.updateSbuCourse(course);
				}
			}
		}// end

		// toSbu.setSeats(toSbu.getSeats() + seats);
		// toSbu.setPmds(toSbu.getPmds() + toSbu.getDuration() * seats);
		// cpDao.updateSbuCourse(toSbu);

		// update parent sbu_course seats and pmds
		List<Sbu> list2 = sbuService.getAllParentSbuList(toSbu.getSbuId());
		if (list2 != null && list2.size() > 0) {
			for (Sbu sbu : list2) {
				SbuCourse course = getSbuCourse(sbu.getId(),
						toSbu.getCourseId());
				if (course != null) {
					course.setSeats(course.getSeats() + seats);
					course.setPmds((int)Math.ceil( (course.getPmds() + course
							.getDuration() * seats)));
					cpDao.updateSbuCourse(course);
				}
			}
		}// end

		// // record history
		// SbuSwapHistory his1 = new SbuSwapHistory();
		// his1.setCourseId(fromSbu.getCourseId());
		// his1.setFromSbuId(fromSbu.getSbuId());
		// his1.setToSbuId(toSbu.getSbuId());
		// his1.setAction(0);//lend
		// his1.setSeats(seats);
		// his1.setDuration(fromSbu.getDuration());
		// //UserDetails detail = (UserDetails)
		// SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		// his1.setActionBy("admin");
		// his1.setActionTime(new Date());
		//
		// historyDao.addSbuSwapHistory(his1);
	}

	public List<CourseMail> getAllCourseBeforeStarting(int week){
		Map map = new HashMap();
		map.put("week", week);
		return cpDao.getAllCourseToBegin(map);
	}
	
	public void deleteCourse(int courseId){
		cpDao.deleteCoursebyId(courseId);
		cpDao.deleteSbuCoursebyId(courseId);
	}
	
	 public void updatedAttend(int participantId, int attend){
		 Participant pa = new Participant();
		 pa.setId(participantId);
		 pa.setAttend(attend);
		 cpDao.updateParticipant(pa);
	 }

	/**
	 * upload course
	 */
	public void uploadCourse(InputStream in, boolean append)
			throws DataAccessDeniedException {

		Workbook wb = null;
		List<Course> list = new ArrayList<Course>();

		try {

			wb = new XSSFWorkbook(in);
			Sheet st = wb.getSheetAt(0);
			Row rw;
			// Cell cl;
			for (int rowNum = 0; rowNum < st.getLastRowNum(); rowNum++) {

				String cellValue = "";
				int asciicode = 10;
				char newLine = (char) asciicode;

				rw = st.getRow(rowNum + 2);
				if (rw == null) {
					continue;
				}

				Course c = new Course();
				String eventName = getStringCellValue(rw.getCell(0));
				String courseName = getStringCellValue(rw.getCell(1));
				String courseUrl = getStringCellValue(rw.getCell(2));
				String startTime = getStringCellValue(rw.getCell(3));
				String endTime = getStringCellValue(rw.getCell(4));
				String duration = getStringCellValue(rw.getCell(5));

				String app1 = getStringCellValue(rw.getCell(6));
				String app2 = getStringCellValue(rw.getCell(7));
				String bpo = getStringCellValue(rw.getCell(8));
				String cc = getStringCellValue(rw.getCell(9));
				String infra = getStringCellValue(rw.getCell(10));
				String latam = getStringCellValue(rw.getCell(11));
				String iandd = getStringCellValue(rw.getCell(12));
				String sogeti = getStringCellValue(rw.getCell(13));
				String fs = getStringCellValue(rw.getCell(14));
				String other = getStringCellValue(rw.getCell(15));
				// String swap = getStringCellValue(rw.getCell(16));

				if (StringUtils.isEmpty(eventName)) {
					continue;
				}

				c.setEventName(eventName);
				c.setName(courseName);
				c.setUrl(courseUrl);
				if (!StringUtils.isEmpty(startTime)) {
					c.setStartTime(DateUtil.formatDate(startTime));
				}
				if (!StringUtils.isEmpty(endTime)) {
					c.setEndTime(DateUtil.formatDate(endTime));
				}
				if(StringUtils.isEmpty(duration)){
					throw new DataAccessDeniedException("eventName:"+eventName +" courseName:" + courseName + " duration is null.");

				}

				c.setDuration(Float.parseFloat(duration));

				List<SbuCourse> sbuCourseList = new ArrayList<SbuCourse>();

				if (!StringUtils.isEmpty(app1)) {
					SbuCourse sc = new SbuCourse();
					sc.setSbuId(UTConstant.SBUID_APP1);
					sc.setDuration(Float.parseFloat(duration));
					sc.setSeats((int) Float.parseFloat(app1));
					sc.setPmds((int) Math.ceil(sc.getDuration() * sc.getSeats()));
					sbuCourseList.add(sc);
				}
				if (!StringUtils.isEmpty(app2)) {
					SbuCourse sc = new SbuCourse();
					sc.setSbuId(UTConstant.SBUID_APP2);
					sc.setDuration(Float.parseFloat(duration));
					sc.setSeats((int) Float.parseFloat(app2));
					sc.setPmds((int) Math.ceil(sc.getDuration() * sc.getSeats()));
					sbuCourseList.add(sc);
				}
				if (!StringUtils.isEmpty(bpo)) {
					SbuCourse sc = new SbuCourse();
					sc.setSbuId(UTConstant.SBUID_BPO);
					sc.setDuration(Float.parseFloat(duration));
					sc.setSeats((int) Float.parseFloat(bpo));
					sc.setPmds((int) Math.ceil(sc.getDuration() * sc.getSeats()));
					sbuCourseList.add(sc);
				}
				if (!StringUtils.isEmpty(cc)) {
					SbuCourse sc = new SbuCourse();
					sc.setSbuId(UTConstant.SBUID_CC);
					sc.setDuration(Float.parseFloat(duration));
					sc.setSeats((int) Float.parseFloat(cc));
					sc.setPmds((int) Math.ceil(sc.getDuration() * sc.getSeats()));
					sbuCourseList.add(sc);
				}

				if (!StringUtils.isEmpty(infra)) {
					SbuCourse sc = new SbuCourse();
					sc.setSbuId(UTConstant.SBUID_INFRA);
					sc.setDuration(Float.parseFloat(duration));
					sc.setSeats((int) Float.parseFloat(infra));
					sc.setPmds((int) Math.ceil(sc.getDuration() * sc.getSeats()));
					sbuCourseList.add(sc);
				}

				if (!StringUtils.isEmpty(latam)) {
					SbuCourse sc = new SbuCourse();
					sc.setSbuId(UTConstant.SBUID_LATAM);
					sc.setDuration(Float.parseFloat(duration));
					sc.setSeats((int) Float.parseFloat(latam));
					sc.setPmds((int) Math.ceil(sc.getDuration() * sc.getSeats()));
					sbuCourseList.add(sc);
				}

				if (!StringUtils.isEmpty(iandd)) {
					SbuCourse sc = new SbuCourse();
					sc.setSbuId(UTConstant.SBUID_IANDD);
					sc.setDuration(Float.parseFloat(duration));
					sc.setSeats((int) Float.parseFloat(iandd));
					sc.setPmds((int) Math.ceil(sc.getDuration() * sc.getSeats()));
					sbuCourseList.add(sc);
				}

				if (!StringUtils.isEmpty(sogeti)) {
					SbuCourse sc = new SbuCourse();
					sc.setSbuId(UTConstant.SBUID_SOGETI);
					sc.setDuration(Float.parseFloat(duration));
					sc.setSeats((int) Float.parseFloat(sogeti));
					sc.setPmds((int) Math.ceil(sc.getDuration() * sc.getSeats()));
					sbuCourseList.add(sc);
				}

				if (!StringUtils.isEmpty(fs)) {
					SbuCourse sc = new SbuCourse();
					sc.setSbuId(UTConstant.SBUID_FS);
					sc.setDuration(Float.parseFloat(duration));
					sc.setSeats((int) Float.parseFloat(fs));
					sc.setPmds((int) Math.ceil(sc.getDuration() * sc.getSeats()));
					sbuCourseList.add(sc);
				}

				if (!StringUtils.isEmpty(other)) {
					SbuCourse sc = new SbuCourse();
					sc.setSbuId(UTConstant.SBUID_OTHER);
					sc.setDuration(Float.parseFloat(duration));
					sc.setSeats((int) Float.parseFloat(other));
					sc.setPmds((int) Math.ceil(sc.getDuration() * sc.getSeats()));
					sbuCourseList.add(sc);
				}

				// if(!StringUtils.isEmpty(swap)){
				// SbuCourse sc = new SbuCourse();
				// sc.setSbuId(UTConstant.SBUID_SWAP);
				// sc.setDuration(Float.parseFloat(duration));
				// sc.setSeats((int)Float.parseFloat(swap));
				// sc.setPmds((int)Math.ceil(sc.getDuration() * sc.getSeats()));
				// sbuCourseList.add(sc);
				// }
				//
				c.setSbuList(sbuCourseList);
				list.add(c);

			}

			if (list.size() == 0) {
				throw new DataAccessDeniedException(
						"There is no valid data in excel.");

			}

			saveCourse(list);

		} catch (Exception e) {
			logger.error("Read Excel File Error Message : " , e);
			throw new DataAccessDeniedException(
					"Read Excel File Error Message ");
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					logger.error("The Method Read Excel File Error Message : "
							,e);
				}
			}
			if (wb != null) {
				try {
					wb.close();
				} catch (IOException e) {
					logger.error("The Method Read Excel File Error Message : "
							, e);
				}
			}
		}

	}
	
	
	
	

	public void updateCourse(Course c) {
		cpDao.updateCourse(c);
		List<SbuCourse> list = c.getSbuList();
		if (list != null && list.size() > 0) {
			for (SbuCourse sc : list) {
				int seats = sc.getSeats();
				if (!StringUtils.isEmpty(c.getDuration())) {
					float duration = c.getDuration();
					sc.setPmds((int) Math.ceil((seats * duration)));
					sc.setDuration(duration);
				}

				cpDao.updateSbuCourse(sc);
			}

		}

	}

	public void saveCourse(List<Course> list) {
		for (Course c : list) {
			cpDao.addCourse(c);
			List<SbuCourse> sbuList = c.getSbuList();
			for (SbuCourse sbu : sbuList) {
				sbu.setCourseId(c.getId());
			}

			if (sbuList != null && sbuList.size() != 0) {
				cpDao.addSbuCourseList(sbuList);
			}

		}
	}

	/**
	 * get the value of Excel cell then transform them to String type
	 * 
	 * @param cell
	 *            cell
	 * @return String value of Cell
	 */
	private String getStringCellValue(Cell cell) {
		if (cell == null) {
			return "";
		}
		String strCell = "";
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_STRING:
			strCell = cell.getStringCellValue();
			break;
		case Cell.CELL_TYPE_NUMERIC:
			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				Date date = cell.getDateCellValue();
				DateFormat formater = new SimpleDateFormat("MM/dd/yyyy");
				strCell = formater.format(date);
				break;
			}
			strCell = String.valueOf(cell.getNumericCellValue());// Note! cast
																	// data from
																	// Double
																	// type to
																	// Integer
																	// type
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			strCell = String.valueOf(cell.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_BLANK:
			strCell = "";
			break;
		default:
			strCell = "";
			break;
		}

		return strCell;
	}

}
