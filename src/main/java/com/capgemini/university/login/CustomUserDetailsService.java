package com.capgemini.university.login;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.User;  
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;



public class CustomUserDetailsService implements UserDetailsService{
    
    protected static Logger logger = Logger.getLogger("CustomUserDetailsService");
//    
//    @Autowired
//    private ISellerService sellerService;

    @Override
    public UserDetails loadUserByUsername(String phoneNum) throws UsernameNotFoundException {
        UserDetails user = null;  
//        try {  
//            Seller seller = sellerService.getSellerByPhoneNum(phoneNum);
//            user = new User(seller.getPhoneNumber(), seller.getPassword(), true, true, true, true,  
//                    getAuthorities(0));  
//  
//        } catch (Exception e) {  
//            logger.error("Error in retrieving user" + e);  
//            throw new BadCredentialsException("Username or Password error");  
//        }  
        
        return user;  
    }
    
    
    public Collection<GrantedAuthority> getAuthorities(Integer access) {  
        
        List<GrantedAuthority> authList = new ArrayList<GrantedAuthority>(2);  
  
        // 所有的用户默认拥有ROLE_USER权限  
        logger.debug("Grant ROLE_USER to this user");  
        authList.add(new GrantedAuthorityImpl("ROLE_USER"));  
  
        // 如果参数access为1.则拥有ROLE_ADMIN权限  
        if (access.compareTo(1) == 0) {  
            logger.debug("Grant ROLE_ADMIN to this user");  
            authList.add(new GrantedAuthorityImpl("ROLE_ADMIN"));  
        }  
  
        return authList;  
    }  

}
