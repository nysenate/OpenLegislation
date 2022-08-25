package gov.nysenate.openleg.processors.law;

import com.google.common.collect.ComparisonChain;
import gov.nysenate.openleg.common.util.DateUtils;
import gov.nysenate.openleg.processors.BaseSourceData;

import java.io.File;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LawFile extends BaseSourceData implements Comparable<LawFile> {
    private static final Pattern
            INITIAL_FILE_PATTERN = Pattern.compile("DATABASE\\.LAW.+"), // Initial dump filename pattern.
            UPDATE_FILE_PATTERN = Pattern.compile("(\\d{8})\\.UPDATE(\\.fixed)?"); // Law updates filename pattern.

    /** Received the initial data dumps from LBDC on this date. */
    private static final LocalDate INITIAL_PUBLISH_DATE = LocalDate.of(2014, 9, 22);

    /** File handle to the source law file. */
    protected File file;

    /** Indicates if this law file is part of the initial data dump. */
    protected boolean isInitialDump = false;

    /** The date when this law source file was published/sent to openleg. */
    protected LocalDate publishedDate;

    /** Indicates if the underlying 'file' reference has been moved into an archive directory. */
    private boolean archived;

    /* --- Constructors --- */

    /**
     * Construct the law file using a valid file handler to a source law file. If the file does not have the
     * expected file name format, either an IllegalArgumentException or DateTimeParseException will be thrown.
     *
     * Example file formats:  DATABASE.LAWA   (initial dump A)
     *                        20141012.UPDATE (update for 10/12/2014)
     *
     * The published date will be derived from the file name.
     * @param file File
     */
    public LawFile(File file) {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("You must supply a valid file handle when constructing a LawFile.");
        }
        this.file = file;
        if (INITIAL_FILE_PATTERN.matcher(getFileName()).matches()) {
            this.publishedDate = INITIAL_PUBLISH_DATE;
            this.isInitialDump = true;
        }
        else {
            Matcher updateFileMatcher = UPDATE_FILE_PATTERN.matcher(getFileName());
            if (updateFileMatcher.matches()) {
                this.publishedDate = LocalDate.from(DateUtils.LRS_LAW_FILE_DATE.parse(updateFileMatcher.group(1)));
            }
            else {
                throw new IllegalArgumentException("Supplied law file " + getFileName() + " does not have a " +
                                                   "recognized file name.");
            }
        }
        if (file.getName().endsWith(".fixed")) {
            setManualFix(true);
        }
    }

    /** --- Functional Getters/Setters --- */

    public String getFileName() {
        return file.getName();
    }

    /** --- Overrides --- */

    @Override
    public int compareTo(LawFile o) {
        return ComparisonChain.start()
            .compare(this.getPublishedDate(), o.getPublishedDate())
            .compare(this.getFileName(), o.getFileName())
            .result();
    }

    @Override
    public String toString() {
        return "LawFile: " + getFileName() + " (" + getPublishedDate() + ")";
    }

    /** --- Basic Getters/Setters */

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isInitialDump() {
        return isInitialDump;
    }

    public LocalDate getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(LocalDate publishedDate) {
        this.publishedDate = publishedDate;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}
