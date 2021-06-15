package gov.nysenate.openleg.legislation.transcripts.hearing;

import com.google.common.base.Splitter;
import gov.nysenate.openleg.legislation.BaseLegislativeContent;
import gov.nysenate.openleg.legislation.SessionYear;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PublicHearing extends BaseLegislativeContent {
    /** The Public Hearing id. */
    private final PublicHearingId id;

    /** The title of the public hearing. */
    private String title;

    /** The date of the public hearing. */
    private final LocalDate date;

    /** The location of this Public Hearing. */
    private String address;

    /** The {@link PublicHearingCommittee}
     * holding this PublicHearing. */
    private List<PublicHearingCommittee> committees;

    /** The raw text of the Public Hearing. */
    private final String text;

    /** The start time of the public hearing. */
    private LocalTime startTime;

    /** The end time of the public hearing. */
    private LocalTime endTime;

    /** --- Constructor --- */

    public PublicHearing(PublicHearingId publicHearingId, LocalDate date, String text) {
        this.id = publicHearingId;
        this.date = date;
        this.text = text;
        this.year = this.date.getYear();
        this.session = SessionYear.of(this.getYear());
    }

    /**
     * Groups public hearing text into pages, which are lists of lines..
     * @param fullText of the hearing.
     */
    public static List<List<String>> getPages(String fullText) {
        fullText = fullText.replaceAll("\r\n", "\n");
        return Splitter.on("\f").splitToList(fullText).stream().map(PublicHearing::getLines)
                .filter(page -> !page.isEmpty()).collect(Collectors.toList());
    }

    private static List<String> getLines(String page) {
        List<String> ret = Splitter.on("\n").trimResults().splitToList(page)
                .stream().dropWhile(String::isEmpty).collect(Collectors.toList());
        // Drops empty Strings from the end of the list as well.
        Collections.reverse(ret);
        ret = ret.stream().dropWhile(String::isEmpty).collect(Collectors.toList());
        Collections.reverse(ret);
        return ret;
    }

    /** --- Basic Getters/Setters --- */

    public PublicHearingId getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<PublicHearingCommittee> getCommittees() {
        return committees;
    }

    public void setCommittees(List<PublicHearingCommittee> committees) {
        this.committees = committees;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}
