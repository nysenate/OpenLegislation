package gov.nysenate.openleg.updates.law;

import gov.nysenate.openleg.updates.ContentUpdateEvent;

public class LawTreeUpdateEvent extends ContentUpdateEvent {

    private final String lawChapterId;

    public LawTreeUpdateEvent(String lawChapterId) {
        this.lawChapterId = lawChapterId;
    }

    public String getLawChapterId() {
        return lawChapterId;
    }
}
