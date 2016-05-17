package com.capgemini.university.service;

import com.capgemini.university.api.exception.DataAccessDeniedException;
import com.capgemini.university.model.CourseMail;

public interface IMailService {
	
	public void notifyLbpsMail();
	
	public void sendSwapMail(Integer mySbuId , Integer giveoutCourseId, Integer swapSbuId,
	         Integer swapCourseId, Integer swapSeats) throws DataAccessDeniedException;
	
}
