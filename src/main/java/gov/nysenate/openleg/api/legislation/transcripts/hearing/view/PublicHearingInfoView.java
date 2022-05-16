package gov.nysenate.openleg.api.legislation.transcripts.hearing.view;

import gov.nysenate.openleg.legislation.transcripts.hearing.HearingHost;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PublicHearingInfoView extends PublicHearingIdView {
    /** Time format to match our Elasticsearch mappings.*/
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final String title, address, startTime, endTime;
    private final LocalDate date;
    private final List<HearingHost> committees;

    public PublicHearingInfoView(PublicHearing publicHearing) {
        super(publicHearing.getId(), publicHearing.getFilename());
        this.title = publicHearing.getTitle();
        this.date = publicHearing.getDate();
        this.committees = new ArrayList<>(publicHearing.getHosts());
        this.address = publicHearing.getAddress();
        this.startTime = formatOrNull(publicHearing.getStartTime());
        this.endTime = formatOrNull(publicHearing.getEndTime());
    }

    private static String formatOrNull(LocalTime ldt) {
        return ldt == null ? null : ldt.format(TIME_FORMAT);
    }

    @Override
    public String getViewType() {
        return "hearing-info";
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getDate() {
        return date;
    }

    public List<HearingHost> getCommittees() {
        return committees;
    }

    public String getAddress() {
        return address;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }
}
