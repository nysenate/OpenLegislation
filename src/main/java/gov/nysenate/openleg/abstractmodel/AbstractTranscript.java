package gov.nysenate.openleg.abstractmodel;

import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Transcript;
import gov.nysenate.openleg.util.HideFrom;

import java.util.Date;
import java.util.List;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.Order;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public abstract class AbstractTranscript {
	
	@Persistent(valueStrategy=IdGeneratorStrategy.INCREMENT)
	@PrimaryKey
	@XStreamAsAttribute
	protected String id;
	
	@Persistent
	@Column(name="time_stamp")
	protected Date timeStamp;
	
	@Persistent
	@Column(name="location")
	protected String location;
	
	@Persistent
	protected String type;
	
	@Persistent
	@Column(name="transcriptText", jdbcType="LONGVARCHAR", length=250000)
	@XStreamAlias("text")
	protected String transcriptText;
	
	@Persistent
	@Column(name="transcriptTextProcessed", jdbcType="LONGVARCHAR", length=250000)
	@HideFrom({Transcript.class})
	protected String transcriptTextProcessed;
	
	@Persistent(serialized = "false",defaultFetchGroup="true")
	@Join	
	@Element(dependent = "false")  
	@Order(column="integer_idx")
	@HideFrom({Transcript.class})
	protected List<Bill> relatedBills;
	
	/*
	@Persistent(serialized = "false",defaultFetchGroup="true")
	@Join
	@Order(column="integer_idx")
	@Element(dependent = "false")  
	protected List<Tag> tags;
	*/
}
