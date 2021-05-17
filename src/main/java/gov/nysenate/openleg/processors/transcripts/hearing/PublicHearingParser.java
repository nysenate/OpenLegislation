package gov.nysenate.openleg.processors.transcripts.hearing;

import gov.nysenate.openleg.common.util.PublicHearingTextUtils;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingFile;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingId;
import gov.nysenate.openleg.legislation.transcripts.hearing.dao.PublicHearingDataService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;

@Service
public class PublicHearingParser {
    @Autowired
    private PublicHearingDataService dataService;

    /**
     * Parses a {@link PublicHearingFile}, extracting a
     * {@link PublicHearing PublicHearing}.
     * @param publicHearingFile
     * @throws IOException
     */
    public void process(PublicHearingFile publicHearingFile) throws IOException {
        String fullText = FileUtils.readFileToString(publicHearingFile.getFile(), Charset.defaultCharset());
        PublicHearingId id = new PublicHearingId(publicHearingFile.getFileName());
        PublicHearing hearing = PublicHearingTextUtils.getHearingFromText(id, fullText);

        LocalDateTime now = LocalDateTime.now();
        hearing.setModifiedDateTime(now);
        hearing.setPublishedDateTime(now);

        dataService.savePublicHearing(hearing, publicHearingFile, true);
    }
}
