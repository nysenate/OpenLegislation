package gov.nysenate.openleg.model.calendar;

import ingest.SenateObject;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import org.apache.lucene.document.Field;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("calendar")
public class Calendar implements SenateObject {

	@XStreamAsAttribute
	protected int year;
	
	@XStreamAsAttribute
	protected String type;
	
	@XStreamAsAttribute
	protected int sessionYear;
	
	@XStreamAsAttribute
	protected int no;
	
//	@HideFrom({Calendar.class, Suppplemental.class})
	protected List<Supplemental> supplementals;
	
	protected String id;	
	
	public final static String TYPE_FLOOR = "floor";
	
	public final static String TYPE_ACTIVE = "active";

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public List<Supplemental> getSupplementals() {
		return supplementals;
	}

	public void setSupplementals(List<Supplemental> supplementals) {
		this.supplementals = supplementals;
	}
	
	@JsonIgnore
	public void addSupplemental(Supplemental supplemental) {
		if(supplementals ==  null) {
			supplementals = new ArrayList<Supplemental>();
		}
		
		int index = -1;
		if((index = supplementals.indexOf(supplemental)) != -1) {
			supplementals.remove(index);
		}
		supplementals.add(supplemental);
	}

	
	@Override
	public boolean equals(Object obj) {
		
		if (obj != null && obj instanceof Calendar)
		{
			if ( ((Calendar)obj).getId().equals(this.getId()))
				return true;
		}
		
		return false;
	}
	
	
	@Override
	public String luceneOid() {
		return id;
	}

	@Override
	public String luceneOsearch() {
		return null;
	}

	@Override
	public String luceneOtype() {
		return "calendar";
	}
	
	@Override
	public HashMap<String,Field> luceneFields() {
		return null;
	}

	@Override
	public String luceneTitle() {
		return null;
	}

	@Override
	public String luceneSummary() {
		return null;
	}
	
	@Override
	public void merge(SenateObject obj) {
		if(!(obj instanceof Calendar))
			return;
		
		this.setId(((Calendar)obj).getId());
		this.setNo(((Calendar)obj).getNo());
		this.setSessionYear(((Calendar)obj).getSessionYear());		
		
		if(this.supplementals == null || this.supplementals.isEmpty()) {
			this.supplementals = ((Calendar)obj).getSupplementals();
		}
		else {
			if(((Calendar)obj).getSupplementals() != null) {
				this.supplementals =  ((Calendar)obj).getSupplementals();
				
//				System.out.println(this.supplementals.size());
//				for(Supplemental supp:this.supplementals) {
//					System.out.println(supp.getReleaseDateTime());
//				}
//				
//				System.out.println(((Calendar)obj).getSupplementals().size());
//				for(Supplemental supp:((Calendar)obj).getSupplementals()) {
//					System.out.println(supp.getReleaseDateTime());
//				}
			}
		}

		this.setType(((Calendar)obj).getType());
		this.setYear(((Calendar)obj).getYear());
	}
}
