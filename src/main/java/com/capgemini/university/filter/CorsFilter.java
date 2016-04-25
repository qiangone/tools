
package com.capgemini.university.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;



public class CorsFilter implements Filter
{

  /**
   * @param arg0
   * @throws ServletException
   */
  public void init(FilterConfig arg0) throws ServletException
  {
  }

  /**
   * @param request
   * @param response
   * @param chain
   * @throws IOException
   * @throws ServletException
   */
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException
  {
	  
	  HttpServletResponse httpResponse = (HttpServletResponse)response;
	  httpResponse.setHeader("Access-Control-Allow-Origin", "*");
	  httpResponse.setHeader("Access-Control-Allow-Methods", "*");
	  httpResponse.setHeader("Access-Control-Max-Age", "6011");
	  
	  httpResponse.setHeader("Access-Control-Allow-Headers", "Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With");

	  
	  chain.doFilter(request, response);
  }

  /**
   * 
   */
  public void destroy()
  {
  }

}
