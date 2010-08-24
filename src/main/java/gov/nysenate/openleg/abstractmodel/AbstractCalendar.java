package gov.nysenate.openleg.abstractmodel;

import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.Supplemental;
import gov.nysenate.openleg.util.HideFrom;

import java.util.List;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.Order;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public abstract class AbstractCalendar extends SenateObject {
	
	@Persistent
	@Column(name="year")
	@XStreamAsAttribute
	protected int year;
	
	@Persistent
	@Column(name="type")
	@XStreamAsAttribute
	protected String type;
	
	@Persistent
	@Column(name="session_year")
	@XStreamAsAttribute
	protected int sessionYear;
	
	@Persistent
	@Column(name="no")
	@XStreamAsAttribute
	protected int no;
	
	@Persistent(serialized = "false",defaultFetchGroup="true",mappedBy="calendar")
	@Join
	@Element(dependent = "true")  
	@Order(column="integer_idx")
	protected List<Supplemental> supplementals;
	
	@Persistent
	@PrimaryKey
	@Column(name="id", jdbcType="VARCHAR", length=100)
	@HideFrom({Calendar.class, Supplemental.class})
	protected String id;	
	
	public final static String TYPE_FLOOR = "floor";
	public final static String TYPE_ACTIVE = "active";
}
