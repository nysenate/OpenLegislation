package gov.nysenate.openleg.api.legislation.agenda.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.agenda.AgendaId;

public class AgendaIdView implements ViewObject
{
    private long number;
    private int year;

    public AgendaIdView(){}
    public AgendaIdView(AgendaId agendaId) {
        if (agendaId != null) {
            this.number = agendaId.getNumber();
            this.year = agendaId.getYear();
        }
    }

    public long getNumber() {
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
