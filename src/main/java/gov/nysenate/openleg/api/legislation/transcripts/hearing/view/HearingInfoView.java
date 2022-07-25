package gov.nysenate.openleg.api.legislation.transcripts.hearing.view;

import gov.nysenate.openleg.legislation.transcripts.hearing.Hearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingHost;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HearingInfoView extends HearingIdView {
    /** Time format to match our Elasticsearch mappings.*/
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final String title, address, startTime, endTime;
    private final LocalDate date;
    private final List<HearingHost> committees;

    public HearingInfoView(Hearing hearing) {
        super(hearing.getId(), hearing.getFilename());
        this.title = hearing.getTitle();
        this.date = hearing.getDate();
        this.committees = new ArrayList<>(hearing.getHosts());
        this.address = hearing.getAddress();
        this.startTime = formatOrNull(hearing.getStartTime());
        this.endTime = formatOrNull(hearing.getEndTime());
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
