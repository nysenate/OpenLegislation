package gov.nysenate.openleg.service.law.event;

import gov.nysenate.openleg.model.law.LawTree;
import gov.nysenate.openleg.service.base.data.ContentUpdateEvent;

import java.time.LocalDateTime;

public class LawUpdateEvent extends ContentUpdateEvent
{
    private LawTree lawTree;

    /** --- Constructors --- */

    public LawUpdateEvent(LawTree lawTree) {
        this(lawTree, LocalDateTime.now());
    }

    public LawUpdateEvent(LawTree lawTree, LocalDateTime updateDateTime) {
        super(updateDateTime);
    }

    /** --- Basic Getters --- */

    public LawTree getLawTree() {
        return lawTree;
    }
}
