package gov.nysenate.openleg.processors.transcripts.hearing;

import gov.nysenate.openleg.common.util.PublicHearingTextUtils;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingCommittee;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingFile;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingId;
import gov.nysenate.openleg.legislation.transcripts.hearing.dao.PublicHearingDataService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.List;

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
        final List<List<String>> pages = PublicHearingTextUtils.getPages(fullText);
        final List<String> firstPage = pages.get(0);
        final List<String> lastPage = pages.get(pages.size() - 1);

        String title = PublicHearingTextUtils.parseTitle(firstPage);
        String address = PublicHearingAddressParser.parse(firstPage);
        var dateTimeParser = new PublicHearingDateParser(firstPage, lastPage);

        List<PublicHearingCommittee> committees = PublicHearingCommitteeParser.parse(firstPage);
        PublicHearingId id = new PublicHearingId(publicHearingFile.getFileName());
        PublicHearing publicHearing = new PublicHearing(id, dateTimeParser.getDate(), fullText);
        publicHearing.setTitle(title);
        publicHearing.setAddress(address);
        publicHearing.setStartTime(dateTimeParser.getStartTime());
        publicHearing.setEndTime(dateTimeParser.getEndTime());
        publicHearing.setCommittees(committees);

        LocalDateTime now = LocalDateTime.now();
        publicHearing.setModifiedDateTime(now);
        publicHearing.setPublishedDateTime(now);

        dataService.savePublicHearing(publicHearing, publicHearingFile, true);
    }
}
