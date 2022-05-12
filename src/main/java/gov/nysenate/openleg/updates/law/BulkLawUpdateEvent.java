package gov.nysenate.openleg.updates.law;

import gov.nysenate.openleg.legislation.law.LawDocument;
import gov.nysenate.openleg.updates.ContentUpdateEvent;

import java.util.Collection;

public class BulkLawUpdateEvent extends ContentUpdateEvent {
    private final Collection<LawDocument> lawDocuments;

    public BulkLawUpdateEvent(Collection<LawDocument> lawDocuments) {
        this.lawDocuments = lawDocuments;
    }

    public Collection<LawDocument> getLawDocuments() {
        return lawDocuments;
    }
}
