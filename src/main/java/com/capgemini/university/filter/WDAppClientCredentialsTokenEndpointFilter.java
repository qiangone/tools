package com.capgemini.university.filter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.StringUtils;

/**
 * 
 * @author zhihuang
 *
 */
public class WDAppClientCredentialsTokenEndpointFilter extends
		ClientCredentialsTokenEndpointFilter {
	
	private AuthenticationEntryPoint authenticationEntryPoint = new OAuth2AuthenticationEntryPoint();
	
	
	public WDAppClientCredentialsTokenEndpointFilter(){
		super("/oauth/token");
		// If authentication fails the type is "Form"
		((OAuth2AuthenticationEntryPoint)authenticationEntryPoint).setTypeName("Form");
	}
	
	public void setAuthenticationEntryPoint(AuthenticationEntryPoint authenticationEntryPoint) {
		this.authenticationEntryPoint = authenticationEntryPoint;
	}
	
	@Override
	public void afterPropertiesSet() {
		super.afterPropertiesSet();
		setAuthenticationFailureHandler(new AuthenticationFailureHandler() {
			public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
					AuthenticationException exception) throws IOException, ServletException {
				if (exception instanceof BadCredentialsException) {
				//	exception = new BadCredentialsException(exception.getMessage(), new BadClientCredentialsException());
				}
				authenticationEntryPoint.commence(request, response, exception);
			}
		});
		setAuthenticationSuccessHandler(new AuthenticationSuccessHandler() {
			public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
					Authentication authentication) throws IOException, ServletException {
				// no-op - just allow filter chain to continue to token endpoint
			}
		});
	}
	
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {

		String clientId = request.getParameter("client_id");
		String clientSecret = request.getParameter("client_secret");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
			throw new BadCredentialsException("Username or Password is null");
		}

		// If the request is already authenticated we can assume that this filter is not needed
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()) {
			return authentication;
		}
		
		if (clientId == null) {
			throw new BadCredentialsException("No client credentials presented");
		}

		if (clientSecret == null) {
			clientSecret = "";
		}

		clientId = clientId.trim();
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(clientId,
				clientSecret);

		return this.getAuthenticationManager().authenticate(authRequest);

	}
}
