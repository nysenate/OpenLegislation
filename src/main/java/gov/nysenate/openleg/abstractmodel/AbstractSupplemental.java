package gov.nysenate.openleg.abstractmodel;

import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.Section;
import gov.nysenate.openleg.model.calendar.Sequence;
import gov.nysenate.openleg.model.calendar.Supplemental;
import gov.nysenate.openleg.util.HideFrom;

import java.util.Date;
import java.util.List;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.Order;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.xml.bind.annotation.XmlTransient;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public abstract class AbstractSupplemental {

	@Persistent
	@PrimaryKey
	@XStreamAsAttribute
	protected String id;
	
	@Persistent
	@Column(name="calendar_date")
	protected Date calendarDate;
	
	@Persistent
	@Column(name="release_date_time")
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
	
}
