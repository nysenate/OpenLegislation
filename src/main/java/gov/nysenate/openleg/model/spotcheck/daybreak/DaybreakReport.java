package gov.nysenate.openleg.model.spotcheck.daybreak;


import com.google.common.base.MoreObjects;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A container that holds all daybreak documents necessary to form a daybreak report
 * Performs checks to make sure all necessary doc types are present
 * @param <DaybreakDoc>
 */
public class DaybreakReport <DaybreakDoc extends DaybreakDocument> {

    /** A map containing the daybreak documents mapped to their doc type */
    private Map<DaybreakDocType, DaybreakDoc> reportDocs;

    /** Specifies the accepted temporal margin of error for the report date of each document */
    private static final Duration reportDateMarginOfError = Duration.ofHours(23);

    /** Is set to the date of the first entry and used as a reference to prevent date piggybacking */
    private LocalDateTime anchorDate;

    /** The official report date, set to the day that the last daybreak message was sent */
    private LocalDate reportDate;

    /** --- Constructors --- */

    public DaybreakReport(){
        reportDocs = new HashMap<>();
        anchorDate = null;
        reportDate = null;
    }

    public DaybreakReport(DaybreakDoc daybreakDoc){
        reportDocs = new HashMap<>();
        reportDocs.put(daybreakDoc.getDaybreakDocType(), daybreakDoc);
        anchorDate = daybreakDoc.getReportDateTime();
        reportDate = anchorDate.toLocalDate();
    }

    public DaybreakReport(Collection<DaybreakDoc> docCollection) throws DaybreakReportInsertException{
        this();
        for(DaybreakDoc daybreakDoc : docCollection){
            insertDaybreakDoc(daybreakDoc);
        }
    }

    /** --- Functions --- */

    /**
     * Attempts to insert a daybreak document into the report.
     * If it is the first document to be inserted it is inserted with no issue.
     * If the document report date is not close enough to this report date an exception is thrown
     * An exception is also thrown if the report already contains a daybreak document of the same type
     * @param daybreakDoc
     * @throws DaybreakReportInsertException
     */
    public void insertDaybreakDoc(DaybreakDoc daybreakDoc) throws DaybreakReportInsertException{
        if (anchorDate==null){
            reportDocs.put(daybreakDoc.getDaybreakDocType(), daybreakDoc);
            anchorDate = daybreakDoc.getReportDateTime();
            reportDate = anchorDate.toLocalDate();
        }
        else if(!dateInMarginOfError(daybreakDoc.getReportDateTime())){
            throw new DaybreakReportInsertException("date out of range" + daybreakDoc.getReportDateTime(),
                    DaybreakReportInsertException.InsertExceptionReason.DATE_OUT_OF_RANGE, daybreakDoc, this.reportDate);
        }
        else if (reportDocs.keySet().contains(daybreakDoc.getDaybreakDocType())){
            throw new DaybreakReportInsertException("report already contains " + daybreakDoc.getDaybreakDocType(),
                    DaybreakReportInsertException.InsertExceptionReason.REPORT_CONTAINS_TYPE, daybreakDoc, this.reportDate);
        }
        else {
            if(daybreakDoc.getReportDateTime().toLocalDate().isAfter(this.reportDate)){
                reportDate = daybreakDoc.getReportDateTime().toLocalDate();
            }
            reportDocs.put(daybreakDoc.getDaybreakDocType(), daybreakDoc);
        }
    }

    public boolean dateInMarginOfError(LocalDateTime dateTime){
        return Duration.between(anchorDate, dateTime).abs().minus(reportDateMarginOfError).isNegative();
    }

    public boolean isComplete(){
        return DaybreakDocType.containsAllDocTypes(reportDocs.keySet());
    }

    /** --- Functional Getters/Setters --- */

    public DaybreakDoc getDaybreakDoc(DaybreakDocType docType){
        return reportDocs.get(docType);
    }

    public DaybreakDoc getDaybreakDoc(String fileName) {
        return reportDocs.get(DaybreakDocType.getFileDocType(fileName));
    }

    public Map<DaybreakDocType, DaybreakDoc> getReportDocs(){
        return Collections.unmodifiableMap(reportDocs);
    }

    /** --- Overrides --- */

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("reportDate", reportDate)
                .toString();
    }

    /** --- Exceptions --- */

    public static class DaybreakReportInsertException extends RuntimeException
    {
        private static final long serialVersionUID = -8868019141371691834L;

        private LocalDate reportDate;
        private DaybreakDocument document;
        private InsertExceptionReason insertExceptionReason;

        public enum InsertExceptionReason{
            REPORT_CONTAINS_TYPE,
            DATE_OUT_OF_RANGE
        }

        public DaybreakReportInsertException(String reason, InsertExceptionReason insertExceptionReason,
                                             DaybreakDocument document, LocalDate reportDate){
            super("Could not insert " + document + " into report-" + reportDate + ": " + reason);
            this.insertExceptionReason = insertExceptionReason;
            this.document = document;
            this.reportDate = reportDate;
        }

        public InsertExceptionReason getInsertExceptionReason() {
            return insertExceptionReason;
        }
        public LocalDate getReportDate() {
            return reportDate;
        }
        public DaybreakDocument getDocument() {
            return document;
        }
    }

    /** --- Getters/Setters --- */

    public LocalDate getReportDate() {
        return reportDate;
    }
}
