package gov.nysenate.openleg.legislation.agenda.dao;

import gov.nysenate.openleg.api.legislation.agenda.WeekOfAgendaInfoMap;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.agenda.Agenda;
import gov.nysenate.openleg.legislation.agenda.AgendaId;
import gov.nysenate.openleg.legislation.agenda.AgendaNotFoundEx;
import gov.nysenate.openleg.processors.bill.LegDataFragment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for retrieving and saving Agenda data.
 */
public interface AgendaDataService {
    /**
     * Retrieves an Agenda via the agenda id.
     *
     * @param agendaId AgendaId
     * @return Agenda
     * @throws AgendaNotFoundEx - If an agenda with the given agendaId could not be found.
     */
    Agenda getAgenda(AgendaId agendaId) throws AgendaNotFoundEx;

    /**
     * Retreives an agenda for the week of a date
     * @param weekOf - LocalDate
     * @return Agenda
     * @throws AgendaNotFoundEx - If no such agenda can be found
     */
    Agenda getAgenda(LocalDate weekOf) throws AgendaNotFoundEx;

    /**
     * Retrieves a list of agenda ids for a given year.
     *
     * @param year int
     * @param idOrder SortOrder
     * @return List<AgendaId>
     */
    List<AgendaId> getAgendaIds(int year, SortOrder idOrder);

    /**
     * Get data from all committee meetings that took place between "from" and "to".
     * See {@link WeekOfAgendaInfoMap} for details on the data structure.
     */
    WeekOfAgendaInfoMap getWeekOfMap(LocalDateTime from, LocalDateTime to);

    /**
     * Saves the Agenda into the persistence layer. If a new Agenda reference is
     * being saved, the appropriate data will be inserted. Otherwise, existing
     * data will be updated with the changed values. To remove an agenda entirely,
     * use the {@link #deleteAgenda(AgendaId)} method.
     *
     * @param agenda Agenda
     * @param legDataFragment LegDataFragment
     * @param postUpdateEvent boolean
     */
    void saveAgenda(Agenda agenda, LegDataFragment legDataFragment, boolean postUpdateEvent);

    /**
     * Deletes an Agenda from the persistence layer that matches the given agenda id.
     * If the Agenda does not already exist, no action will occur.
     *
     * @param agendaId AgendaId - Remove agenda that matches this id.
     */
    void deleteAgenda(AgendaId agendaId);
}
