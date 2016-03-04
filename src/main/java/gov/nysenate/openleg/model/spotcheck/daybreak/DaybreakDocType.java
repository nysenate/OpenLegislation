package gov.nysenate.openleg.model.spotcheck.daybreak;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Designates the file type of a daybreak document
 * Contains methods to identify the daybreak doc type of daybreak files and email messages
 */
public enum DaybreakDocType {
    PAGE_FILE       (".page_file.txt",      "Job ABPSDD - LBDC all Bills"),
    SENATE_LOW      (".senate.low.html",    "Sen Act Title Sum Spon Law 1-4000"),
    SENATE_HIGH     (".senate.high.html",   "Sen Act Title Sum Spon Law 4001-9999"),
    ASSEMBLY_LOW    (".assembly.low.html",  "Asm Act Title Sum Spon Law 1-4000"),
    ASSEMBLY_HIGH   (".assembly.high.html", "Asm Act Title Sum Spon Law 4001-99999")
    ;

    String localFileExt;
    String subjectContains;

    private DaybreakDocType(String localFileExt, String subjectContains){
        this.localFileExt = localFileExt;
        this.subjectContains = subjectContains;
    }

    public String getLocalFileExt(){
        return localFileExt;
    }

    private static List<DaybreakDocType> allDocTypes =
            new LinkedList<>(Arrays.asList(DaybreakDocType.values()));

    public static DaybreakDocType getMessageDocType(String messageSubject){
        for(DaybreakDocType type : DaybreakDocType.values()){
            if(messageSubject.contains(type.subjectContains)){
                return type;
            }
        }
        return null;
    }

    public static DaybreakDocType getFileDocType(String fileName){
        for(DaybreakDocType type : DaybreakDocType.values()){
            if(fileName.contains(type.localFileExt)){
                return type;
            }
        }
        return null;
    }

    public static String[] getFileExts(){
        String[] fileExts = new String[DaybreakDocType.values().length];
        for( int i=0; i<DaybreakDocType.values().length; i++){
            fileExts[i] = DaybreakDocType.values()[i].localFileExt;
        }
        return fileExts;
    }

    public static boolean containsAllDocTypes(Collection<DaybreakDocType> docTypeCollection){
        return docTypeCollection.containsAll(allDocTypes);
    }
}
