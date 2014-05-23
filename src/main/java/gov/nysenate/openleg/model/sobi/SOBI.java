package gov.nysenate.openleg.model.sobi;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The SOBI class represents a SOBI file and contains the metadata of the file as well as the
 * body text.
 */
public class SOBI
{
    public static final String DEFAULT_ENCODING = "CP850";

    /** The format required for the SOBI file name. e.g. SOBI.D130323.T065432.TXT */
    public static final SimpleDateFormat sobiDateFormat = new SimpleDateFormat("'SOBI.D'yyMMdd'.T'HHmmss'.TXT'");

    /** The file name of the SOBI file, serves as the unique identifier */
    private String fileName;

    /** The published datetime of the SOBI file which is determined via the file name */
    private Date publishedDateTime;

    /** The datetime when the SOBI file was processed by the system */
    private Date processedDateTime;

    /** The actual text body of the file */
    private String text;

    /** --- Constructors --- */

    public SOBI(File sobiFile) throws IOException, ParseException {
        this(sobiFile, DEFAULT_ENCODING);
    }

    public SOBI(File sobiFile, String encoding) throws IOException, ParseException {
        this.fileName = sobiFile.getName();
        this.publishedDateTime = sobiDateFormat.parse(fileName);
        this.text = FileUtils.readFileToString(sobiFile, encoding);
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

    public void setText(String text) {
        this.text = text;
    }
}