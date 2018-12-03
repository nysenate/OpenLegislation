package gov.nysenate.openleg.model.sourcefiles;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.File;
import java.time.LocalDateTime;

public interface SourceFile {

    SourceType getSourceType();

    String getFileName();

    @JsonIgnore
    String getText();

    LocalDateTime getStagedDateTime();

    void setStagedDateTime(LocalDateTime l);

    boolean isArchived();

    void setArchived(boolean b);

    String getEncoding();

    File getFile();

    void setFile(File f);

    LocalDateTime getPublishedDateTime();
}