package gov.nysenate.openleg.api.legislation.agenda;

import gov.nysenate.openleg.legislation.agenda.AgendaInfoCommittee;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A strange class, used to abstract away the mess of mapping something to a List.
 */
public class WeekOfAgendaInfoMap extends HashMap<LocalDate, List<AgendaInfoCommittee>> {
    public void addCommittee(LocalDate weekOf, AgendaInfoCommittee committee) {
        if (!containsKey(weekOf)) {
            put(weekOf, new ArrayList<>());
        }
        get(weekOf).add(committee);
    }
}
