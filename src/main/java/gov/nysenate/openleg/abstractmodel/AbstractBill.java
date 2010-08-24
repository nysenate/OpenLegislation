package gov.nysenate.openleg.abstractmodel;

import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.BillEvent;
import gov.nysenate.openleg.model.Person;
import gov.nysenate.openleg.model.Vote;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.Supplemental;
import gov.nysenate.openleg.model.committee.Meeting;
import gov.nysenate.openleg.util.HideFrom;
import gov.nysenate.openleg.xstream.BillListConverter;

import java.util.List;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.Order;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;

public abstract class AbstractBill extends SenateObject {

	@Persistent
	@XStreamAsAttribute
	protected int year;
	
	@Persistent
	@PrimaryKey
	@Column(name="senate_bill_no", jdbcType="VARCHAR", length=20)
	@XStreamAlias("senateId")
	@XStreamAsAttribute
	protected String senateBillNo;	
	
	@Persistent
	@Column(name="title", jdbcType="VARCHAR", length=1000)
	@XStreamAsAttribute
	protected String title;
	
	@Persistent
	@Column(name="law_section")
	@XStreamAsAttribute
	protected String lawSection;
	
	@Persistent
	@Column(name="same_as", jdbcType="VARCHAR", length=256)
	@XStreamAsAttribute
	protected String sameAs;
	
	@Persistent(defaultFetchGroup="true")
	protected Person sponsor;
	
	@Persistent(defaultFetchGroup="true")
	@Join
	@Order(column="integer_idx")
	@XStreamAlias("cosponsors")
	protected List<Person> coSponsors;
	
	@Persistent(defaultFetchGroup="true")
	@XStreamConverter(BillListConverter.class)
	@HideFrom({Meeting.class, Calendar.class, Supplemental.class})
	protected List<Bill> amendments;
	
	@Persistent
	@Column(name="summary", jdbcType="VARCHAR", length=10000)
	protected String summary;
	
	@Persistent
	@Column(name="current_committee")
	@XStreamAlias("committee")
	protected String currentCommittee;
	
	@Persistent(defaultFetchGroup="true")
	@Order(column="bill_events_integer_idx")
	@Element(column="bill_events_senate_bill_no_own")
	@XStreamAlias("actions")
	@XStreamConverter(BillListConverter.class)
	@HideFrom({Meeting.class, Calendar.class, Supplemental.class})
	protected List<BillEvent> billEvents;
	
	@Persistent
	@Column(name="FULLTEXT", jdbcType="LONGVARCHAR", length=250000)
	@XStreamAlias("text")
	@HideFrom({Meeting.class, Calendar.class, Supplemental.class})
	protected String fulltext;
	
	@Persistent
	@Column(name="MEMO", jdbcType="LONGVARCHAR", length=250000)
	@HideFrom({Meeting.class, Calendar.class, Supplemental.class})
	protected String memo;
	
	@Persistent
	@HideFrom({Bill.class,Meeting.class, Calendar.class, Supplemental.class})
	protected String law;
	
	@Persistent
	@Column(name="act_clause", jdbcType="VARCHAR", length=10000)
	@HideFrom({Bill.class,Meeting.class, Calendar.class, Supplemental.class})
	protected String actClause;
	
	@Persistent
	@Column(name="sort_index")
	@HideFrom({Bill.class,Meeting.class, Calendar.class, Supplemental.class})
	protected int sortIndex = -1;
	
	@Persistent(defaultFetchGroup="true",mappedBy="bill")
	@Join
	@Order(column="integer_idx")
	@HideFrom({Bill.class})
	protected List<Vote> votes;

	@Persistent(defaultFetchGroup="true")
	@HideFrom({Bill.class,Meeting.class, Calendar.class, Supplemental.class})
	protected Bill latestAmendment;

}