package com.capgemini.university.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.capgemini.university.model.Lbps;
import com.capgemini.university.model.Sbu;
import com.capgemini.university.model.SbuLbps;

@Repository
public interface SbuDao {

	public List<Sbu> getSbu(Map map);
	
	public List<SbuLbps> getSbuLbps(Map map);

	public void updateSbu(Sbu sbu);

	public void addSbu(Sbu sbu);

	public void addLbps(Lbps lbps);

	public void updateLbps(Lbps lbps);

	public void addSbuLbps(SbuLbps sb);
	
	public void updateSbuLbps(SbuLbps sb);
	
	
	
	public List<Lbps> getLbps(Map map);
	
	

	public List<Map> countSeatsOfOtherSbu(Map map);

	public List<Map> countSwapSeatsOfOtherSbu(Map map);

	public List<Map> countSeatsOfSubSbu(Map map);

	public List<Map> countParticipantsOfSbu(Map map);
	
	public List<Sbu> countParticipantsByCourse(Map map);
	
	public Map countParticipantAndPmds(Map map);
	
	public void convertToFreeSeats();

}
