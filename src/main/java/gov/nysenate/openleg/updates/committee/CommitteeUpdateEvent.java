package gov.nysenate.openleg.updates.committee;

import gov.nysenate.openleg.legislation.committee.Committee;
import gov.nysenate.openleg.updates.ContentUpdateEvent;

public record CommitteeUpdateEvent(Committee committee) implements ContentUpdateEvent {}
