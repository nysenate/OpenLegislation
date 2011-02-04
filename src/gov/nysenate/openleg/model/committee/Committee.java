package gov.nysenate.openleg.model.committee;

import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("committee")
public class Committee {

	private String name;
	
	private String chair;
	
	private ArrayList<Meeting> meetings;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getChair() {
		return chair;
	}

	public void setChair(String chair) {
		this.chair = chair;
	}

	public void setMeetings(ArrayList<Meeting> meetings) {
		this.meetings = meetings;
	}

	@JsonIgnore
	public ArrayList<Meeting> getMeetings() {
		return meetings;
	}

	
	
	
}
