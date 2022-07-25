package gov.nysenate.openleg.spotchecks.alert.calendar;

import org.jsoup.select.Elements;


public abstract class BaseCalendarAlertParser {
    protected Elements deleteHeaderRow(Elements entryRows) {
        return new Elements(entryRows.subList(1, entryRows.size()));
    }
}
