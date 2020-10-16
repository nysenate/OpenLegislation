package gov.nysenate.openleg.dao.agenda.data;

import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.agenda.Agenda;
import gov.nysenate.openleg.model.agenda.AgendaId;
import gov.nysenate.openleg.model.sourcefiles.LegDataFragment;
import org.springframework.dao.DataAccessException;

import java.time.LocalDate;
import java.util.List;

/**
 * DAO interface for retrieving and persisting agenda data.
 */
public interface AgendaDao
{
    /**
     * Get an agenda via the AgendaId.
     *
     * @param agendaId AgendaId - Retrieve agenda that matches this id.
     * @return Agenda
     * @throws DataAccessException
     */
    Agenda getAgenda(AgendaId agendaId) throws DataAccessException;

    /**
     * Get an agenda that starts with a certain date
     * @param weekOf LocalDate - a date (should be a monday)
     * @return Agenda
     * @throws DataAccessException - if no such agenda exists
     */
    Agenda getAgenda(LocalDate weekOf) throws DataAccessException;

    /**
     * Retrieve a list of agenda ids for all the agendas processed during the given year.
     *
     * @param year int - Search for agendas during this year.
     * @param idOrder SortOrder - Specifies the sort order for the returned ids.
     * @return List<AgendaId>
     */
    List<AgendaId> getAgendaIds(int year, SortOrder idOrder);

    /**
     * Updates the agenda or inserts it if it does not yet exist. This includes all the
     * addenda that are stored within the agenda. Associates the the LegDataFragment that
     * triggered the update (set null if not applicable).
     *
     * @param agenda Agenda - The agenda to save.
     * @param legDataFragment LegDataFragment - The fragment that triggered this update.
     * @throws DataAccessException
     */
    void updateAgenda(Agenda agenda, LegDataFragment legDataFragment) throws DataAccessException;

    /**
     * Deletes an agenda and all of it's associated addenda.
     *
     * @param agendaId AgendaId - Delete the agenda that matches this AgendaId.
     */
    void deleteAgenda(AgendaId agendaId);
}
