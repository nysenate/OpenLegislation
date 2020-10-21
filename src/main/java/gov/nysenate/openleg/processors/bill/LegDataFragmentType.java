package gov.nysenate.openleg.processors.bill;

/**
 * SOBIFragments are constructed to delineate a SOBI file into components based on a
 * common entity type. The LegDataFragmentType enum lists all the available entities
 * that a LegDataFragment can represent and regex patterns to extract them.
 */
public enum LegDataFragmentType
{
    // Original SOBI types

    BILL            (false, "[0-9]{4}[A-Z][0-9]{5}[ A-Z].+", ""),
    AGENDA          (true, "<senagenda .+", "</senagenda.+"),
    AGENDA_VOTE     (true, "<senagendavote .+", "</senagendavote.+"),
    CALENDAR        (true, "<sencalendar .+", "</sencalendar.+"),
    CALENDAR_ACTIVE (true, "<sencalendaractive .+", "</sencalendaractive.+"),
    COMMITTEE       (true, "<sencommmem .+", "</sencommmem.+"),
    ANNOTATION      (true, "<senannotated .+", "</senannotated.+"),

    // Newer SOBI types

    ANACT           (true, "<anact.+", "</anact.+"),                              // An act to
    APPRMEMO        (true, "<approval_memorandum .+", "</approval_memorandum.+"), // Approval memo
    BILLSTAT        (true, "<billstatus .+", "</billstatus.+"),                   // Bill status
    BILLTEXT        (true, "<billtext_html .+", "</billtext_html.+"),             // Bill text
    LDBLURB         (true, "<sponsor_blurb .+", "</sponsor_blurb.+"),             // Blurb
    LDSPON          (true, "<sponsor_data .+", "</sponsor_data.+"),               // Sponsor
    LDSUMM          (true, "<digestsummary .+", "</digestsummary.+"),             // Summary
    SAMEAS          (true, "<sameas .+", "</sameas>.+"),                          // Same as
    SENMEMO         (true, "<senate_billmemo .+", "</senate_billmemo.+"),         // Memo
    VETOMSG         (true, "<veto_message .+", "</veto_message.+"),               // Veto memo
    SENFLVOTE       (true, "<senfloorvote .+","</senfloorvote>.+");

    boolean isXml;
    String startPattern;
    String endPattern;

    LegDataFragmentType(boolean isXml, String startPattern, String endPattern) {
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

    /**
     * Attempt to identify a {@link LegDataFragmentType} by the given line text.
     *
     * Returns null if no type matches.
     * @param line String
     * @return {@link LegDataFragmentType}
     */
    public static LegDataFragmentType matchFragmentType(String line) {
        for (LegDataFragmentType fragmentType : LegDataFragmentType.values()) {
            if (line.matches(fragmentType.getStartPattern())) {
                return fragmentType;
            }
        }
        return null;
    }
}
