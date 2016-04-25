package com.capgemini.university.util;

import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;

/**
 * Created by perrhu on 12/4/2015.
 */
public class SmsUtil{
	public static final String URL = "http://gw.api.taobao.com/router/rest";
	public static final String APPKEY = "23327967";
	public static final String APPSECRET = "bbc7a035cf2c27eb62ef52e6d4c6167f";
	public static final int TYPE_REGISTER = 1, TYPE_FIND_PASSWORD = 2, TYPE_UPDATE_PASSWORD = 3;
	private static final String PRODUCT_NAME = "微店(Capgemini)";
	public static AlibabaAliqinFcSmsNumSendRequest buildSmsRequest(String phoneNumber, String code, int type) {
		
    	AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
    	req.setSmsType("normal");
    	req.setSmsParamString("{code:\"" + code + "\",product:\""+ PRODUCT_NAME + "\"}");
    	req.setRecNum(phoneNumber);
    	switch (type) {
    		case TYPE_REGISTER:
    			req.setSmsFreeSignName("注册验证");
    			req.setSmsTemplateCode("SMS_6270025");
    			break;
    		case TYPE_FIND_PASSWORD:
    			req.setSmsFreeSignName("变更验证");
    			req.setSmsTemplateCode("SMS_6270022");
    			break;
    	}
    	return req;
    	
    	
		
	}
	
	public static String createSecurityCode() {
		boolean numberFlag = true;
		int length = 6;
		String retStr = "";
		String strTable = numberFlag ? "1234567890" : "1234567890abcdefghijkmnpqrstuvwxyz";
		int len = strTable.length();
		boolean bDone = true;
		do {
			retStr = "";
			int count = 0;
			for (int i = 0 ;  i <length; i++)  {
				double dblR = Math.random() * len;
				int intR = (int) Math.floor(dblR);
				char c = strTable.charAt(intR);
				if (('0' <=c ) && (c <= '9')) {
					count++;
				}
				retStr += strTable.charAt(intR);
			}
			if (count >= length) {
				bDone = false;
			}
		} while (bDone);
		return retStr;
	}

}
