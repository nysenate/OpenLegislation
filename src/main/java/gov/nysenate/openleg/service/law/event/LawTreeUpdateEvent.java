package gov.nysenate.openleg.service.law.event;

import gov.nysenate.openleg.service.base.data.ContentUpdateEvent;

public class LawTreeUpdateEvent extends ContentUpdateEvent {

    private final String lawChapterId;

    public LawTreeUpdateEvent(String lawChapterId) {
        this.lawChapterId = lawChapterId;
    }

    public String getLawChapterId() {
        return lawChapterId;
    }
}
