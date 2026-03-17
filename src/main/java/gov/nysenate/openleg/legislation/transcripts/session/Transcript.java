package gov.nysenate.openleg.legislation.transcripts.session;

import gov.nysenate.openleg.legislation.BaseLegislativeContent;
import gov.nysenate.openleg.legislation.bill.BaseBillId;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;

/**
 * A Transcript is a written record of a Senate session.
 */
public class Transcript extends BaseLegislativeContent {
    private final TranscriptId id;
    private final DayType dayType;
    private final String location, text, filename;
    private final LinkedHashSet<BaseBillId> linkedBills;

    /** --- Constructors --- */

    public Transcript(TranscriptId id, DayType dayType, String filename, String location, String text) {
        this(id, dayType, filename, location, text, new LinkedHashSet<>());
    }

    public Transcript(TranscriptId id, DayType dayType, String filename, String location, String text, LinkedHashSet<BaseBillId> linkedBills) {
        super(id.dateTime().getYear());
        this.id = id;
        if (dayType == null) {
            throw new IllegalArgumentException("dayType cannot be null");
        }
        this.dayType = dayType;
        this.location = location;
        this.text =  text;
        this.filename = filename;
        this.linkedBills = linkedBills;
    }

    public TranscriptId getId() {
        return id;
    }

    public LocalDateTime getDateTime() {
        return id.dateTime();
    }

    public String getSessionType() {
        return id.sessionType().toString();
    }

    @Nonnull
    public DayType getDayType() {
        return dayType;
    }

    public String getLocation() {
        return location;
    }

    /**
     * Returns the text as it is stored in the database.
     */
    public String getUnformattedText() {
        return text;
    }

    /**
     * Returns the text with all links removed
     */
    public String getPlainText() {
        return text.replaceAll("</?a[^>]*>", "");
    }

    /**
     * Returns the text with links matching source url
     */
    public String getLinkedText(String linkBase) {
        return text.replaceAll("<a href=\"", "$0" + linkBase);
    }

    public String getFilename() {
        return filename;
    }

    public LinkedHashSet<BaseBillId> getLinkedBills() {
        return linkedBills;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transcript that = (Transcript) o;
        return Objects.equals(id, that.id) && dayType == that.dayType &&
                Objects.equals(location, that.location) && Objects.equals(text, that.text) &&
                Objects.equals(filename, that.filename) && Objects.equals(linkedBills, that.linkedBills);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, dayType, location, text, filename, linkedBills);
    }
}
