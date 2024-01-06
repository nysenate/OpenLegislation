package gov.nysenate.openleg.processors.bill.sobi;

import gov.nysenate.openleg.processors.bill.*;
import org.apache.commons.lang3.time.DateUtils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * The SobiFile class wraps the sobi files sent from LBDC and retains some basic meta data.
 * SobiFiles can be broken down into SobiFragments which store data about the type of content in
 * the file and various processing related meta data.
 *
 * @see LegDataFragment
 */
public class SobiFile extends BaseSourceFile {

    /**
     * The format required for the SOBI file name. e.g. SOBI.D130323.T065432.TXT
     */
    public static final String sobiDateFullPattern = "'SOBI.D'yyMMdd'.T'HHmmss'.TXT'";

    /**
     * Alternate format for SOBI files with no seconds specified in the filename
     */
    private static final String sobiDateNoSecsPattern = "'SOBI.D'yyMMdd'.T'HHmm'.TXT'";

    /**
     * --- Constructors ---
     */

    public SobiFile(File sobiFile) throws IOException, LegDataFileNotFoundEx {
        super(sobiFile);
    }

    public SobiFile(File sobiFile, String encoding) throws IOException, LegDataFileNotFoundEx {
        super(sobiFile, encoding);
    }

    @Override
    public SourceType getSourceType() {
        return SourceType.SOBI;
    }

    /**
     * 'ls | grep -o '[a-Z]\{4\}[a-Z_]\+' | sort | uniq'
     * The published datetime is determined via the file name. If an error is encountered when
     * parsing the date, the last modified datetime of the file will be used instead.
     *
     * @throws InvalidLegDataFileNameEx if this sobi has a filename that cannot be parsed
     */
    @Override
    public LocalDateTime getPublishedDateTime() throws InvalidLegDataFileNameEx {
        String fileName = getFileName();
        if ("xml".equalsIgnoreCase(fileName.substring(fileName.length() - 3))) {
            fileName = fileName.substring(0, 23);
        }
        try {
            return LocalDateTime.ofInstant(DateUtils.parseDate(fileName, sobiDateFullPattern,
                    sobiDateNoSecsPattern).toInstant(), ZoneId.systemDefault());
        } catch (ParseException ex) {
            throw new InvalidLegDataFileNameEx(fileName, ex);
        }
    }
}