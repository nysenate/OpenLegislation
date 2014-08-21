package gov.nysenate.openleg.scripts.admin;

import java.util.*;

public enum DaybreakDocType {
    PAGE_FILE       (".page_file.txt",      "Job ABPSDD - LBDC all Bills"),
    SENATE_LOW      (".senate.low.html",    "Sen Act Title Sum Spon Law 1-4000"),
    SENATE_HIGH     (".senate.high.html",   "Sen Act Title Sum Spon Law 4001-9999"),
    ASSEMBLY_LOW    (".assembly.low.html",  "Asm Act Title Sum Spon Law 1-4000"),
    ASSEMBLY_HIGH   (".assembly.high.html", "Asm Act Title Sum Spon Law 4001-99999")
    ;

    String localFileExt;
    String subjectContains;

    DaybreakDocType(String localFileExt, String subjectContains){
        this.localFileExt = localFileExt;
        this.subjectContains = subjectContains;
    }

    public String getLocalFileExt(){
        return localFileExt;
    }

    private static List<DaybreakDocType> allMessageTypes =
            new LinkedList<DaybreakDocType>(Arrays.asList(DaybreakDocType.values()));

    public static DaybreakDocType getMessageType(String messageSubject){
        for(DaybreakDocType type : DaybreakDocType.values()){
            if(messageSubject.contains(type.subjectContains)){
                return type;
            }
        }
        return null;
    }

    public static boolean containsAllMessages(Collection<DaybreakDocType> messageTypeCollection){
        return messageTypeCollection.containsAll(allMessageTypes);
    }
}
