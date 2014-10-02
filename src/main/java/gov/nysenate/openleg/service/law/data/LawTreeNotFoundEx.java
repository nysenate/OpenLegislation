package gov.nysenate.openleg.service.law.data;

import java.time.LocalDate;

public class LawTreeNotFoundEx extends RuntimeException
{
    private static final long serialVersionUID = -1464138957856547835L;

    public LawTreeNotFoundEx(String lawId, LocalDate endPubDate, String details) {
        super("Law Tree with law id: " + lawId + " and end pub date: " + endPubDate + " could not be found. \n" + details);
    }
}
