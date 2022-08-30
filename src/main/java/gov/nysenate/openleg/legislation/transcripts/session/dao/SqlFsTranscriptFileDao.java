package gov.nysenate.openleg.legislation.transcripts.session.dao;

import gov.nysenate.openleg.legislation.transcripts.SqlAbstractTranscriptFileDao;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptFile;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileNotFoundException;

@Repository
public class SqlFsTranscriptFileDao extends SqlAbstractTranscriptFileDao<TranscriptFile>
        implements TranscriptFileDao {

    @Override
    protected boolean isHearing() {
        return false;
    }

    @Override
    protected TranscriptFile getFile(File file) throws FileNotFoundException {
        return new TranscriptFile(file);
    }
}
