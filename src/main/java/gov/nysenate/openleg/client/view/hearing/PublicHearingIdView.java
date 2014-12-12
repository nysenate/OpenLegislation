package gov.nysenate.openleg.client.view.hearing;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.hearing.PublicHearing;
import gov.nysenate.openleg.model.hearing.PublicHearingId;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PublicHearingIdView implements ViewObject
{
    private String filename;

    public PublicHearingIdView(PublicHearingId publicHearingId) {
        this.filename = publicHearingId.getFileName();
    }

    public String getFilename() {
        return filename;
    }

    @Override
    public String getViewType() {
        return "hearing-id";
    }
}
