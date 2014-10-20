package gov.nysenate.openleg.service.law.data;

import java.time.LocalDate;

public class LawTreeNotFoundEx extends RuntimeException
{
    private static final long serialVersionUID = -1464138957856547835L;

    protected String lawId;
    protected LocalDate endPubDate;
    protected String details;

    public LawTreeNotFoundEx(String lawId, LocalDate endPubDate, String details) {
        super("Law Tree with law id: " + lawId + " and end pub date: " + endPubDate + " could not be found. \n" + details);
        this.lawId = lawId;
        this.endPubDate = endPubDate;
        this.details = details;
    }

    public String getLawId() {
        return lawId;
    }

    public LocalDate getEndPubDate() {
        return endPubDate;
    }

    public String getDetails() {
        return details;
    }
}
