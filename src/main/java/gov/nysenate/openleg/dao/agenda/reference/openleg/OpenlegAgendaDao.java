package gov.nysenate.openleg.dao.agenda.reference.openleg;

import gov.nysenate.openleg.client.view.agenda.AgendaView;
import gov.nysenate.openleg.model.agenda.AgendaId;

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
