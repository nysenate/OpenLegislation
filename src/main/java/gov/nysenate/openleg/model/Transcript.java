package gov.nysenate.openleg.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
import org.apache.lucene.document.Field;

import gov.nysenate.openleg.lucene.DocumentBuilder;
import gov.nysenate.openleg.lucene.LuceneField;
import gov.nysenate.openleg.lucene.LuceneObject;
import gov.nysenate.openleg.model.SenateObject;
import gov.nysenate.openleg.util.HideFrom;

@PersistenceCapable
@XmlRootElement
@Cacheable
@XStreamAlias("transcript")
public class Transcript  extends SenateObject implements LuceneObject {

	@Persistent(valueStrategy=IdGeneratorStrategy.INCREMENT)
	@PrimaryKey
	@XStreamAsAttribute
	protected String id;
	
	@Persistent
	@Column(name="time_stamp")
	@LuceneField("when")
	protected Date timeStamp;
	
	@Persistent
	@Column(name="location")
	@LuceneField
	protected String location;
	
	@Persistent
	@LuceneField("session-type")
	protected String type;
	
	@Persistent
	@Column(name="transcriptText", jdbcType="LONGVARCHAR", length=250000)
	@XStreamAlias("full")
	@LuceneField("full")
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
	@LuceneField
	protected List<Bill> relatedBills;
	
	/*
	@Persistent(serialized = "false",defaultFetchGroup="true")
	@Join
	@Order(column="integer_idx")
	@Element(dependent = "false")
	@LuceneField  
	protected List<Tag> tags;
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
	
	@Override
	public String luceneOid() {
		return (type.replaceAll(" ", "-").toLowerCase())+((timeStamp != null) ? "-"+ new SimpleDateFormat("MM-dd-yyyy").format(timeStamp):"") + "-" + id;
	}
	
	@Override
	public String luceneOsearch() {
		return transcriptText;
	}

	@Override
	public String luceneOtype() {
		return "transcript";
	}
	
	
	@Override
	public String luceneSummary() {
		return location;
	}
	
	@Override
	public String luceneTitle() {
		return type;
	}

	@Override
	public HashMap<String,Field> luceneFields() {
		HashMap<String,Field> fields = new HashMap<String,Field>();
		fields.put("when", new Field("when",(timeStamp == null) ? "" : timeStamp.getTime()+"", DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
		return fields;
	}

	public String getLuceneRelatedBills() {
		StringBuilder response = new StringBuilder();
		for(Bill bill : relatedBills) {
			response.append(bill.getSenateBillNo() + ", ");
		}
		return response.toString().replaceAll(", $", "");
	}
}
