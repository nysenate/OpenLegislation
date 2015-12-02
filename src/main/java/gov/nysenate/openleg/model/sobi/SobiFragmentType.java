package gov.nysenate.openleg.model.sobi;

/**
 * SOBIFragments are constructed to delineate a SOBI file into components based on a
 * common entity type. The SobiFragmentType enum lists all the available entities
 * that a SobiFragment can represent and regex patterns to extract them.
 */
public enum SobiFragmentType
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
    BILLTEXT        (true, "<billtext .+", "</billtext.+"),                       // Bill text
    LDBLURB         (true, "<sponsor_blurb .+", "</sponsor_blurb.+"),             // Blurb
    LDSPON          (true, "<sponsor_data .+", "</sponsor_data.+"),               // Sponsor
    LDSUMM          (true, "<digestsummary .+", "</digestsummary.+"),             // Summary
    SAMEAS          (true, "<sameas .+", "</sameas.+"),                           // Same as
    SENMEMO         (true, "<senate_billmemo .+", "</senate_billmemo.+"),         // Memo
    SENAGENV        (true, "<senagendavote .+", "</senagendavote.+"),             // Agenda Vote
    VETOMSG         (true, "<veto_message .+", "</veto_message.+");               // Veto memo

    boolean isXml;
    String startPattern;
    String endPattern;

    SobiFragmentType(boolean isXml) {
        this.isXml = isXml;
    }

    SobiFragmentType(boolean isXml, String startPattern, String endPattern) {
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
