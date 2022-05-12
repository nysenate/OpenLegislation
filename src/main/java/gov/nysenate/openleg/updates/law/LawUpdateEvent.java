package gov.nysenate.openleg.updates.law;

import gov.nysenate.openleg.legislation.law.LawDocument;
import gov.nysenate.openleg.updates.ContentUpdateEvent;

public class LawUpdateEvent extends ContentUpdateEvent {
    // TODO: unused?
    private final LawDocument lawDoc;

    public LawUpdateEvent(LawDocument lawDoc) {
        this.lawDoc = lawDoc;
    }

    public LawDocument getLawDoc() {
        return lawDoc;
    }
}
