package gov.nysenate.openleg.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;



@XStreamAlias("transcript")
public class Transcript extends BaseObject {

    @XStreamAsAttribute
    protected String id;

    protected Date timeStamp;

    protected String location;

    protected String type;

    @XStreamAlias("full")
    protected String transcriptText;

    protected String transcriptTextProcessed;

    protected List<Bill> relatedBills;

    public Collection<Fieldable> luceneFields() {
        Collection<Fieldable> fields = new ArrayList<Fieldable>();
        fields.add(new Field("relatedBills", this.getRelatedBills().toString(), Field.Store.YES, Field.Index.ANALYZED));
        fields.add(new Field("full", this.getTranscriptText(), Field.Store.YES, Field.Index.ANALYZED));
        fields.add(new Field("session-type", this.getType(), Field.Store.YES, Field.Index.ANALYZED));
        fields.add(new Field("location", this.getLocation(), Field.Store.YES, Field.Index.ANALYZED));
        fields.add(new Field("when", String.valueOf(this.getTimeStamp().getTime()), Field.Store.YES, Field.Index.ANALYZED)); // ((timeStamp == null) ? new Date().getTime() : timeStamp.getTime())+""
        return fields;
    }

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
}
