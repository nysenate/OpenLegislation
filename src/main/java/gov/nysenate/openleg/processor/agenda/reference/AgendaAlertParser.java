package gov.nysenate.openleg.processor.agenda.reference;

import gov.nysenate.openleg.model.agenda.AgendaInfoCommitteeItem;
import gov.nysenate.openleg.model.spotcheck.agenda.AgendaAlertInfoCommittee;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.CommitteeId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReferenceId;
import gov.nysenate.openleg.processor.base.ParseError;
import gov.nysenate.openleg.util.DateUtils;
import gov.nysenate.openleg.util.ScrapeUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AgendaAlertParser {

    private static final Logger logger = LoggerFactory.getLogger(AgendaAlertParser.class);

    private static final Pattern agendaAlertFilenamePattern =
            Pattern.compile("^agenda_alert-(\\d{8})-[A-z\\._]+-([A-z]+)-(\\d{8}T\\d{6}).html$");
//  This pattern parses both full and individual agenda alert filenames, but currently we can't reliably process full alerts
//            Pattern.compile("^agenda_alert-(\\d{8})-[A-z\\._-]+(\\d{8}T\\d{6}).html$");

    private static final Pattern committeeNamePattern =
            Pattern.compile("^\\s*Senate\\s+Standing\\s+Committee\\s+on\\s+([A-z, ]+)\\s*$");

    private static final Pattern chairPattern = Pattern.compile("^\\s*Senator\\s+([A-z\\.'-, ]*),\\s+Chair\\s*$");

    private static final Pattern meetingTimePattern =
            Pattern.compile("^\\s*(?:(\\d{1,2}:\\d{2} (?:AM|PM)|12 Noon)\\s*,\\s+)?(?:[A-z]+day\\s*,\\s+)?([A-z]+ \\d+, \\d{4})\\s*$");

    /**
     * Parses an agenda alert html file, yielding a list of committee meeting references
     *
     * @param agendaAlert File - The file containing the alert text
     * @return List<AgendaAlertInfoCommittee>
     * @throws IOException
     */
    public static List<AgendaAlertInfoCommittee> parseAgendaAlert(File agendaAlert) throws IOException, ParseError {
        Matcher filenameMatcher = agendaAlertFilenamePattern.matcher(agendaAlert.getName());
        if (!filenameMatcher.matches()) {
            throw new IllegalArgumentException("agenda alert filename does not match specification: " + agendaAlert.getName());
        }

        LocalDate weekOf = LocalDate.parse(filenameMatcher.group(1), DateTimeFormatter.BASIC_ISO_DATE);
        LocalDateTime refDateTime = LocalDateTime.parse(filenameMatcher.group(3), DateUtils.BASIC_ISO_DATE_TIME);

        // Todo find a way to parse addenda from alert text
        String addendumString = filenameMatcher.group(2);
        Version addendum = Version.of(addendumString);

        List<AgendaAlertInfoCommittee> alertInfoCommittees = new ArrayList<>();

        String fileContents = FileUtils.readFileToString(agendaAlert, "ISO-8859-1")
                        .replaceAll("\u001A", "");  // Replace unknown characters with a blank

        Document document = Jsoup.parse(fileContents);
        Elements bodyElements = document.getElementsByTag("body").first().children();

        Element headerElement = null, notesElement = null, billTableElement = null;

        // committee meetings consist of a header (<h3>) notes (<p>) and a bill listing(<table>)
        // iterate through all committee meeting elements, parsing each
        for (Element currentElement : bodyElements) {
            if ("p".equalsIgnoreCase(currentElement.tag().getName())) {
                notesElement = currentElement;
            } else if ("table".equalsIgnoreCase(currentElement.tag().getName())) {
                billTableElement = currentElement;
            } else {
                if (headerElement != null) {
                    alertInfoCommittees.add(parseInfoCommittee(refDateTime, weekOf,
                            headerElement, notesElement, billTableElement, addendum));
                    headerElement = notesElement = billTableElement = null;
                }
                if ("h3".equalsIgnoreCase(currentElement.tag().getName())) {
                    headerElement = currentElement;
                }
            }
        }

        return alertInfoCommittees;
    }

    private static AgendaAlertInfoCommittee parseInfoCommittee(LocalDateTime refDateTime, LocalDate weekOf,
            Element headerElement, Element notesElement, Element billTableElement, Version addendum) throws ParseError {
        AgendaAlertInfoCommittee aaic = new AgendaAlertInfoCommittee();
        aaic.setReferenceId(new SpotCheckReferenceId(SpotCheckRefType.LBDC_AGENDA_ALERT, refDateTime));
        aaic.setWeekOf(weekOf);
        aaic.setAddendum(addendum);

        String[] headerLines = ScrapeUtils.getFormattedText(headerElement).split("\n");
        aaic.setCommitteeId(getCommitteeId(headerLines[0]));
        aaic.setChair(getChair(headerLines[1]));
        aaic.setMeetingDateTime(getMeetingTime(headerLines[2]));
        aaic.setLocation(headerLines[3].trim());

        aaic.setNotes(notesElement != null ? ScrapeUtils.getFormattedText(notesElement).trim() : "");

        if (billTableElement != null) {
            getCommitteeItems(billTableElement, SessionYear.of(aaic.getWeekOf().getYear()))
                    .forEach(aaic::addInfoCommitteeItem);
        }

        return aaic;
    }

    private static CommitteeId getCommitteeId(String committeeNameLine) throws ParseError {
        Matcher committeeNameMatcher = committeeNamePattern.matcher(committeeNameLine);
        if (committeeNameMatcher.matches()) {
            return new CommitteeId(Chamber.SENATE, committeeNameMatcher.group(1).trim());
        }
        throw new ParseError("could not parse committee name from " + committeeNameLine + "");
    }

    private static String getChair(String chairLine) throws ParseError {
        Matcher chairMatcher = chairPattern.matcher(chairLine);
        if (chairMatcher.matches()) {
            return chairMatcher.group(1);
        }
        throw new ParseError("could not parse chair " + chairLine + "");
    }

    private static LocalDateTime getMeetingTime(String meetingTimeLine) throws ParseError {
        Matcher meetingTimeMatcher = meetingTimePattern.matcher(meetingTimeLine);
        if (meetingTimeMatcher.matches()) {
            LocalDate meetingDay = LocalDate.parse(meetingTimeMatcher.group(2),
                    DateTimeFormatter.ofPattern("MMMM d, yyyy"));
            String timeString = meetingTimeMatcher.group(1);
            LocalTime meetingTime;
            if (StringUtils.isBlank(timeString)) {
                meetingTime = LocalTime.MIDNIGHT;
            } else if ("12 Noon".equals(timeString)) {
                meetingTime = LocalTime.NOON;
            } else {
                meetingTime = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("h:mm a"));
            }
            return meetingDay.atTime(meetingTime);
        }
        throw new ParseError("could not parse meeting time from '" + meetingTimeLine + "'");
    }

    private static List<AgendaInfoCommitteeItem> getCommitteeItems(Element billTableElement, SessionYear sessionYear) {
        Elements rows = billTableElement.getElementsByTag("tr");
        List<AgendaInfoCommitteeItem> committeeItems = new ArrayList<>();
        rows.stream().filter(row -> row.getElementsByTag("th").isEmpty())
                .forEach(row -> {
                    String[] billEntry = ScrapeUtils.getFormattedText(row.children().first()).split("\n");
                    committeeItems.add(new AgendaInfoCommitteeItem(
                            new BillId(billEntry[0].replaceAll("^\\s*(\\d+[A-z]?)\\s*$", "S$1"), sessionYear),
                            billEntry.length > 1 ? billEntry[1].trim() : ""
                    ));
                });
        return committeeItems;
    }
}
