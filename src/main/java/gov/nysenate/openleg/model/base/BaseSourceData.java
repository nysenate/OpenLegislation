package gov.nysenate.openleg.model.base;

import java.time.LocalDateTime;

/**
 * Contains general housekeeping details for objects that represent source data that is consumed by the
 * data processors.
 */
public abstract class BaseSourceData
{
    /** The date/time when the source data was recorded into the backing store. */
    private LocalDateTime stagedDateTime;

    /** If true, this source data is awaiting processing. */
    private boolean pendingProcessing;

    /** The number of times this data has been processed. */
    private int processedCount;

    /** The datetime when the source data was last processed. */
    private LocalDateTime processedDateTime;

    /** True if this source is a non-canonical patch */
    private boolean manualFix = false;

    /** A message documenting a manual fix */
    private String manualFixNotes;

    /** --- Basic Getters/Setters --- */

    public LocalDateTime getStagedDateTime() {
        return stagedDateTime;
    }

    public void setStagedDateTime(LocalDateTime stagedDateTime) {
        this.stagedDateTime = stagedDateTime;
    }

    public boolean isPendingProcessing() {
        return pendingProcessing;
    }

    public void setPendingProcessing(boolean pendingProcessing) {
        this.pendingProcessing = pendingProcessing;
    }

    public int getProcessedCount() {
        return processedCount;
    }

    public void setProcessedCount(int processedCount) {
        this.processedCount = processedCount;
    }

    public LocalDateTime getProcessedDateTime() {
        return processedDateTime;
    }

    public void setProcessedDateTime(LocalDateTime processedDateTime) {
        this.processedDateTime = processedDateTime;
    }

    public boolean isManualFix() {
        return manualFix;
    }

    public void setManualFix(boolean manualFix) {
        this.manualFix = manualFix;
    }

    public String getManualFixNotes() {
        return manualFixNotes;
    }

    public void setManualFixNotes(String manualFixNotes) {
        this.manualFixNotes = manualFixNotes;
    }
}
