package gov.nysenate.openleg.model.committee;

import gov.nysenate.openleg.model.ISenateObject;
import gov.nysenate.openleg.model.SenateObject;

import java.util.HashMap;
import java.util.List;

import org.apache.lucene.document.Fieldable;
import org.codehaus.jackson.annotate.JsonIgnore;

public class Agenda extends SenateObject {

	private String id;
	
	private int number;
	
	private int sessionYear;

	private int year;
	
	private List<Addendum> addendums;
	
	public Agenda() {
		super();
	}

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
	public HashMap<String, Fieldable> luceneFields() {
		return null;
	}

	@Override
	public void merge(ISenateObject obj) {
		if(!(obj instanceof Agenda))
			return;
		
		super.merge(obj);
		
		if(this.addendums == null || this.addendums.isEmpty()) {
			this.addendums = ((Agenda)obj).getAddendums();
		}
		else {
			if(((Agenda)obj).getAddendums() != null) {
				
				
				for(int i = 0; i < ((Agenda)obj).getAddendums().size(); i++) {
					Addendum addendum = ((Agenda)obj).getAddendums().get(i);
					
					if(this.addendums.contains(addendum)) {
						Addendum tAd = this.addendums.get(this.addendums.indexOf(addendum));
						addendum.setPublicationDateTime(tAd.getPublicationDateTime());
						addendum.setWeekOf(tAd.getWeekOf());

						this.addendums.remove(addendum);
					}
					this.addendums.add(addendum);
				}
			}
		}
		
		this.setId(((Agenda)obj).getId());
		this.setNumber(((Agenda)obj).getNumber());
		this.setSessionYear(((Agenda)obj).getSessionYear());
		this.setYear(((Agenda)obj).getYear());
	}

	
}
