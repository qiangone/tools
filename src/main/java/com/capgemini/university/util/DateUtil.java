/* Copyright 2011 CAPGEMINI Financial Service GBU, Inc. All rights
 * reserved.
 * Use is subject to license terms. */

package com.capgemini.university.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


// TODO Add class/interface import here and remove this line.

/**
 * TODO Add class/interface description here and remove this line.
 * 
 * @author calvye, create on 12 Jun 2012
 * Revision History:
 *      TODO Revised by XXXX on 201x/xx/xx, modified xxx
 */

public class DateUtil
{
	
	private final static Log logger = LogFactory.getLog(DateUtil.class);

    public static Date getFirstDateOfYear(int year)
    {
        return getDate(year, 1, 1);

    }

    public static Date getLastDateOfYear(int year)
    {
        return getDate(year, 12, 31);
    }

    public static Date getDate(int year, int month, int day)
    {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }
    
    public static int getDayOfYear(Date date)
    {
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(date);
    	return cal.get(Calendar.DAY_OF_YEAR);
    }

    public static Date getLastDayOfMonth(Date date)
    {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int day = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, day);
        return cal.getTime();
    }

    public static Date addDay(Date start, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        cal.add(Calendar.DAY_OF_YEAR, days);
        return cal.getTime();

    }

    /**
     * Get the current year
     * @return
     */
    public static int getCurrentYear()
    {
        return getYear(new Date());
    }

    public static int getYear(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        return year;
    }

    public static int getDay(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.DAY_OF_MONTH);
        return year;
    }

    public static int getMonth(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH) + 1;
        return month;
    }

//    public static List<Integer> getRecentYears()
//    {
//        int year = getCurrentYear();
//        int month = getMonth(new Date());
//
//        List<Integer> list = new ArrayList<Integer>();
//        if (month < 12)
//        {
//            list.add(year - 1);
//        }
//        list.add(year);
//        if (month > 0)
//        {
//            list.add(year + 1);
//        }
//        return list;
//    }

    /**
     * Get the differnt number of month between two days
     * @param startDate
     * @param endDate
     * @return
     */
    public static int getDiffMonth(Date startDate, Date endDate)
    {
        if (startDate.compareTo(endDate) > 0)
        {
            return -1;
        }

        Calendar calStart = Calendar.getInstance();
        calStart.setTime(startDate);

        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(endDate);

        int diffYear = calEnd.get(Calendar.YEAR) - calStart.get(Calendar.YEAR);
        int diffMonth = calEnd.get(Calendar.MONTH) - calStart.get(Calendar.MONTH);
        return diffYear * 12 + diffMonth;
    }

    public static int getDiffDay(Date startDate, Date endDate)
    {
        if (startDate.compareTo(endDate) > 0)
        {
            return -1;
        }

        long start = startDate.getTime();
        long end = endDate.getTime();

        int day = (int) ((end - start) / (1000 * 60 * 60 * 24));

        return day;
    }

    /**
     * Get the year of the date
     * @param date
     * @return
     */
    public static int getYearOfDate(Date date)
    {
        Calendar calStart = Calendar.getInstance();
        calStart.setTime(date);
        return calStart.get(Calendar.YEAR);
    }

    public static Date formatUserFromLDAP(String date)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        try
        {
            return format.parse(date);
        } catch (ParseException e) {
        	logger.error("Format UserFrom LDAP Error Message : " + e);
            throw new RuntimeException(e);
        }
    }
    
    public static Date formatDate(String time)
    {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        try
        {
            return format.parse(time);
        } catch (Exception e) {
        	logger.error("Format UserFrom LDAP Error Message : " + e);
            throw new RuntimeException(e);
        }
    }
    
    
    
    
    public static Date dateParseWithSec(String date)
    {
        if (StringUtils.isEmpty(date)) return null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            return format.parse(date);
        } catch (ParseException e)
        {
        	logger.error("Date ParseWithSec Error Message : " + e);
            throw new RuntimeException(e);
        }
    }
    
    
    public static String dateFormat(Date date) {
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    	return format.format(date);
    }
    
    public static String dateFormatWithSec(Date date) {
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	return format.format(date);
    }
    
	public static int getDaysByYearMonth(int year, int month) {  
        
        Calendar a = Calendar.getInstance();  
        a.set(Calendar.YEAR, year);  
        a.set(Calendar.MONTH, month - 1);  
        a.set(Calendar.DATE, 1);  
        a.roll(Calendar.DATE, -1);  
        int maxDate = a.get(Calendar.DATE);  
        return maxDate;  
    } 
}
