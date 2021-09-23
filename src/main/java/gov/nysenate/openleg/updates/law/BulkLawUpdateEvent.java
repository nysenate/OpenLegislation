package gov.nysenate.openleg.updates.law;

import gov.nysenate.openleg.legislation.law.LawDocument;
import gov.nysenate.openleg.updates.ContentUpdateEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

public class BulkLawUpdateEvent extends ContentUpdateEvent
{
    private Collection<LawDocument> lawDocuments = new ArrayList<>();

    /** --- Constructors --- */

    public BulkLawUpdateEvent(Collection<LawDocument> lawDocument) {
        this(lawDocument, LocalDateTime.now());
    }

    public BulkLawUpdateEvent(Collection<LawDocument> lawDocuments, LocalDateTime updateDateTime) {
        super(updateDateTime);
        this.lawDocuments = lawDocuments;
    }

    /** --- Basic Getters --- */

    public Collection<LawDocument> getLawDocuments() {
        return lawDocuments;
    }
}
