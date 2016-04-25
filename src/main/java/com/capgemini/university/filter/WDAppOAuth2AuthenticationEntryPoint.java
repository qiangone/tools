/**
 * CAPGEMINI APPLIANCE CHAINS.
 * Copyright (c) 2015-2015 All Rights Reserved.
 */
package com.capgemini.university.filter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;

/**
 * 
 * functional description： XXXXXX
 * @author  zhihuang@capgemini.com
 * @created Dec 21, 2015 11:31:04 AM
 * @date Dec 21, 2015 11:31:04 AM
 */

public class WDAppOAuth2AuthenticationEntryPoint extends OAuth2AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
       
        super.commence(request, response, authException);
    }
    
    

}
