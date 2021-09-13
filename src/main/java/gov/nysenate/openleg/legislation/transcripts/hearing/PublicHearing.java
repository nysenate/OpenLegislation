package gov.nysenate.openleg.legislation.transcripts.hearing;

import com.google.common.base.Splitter;
import gov.nysenate.openleg.legislation.BaseLegislativeContent;
import gov.nysenate.openleg.legislation.SessionYear;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class PublicHearing extends BaseLegislativeContent {
    // Uniquely identifies a hearing, but may not be available if the hearing is not in the database yet.
    private PublicHearingId id;
    private final String filename, text, title, address;
    private final LocalDate date;
    private final LocalTime startTime, endTime;

    /** The {@link HearingHost}s holding this PublicHearing. */
    private Set<HearingHost> hosts;

    public PublicHearing(String filename, String text, String title, String address,
                         LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.filename = filename;
        this.date = date;
        this.text = text;
        this.title = title;
        this.address = address;
        this.startTime = startTime;
        this.endTime = endTime;
        this.year = this.date.getYear();
        this.session = SessionYear.of(this.getYear());
    }

    public static boolean isWrongFormat(List<List<String>> pages) {
        return pages.get(1).stream().anyMatch(str -> str.contains("Geneva Worldwide, Inc."));
    }

    /**
     * Groups public hearing text into pages, which are lists of lines.
     * @param fullText of the hearing.
     */
    public static List<List<String>> getPages(String fullText) {
        fullText = fullText.replaceAll("\r\n", "\n");
        return Splitter.on("\f").splitToList(fullText).stream().map(PublicHearing::getLines)
                .filter(page -> !page.isEmpty()).collect(Collectors.toList());
    }

    private static List<String> getLines(String page) {
        List<String> ret = Splitter.on("\n").splitToList(page)
                .stream().dropWhile(String::isEmpty).collect(Collectors.toList());
        // Drops empty Strings from the end of the list as well.
        Collections.reverse(ret);
        ret = ret.stream().dropWhile(String::isEmpty).collect(Collectors.toList());
        Collections.reverse(ret);
        return ret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PublicHearing that = (PublicHearing) o;
        return Objects.equals(id, that.id) && Objects.equals(filename, that.filename) &&
                Objects.equals(text, that.text) && Objects.equals(title, that.title) &&
                Objects.equals(address, that.address) && Objects.equals(date, that.date) &&
                Objects.equals(startTime, that.startTime) && Objects.equals(endTime, that.endTime) &&
                Objects.equals(hosts, that.hosts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, filename, text, title, address, date, startTime, endTime, hosts);
    }

    public void setId(PublicHearingId id) {
        this.id = id;
    }

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

    public String getAddress() {
        return address;
    }

    public Set<HearingHost> getHosts() {
        return hosts;
    }

    public void setHosts(Set<HearingHost> hosts) {
        this.hosts = hosts;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public String getFilename() {
        return filename;
    }
}
