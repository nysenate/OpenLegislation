package gov.nysenate.openleg.updates.law;

import gov.nysenate.openleg.legislation.law.LawDocument;
import gov.nysenate.openleg.updates.ContentUpdateEvent;

import java.time.LocalDateTime;

public class LawUpdateEvent extends ContentUpdateEvent
{
    private LawDocument lawDoc;

    /** --- Constructors --- */

    public LawUpdateEvent(LawDocument lawDoc) {
        this(lawDoc, LocalDateTime.now());
    }

    public LawUpdateEvent(LawDocument lawDoc, LocalDateTime updateDateTime) {
        super(updateDateTime);
    }

    /** --- Basic Getters --- */

    public LawDocument getLawDoc() {
        return lawDoc;
    }
}
