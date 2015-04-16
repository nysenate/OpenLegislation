package gov.nysenate.openleg.model.agenda;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.entity.CommitteeId;

import java.io.Serializable;

public class CommitteeAgendaAddendumId extends CommitteeAgendaId implements Serializable {

    private static final long serialVersionUID = -2042033194755249828L;

    protected Version addendum;

    public CommitteeAgendaAddendumId(AgendaId agendaId, CommitteeId committeeId, Version addendum) {
        super(agendaId, committeeId);
        this.addendum = addendum;
    }

    /** --- Overridden Methods --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CommitteeAgendaAddendumId that = (CommitteeAgendaAddendumId) o;
        return Objects.equal(addendum, that.addendum);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), addendum);
    }

    @Override
    public String toString() {
        return super.toString() + "-" + addendum.name();
    }

    @Override
    public int compareTo(CommitteeAgendaId o) {
        int superResult = super.compareTo(o);
        if (superResult == 0 && o instanceof CommitteeAgendaAddendumId) {
            return ComparisonChain.start()
                    .compare(this.addendum, ((CommitteeAgendaAddendumId) o).addendum)
                    .result();
        }
        return superResult;
    }

    /** --- Getters --- */

    public Version getAddendum() {
        return addendum;
    }
}
