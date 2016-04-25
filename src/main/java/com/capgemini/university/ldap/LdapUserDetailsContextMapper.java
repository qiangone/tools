/* Copyright 2011 CAPGEMINI Financial Service GBU, Inc. All rights
 * reserved.
 * Use is subject to license terms. */

package com.capgemini.university.ldap;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;



public class LdapUserDetailsContextMapper implements UserDetailsContextMapper
{
	
	private static Log logger = LogFactory.getLog(LdapUserDetailsContextMapper.class);
	

	@Override
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username_encry,
            Collection<? extends GrantedAuthority> authorities)
    {
		return null;

    }

    @Override
    public void mapUserToContext(UserDetails user, DirContextAdapter ctx)
    {
        throw new UnsupportedOperationException(
                "LdapUserDetailsContextMapper only supports reading from a context. Please" +
                        "use a subclass if mapUserToContext() is required.");

    }
    
    

}
