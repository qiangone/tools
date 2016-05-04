package com.capgemini.university.ldap;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.springframework.ldap.core.AttributesMapper;

import com.capgemini.university.model.Participant;

public class ParticipantMapper implements AttributesMapper{

	@Override
	public Participant mapFromAttributes(Attributes attrs) throws NamingException {
		Participant par = new Participant();
		
		if(attrs.get("cn")!=null){
			par.setCnName((String)attrs.get("cn").get());
		}
		if(attrs.get("displayName")!=null){
			par.setDisplayName((String)attrs.get("displayName").get());
		}
		
		if(attrs.get("mail")!=null){
			par.setEmail((String)attrs.get("mail").get());
		}
		
		if(attrs.get("thumbnailPhoto")!=null){
			par.setThumbnailPhoto((byte[])attrs.get("thumbnailPhoto").get());
		}
		
		
		return par;
	}

}
