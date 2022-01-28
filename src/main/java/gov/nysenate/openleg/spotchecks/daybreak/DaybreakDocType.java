package gov.nysenate.openleg.spotchecks.daybreak;

import java.util.Arrays;

/**
 * Designates the file type of a daybreak document.
 * Contains methods to identify the daybreak doc type of daybreak files and email messages.
 */
public enum DaybreakDocType {
    PAGE_FILE       (".page_file.txt",      "Job ABPSDD - LBDC all Bills"),
    SENATE_LOW      (".senate.low.html",    "SEN Act Title Sum Spon Law 1-4000"),
    SENATE_HIGH     (".senate.high.html",   "SEN Act Title Sum Spon Law 4001-99999"),
    ASSEMBLY_LOW    (".assembly.low.html",  "ASM Act Title Sum Spon Law 1-4000"),
    ASSEMBLY_HIGH   (".assembly.high.html", "ASM Act Title Sum Spon Law 4001-99999")
    ;

    private final String localFileExt;
    private final String subjectContains;

    DaybreakDocType(String localFileExt, String subjectContains){
        this.localFileExt = localFileExt;
        this.subjectContains = subjectContains;
    }

    public String getLocalFileExt(){
        return localFileExt;
    }

    public static DaybreakDocType getMessageDocType(String messageSubject) {
        return getType(messageSubject, true);
    }

    public static DaybreakDocType getFileDocType(String fileName) {
        return getType(fileName, false);
    }

    private static DaybreakDocType getType(String toMatch, boolean searchSubject) {
        return Arrays.stream(DaybreakDocType.values())
                .filter(type -> toMatch.contains(searchSubject ? type.subjectContains : type.localFileExt))
                .findFirst().orElse(null);
    }
}
