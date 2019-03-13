package gov.nysenate.openleg.model.sourcefiles;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.MoreObjects;

import gov.nysenate.openleg.dao.base.SqlBaseDao;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseSourceFile implements SourceFile {
    @Autowired
    SqlBaseDao sqlBaseDao;
    /**
     * SOBI and XML files are (mostly) in a CP850 or similar encoding. This was determined from the
     * byte mapping of
     * paragraph/section characters to 244/245. This can't be 100% correct though because the degree symbol
     * must be 193 in the correct code set. See SOBI.D120612.T125850.TXT.
     */
    private static final String DEFAULT_ENCODING = "CP850";
    private static final Logger logger = LoggerFactory.getLogger(BaseSourceFile.class);
    
    /** Reference to the actual file. */
    private File file;
    
    /** The encoding this file was written in. */
    private String encoding;
    
    /** The datetime when the Sobi or XML files was recorded into the backing store. */
    private LocalDateTime stagedDateTime;
    
    /** Indicates if the underlying 'file' reference has been moved into an archive directory. */
    private boolean archived;

    protected File standingDir;
    
    /** --- Constructors --- */
    
    public BaseSourceFile(File sobiFile) throws IOException, LegDataFileNotFoundEx {
        this(sobiFile, DEFAULT_ENCODING);
    }
    
    public BaseSourceFile(File infile, String encoding) throws IOException, LegDataFileNotFoundEx {
        if(infile.exists()){
            this.file = infile;
            this.encoding = encoding;
            archived = false;
            // Attempt to parse the file name, raising an exception if the name is invalid
            getPublishedDateTime();
        }
        else{
            throw new FileNotFoundException(infile.getAbsolutePath());
        }
    }
    
    /** --- Functional Getters/Setters --- */

    /** The file name serves as the unique identifier for the Sobi or XML files. */
    @Override
    public String getFileName(){
        return file.getName();
    }
    
    /**
     * Retrieves the text contained within the file. The text is not saved due to the
     * added memory overhead when retaining references to Sobi or XML files.
     */
    @Override
    @JsonIgnore
    public String getText(){
        try{
            return FileUtils.readFileToString(file, encoding);
        }
        catch(IOException e){
            throw new UnreadableLegDataEx(this, e);
        }
    }
    
    @Override
    public LocalDateTime getPublishedDateTime() throws InvalidLegDataFileNameEx {
        return null;
    }
    
    /** --- Override Methods --- */
    
    @Override
    public String toString(){
        return MoreObjects.toStringHelper(this)
                .add("file", file)
                .add("encoding", encoding)
                .add("stagedDateTime", stagedDateTime)
                .add("archived", archived)
                .toString();
    }
    
    /** --- Basic Getters/Setters --- */
    
    public File getFile(){
        return file;
    }
    
    public void setFile(File file){
        this.file = file;
    }
    
    public String getEncoding(){
        return encoding;
    }
    
    public LocalDateTime getStagedDateTime(){
        return stagedDateTime;
    }
    
    public void setStagedDateTime(LocalDateTime stagedDateTime){
        this.stagedDateTime = stagedDateTime;
    }
    
    public boolean isArchived(){
        return archived;
    }
    
    public void setArchived(boolean archived){
        this.archived = archived;
    }
}