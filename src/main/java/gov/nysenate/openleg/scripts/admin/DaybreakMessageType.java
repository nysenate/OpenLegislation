package gov.nysenate.openleg.scripts.admin;

import java.util.*;

public enum DaybreakMessageType {
    PAGE_FILE       (".page_file.txt",      "Job ABPSDD - LBDC all Bills"),
    SENATE_LOW      (".senate.low.html",    "Sen Act Title Sum Spon Law 1-4000"),
    SENATE_HIGH     (".senate.high.html",   "Sen Act Title Sum Spon Law 4001-9999"),
    ASSEMBLY_LOW    (".assembly.low.html",  "Asm Act Title Sum Spon Law 1-4000"),
    ASSEMBLY_HIGH   (".assembly.high.html", "Asm Act Title Sum Spon Law 4001-99999")
    ;

    String localFileExt;
    String matchString;

    DaybreakMessageType(String localFileExt, String matchString){
        this.localFileExt = localFileExt;
        this.matchString = matchString;
    }

    public String getLocalFileExt(){
        return localFileExt;
    }

    private static List<DaybreakMessageType> allMessageTypes =
            new LinkedList<DaybreakMessageType>(Arrays.asList(DaybreakMessageType.values()));

    public static DaybreakMessageType getMessageType(String messageTitle){
        for(DaybreakMessageType type : DaybreakMessageType.values()){
            if(messageTitle.contains(type.matchString)){
                return type;
            }
        }
        return null;
    }

    public static boolean containsAllMessages(Collection<DaybreakMessageType> messageTypeCollection){
        return messageTypeCollection.containsAll(allMessageTypes);
    }
}
