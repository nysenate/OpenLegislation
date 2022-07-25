package gov.nysenate.openleg.legislation.agenda.dao;

import gov.nysenate.openleg.api.legislation.agenda.WeekOfAgendaInfoMap;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.agenda.Agenda;
import gov.nysenate.openleg.legislation.agenda.AgendaId;
import gov.nysenate.openleg.processors.bill.LegDataFragment;
import org.springframework.dao.DataAccessException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DAO interface for retrieving and persisting agenda data.
 */
public interface AgendaDao {
    /**
     * Get an agenda via the AgendaId.
     * @param agendaId AgendaId - Retrieve agenda that matches this id.
     * @return Agenda
     * @throws DataAccessException
     */
    Agenda getAgenda(AgendaId agendaId) throws DataAccessException;

    /**
     * Get an agenda that starts with a certain date.
     *
     * @param weekOf LocalDate - a date (should be a monday)
     * @return Agenda
     * @throws DataAccessException - if no such agenda exists
     */
    Agenda getAgenda(LocalDate weekOf) throws DataAccessException;

    /**
     * Get data from all committee meetings that took place between "from" and "to".
     * See {@link WeekOfAgendaInfoMap} for details on the data structure.
     */
    WeekOfAgendaInfoMap getWeekOfMap(LocalDateTime from, LocalDateTime to);

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
     * addenda that are stored within the agenda. Associates the LegDataFragment that
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
