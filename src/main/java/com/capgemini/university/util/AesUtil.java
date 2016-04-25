package com.capgemini.university.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AesUtil {

	private static final String ENCRY_KEY = "0102030405060708";
	
	private static Log logger = LogFactory.getLog(AesUtil.class);
	
//	public static String encrypt(String sSrc) throws Exception {
//		try {
//			String sKey = ENCRY_KEY;
//			if (sKey == null) {
//				System.out.print("Key is null");
//				return null;
//			}
//
//			if (sKey.length() != 16) {
//				System.out.print("Key's length not 16");
//				return null;
//			}
//			byte[] raw = sKey.getBytes();
//			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
//			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//			IvParameterSpec iv = new IvParameterSpec(
//					"0102030405060708".getBytes());
//			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
//			byte[] encrypted = cipher.doFinal(sSrc.getBytes());
//			// return encrypted.toString();
//			return Base64.encodeBase64String(encrypted);
//		} catch (Exception e) {
//			logger.error("Encryption Error Message : " + e);
//		}
//		return null;
//
//	}
//
//	public static String decrypt(String sSrc) throws Exception {
//		try {   
//			String sKey = ENCRY_KEY;
//            
//            if (sKey == null) {     
//                System.out.print("Key is null");     
//                return null;     
//            }     
//                
//            if (sKey.length() != 16) {     
//                System.out.print("Key's length not 16");     
//                return null;     
//            }     
//            byte[] raw = sKey.getBytes();     
//            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");     
//            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");     
//            IvParameterSpec iv = new IvParameterSpec("0102030405060708"     
//                    .getBytes());     
//            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);     
//            byte[] encrypted1 =Base64.decodeBase64(sSrc);
//              
//            byte[] original = cipher.doFinal(encrypted1);     
//            String originalString = new String(original);     
//            return originalString;     
//             
//        } catch (Exception ex) {     
//        	logger.error("Decryption Error Message : " + ex); 
//            throw ex;
//        }  
//	}
//
//	public static void main(String[] args) throws Exception {
//		 String Code = "india";
//		 String codE = encrypt(Code);;
//		
//		 System.out.println("原文：" + Code);
//		 System.out.println("密钥：" + ENCRY_KEY);
//		 System.out.println("密文：" + codE);
//		 System.out.println("解密：" + decrypt(codE));
//
//		System.out.println("解密：" + decrypt("zQC05WNaTniiRIL7bluyLg=="));
//		System.out.println("解密：" + decrypt("+8v1Okl5z+M0ZyWEeWnJhg=="));
//	}

}
