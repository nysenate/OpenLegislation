package gov.nysenate.openleg.dao.agenda.reference.openleg;

import gov.nysenate.openleg.client.view.agenda.AgendaView;

import java.util.List;

public interface OpenlegAgendaDao {
    /**
     * Given a session year and apiKey, return the list of BillView from openleg.
     * @param sessionYear
     * @return List of BillView
     */
    public List<AgendaView> getOpenlegAgendaView(String sessionYear, String apiKey);
}
