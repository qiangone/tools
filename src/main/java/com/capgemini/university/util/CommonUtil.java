package com.capgemini.university.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;


public class CommonUtil {
	
	public static String getLogisticsById(String id, String url){
		InputStream is = null;
		try {

						
			int s = url.indexOf("<");
			int e = url.indexOf(">");
			String urlString = url.substring(0, s) + id + url.substring(e + 1);

			URL url1 = new URL(urlString);
			URI uri = new URI(url1.getProtocol(), url1.getHost() + ":"
					+ url1.getPort(), url1.getPath(), url1.getQuery(), null);

			ClientHttpRequest request = new SimpleClientHttpRequestFactory()
					.createRequest(uri, HttpMethod.GET);

			// for json
			request.getHeaders().set("Content-Type", "application/json");
//			request.getHeaders().set("Content-Type", "application/xml");

			ClientHttpResponse response = request.execute();
			int respCode = response.getStatusCode().value();
			if (respCode == 200) {

				// Success
				is = response.getBody();
				
				int size = is.available();
	            byte[] jsonBytes = new byte[size];
	            is.read(jsonBytes);
	            String message = new String(jsonBytes, "GBK");
	            is.close();
	            
	            return message;
//				Scanner inScn = new Scanner(in);
//
//				while (inScn.hasNextLine()) {
//
//					String str = inScn.nextLine();
//					System.out.println(str);
//				}
			}

			return null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ex) {
				}
			}
		}  

		return null;
	}
	
	
	

	
}
