package gov.nysenate.openleg.processor.hearing;

import gov.nysenate.openleg.dao.hearing.PublicHearingDao;
import gov.nysenate.openleg.model.hearing.PublicHearing;
import gov.nysenate.openleg.model.hearing.PublicHearingCommittee;
import gov.nysenate.openleg.model.hearing.PublicHearingFile;
import gov.nysenate.openleg.model.hearing.PublicHearingId;
import gov.nysenate.openleg.service.hearing.PublicHearingDataService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PublicHearingParser
{
    @Autowired
    private PublicHearingFileParser fileParser;

    @Autowired
    private PublicHearingTextParser textParser;

    @Autowired
    private PublicHearingDataService dataService;

    @Autowired
    private PublicHearingTitleParser titleParser;

    @Autowired
    private PublicHearingAddressParser addressParser;

    @Autowired
    private PublicHearingDateParser dateTimeParser;

    @Autowired
    private PublicHearingCommitteeParser committeeParser;

    @Autowired
    private PublicHearingAttendanceParser attendanceParser; // TODO: Not yet implemented

    /**
     * Parses a {@link PublicHearingFile}, extracting a
     * {@link gov.nysenate.openleg.model.hearing.PublicHearing PublicHearing}.
     * @param publicHearingFile
     * @throws IOException
     */
    public void process(PublicHearingFile publicHearingFile) throws IOException, ParseException, DateTimeParseException {
        final List<List<String>> pages = fileParser.getPublicHearingPages(publicHearingFile);
        final List<String> firstPage = pages.get(0);

        String title = titleParser.parse(firstPage);
        String address = addressParser.parse(firstPage);
        LocalDateTime dateTime = dateTimeParser.parse(firstPage);
        List<PublicHearingCommittee> committees = committeeParser.parse(firstPage);
        String text = textParser.parse(pages);

        PublicHearingId id = new PublicHearingId(title, dateTime);
        PublicHearing publicHearing = new PublicHearing(id, address, text);
        publicHearing.setCommittees(committees);

        dataService.savePublicHearing(publicHearing, publicHearingFile);
    }
}
