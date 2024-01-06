package gov.nysenate.openleg.processors.bill;

/**
 * SOBIFragments are constructed to delineate a SOBI file into components based on a
 * common entity type. The LegDataFragmentType enum lists all the available entities
 * that a LegDataFragment can represent and regex patterns to extract them.
 */
public enum LegDataFragmentType
{
    // Original SOBI types

    // BILL is the only non-XML enum here.
    BILL            ("[0-9]{4}[A-Z][0-9]{5}[ A-Z].+", ""),
    AGENDA          ("<senagenda .+", "</senagenda.+"),
    AGENDA_VOTE     ("<senagendavote .+", "</senagendavote.+"),
    CALENDAR        ("<sencalendar .+", "</sencalendar.+"),
    CALENDAR_ACTIVE ("<sencalendaractive .+", "</sencalendaractive.+"),
    COMMITTEE       ("<sencommmem .+", "</sencommmem.+"),
    ANNOTATION      ("<senannotated .+", "</senannotated.+"),

    // Newer SOBI types

    ANACT           ("<anact.+", "</anact.+"),                              // An act to
    APPRMEMO        ("<approval_memorandum .+", "</approval_memorandum.+"), // Approval memo
    BILLSTAT        ("<billstatus .+", "</billstatus.+"),                   // Bill status
    BILLTEXT        ("<billtext_html .+", "</billtext_html.+"),             // Bill text
    LDBLURB         ("<sponsor_blurb .+", "</sponsor_blurb.+"),             // Blurb
    LDSPON          ("<sponsor_data .+", "</sponsor_data.+"),               // Sponsor
    LDSUMM          ("<digestsummary .+", "</digestsummary.+"),             // Summary
    SAMEAS          ("<sameas .+", "</sameas>.+"),                          // Same as
    SENMEMO         ("<senate_billmemo .+", "</senate_billmemo.+"),         // Memo
    VETOMSG         ("<veto_message .+", "</veto_message.+"),               // Veto memo
    SENFLVOTE       ("<senfloorvote .+","</senfloorvote>.+");

    private final String startPattern;
    private final String endPattern;

    LegDataFragmentType(String startPattern, String endPattern) {
        this.startPattern = startPattern;
        this.endPattern = endPattern;
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
