package gov.nysenate.openleg.service.agenda.data;

import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.agenda.*;
import gov.nysenate.openleg.model.sobi.SobiFragment;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for retrieving and saving Agenda data.
 */
public interface AgendaDataService
{
    /**
     * Retrieves an Agenda via the agenda id.
     *
     * @param agendaId AgendaId
     * @return Agenda
     * @throws AgendaNotFoundEx - If an agenda with the given agendaId could not be found.
     */
    public Agenda getAgenda(AgendaId agendaId) throws AgendaNotFoundEx;

    /**
     * Retreives an agenda for the week of a date
     * @param weekOf - LocalDate
     * @return Agenda
     * @throws AgendaNotFoundEx - If no such agenda can be found
     */
    public Agenda getAgenda(LocalDate weekOf) throws AgendaNotFoundEx;

    /**
     * Retrieves a list of agenda ids for a given year.
     *
     * @param year int
     * @param idOrder SortOrder
     * @return List<AgendaId>
     */
    public List<AgendaId> getAgendaIds(int year, SortOrder idOrder);

    /**
     * Saves the Agenda into the persistence layer. If a new Agenda reference is
     * being saved, the appropriate data will be inserted. Otherwise, existing
     * data will be updated with the changed values. To remove an agenda entirely,
     * use the {@link #deleteAgenda(AgendaId)} method.
     *
     * @param agenda Agenda
     * @param sobiFragment SobiFragment
     * @param postUpdateEvent boolean
     */
    public void saveAgenda(Agenda agenda, SobiFragment sobiFragment, boolean postUpdateEvent);

    /**
     * Deletes an Agenda from the persistence layer that matches the given agenda id.
     * If the Agenda does not already exist, no action will occur.
     *
     * @param agendaId AgendaId - Remove agenda that matches this id.
     */
    public void deleteAgenda(AgendaId agendaId);
}
