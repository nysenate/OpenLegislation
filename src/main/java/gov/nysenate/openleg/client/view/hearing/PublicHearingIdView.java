package gov.nysenate.openleg.client.view.hearing;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.hearing.PublicHearingId;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

public class PublicHearingIdView implements ViewObject
{
    private String title;
    private LocalDateTime dateTime;

    public PublicHearingIdView(PublicHearingId hearingId) {
        this.title = hearingId.getTitle();
        this.dateTime = hearingId.getDateTime();
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    @Override
    public String getViewType() {
        return "hearing-id";
    }
}
