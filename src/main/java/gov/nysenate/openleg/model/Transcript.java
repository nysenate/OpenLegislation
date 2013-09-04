package gov.nysenate.openleg.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 *
 * @author GraylinKim
 */
public class Transcript extends BaseObject
{
    /**
     *
     */
    protected String id;

    /**
     *
     */
    protected Date timeStamp;

    /**
     *
     */
    protected String location;

    /**
     *
     */
    protected String type;

    /**
     *
     */
    protected String transcriptText;

    /**
     *
     */
    protected String transcriptTextProcessed;

    /**
     *
     */
    protected List<Bill> relatedBills;

    /**
     * JavaBean Constructor
     */
    public Transcript()
    {
        relatedBills = new ArrayList<Bill>();
    }

    public String getId() {
        return id;
    }

    /**
     * The object type of the trascript.
     */
    public String getOtype()
    {
        return "transcript";
    }

    public String getOid()
    {
        return this.getId();
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

    public int getYear() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(timeStamp);
        return cal.get(Calendar.YEAR);
    }
}
