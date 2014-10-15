package gov.nysenate.openleg.client.view.agenda;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.agenda.AgendaId;

public class AgendaIdView implements ViewObject
{
    private int number;
    private int year;

    public AgendaIdView(AgendaId agendaId) {
        this.number = agendaId.getNumber();
        this.year = agendaId.getYear();
    }

    public int getNumber() {
        return number;
    }

    public int getYear() {
        return year;
    }

    @Override
    public String getViewType() {
        return "agenda-id";
    }
}
