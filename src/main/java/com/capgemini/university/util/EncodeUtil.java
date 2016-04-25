package com.capgemini.university.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import sun.misc.BASE64Encoder;

public class EncodeUtil {
	
	public static String getsss(){
		try {  
            MessageDigest md = MessageDigest.getInstance("md5");  
            byte md5[] = md.digest("fasfsafd".getBytes());  
            // base64编码--任意二进制编码明文字符 adfsdfsdfsf  
            BASE64Encoder encoder = new BASE64Encoder();  
            return encoder.encode(md5);  
        } catch (NoSuchAlgorithmException e) {  
            throw new RuntimeException(e);  
        }  
	}
	
	public static void main(String[] args){
		System.out.print(getsss());
	}
}
