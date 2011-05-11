package gov.nysenate.openleg.model.bill;

import gov.nysenate.openleg.ingest.ISenateObject;
import gov.nysenate.openleg.ingest.SenateObject;
import gov.nysenate.openleg.lucene.DocumentBuilder;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("billevent")
public class BillEvent extends SenateObject {

    private String billEventId;
	private Date eventDate;
	private String eventText;
	
	public BillEvent() {
		super();
	}
	
	public BillEvent (Bill bill, Date eventDate, String eventText)
	{
		this(bill.getSenateBillNo(), eventDate, eventText);
	}
	
	public BillEvent(String billNumber, Date eventDate, String eventText) {
		super();
		this.eventDate = eventDate; 
		this.eventText = eventText;
		
		try
		{
			this.billEventId = billNumber + "-" + eventDate.getTime() + "-" + URLEncoder.encode(eventText,"utf-8");
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
	public HashMap<String, Fieldable> luceneFields() {
		HashMap<String,Fieldable> fields = new HashMap<String,Fieldable>();
		
		fields.put("when", new Field("when",eventDate.getTime()+"",DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
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
	public void merge(ISenateObject obj) {
		return;
	}
	
	public static class ByEventDate implements Comparator<BillEvent> {

		/*
		 * sorted newest to oldest
		 */
		@Override
		public int compare(BillEvent be1, BillEvent be2) {
			int ret = be1.getEventDate().compareTo(be2.getEventDate());
			if(ret == 0) {
				return -1;
			}
			return ret*-1;
		}
		
	}
}
	
	

