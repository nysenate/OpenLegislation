package gov.nysenate.openleg.model.committee;


import ingest.SenateObject;

import java.util.HashMap;
import java.util.List;

import org.apache.lucene.document.Field;
import org.codehaus.jackson.annotate.JsonIgnore;

public class Agenda implements SenateObject{

	private String id;
	
	private int number;
	
	private int sessionYear;

	private int year;
	
	private List<Addendum> addendums;

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public List<Addendum> getAddendums() {
		return addendums;
	}

	public void setAddendums(List<Addendum> addendums) {
		this.addendums = addendums;
	}

	public int getSessionYear() {
		return sessionYear;
	}

	public void setSessionYear(int sessionYear) {
		this.sessionYear = sessionYear;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@JsonIgnore
	public Meeting getCommitteeMeeting(String id) {
		for(Addendum addendum:this.getAddendums()) {
			for(Meeting meeting:addendum.getMeetings()) {
				if(id.equals(meeting.getId())) {
					return meeting;
				}
			}
		}
		return null;
	}
	
	public void removeCommitteeMeeting(Meeting meeting) {
		for(Addendum addendum:this.getAddendums()) {
			addendum.removeMeeting(meeting);
		}
	}

	@Override
	public boolean equals(Object obj) {
		
		if (obj != null && obj instanceof Agenda)
		{
			if ( ((Agenda)obj).getId().equals(this.getId()))
				return true;
		}
		
		return false;
	}

	@Override
	public String luceneOid() {
		return this.id;
	}

	@Override
	public String luceneOsearch() {
		return null;
	}

	@Override
	public String luceneOtype() {
		return "agenda";
	}

	@Override
	public String luceneSummary() {
		return null;
	}

	@Override
	public String luceneTitle() {
		return null;
	}

	@Override
	public HashMap<String, Field> luceneFields() {
		return null;
	}

	@Override
	public void merge(SenateObject obj) {
		if(!(obj instanceof Agenda))
			return;
		
		if(this.addendums == null || this.addendums.isEmpty()) {
			this.addendums = ((Agenda)obj).getAddendums();
		}
		else {
			if(((Agenda)obj).getAddendums() != null) {
				this.addendums = ((Agenda)obj).getAddendums();
			}
		}
		
		this.setId(((Agenda)obj).getId());
		this.setNumber(((Agenda)obj).getNumber());
		this.setSessionYear(((Agenda)obj).getSessionYear());
		this.setYear(((Agenda)obj).getYear());
	}

	
}
