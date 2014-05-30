package gov.nysenate.openleg.model.sobi;

import org.apache.commons.io.FileUtils;

import java.io.File;
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
    public static final String DEFAULT_ENCODING = "CP850";

    /** The format required for the SOBI file name. e.g. SOBI.D130323.T065432.TXT */
    public static final SimpleDateFormat sobiDateFormat = new SimpleDateFormat("'SOBI.D'yyMMdd'.T'HHmmss'.TXT'");

    /** The file name of the SOBI file, serves as the unique identifier */
    private String fileName;

    /** The published datetime of the SOBI file which is determined via the file name */
    private Date publishedDateTime;

    /** The datetime when the SOBI file was last processed */
    private Date processedDateTime;

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

    public SOBIFile(File sobiFile, String encoding) throws IOException {
        if (sobiFile.exists()) {
            this.fileName = sobiFile.getName();
            this.text = FileUtils.readFileToString(sobiFile, encoding);
            this.processedCount = 0;
            this.pendingProcessing = false;
            try {
                this.publishedDateTime = sobiDateFormat.parse(fileName);
            }
            catch (ParseException ex) {
                this.publishedDateTime = new Date(sobiFile.lastModified());
                ex.printStackTrace();
            }
        }
        else {
            throw new IOException("Given sobiFile does not exist in the file system! (" + sobiFile.getAbsolutePath() + ")");
        }
    }

    /** --- Override Methods --- */
    @Override
    public String toString() {
        return "SOBIFile {fileName='" + fileName + '\'' + ", processedCount=" + processedCount + '}';
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