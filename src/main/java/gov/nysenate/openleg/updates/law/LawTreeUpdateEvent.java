package gov.nysenate.openleg.updates.law;

import gov.nysenate.openleg.updates.ContentUpdateEvent;

public record LawTreeUpdateEvent(String lawChapterId) implements ContentUpdateEvent {}
