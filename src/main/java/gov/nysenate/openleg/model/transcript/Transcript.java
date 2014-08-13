package gov.nysenate.openleg.model.transcript;

import gov.nysenate.openleg.model.base.BaseLegislativeContent;
import gov.nysenate.openleg.model.bill.Bill;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author GraylinKim
 */
public class Transcript extends BaseLegislativeContent
{
    /**
     *
     */
    protected String id;

    /**
     *
     */
    protected LocalDateTime timeStamp;

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


    public Transcript() {
        relatedBills = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    /**
     * The object type of the transcript.
     */
    public String getOtype() {
        return "transcript";
    }

    public String getOid() {
        return this.getId();
    }

    public LocalDateTime getTimeStamp() {
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

    public void setTimeStamp(LocalDateTime timeStamp) {
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
}
