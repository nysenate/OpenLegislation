package gov.nysenate.openleg.updates.law;

import gov.nysenate.openleg.legislation.law.LawDocument;
import gov.nysenate.openleg.updates.ContentUpdateEvent;

import java.util.Collection;

public record BulkLawUpdateEvent(Collection<LawDocument> lawDocuments) implements ContentUpdateEvent {}
