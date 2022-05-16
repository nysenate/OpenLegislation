package gov.nysenate.openleg.updates.agenda;

import gov.nysenate.openleg.legislation.agenda.Agenda;
import gov.nysenate.openleg.updates.ContentUpdateEvent;

import java.util.Collection;

public record BulkAgendaUpdateEvent(Collection<Agenda> agendas) implements ContentUpdateEvent {}
