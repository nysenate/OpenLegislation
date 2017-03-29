package gov.nysenate.openleg.model.sourcefiles.sobi;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

public interface SourceFile {
    //TODO JavaDoc comment
    String getFileName();
    
    @JsonIgnore
    String getText();
    
    LocalDateTime getPublishedDateTime() throws InvalidSobiNameEx;
}