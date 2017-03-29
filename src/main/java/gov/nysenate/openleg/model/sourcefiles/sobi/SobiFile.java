package gov.nysenate.openleg.model.sourcefiles.sobi;

import org.apache.commons.lang3.time.DateUtils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;

import gov.nysenate.openleg.model.sourcefiles.BaseSourceFile;

/**
 * The SobiFile class wraps the sobi files sent from LBDC and retains some basic meta data.
 * SobiFiles can be broken down into SobiFragments which store data about the type of content in
 * the file and various processing related meta data.
 *
 * @see SobiFragment
 */
public class SobiFile extends BaseSourceFile {
    
    /** The format required for the SOBI file name. e.g. SOBI.D130323.T065432.TXT */
    private static final String sobiDateFullPattern = "'SOBI.D'yyMMdd'.T'HHmmss'.TXT'";
    
    /** Alternate format for SOBI files with no seconds specified in the filename */
    private static final String sobiDateNoSecsPattern = "'SOBI.D'yyMMdd'.T'HHmm'.TXT'";
    
    /** --- Constructors --- */
    
    public SobiFile(File sobiFile) throws IOException, SobiFileNotFoundEx{
        super(sobiFile);
    }
    
    public SobiFile(File file, String encoding) throws IOException, SobiFileNotFoundEx{
        super(file, encoding);
    }
    
    /**
     * 'ls | grep -o '[a-Z]\{4\}[a-Z_]\+' | sort | uniq'
     * The published datetime is determined via the file name. If an error is encountered when
     * parsing the date, the last modified datetime of the file will be used instead.
     *
     * @throws InvalidSobiNameEx if this sobi has a filename that cannot be parsed
     */
    @Override
    public LocalDateTime getPublishedDateTime() throws InvalidSobiNameEx{
        String fileName = getFileName();
        if("xml".equals(fileName.substring(fileName.length() - 3).toLowerCase())){
            fileName = fileName.substring(0, 23);
        }
        try{
            return LocalDateTime.ofInstant(DateUtils.parseDate(fileName, sobiDateFullPattern,
                    sobiDateNoSecsPattern).toInstant(), ZoneId.systemDefault());
        }
        catch(ParseException ex){
            throw new InvalidSobiNameEx(fileName, ex);
        }
    }
}