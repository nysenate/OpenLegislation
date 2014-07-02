package gov.nysenate.openleg.model.sobi;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The SOBIFile class represents a SOBI file and contains the metadata of the file as well as the body text.
 * This class doesn't have any awareness about the type of content in the file whereas the SOBIFragments that
 * are generated do.
 *
 * @see gov.nysenate.openleg.model.sobi.SOBIFragment
 */
public class SOBIFile
{
    /**
     * SOBI files are (mostly) in a CP850 or similar encoding. This was determined from the byte mapping of
     * paragraph/section characters to 244/245. This can't be 100% correct though because the degree symbol
     * must be 193 in the correct code set. See SOBI.D120612.T125850.TXT.
     */
    public static final String DEFAULT_ENCODING = "CP850";

    /** The format required for the SOBI file name. e.g. SOBI.D130323.T065432.TXT */
    private static final String sobiDateFullPattern = "'SOBI.D'yyMMdd'.T'HHmmss'.TXT'";

    /** Alternate format for SOBI files with no seconds specified in the filename */
    private static final String sobiDateNoSecsPattern = "'SOBI.D'yyMMdd'.T'HHmm'.TXT'";

    /** The file name of the SOBI file, serves as the unique identifier */
    private String fileName;

    /** The published datetime of the SOBI file which is determined via the file name */
    private Date publishedDateTime;

    /** The datetime when the SOBI file was last processed */
    private Date processedDateTime;

    /** The datetime when the SOBI file was last staged for processing */
    private Date stagedDateTime;

    /** The actual text body of the file */
    private String text;

    /** If true, the SOBIFile is awaiting processing */
    private boolean pendingProcessing;

    /** The number of times this file has been processed */
    private int processedCount;

    /** --- Constructors --- */

    public SOBIFile(File sobiFile) throws IOException {
        this(sobiFile, DEFAULT_ENCODING);
    }

    public SOBIFile(File file, String encoding) throws IOException {
        if (file.exists()) {
            this.fileName = file.getName();
            this.text = FileUtils.readFileToString(file, encoding);
            this.processedCount = 0;
            this.pendingProcessing = false;
            try {
                this.publishedDateTime = DateUtils.parseDate(fileName, sobiDateFullPattern, sobiDateNoSecsPattern);
            }
            catch (ParseException ex) {
                this.publishedDateTime = new Date(file.lastModified());
                ex.printStackTrace();
            }
        }
        else {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
    }

    /** --- Override Methods --- */
    @Override
    public String toString() {
        return "SOBIFile{" +
                "fileName='" + fileName + '\'' +
                ", processedCount=" + processedCount +
                ", processedDateTime=" + processedDateTime +
                ", publishedDateTime=" + publishedDateTime +
                ", pendingProcessing=" + pendingProcessing +
                '}';
    }

    /** --- Functional Getters/Setters --- */

    public void incrementProcessedCount() {
        this.processedCount++;
    }

    /** --- Basic Getters/Setters --- */

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Date getPublishedDateTime() {
        return publishedDateTime;
    }

    public void setPublishedDateTime(Date publishedDateTime) {
        this.publishedDateTime = publishedDateTime;
    }

    public Date getProcessedDateTime() {
        return processedDateTime;
    }

    public void setProcessedDateTime(Date processedDateTime) {
        this.processedDateTime = processedDateTime;
    }

    public Date getStagedDateTime() {
        return stagedDateTime;
    }

    public void setStagedDateTime(Date stagedDateTime) {
        this.stagedDateTime = stagedDateTime;
    }

    public String getText() {
        return text;
    }

    public int getProcessedCount() {
        return processedCount;
    }

    public void setProcessedCount(int processedCount) {
        this.processedCount = processedCount;
    }

    public boolean isPendingProcessing() {
        return pendingProcessing;
    }

    public void setPendingProcessing(boolean pendingProcessing) {
        this.pendingProcessing = pendingProcessing;
    }
}