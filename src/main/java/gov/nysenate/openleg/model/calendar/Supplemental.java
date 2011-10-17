package gov.nysenate.openleg.model.calendar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.codehaus.jackson.annotate.JsonIgnore;

import gov.nysenate.openleg.lucene.DocumentBuilder;
import gov.nysenate.openleg.lucene.LuceneField;
import gov.nysenate.openleg.model.SenateObject;

@XStreamAlias("supplemental")
public class Supplemental extends SenateObject {
	
	protected String id;
	
	@LuceneField("when")
	protected Date calendarDate;
	
	@LuceneField("releasedate")
	protected Date releaseDateTime;
	
	protected List<Section> sections;
	
	protected List<Sequence> sequences;
	
	protected String supplementalId;
	
	protected Calendar calendar;
	
	public void addSequence(Sequence sequence) {
		if(sequences == null) {
			sequences = new ArrayList<Sequence>();
		}
		else {
			sequences.remove(sequence);
		}
		
		sequences.add(sequence);
	}
	
	public List<Sequence> getSequences() {
		return sequences;
	}

	public void setSequences(List<Sequence> sequences) {
		this.sequences = sequences;
	}

	public Date getCalendarDate() {
		return calendarDate;
	}

	public void setCalendarDate(Date calendarDate) {
		this.calendarDate = calendarDate;
	}

	public Date getReleaseDateTime() {
		return releaseDateTime;
	}

	public void setReleaseDateTime(Date releaseDateTime) {
		this.releaseDateTime = releaseDateTime;
	}

	public String getSupplementalId() {
		return supplementalId;
	}

	public void setSupplementalId(String supplementalId) {
		this.supplementalId = supplementalId;
	}

	public List<Section> getSections() {
		return sections;
	}

	public void setSections(List<Section> sections) {
		this.sections = sections;
	}
	
	@JsonIgnore
	public Calendar getCalendar() {
		return calendar;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean equals(Object obj) {
		
		if (obj != null && obj instanceof Supplemental)
		{
			if ( ((Supplemental)obj).getId().equals(this.getId()))
				return true;
		}
		
		return false;
	}

	
	@Override
	public String luceneOid() {
		String oid = "";
		if(calendar.getId().startsWith("cal-floor")) {
			oid = "floor-" + new SimpleDateFormat("MM-dd-yyyy").format(this.getCalendarDate());

		}
		else {
			if(sequences != null && sequences.size() > 0) {
				oid = "active-" + new SimpleDateFormat("MM-dd-yyyy").format(sequences.get(0).getActCalDate());
			}
		}
		
		return oid;
	}

	@Override
	public String luceneOtype() {
		return "calendar";
	}

	@Override
	public HashMap<String, Fieldable> luceneFields() {
		HashMap<String,Fieldable> fields = new HashMap<String,Fieldable>();
		
		Calendar calendar = this.getCalendar();
		
		fields.put("ctype",new Field("ctype",calendar.getType(), DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
				
		StringBuilder searchContent = new StringBuilder();
		String title;
		
		title = calendar.getNo() + " - " + calendar.getType();
		
		if (calendarDate!=null)
			title += " - " + java.text.DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendarDate);
		
		
		else if (releaseDateTime!=null)
		{
			title += " - " + java.text.DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendarDate);
		}
		else if (sequences!=null)
		{
			if(sequences != null && sequences.size() > 0) {
				title += " - " + java.text.DateFormat.getDateInstance(DateFormat.MEDIUM).format(sequences.get(0).getActCalDate());
			}
		}
		
		StringBuilder bills = new StringBuilder("");
		
		searchContent.append(title);
		
		StringBuilder sbSummary = new StringBuilder();
		
		if (sections != null) {
			Iterator<Section> itSections = sections.iterator();
			while (itSections.hasNext()) {
				Section section = itSections.next();
				sbSummary.append(section.getName()).append(": ");
				sbSummary.append(section.getCalendarEntries().size()).append(" bill(s); ");
				
				for(CalendarEntry ce:section.getCalendarEntries()) {
					bills.append(ce.getBill().getSenateBillNo() + ", ");
				}
			}
		}
		
		if (sequences != null) {
			if(sequences != null && sequences.size() > 0) {
				Sequence sequence = sequences.get(0);
				
				if (sequence.getNotes()!=null)
					sbSummary.append(sequence.getNotes());
				
				sbSummary.append(" ").append(sequence.getCalendarEntries().size()).append(" bill(s)");
				
				for(CalendarEntry ce:sequence.getCalendarEntries()) {
					bills.append(ce.getBill().getSenateBillNo() + ", ");
				}
			}
		}
		
		fields.put("bills",new Field("bills",bills.toString(), DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
		
		String summary = sbSummary.toString().trim();
		
		fields.put("summary",new Field("summary",summary, DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
		fields.put("title",new Field("title",title, DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
		fields.put("osearch",new Field("osearch",searchContent.toString(), DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
		
		String oid = "";
		if(calendar.getId().startsWith("cal-floor")) {
			oid = "floor-" + new SimpleDateFormat("MM-dd-yyyy").format(this.getCalendarDate());
			fields.put("when", new Field("when",this.getCalendarDate().getTime()+"",DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));

		}
		else {
			if(sequences != null && sequences.size() > 0) {
				Sequence sequence = sequences.get(0);
				
				oid = "active-" + new SimpleDateFormat("MM-dd-yyyy").format(sequence.getActCalDate());
				fields.put("when", new Field("when",sequence.getActCalDate().getTime()+"",DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
			}
		}
				
		fields.put("oid",new Field("oid",oid, DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
				
		return fields;
	}
	
	@Override
	public String luceneOsearch() {
		return "";
	}

	@Override
	public String luceneTitle() {
		return "";
	}

	@Override
	public String luceneSummary() {
		return "";
	}
}