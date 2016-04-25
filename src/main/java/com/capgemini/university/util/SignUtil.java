package com.capgemini.university.util;

import java.io.InputStream;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.json.JSONObject;


/**
 * 微信接口认证签名
 * 
 * @author deadai
 *
 */
public class SignUtil {
	private static String token = "weixintest";

	public static boolean checkSignature(String signature, String timestamp,
			String nonce) {
		String[] arr = new String[] { token, timestamp, nonce };

		Arrays.sort(arr);
		StringBuilder content = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			content.append(arr[i]);
		}
		MessageDigest md = null;
		String tmpStr = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
			byte[] digest = md.digest(content.toString().getBytes());
			tmpStr = byteToStr(digest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} finally {
			content = null;
		}
		return tmpStr != null ? tmpStr.equals(signature.toUpperCase()) : false;
	}

	public static String getSignature(String jsapi_ticket, String timestamp,
			String nonce, String jsurl) {
		/****
		 * 对jsapi_ticket, timestamp 和 nonce按字典排序
		 * 对所有待签名参数按照字段名的ASCII码从小到大排序（字典序）后，
		 * 使用URL键值对的格式（即key1=value1&key2=value2...）拼接成字符串string1。 所有参数名均为小写字符。
		 * 再对string1 进行SHA1加密， 字段名和字段值URL转义。即signature=sha1(string1)。
		 */
		String[] paramArr = new String[] { "jsapi_ticket=" + jsapi_ticket,
				"timestamp=" + timestamp, "noncestr=" + nonce, "url=" + jsurl };
		Arrays.sort(paramArr);
		// 將排序后的結果拼接成字符串
		String content = paramArr[0].concat("&" + paramArr[1])
				.concat("&" + paramArr[2]).concat("&" + paramArr[3]);
		System.out.println("拼接后的字符串為：" + content);
		String generateSignature = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			// 對拼接后的字符串進行sha1加密
			byte[] digest = md.digest(content.toString().getBytes());
			generateSignature = byteToStr(digest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		// 將加密后的字符串和signature進行對比
		if (generateSignature != null) {
			return generateSignature;
		} else {
			return "false";
		}
	}

	/**
	 * Convert byte array to 16 string
	 * 
	 * @param digest
	 * @return
	 */
	private static String byteToStr(byte[] digest) {
		String strDigest = "";
		for (int i = 0; i < digest.length; i++) {
			strDigest += byteToHexStr(digest[i]);
		}
		return strDigest;
	}

	/**
	 * convert byte to 16
	 * 
	 * @param b
	 * @return
	 */
	private static String byteToHexStr(byte b) {
		char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
				'B', 'C', 'D', 'E', 'F' };
		char[] tmpArr = new char[2];
		tmpArr[0] = Digit[(b >>> 4) & 0X0F];
		tmpArr[1] = Digit[b & 0X0F];
		String s = new String(tmpArr);
		return s;
	}
	
	 public static String getPayCustomSign(Map<String, String> bizObj,String key) throws Exception {
	        String bizString = FormatBizQueryParaMap(bizObj,
	                false);
	        return sign(bizString, key);
	    }
	 
	 public static String FormatBizQueryParaMap(Map<String, String> paraMap,
	            boolean urlencode) throws Exception {
	        String buff = "";
	        try {
	            List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(
	                    paraMap.entrySet());
	            Collections.sort(infoIds,
	                    new Comparator<Map.Entry<String, String>>() {
	                        public int compare(Map.Entry<String, String> o1,
	                                Map.Entry<String, String> o2) {
	                            return (o1.getKey()).toString().compareTo(
	                                    o2.getKey());
	                        }
	                    });
	            for (int i = 0; i < infoIds.size(); i++) {
	                Map.Entry<String, String> item = infoIds.get(i);
	                //System.out.println(item.getKey());
	                if (item.getKey() != "") {
	                    String key = item.getKey();
	                    String val = item.getValue();
	                    if (urlencode) {
	                        val = URLEncoder.encode(val, "utf-8");
	                    }
	                    buff += key + "=" + val + "&";
	                }
	            }
	            if (buff.isEmpty() == false) {
	                buff = buff.substring(0, buff.length() - 1);
	            }
	        } catch (Exception e) {
	            throw new Exception(e.getMessage());
	        }
	        return buff;
	    }
	 
	 public final static String MD5(String s) {
	        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};       
	        try {
	            byte[] btInput = s.getBytes();
	            MessageDigest mdInst = MessageDigest.getInstance("MD5");
	            mdInst.update(btInput);
	            byte[] md = mdInst.digest();
	            int j = md.length;
	            char str[] = new char[j * 2];
	            int k = 0;
	            for (int i = 0; i < j; i++) {
	                byte byte0 = md[i];
	                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
	                str[k++] = hexDigits[byte0 & 0xf];
	            }
	            return new String(str);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	       }
	    }
	 
	 public static String sign(String content, String key)
	            throws Exception{
	        String signStr = "";
	        signStr = content + "&key=" + key;
	        return MD5(signStr).toUpperCase();
	    }
	 
	 public static String ArrayToXml(Map<String, String> arr) {
	        String xml = "<xml>";
	        Iterator<Entry<String, String>> iter = arr.entrySet().iterator();
	        while (iter.hasNext()) {
	            Entry<String, String> entry = iter.next();
	            String key = entry.getKey();
	            String val = entry.getValue();
	            if (IsNumeric(val)) {
	                xml += "<" + key + ">" + val + "</" + key + ">";
	            } else
	                xml += "<" + key + "><![CDATA[" + val + "]]></" + key + ">";
	        }
	        xml += "</xml>";
	        return xml;
	    }
	 
	 public static boolean IsNumeric(String str) {
	        if (str.matches("\\d *")) {
	            return true;
	        } else {
	            return false;
	        }
	    }
	 
	 public static  JSONObject xml2JSON(String res) {  
         JSONObject obj = new JSONObject();  
         try {  
             Document doc = DocumentHelper.parseText(res);  
             Element root = doc.getRootElement();  
             obj.put(root.getName(), iterateElement(root));  
             return obj;  
         } catch (Exception e) {  
             return null;  
         }  
     }  
	 
	 public static  Map parsePayCallback(String res) {  
         try {  
             Document doc = DocumentHelper.parseText(res);  
             Element root = doc.getRootElement(); 
             List jiedian = root.elements() ;
             Element et = null;  
             Map obj = new HashMap();  
             List list = null;  
             for (int i = 0; i < jiedian.size(); i++) {  
                 list = new LinkedList();  
                 et = (Element) jiedian.get(i);  
                 if (et.getTextTrim().equals("")) {  
                     if (et.elements().size() == 0)  
                         continue;  
                     if (obj.containsKey(et.getName())) {  
                         list = (List) obj.get(et.getName());  
                     }  
                     list.add(iterateElement(et));  
                     obj.put(et.getName(), list);  
                 } else {  
                     if (obj.containsKey(et.getName())) {  
                         list = (List) obj.get(et.getName());  
                     }  
                     list.add(et.getTextTrim());  
                     obj.put(et.getName(), list);  
                 }  
             }
             return obj;  
         } catch (Exception e) {  
             return null;  
         }  
     }  
/** 
      * 一个迭代方法 
      *  
      * @param element 
      *            : org.jdom.Element 
      * @return java.util.Map 实例 
      */  
     @SuppressWarnings("unchecked")  
     private static Map  iterateElement(Element element) {  
         List jiedian = element.elements() ;
         Element et = null;  
         Map obj = new HashMap();  
         List list = null;  
         for (int i = 0; i < jiedian.size(); i++) {  
             list = new LinkedList();  
             et = (Element) jiedian.get(i);  
             if (et.getTextTrim().equals("")) {  
                 if (et.elements().size() == 0)  
                     continue;  
                 if (obj.containsKey(et.getName())) {  
                     list = (List) obj.get(et.getName());  
                 }  
                 list.add(iterateElement(et));  
                 obj.put(et.getName(), list);  
             } else {  
                 if (obj.containsKey(et.getName())) {  
                     list = (List) obj.get(et.getName());  
                 }  
                 list.add(et.getTextTrim());  
                 obj.put(et.getName(), list);  
             }  
         }  
         return obj;  
     }  
	 
}
