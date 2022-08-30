package gov.nysenate.openleg.legislation.transcripts.hearing.dao;

import gov.nysenate.openleg.legislation.transcripts.SqlAbstractTranscriptFileDao;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingFile;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileNotFoundException;

@Repository
public class SqlFsHearingFileDao extends SqlAbstractTranscriptFileDao<HearingFile>
        implements HearingFileDao {

    @Override
    protected boolean isHearing() {
        return true;
    }

    @Override
    protected HearingFile getFile(File file) throws FileNotFoundException {
        return new HearingFile(file);
    }
}
