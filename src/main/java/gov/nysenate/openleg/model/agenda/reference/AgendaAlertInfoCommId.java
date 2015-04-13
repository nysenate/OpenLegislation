package gov.nysenate.openleg.model.agenda.reference;

import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.entity.CommitteeId;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AgendaAlertInfoCommId extends AgendaAlertId {

    /** The committee that is meeting according to the alert */
    protected CommitteeId committeeId;

    /** The addendum version of the committee meeting */
    protected Version addendum;

    /** --- Constructors --- */

    public AgendaAlertInfoCommId(LocalDateTime referenceDateTime, LocalDate weekOf,
                                 CommitteeId committeeId, Version addendum) {
        super(referenceDateTime, weekOf);
        this.committeeId = committeeId;
        this.addendum = addendum;
    }

    /** --- Getters / Setters --- */

    public CommitteeId getCommitteeId() {
        return committeeId;
    }

    public Version getAddendum() {
        return addendum;
    }
}
