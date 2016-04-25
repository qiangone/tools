package com.capgemini.university.util;

import java.util.regex.Pattern;

/**
 * Created by perrhu on 12/4/2015.
 * This is a validate Util.
 */
public class ValidateUtil{
	public static boolean isPhoneNum(String phoneNum){
		if(phoneNum == null)
			return false;
		return validation("^[1][3,4,5,7,8][0-9]{9}$",
				phoneNum.replace("+86", ""));
	}

	public static boolean isSecurityCode(String securityCode){
		if(securityCode == null)
			return false;
		return validation("^[\\d]{4}$",
				securityCode);
	}

	public static boolean isPassword(String pwd){
		/**
		 * [0-9A-Za-z] {6,16}
		 */
		return validation("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$", pwd);
	}

	public static boolean isEmail(String mail){
		return validation(
				"^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$",
				mail);
	}

	/**
	 * regex validate function
	 *
	 * @param pattern
	 * @param str
	 * @return boolean
	 */
	public static boolean validation(String pattern, String str){
		if(str == null)
			return false;
		return Pattern.compile(pattern).matcher(str).matches();
	}

}
