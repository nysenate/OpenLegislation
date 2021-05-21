package gov.nysenate.openleg.processors.transcripts.hearing;

import gov.nysenate.openleg.common.util.PublicHearingTextUtils;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingFile;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingId;
import gov.nysenate.openleg.legislation.transcripts.hearing.dao.PublicHearingDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

@Service
public class PublicHearingParser {
    private final static String QUOTE = Character.toString(146), BAD_HYPHEN = Character.toString(150),
            SOFT_HYPHEN = Character.toString(173);
    @Autowired
    private PublicHearingDataService dataService;

    /**
     * Parses a {@link PublicHearingFile}, extracting a
     * {@link PublicHearing PublicHearing}.
     * @param publicHearingFile
     * @throws IOException
     */
    public void process(PublicHearingFile publicHearingFile) throws IOException {
        String fullText = Files.readString(publicHearingFile.getFile().toPath(), StandardCharsets.ISO_8859_1);
        // Corrects bad characters.
        fullText = fullText.replaceAll(QUOTE, "'").replaceAll(BAD_HYPHEN + "|" + SOFT_HYPHEN, "-");
        Files.writeString(publicHearingFile.getFile().toPath(), fullText, StandardOpenOption.TRUNCATE_EXISTING);
        PublicHearingId id = new PublicHearingId(publicHearingFile.getFileName());
        PublicHearing hearing = PublicHearingTextUtils.getHearingFromText(id, fullText);

        LocalDateTime now = LocalDateTime.now();
        hearing.setModifiedDateTime(now);
        hearing.setPublishedDateTime(now);

        dataService.savePublicHearing(hearing, publicHearingFile, true);
    }
}
