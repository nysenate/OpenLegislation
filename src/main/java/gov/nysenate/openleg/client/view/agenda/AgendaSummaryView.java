package gov.nysenate.openleg.client.view.agenda;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.agenda.Agenda;

import java.time.LocalDate;

public class AgendaSummaryView implements ViewObject
{
    private AgendaIdView id;
    private LocalDate weekOf;
    private int numInfoAddenda;
    private int numVoteAddenda;
    private int billsConsidered;
    private int billVotedOn;

    public AgendaSummaryView(Agenda agenda) {
        if (agenda != null) {
            this.id = new AgendaIdView(agenda.getId());
            this.weekOf = agenda.getAgendaInfoAddenda().values().stream()
                .filter(ia -> ia.getWeekOf() != null)
                .map(ia -> ia.getWeekOf())
                .findAny().orElse(null);
            this.numInfoAddenda = agenda.getAgendaInfoAddenda().size();
            this.numVoteAddenda = agenda.getAgendaVoteAddenda().size();
            this.billsConsidered = agenda.getAgendaInfoAddenda().values().stream()
                .flatMap(ia -> ia.getCommitteeInfoMap().values().stream())
                .map(ic -> ic.getItems().size())
                .reduce(0, Integer::sum);
            this.billVotedOn = agenda.getAgendaVoteAddenda().values().stream()
                .flatMap(ia -> ia.getCommitteeVoteMap().values().stream())
                .map(ic -> ic.getVotedBills().size())
                .reduce(0, Integer::sum);
        }
    }

    public AgendaIdView getId() {
        return id;
    }

    public LocalDate getWeekOf() {
        return weekOf;
    }

    public int getBillsConsidered() {
        return billsConsidered;
    }

    public int getBillVotedOn() {
        return billVotedOn;
    }

    public int getNumInfoAddenda() {
        return numInfoAddenda;
    }

    public int getNumVoteAddenda() {
        return numVoteAddenda;
    }

    @Override
    public String getViewType() {
        return "agenda-summary";
    }
}
