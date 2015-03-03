package gov.nysenate.openleg.service.law.event;

import gov.nysenate.openleg.model.law.LawDocument;
import gov.nysenate.openleg.model.law.LawTree;
import gov.nysenate.openleg.service.base.data.ContentUpdateEvent;

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
