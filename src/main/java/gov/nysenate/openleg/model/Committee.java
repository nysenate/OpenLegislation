package gov.nysenate.openleg.model;

import gov.nysenate.openleg.model.committee.Meeting;
import java.util.ArrayList;

import javax.jdo.annotations.Cacheable;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@PersistenceCapable
@XmlRootElement
@Cacheable
@XStreamAlias("committee")
public class Committee  extends SenateObject {

	@Persistent
	private String name;
	
	@Persistent
	private String chair;
	
	private ArrayList<Meeting> meetings;

	/**
	 * @return the name
	 */
	@XmlAttribute
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the chair
	 */
	@XmlAttribute
	public String getChair() {
		return chair;
	}

	/**
	 * @param chair the chair to set
	 */
	public void setChair(String chair) {
		this.chair = chair;
	}

	public void setMeetings(ArrayList<Meeting> meetings) {
		this.meetings = meetings;
	}


	@XmlElementWrapper(name = "meetings")
	@XmlElement(name = "meeting")
	public ArrayList<Meeting> getMeetings() {
		return meetings;
	}

	
	
	
}
