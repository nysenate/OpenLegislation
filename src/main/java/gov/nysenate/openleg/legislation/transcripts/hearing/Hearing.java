package gov.nysenate.openleg.legislation.transcripts.hearing;

import gov.nysenate.openleg.legislation.BaseLegislativeContent;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Set;

public class Hearing extends BaseLegislativeContent {
    // Uniquely identifies a hearing, but may not be available if the hearing is not in the database yet.
    private HearingId id;
    private final String filename, text, title, address;
    private final LocalDate date;
    private final LocalTime startTime, endTime;

    /** The {@link HearingHost}s holding this Hearing. */
    private Set<HearingHost> hosts;

    public Hearing(String filename, String text, String title, String address,
                   LocalDate date, LocalTime startTime, LocalTime endTime) {
        super(date.getYear());
        this.filename = filename;
        this.date = date;
        this.text = text;
        this.title = title;
        this.address = address;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hearing that = (Hearing) o;
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

    public void setId(HearingId id) {
        this.id = id;
    }

    public HearingId getId() {
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
