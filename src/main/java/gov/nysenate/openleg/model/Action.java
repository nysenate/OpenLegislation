package gov.nysenate.openleg.model;

import gov.nysenate.openleg.lucene.DocumentBuilder;
import gov.nysenate.openleg.util.TextFormatter;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("action")
public class Action extends SenateObject {

    private String id;
    private Date date;
    private String text = "";

    private Bill bill;

    public Action() {
        super();
    }

    public Action (Bill bill, Date eventDate, String eventText) {
        this(bill.getSenateBillNo(), eventDate, eventText);
    }

    public Action(String billNumber, Date eventDate, String eventText) {
        super();
        this.date = eventDate;
        this.text = eventText;

        try {
            this.id = billNumber + "-" + eventDate.getTime() + "-" + URLEncoder.encode(eventText,"utf-8");
        }
        catch (Exception e) { }
    }

    @JsonIgnore
    public String getBillId() {
        return id.substring(0,id.indexOf("-", id.indexOf("-") + 1));
    }


    public String getId() {
        return id;
    }



    public Date getDate() {
        return date;
    }



    public String getText() {
        return text;
    }



    public void setId(String billEventId) {
        this.id = billEventId;
    }



    public void setDate(Date eventDate) {
        this.date = eventDate;
    }



    public void setText(String eventText) {
        this.text = eventText;
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }



    @Override
    public boolean equals(Object obj) {

        if (obj != null && obj instanceof Action)
        {
            Action other = (Action)obj;

            String thisId = TextFormatter.append(
                    this.getText(),"-",
                    this.getDate().getTime());
            String thatId =  TextFormatter.append(
                    other.getText(),"-",
                    other.getDate().getTime());

            return (thisId.equals(thatId));
        }

        return false;
    }

    @JsonIgnore
    @Override
    public HashMap<String, Fieldable> luceneFields() {
        HashMap<String,Fieldable> fields = new HashMap<String,Fieldable>();

        fields.put("when", new Field("when",date.getTime()+"",DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
        fields.put("billno", new Field("billno",getBillId(), DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));

        return fields;
    }

    @JsonIgnore
    @Override
    public String luceneOid() {
        return id;
    }

    @JsonIgnore
    @Override
    public String luceneOsearch() {

        StringBuilder searchContent = new StringBuilder();
        searchContent.append(getBillId()).append(" ");


        searchContent.append(text);

        return text.toString();
    }

    @JsonIgnore
    @Override
    public String luceneOtype() {
        return "action";
    }

    @JsonIgnore
    @Override
    public String luceneSummary() {
        return java.text.DateFormat.getDateInstance(DateFormat.MEDIUM).format(date);
    }

    @JsonIgnore
    @Override
    public String luceneTitle() {
        return text;
    }

    @SuppressWarnings("deprecation")
    @Override
    @JsonIgnore
    public int getYear() {
        if(date != null) {
            return date.getYear();
        }
        return 9999;
    }

    @Override
    @JsonIgnore
    public void merge(ISenateObject obj) {
        return;
    }

    public static class ByEventDate implements Comparator<Action> {

        /*
         * sorted newest to oldest
         */
        @Override
        public int compare(Action be1, Action be2) {
            int ret = be1.getDate().compareTo(be2.getDate());
            if(ret == 0) {
                return -1;
            }
            return ret*-1;
        }

    }

    @Override
    public String toString() {
        return date.toString()+" "+text;
    }
}



