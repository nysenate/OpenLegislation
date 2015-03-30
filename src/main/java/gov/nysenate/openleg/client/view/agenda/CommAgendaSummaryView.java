package gov.nysenate.openleg.client.view.agenda;

import gov.nysenate.openleg.model.agenda.Agenda;
import gov.nysenate.openleg.model.agenda.CommitteeAgendaId;

import java.time.LocalDate;
import java.util.Optional;

public class CommAgendaSummaryView extends CommAgendaIdView
{
    protected LocalDate weekOf;
    private long totalAddendum;
    private int totalBillsConsidered;
    private int totalBillsVotedOn;

    public CommAgendaSummaryView(CommitteeAgendaId committeeAgendaId, Agenda agenda) {
        super(committeeAgendaId);
        this.weekOf = agenda.getWeekOf().orElse(null);
        this.totalAddendum = agenda.getAgendaInfoAddenda().values().stream()
            .filter(ia -> ia.getCommitteeInfoMap().containsKey(committeeAgendaId.getCommitteeId()))
            .count();
        this.totalBillsConsidered = agenda.totalBillsConsidered(Optional.of(committeeAgendaId.getCommitteeId()));
        this.totalBillsVotedOn = agenda.totalBillsVoted(Optional.of(committeeAgendaId.getCommitteeId()));
    }

    @Override
    public String getViewType() {
        return "committee-agenda-summary";
    }

    public LocalDate getWeekOf() {
        return weekOf;
    }

    public long getTotalAddendum() {
        return totalAddendum;
    }

    public int getTotalBillsConsidered() {
        return totalBillsConsidered;
    }

    public int getTotalBillsVotedOn() {
        return totalBillsVotedOn;
    }
}
