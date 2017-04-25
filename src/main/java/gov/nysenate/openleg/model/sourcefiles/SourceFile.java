package gov.nysenate.openleg.model.sourcefiles;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nysenate.openleg.model.sourcefiles.sobi.InvalidSobiNameEx;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public interface SourceFile {
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