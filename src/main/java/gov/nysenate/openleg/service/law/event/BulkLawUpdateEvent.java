package gov.nysenate.openleg.service.law.event;

import gov.nysenate.openleg.model.law.LawTree;
import gov.nysenate.openleg.service.base.data.ContentUpdateEvent;

import java.time.LocalDateTime;
import java.util.Collection;

public class BulkLawUpdateEvent extends ContentUpdateEvent
{
    private Collection<LawTree> lawTrees;

    /** --- Constructors --- */

    public BulkLawUpdateEvent(Collection<LawTree> lawTrees) {
        this(lawTrees, LocalDateTime.now());
    }

    public BulkLawUpdateEvent(Collection<LawTree> lawTrees, LocalDateTime updateDateTime) {
        super(updateDateTime);
        this.lawTrees = lawTrees;
    }

    /** --- Basic Getters --- */

    public Collection<LawTree> getLawTrees() {
        return lawTrees;
    }
}
