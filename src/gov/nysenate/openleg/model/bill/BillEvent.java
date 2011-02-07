package gov.nysenate.openleg.model.bill;

import gov.nysenate.openleg.lucene.DocumentBuilder;

import ingest.SenateObject;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.lucene.document.Field;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("billevent")
public class BillEvent implements SenateObject {

    private String billEventId;
	private Date eventDate;
	private String eventText;
	
	public BillEvent() {
		
	}
	
	public BillEvent (Bill bill, Date eventDate, String eventText)
	{
		this.eventDate = eventDate; 
		this.eventText = eventText;
		
		try
		{
			this.billEventId = bill.getSenateBillNo() + "-" + eventDate.getTime() + "-" + URLEncoder.encode(eventText,"utf-8");
		}
		catch (Exception e)
		{
			//foo
		}
	}
	
	@JsonIgnore
	public String getBillId() {
		return billEventId.substring(0,billEventId.indexOf("-", billEventId.indexOf("-") + 1));
	}
	
	
	public String getBillEventId() {
		return billEventId;
	}



	public Date getEventDate() {
		return eventDate;
	}



	public String getEventText() {
		return eventText;
	}



	public void setBillEventId(String billEventId) {
		this.billEventId = billEventId;
	}



	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}



	public void setEventText(String eventText) {
		this.eventText = eventText;
	}



	@Override
	public boolean equals(Object obj) {
		
		if (obj != null && obj instanceof BillEvent)
		{
			String thisId = getBillEventId();
			String thatId =  ((BillEvent)obj).getBillEventId();
			
			return (thisId.equals(thatId));
		}
		
		return false;
	}
	
	@JsonIgnore
	@Override
	public HashMap<String, Field> luceneFields() {
		HashMap<String,Field> fields = new HashMap<String,Field>();
		
		
		fields.put("when", new Field("when",eventDate.getTime()+"", DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
		fields.put("billno", new Field("billno",getBillId(), DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
			
		
		return fields;
	}

	@JsonIgnore
	@Override
	public String luceneOid() {
		return billEventId;
	}

	@JsonIgnore
	@Override
	public String luceneOsearch() {
		
		
		StringBuilder searchContent = new StringBuilder();
		searchContent.append(getBillId()).append(" ");
		
		
		searchContent.append(eventText);
		
		return eventText.toString();
	}

	@JsonIgnore
	@Override
	public String luceneOtype() {
		return "action";
	}

	@JsonIgnore
	@Override
	public String luceneSummary() {
		return java.text.DateFormat.getDateInstance(DateFormat.MEDIUM).format(eventDate);
	}

	@JsonIgnore
	@Override
	public String luceneTitle() {
		return eventText;
	}

	@SuppressWarnings("deprecation")
	@Override
	@JsonIgnore
	public int getYear() {
		if(eventDate != null) {
			return eventDate.getYear();
		}
		return 9999;
	}

	@Override
	@JsonIgnore
	public void merge(SenateObject obj) {
		return;
	}
}
	
	

