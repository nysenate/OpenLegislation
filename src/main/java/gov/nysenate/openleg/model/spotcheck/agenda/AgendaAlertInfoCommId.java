package gov.nysenate.openleg.model.spotcheck.agenda;

import com.google.common.base.Objects;
import gov.nysenate.openleg.model.agenda.AgendaId;
import gov.nysenate.openleg.model.agenda.CommitteeAgendaAddendumId;
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

    /** --- Overridden Methods --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AgendaAlertInfoCommId)) return false;
        if (!super.equals(o)) return false;
        AgendaAlertInfoCommId that = (AgendaAlertInfoCommId) o;
        return Objects.equal(committeeId, that.committeeId) &&
                Objects.equal(addendum, that.addendum);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), committeeId, addendum);
    }

    @Override
    public String toString() {
        return committeeId + " " + addendum + " " + super.toString();
    }

    /** --- Functional Getters / Setters --- */

    // The alerts do not use openleg agenda ids so an agenda id is needed to generate the corresponding CommitteeAgendaAddendumId
    public CommitteeAgendaAddendumId getCommiteeAgendaAddendumId(AgendaId agendaId) {
        return new CommitteeAgendaAddendumId(agendaId != null ? agendaId : new AgendaId(0, 0),
                committeeId, addendum);
    }

    /** --- Getters / Setters --- */

    public CommitteeId getCommitteeId() {
        return committeeId;
    }

    public Version getAddendum() {
        return addendum;
    }
}
