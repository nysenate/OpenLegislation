package gov.nysenate.openleg.dao.sourcefiles;
import gov.nysenate.openleg.model.sourcefiles.SourceFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Robert Bebber on 4/3/17.
 */
public interface SourceFileDao {

    /**
     * Updates an existing SourceFile in the backing store with the given instance or inserts it if
     * the record doesn't already exist.
     *
     * @param sourceFile SourceFile - The SourceFile instance to be updated.
     */
    void updateSourceFile(SourceFile sourceFile);

    SourceFile getSourceFile(String sourceFile);

    Map<String, SourceFile> getSourceFiles(List<String> fileNames);
}
