package gov.nysenate.openleg.model.sobi;

/**
 * SOBIFragments are constructed to delineate a SOBI file into components based on a
 * common entity type. The SOBIFragmentType enum lists all the available entities
 * that a SOBIFragment can represent and regex patterns to extract them.
 */
public enum SOBIFragmentType
{
    BILL            (false, "[0-9]{4}[A-Z][0-9]{5}[ A-Z].+", ""),
    AGENDA          (true, "<senagenda.+", "</senagenda.+"),
    AGENDA_VOTE     (true, "<senagendavote.+", "</senagendavote.+"),
    CALENDAR        (true, "<sencalendar.+", "</sencalendar.+"),
    CALENDAR_ACTIVE (true, "<sencalendaractive.+", "</sencalendaractive.+"),
    COMMITTEE       (true, "<sencommmem.+", "</sencommmem.+"),
    ANNOTATION      (true, "<senannotated.+", "</senannotated.+");

    boolean isXml;
    String startPattern;
    String endPattern;

    SOBIFragmentType(boolean isXml, String startPattern, String endPattern) {
        this.isXml = isXml;
        this.startPattern = startPattern;
        this.endPattern = endPattern;
    }

    public boolean isXml() {
        return isXml;
    }

    public String getStartPattern() {
        return startPattern;
    }

    public String getEndPattern() {
        return endPattern;
    }
}
