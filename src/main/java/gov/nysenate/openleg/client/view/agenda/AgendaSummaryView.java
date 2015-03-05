package gov.nysenate.openleg.client.view.agenda;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.agenda.Agenda;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AgendaSummaryView implements ViewObject
{
    private AgendaIdView id;
    private LocalDate weekOf;
    private LocalDateTime publishedDateTime;
    private int totalAddendum;
    private int totalBillsConsidered;
    private int totalBillsVotedOn;
    private long totalCommittees;

    public AgendaSummaryView(Agenda agenda) {
        if (agenda != null) {
            this.id = new AgendaIdView(agenda.getId());
            this.weekOf = agenda.getAgendaInfoAddenda().values().stream()
                .filter(ia -> ia.getWeekOf() != null)
                .map(ia -> ia.getWeekOf())
                .findAny().orElse(null);
            this.publishedDateTime = agenda.getPublishedDateTime();
            this.totalAddendum = agenda.getAgendaInfoAddenda().size();
            this.totalBillsConsidered = agenda.getAgendaInfoAddenda().values().stream()
                .flatMap(ia -> ia.getCommitteeInfoMap().values().stream())
                .map(ic -> ic.getItems().size())
                .reduce(0, Integer::sum);
            this.totalBillsVotedOn = agenda.getAgendaVoteAddenda().values().stream()
                .flatMap(ia -> ia.getCommitteeVoteMap().values().stream())
                .map(ic -> ic.getVotedBills().size())
                .reduce(0, Integer::sum);
            this.totalCommittees = agenda.getAgendaInfoAddenda().values().stream()
                .flatMap(ia -> ia.getCommitteeInfoMap().keySet().stream())
                .distinct()
                .count();
        }
    }

    public AgendaIdView getId() {
        return id;
    }

    public LocalDate getWeekOf() {
        return weekOf;
    }

    public LocalDateTime getPublishedDateTime() {
        return publishedDateTime;
    }

    public int getTotalAddendum() {
        return totalAddendum;
    }

    public int getTotalBillsConsidered() {
        return totalBillsConsidered;
    }

    public int getTotalBillsVotedOn() {
        return totalBillsVotedOn;
    }

    public long getTotalCommittees() {
        return totalCommittees;
    }

    @Override
    public String getViewType() {
        return "agenda-summary";
    }
}
