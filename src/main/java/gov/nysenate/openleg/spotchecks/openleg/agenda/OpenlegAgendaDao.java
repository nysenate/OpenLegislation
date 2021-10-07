package gov.nysenate.openleg.spotchecks.openleg.agenda;

import gov.nysenate.openleg.api.legislation.agenda.view.AgendaView;
import gov.nysenate.openleg.legislation.agenda.AgendaId;

import java.util.List;

public interface OpenlegAgendaDao {
    /**
     * Given a session year and apiKey, return the list of BillView from openleg.
     * @param year
     * @return List of BillView
     */
    List<AgendaView> getAgendaViews(int year);

    /**
     * Get an agenda by agenda id.
     * @param agendaId
     * @return
     */
    AgendaView getAgendaView(AgendaId agendaId);
}
