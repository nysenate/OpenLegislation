package gov.nysenate.openleg.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.Cacheable;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.Order;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.xml.bind.annotation.XmlRootElement;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@PersistenceCapable
@XmlRootElement
@Cacheable
@XStreamAlias("transcript")
public class Transcript  extends SenateObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3484474976358992284L;

	@Persistent(valueStrategy=IdGeneratorStrategy.INCREMENT)
	@PrimaryKey
	@XStreamAsAttribute
	private String id;
	
	@Persistent
	@Column(name="time_stamp")
	private Date timeStamp;
	
	@Persistent
	@Column(name="location")
	private String location;
	
	@Persistent
	private String type;
	
	@Persistent
	@Column(name="transcriptText", jdbcType="LONGVARCHAR", length=250000)
	@XStreamAlias("text")
	private String transcriptText;
	
	@Persistent
	@Column(name="transcriptTextProcessed", jdbcType="LONGVARCHAR", length=250000)
	private String transcriptTextProcessed;


	
	
	@Persistent(serialized = "false",defaultFetchGroup="true")
	@Join	
	@Element(dependent = "false")  
	@Order(column="integer_idx")
	private List<Bill> relatedBills;
	
	/*
	@Persistent(serialized = "false",defaultFetchGroup="true")
	@Join
	@Order(column="integer_idx")
	@Element(dependent = "false")  
	private List<Tag> tags;
	*/
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the timeStamp
	 */
	public Date getTimeStamp() {
		return timeStamp;
	}

	/**
	 * @param timeStamp the timeStamp to set
	 */
	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return the transcriptText
	 */
	public String getTranscriptText() {
		return transcriptText;
	}

	/**
	 * @param transcriptText the transcriptText to set
	 */
	public void setTranscriptText(String transcriptText) {
		this.transcriptText = transcriptText;
	}

	/**
	 * @return the relatedBills
	 */
	public List<Bill> getRelatedBills() {
		return relatedBills;
	}

	/**
	 * @param relatedBills the relatedBills to set
	 */
	public void setRelatedBills(List<Bill> relatedBills) {
		this.relatedBills = relatedBills;
	}

	
	/**
	 * @return the transcriptTextProcessed
	 */
	public String getTranscriptTextProcessed() {
		return transcriptTextProcessed;
	}

	/**
	 * @param transcriptTextProcessed the transcriptTextProcessed to set
	 */
	public void setTranscriptTextProcessed(String transcriptTextProcessed) {
		this.transcriptTextProcessed = transcriptTextProcessed;
	}
	
}
