package gov.nysenate.openleg.updates.law;

import gov.nysenate.openleg.legislation.law.LawDocument;
import gov.nysenate.openleg.updates.ContentUpdateEvent;

public record LawUpdateEvent(LawDocument lawDoc) implements ContentUpdateEvent {}
