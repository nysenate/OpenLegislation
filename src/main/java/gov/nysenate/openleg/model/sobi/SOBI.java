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
    /** The format required for the SOBI file name. e.g. SOBI.D130323.T065432.TXT */
    public static SimpleDateFormat sobiDateFormat = new SimpleDateFormat("'SOBI.D'yyMMdd'.T'HHmmss'.TXT'");

    private String fileName;

    private Date publishedDateTime;

    private Date processedDateTime;

    private String text;

    public SOBI(File sobiFile, String encoding) throws IOException, ParseException {
        fileName = sobiFile.getName();
        publishedDateTime = sobiDateFormat.parse(fileName);
        text = FileUtils.readFileToString(sobiFile, encoding);
    }

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