package gov.nysenate.openleg.model;

import gov.nysenate.openleg.lucene.DocumentBuilder;
import gov.nysenate.openleg.lucene.LuceneField;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;



@XStreamAlias("transcript")
public class Transcript extends SenateObject {

    @XStreamAsAttribute
    protected String id;

    @LuceneField("when")
    protected Date timeStamp;

    @LuceneField
    protected String location;

    @LuceneField("session-type")
    protected String type;

    @XStreamAlias("full")
    @LuceneField("full")
    protected String transcriptText;

    //	@HideFrom({Transcript.class})
    protected String transcriptTextProcessed;

    //	@HideFrom({Transcript.class})
    @LuceneField
    protected List<Bill> relatedBills;

    public Transcript() {

    }

    public String getId() {
        return id;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public String getLocation() {
        return location;
    }

    public String getType() {
        return type;
    }

    public String getTranscriptText() {
        return transcriptText;
    }

    public String getTranscriptTextProcessed() {
        return transcriptTextProcessed;
    }

    public List<Bill> getRelatedBills() {
        return relatedBills;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTranscriptText(String transcriptText) {
        this.transcriptText = transcriptText;
    }

    public void setTranscriptTextProcessed(String transcriptTextProcessed) {
        this.transcriptTextProcessed = transcriptTextProcessed;
    }

    public void setRelatedBills(List<Bill> relatedBills) {
        this.relatedBills = relatedBills;
    }

    @Override
    public String luceneOid() {
        return (type.replaceAll(" ", "-").toLowerCase())+((timeStamp != null) ? "-"+ new SimpleDateFormat("MM-dd-yyyy").format(timeStamp):"");
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
    public HashMap<String,Fieldable> luceneFields() {
        HashMap<String,Fieldable> fields = new HashMap<String,Fieldable>();
        fields.put("when", new Field("when",((timeStamp == null) ? new Date().getTime() : timeStamp.getTime())+"",DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
        return fields;
    }

    @JsonIgnore
    public String getLuceneRelatedBills() {
        if(relatedBills == null)
            return "";

        StringBuilder response = new StringBuilder();
        for(Bill bill : relatedBills) {
            response.append(bill.getSenateBillNo() + ", ");
        }
        return response.toString().replaceAll(", $", "");
    }

    @Override
    @JsonIgnore
    public int getYear() {
        if(timeStamp != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(timeStamp);

            return cal.get(Calendar.YEAR);
        }
        return 9999;
    }

    @Override
    public void merge(ISenateObject obj) {
        if(!(obj instanceof Transcript))
            return;

        this.id = ((Transcript)obj).getId();
        this.location = ((Transcript)obj).getLocation();
        this.relatedBills = ((Transcript)obj).getRelatedBills();
        this.timeStamp = ((Transcript)obj).getTimeStamp();
        this.transcriptText = ((Transcript)obj).getTranscriptText();
        this.transcriptTextProcessed = ((Transcript)obj).getTranscriptTextProcessed();
    }


}
