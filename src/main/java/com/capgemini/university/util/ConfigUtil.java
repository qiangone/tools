package com.capgemini.university.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.capgemini.university.api.exception.DataAccessDeniedException;

public class ConfigUtil {
	
	private static final Log logger = LogFactory.getLog(ConfigUtil.class);
	private static Properties props = null;
	
	public static void loadProperties(){	
		
		try {
			InputStream in = ConfigUtil.class.getResourceAsStream("/common.properties");
			props = new Properties();
			props.load(in);
		} catch (IOException e) {
			logger.error("Can not load config file common.properties! "+e);
		}
		
	}
	
	public static String getPropertyValue(String key){
		if(key == null || key.length()==0){
			return null;
		}
		if(props == null){
			loadProperties();
		}
		return props.getProperty(key);
	}
	

	public static String getWDStaticResPath() throws DataAccessDeniedException{
		try{
			String wdappName = ConfigUtil.getPropertyValue("wdapp.app.name");
			String staticResName = ConfigUtil.getPropertyValue("wdapp.static.resource.name");
			String WD_STATIC_RES_PART = System.getProperty("wdapp.root").replace(wdappName, staticResName); 
			return WD_STATIC_RES_PART;
		}catch(Exception e){
			logger.error("getWDStaticResPath exception:" +e);
			throw new DataAccessDeniedException("ConfigUtil.getWDStaticResPath method exception");
		}
		
	}
 

}
