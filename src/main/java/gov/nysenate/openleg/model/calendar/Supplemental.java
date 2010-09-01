package gov.nysenate.openleg.model.calendar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.jdo.annotations.Cacheable;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.Order;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.apache.lucene.document.Field;

import gov.nysenate.openleg.PMF;
import gov.nysenate.openleg.lucene.DocumentBuilder;
import gov.nysenate.openleg.lucene.LuceneField;
import gov.nysenate.openleg.lucene.LuceneObject;
import gov.nysenate.openleg.model.SenateObject;
import gov.nysenate.openleg.util.HideFrom;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
@XmlRootElement
@Cacheable
@XStreamAlias("supplemental")
public class Supplemental  extends SenateObject implements LuceneObject {
	
	@Persistent
	@PrimaryKey
	@XStreamAsAttribute
	protected String id;
	
	@Persistent
	@Column(name="calendar_date")
	@LuceneField("when")
	protected Date calendarDate;
	
	@Persistent
	@Column(name="release_date_time")
	@LuceneField("releasedate")
	protected Date releaseDateTime;
	
	@Persistent(serialized = "false",defaultFetchGroup="true",mappedBy="supplemental")
	@Join
	@Element(dependent = "true") 
	@Order(column="integer_idx")
	protected List<Section> sections;
	
	@Persistent(serialized = "false",dependent = "true",defaultFetchGroup="true",mappedBy="supplemental")
	protected Sequence sequence;
	
	@Persistent
	@Column(name="supplemental_id")	
	@HideFrom({Calendar.class, Supplemental.class})
	protected String supplementalId;
	
	@Persistent
	@XmlTransient
	@Element(dependent = "false")  
	@HideFrom({Calendar.class, Supplemental.class})
	protected Calendar calendar;
	
	/**
	 * @return the sequence
	 */
	public Sequence getSequence() {
		return sequence;
	}

	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(Sequence sequence) {
		this.sequence = sequence;
	}

	/**
	 * @return the calendarDate
	 */
	public Date getCalendarDate() {
		return calendarDate;
	}

	/**
	 * @param calendarDate the calendarDate to set
	 */
	public void setCalendarDate(Date calendarDate) {
		this.calendarDate = calendarDate;
	}

	/**
	 * @return the releaseDateTime
	 */
	public Date getReleaseDateTime() {
		return releaseDateTime;
	}

	/**
	 * @param releaseDateTime the releaseDateTime to set
	 */
	public void setReleaseDateTime(Date releaseDateTime) {
		this.releaseDateTime = releaseDateTime;
	}

	/**
	 * @return the supplementalId
	 */
	 @XmlAttribute
	public String getSupplementalId() {
		return supplementalId;
	}

	/**
	 * @param supplementalId the supplementalId to set
	 */
	public void setSupplementalId(String supplementalId) {
		this.supplementalId = supplementalId;
	}

	/**
	 * @return the sections
	 */
	@XmlElementWrapper(name = "sections")
	@XmlElement(name = "section")
	public List<Section> getSections() {
		return sections;
	}

	/**
	 * @param sections the sections to set
	 */
	public void setSections(List<Section> sections) {
		this.sections = sections;
	}
	
	/**
	 * @return the calendar
	 */
	 @XmlTransient
	public Calendar getCalendar() {
		return calendar;
	}

	/**
	 * @param calendar the calendar to set
	 */
	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	/**
	 * @return the id
	 */
	@XmlTransient
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
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
		return "";
	}

	@Override
	public String luceneOtype() {
		return "calendar";
	}

	@Override
	public HashMap<String,Field> luceneFields() {
		HashMap<String,Field> fields = new HashMap<String,Field>();
		
		Calendar calendar = (Calendar)PMF.getDetachedObject(Calendar.class, "id", id.split("-supp")[0], "no descending");
		
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
		else if (sequence!=null)
		{
			title += " - " + java.text.DateFormat.getDateInstance(DateFormat.MEDIUM).format(sequence.getActCalDate());
		}
		
		searchContent.append(title);
		
		StringBuilder sbSummary = new StringBuilder();
		
		if (sections != null) {
			Iterator<Section> itSections = sections.iterator();
			while (itSections.hasNext()) {
				Section section = itSections.next();
				sbSummary.append(section.getName()).append(": ");
				sbSummary.append(section.getCalendarEntries().size()).append(" bill(s); ");
			}
		}
		
		if (sequence != null) {
			if (sequence.getNotes()!=null)
				sbSummary.append(sequence.getNotes());
			
			sbSummary.append(" ").append(sequence.getCalendarEntries().size()).append(" bill(s)");
			
		}
		
		String summary = sbSummary.toString().trim();
		
		fields.put("summary",new Field("summary",summary, DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
		fields.put("title",new Field("title",title, DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
		fields.put("osearch",new Field("osearch",searchContent.toString(), DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
		
		String oid = "";
		if(calendar.getId().startsWith("cal-floor")) {
			oid = "floor-" + new SimpleDateFormat("MM-dd-yyyy").format(releaseDateTime);
			fields.put("when", new Field("when",releaseDateTime.getTime()+"", DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
		}
		else {
			oid = "active-" + new SimpleDateFormat("MM-dd-yyyy").format(sequence.getActCalDate());
			fields.put("when", new Field("when",sequence.getActCalDate().getTime()+"", DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
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
/*
<supplemental id="">
<caldate>2009-11-05</caldate>
<releasedate>2009-11-10</releasedate>
<releasetime>T11.23.00Z</releasetime>
<sections>
<section name="BILLS ON ORDER OF FIRST REPORT" type="C" cd="0150">
<calnos>
<calno no="0000000002">
<bill no="S00430A" />
<sponsor><![CDATA[ROBACH]]></sponsor>
<subbill no="" />
<subsponsor></subsponsor>
</calno>
*/