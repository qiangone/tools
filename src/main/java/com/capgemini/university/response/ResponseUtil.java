package com.capgemini.university.response;

public class ResponseUtil {
	public static WDResponse apiSuccess(Object content,String msg) {
		WDResponse response = new WDResponse();
		WDResult result = new WDResult();
		result.setContent(content);
		response.setResult(result);
		
		//access code info
		Info info = new Info();
		info.setCode("200");
		info.setMsg(msg);
		response.setInfo(info);
		return response;
	}
	
	public static WDResponse apiError(String code,String msg) {
		WDResponse response = new WDResponse();
		WDResult nullresult = new WDResult();
		nullresult.setContent(null);
//		response.setResult("false");
		response.setResult(nullresult);
		
		//access code info
		Info info = new Info();
		info.setCode(code);
		info.setMsg(msg);
		response.setInfo(info);
		return response;
	}
	
	public static boolean isNotNullString(String string) {
		if (string == null || string.equals("")) {
			return false;
		} else {
			return true;
		}
	}
}
