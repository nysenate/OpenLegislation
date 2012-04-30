package gov.nysenate.openleg.model;

import gov.nysenate.openleg.lucene.DocumentBuilder;
import gov.nysenate.openleg.lucene.LuceneField;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("meeting")
@XmlRootElement
public class Meeting extends SenateObject {

    @LuceneField("when")
    protected Date meetingDateTime;

    protected String meetday;

    @LuceneField
    protected String location;

    protected String id;

    @LuceneField("committee")
    protected String committeeName;

    @LuceneField("chair")
    protected String committeeChair;

    @LuceneField
    protected List<Bill> bills;

    @LuceneField
    protected String notes;

    @LuceneField
    protected List<Addendum> addendums;

    public Meeting() {
        addendums = new ArrayList<Addendum>();
        bills = new ArrayList<Bill>();
    }

    public Meeting(String id) {
        this.setId(id);
        addendums = new ArrayList<Addendum>();
        bills = new ArrayList<Bill>();
    }

    public String getCommitteeChair() {
        return committeeChair;
    }

    public void setCommitteeChair(String committeeChair) {
        this.committeeChair = committeeChair;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonIgnore
    public List<Addendum> getAddendums() {
        return addendums;
    }

    public void setAddendums(List<Addendum> addendums) {
        this.addendums = addendums;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMeetday() {
        return meetday;
    }

    public void setMeetday(String meetday) {
        this.meetday = meetday;
    }


    public Date getMeetingDateTime() {
        return meetingDateTime;
    }

    public void setMeetingDateTime(Date meetingDateTime) {
        this.meetingDateTime = meetingDateTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<Bill> getBills() {
        return bills;
    }

    public void setBills(List<Bill> bills) {
        this.bills = bills;
    }

    public String getCommitteeName() {
        return committeeName;
    }

    public void setCommitteeName(String committeeName) {
        this.committeeName = committeeName;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj != null && obj instanceof Meeting)
        {
            if ( ((Meeting)obj).getId().equals(this.getId()))
                return true;
        }

        return false;
    }


    @Override
    public String luceneOid() {
        return committeeName+"-"+new SimpleDateFormat("MM-dd-yyyy").format(meetingDateTime);
    }

    @Override
    public String luceneOsearch() {
        return committeeName + " - " + committeeChair + " - " + location + " - " + notes;
    }

    @Override
    public String luceneOtype()	{
        return "meeting";
    }

    @Override
    public String luceneSummary() {
        return location;
    }

    @Override
    public String luceneTitle() {
        DateFormat df = java.text.DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
        return committeeName + " - " + df.format(meetingDateTime);
    }

    @Override
    public HashMap<String,Fieldable> luceneFields()	{
        HashMap<String,Fieldable> fields = new HashMap<String,Fieldable>();
        fields.put("when", new Field("when", meetingDateTime.getTime()+"",DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));

        /*
         * creates a sortable index based on the timestamp of the day the meeting occurred
         * and the inversion of the first two characters of the meeting name (e.g. A -> Z, Y -> B, etc.)
         */
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.meetingDateTime);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);

        char c1 = invertCharacter(this.getCommitteeName().charAt(0));
        char c2 = invertCharacter(this.getCommitteeName().charAt(1));
        char c3 = invertCharacter(this.getCommitteeName().charAt(2));
        char c4 = invertCharacter(this.getCommitteeName().charAt(3));

        fields.put("sortindex", new Field("sortindex",cal.getTimeInMillis()+"-" + c1 + "" + c2 + "" + c3 + "" + c4, DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
        return fields;
    }

    @JsonIgnore
    public String getLuceneBills() {
        StringBuilder response = new StringBuilder();
        for(Bill bill : bills) {
            response.append(bill.getSenateBillNo() + ", ");
        }
        return response.toString().replaceAll(", $", "");
    }

    @Override
    public String toString() {
        return this.id + " : " + meetingDateTime.getTime();
    }

    public char invertCharacter(char c) {
        if(Character.isUpperCase(c)) {
            return (char)((c - 90) * -1 + 65);
        }
        else if(Character.isLowerCase(c)){
            return (char)((c - 122) * -1 + 97);
        }
        return 'Z';
    }
}