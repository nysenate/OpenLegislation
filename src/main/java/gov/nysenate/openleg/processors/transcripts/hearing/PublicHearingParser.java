package gov.nysenate.openleg.processors.transcripts.hearing;

import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingFile;
import org.apache.commons.io.Charsets;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class PublicHearingParser {
    private static final Charset CP_1252 = Charsets.toCharset("CP1252");

    /**
     * Parses a {@link PublicHearingFile}, extracting a
     * {@link PublicHearing PublicHearing}.
     * @param publicHearingFile
     * @throws IOException
     */
    public static PublicHearing process(PublicHearingFile publicHearingFile) throws IOException {
        String fullText = Files.readString(publicHearingFile.getFile().toPath(), CP_1252);
        return PublicHearingTextUtils.getHearingFromText(publicHearingFile.getFileName(), fullText);
    }
}
