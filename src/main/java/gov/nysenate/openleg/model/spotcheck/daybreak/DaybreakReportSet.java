package gov.nysenate.openleg.model.spotcheck.daybreak;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;

/**
 * This class encapsulates a set of daybreak documents that have not yet been grouped into reports
 * Daybreak documents are fed individually into a DaybreakReportSet object and grouped into reports.
 * Once all documents are fed in, complete reports, partial reports and duplicate documents can be retrieved
 */
public class DaybreakReportSet<DaybreakDoc extends DaybreakDocument> {

    Logger logger = LoggerFactory.getLogger(DaybreakReportSet.class);

    /** Stores any generated reports */
    private Set<DaybreakReport<DaybreakDoc> > reportSet;

    /** Stores any Documents that have been found to be of the same type and report date as a document in an existing report */
    private List<DaybreakDoc> duplicateDocuments;

    /** --- Constructors --- */

    public DaybreakReportSet(){
        reportSet = new HashSet<>();
        duplicateDocuments = new LinkedList<>();
    }

    /** --- Functional Getters/Setters */

    /**
     * Takes in a daybreak document and attempts to fit it into a report.
     * Files the document as a duplicate if a document with the same report date and type is found
     * @param daybreakDoc
     */
    public void insertDaybreakDocument(DaybreakDoc daybreakDoc){
        for(DaybreakReport<DaybreakDoc> daybreakReport : reportSet){
            try{
                daybreakReport.insertDaybreakDoc(daybreakDoc);
                return;
            }
            catch (DaybreakReport.DaybreakReportInsertException ex) {
                // Insert the document into the duplicate list if it was found to be a duplicate
                if(ex.getInsertExceptionReason() ==
                        DaybreakReport.DaybreakReportInsertException.InsertExceptionReason.REPORT_CONTAINS_TYPE){
                    logger.error("Duplicate document detected for report-" + daybreakReport + " type-" +
                            daybreakDoc.getDaybreakDocType() + "" + daybreakDoc);
                    duplicateDocuments.add(daybreakDoc);
                    return;
                }
            }
        }
        // If it is not a fit or a duplicate, create a new report
        reportSet.add(new DaybreakReport<>(daybreakDoc));
    }

    /**
     * Returns all reports that are complete
     * @return
     */
    public Map<LocalDate, DaybreakReport<DaybreakDoc>> getCompleteReports() {
        Map<LocalDate, DaybreakReport<DaybreakDoc>> completeReports = new HashMap<>();
        for(DaybreakReport<DaybreakDoc> daybreakReport : reportSet){
            if(daybreakReport.isComplete()){
                completeReports.put(daybreakReport.getReportDate(), daybreakReport);
            }
        }
        return completeReports;
    }

    /**
     * Returns all reports that are not yet complete
     * @return
     */
    public Map<LocalDate, DaybreakReport<DaybreakDoc>> getPartialReports() {
        Map<LocalDate, DaybreakReport<DaybreakDoc>> partialReports = new HashMap<>();
        for(DaybreakReport<DaybreakDoc> daybreakReport : reportSet){
            if(!daybreakReport.isComplete()){
                partialReports.put(daybreakReport.getReportDate(), daybreakReport);
            }
        }
        return partialReports;
    }

    /** --- Getters/Setters --- */

    public List<DaybreakDoc> getDuplicateDocuments() {
        return duplicateDocuments;
    }
}
