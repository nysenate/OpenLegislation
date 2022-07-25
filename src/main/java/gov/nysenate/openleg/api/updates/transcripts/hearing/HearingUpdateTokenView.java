package gov.nysenate.openleg.api.updates.transcripts.hearing;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.api.legislation.transcripts.hearing.view.HearingIdView;
import gov.nysenate.openleg.updates.transcripts.hearing.HearingUpdateToken;

import java.time.LocalDateTime;

public record HearingUpdateTokenView(HearingIdView hearingId, LocalDateTime dateTime) implements ViewObject {
    public HearingUpdateTokenView(HearingUpdateToken token, String filename) {
        this(new HearingIdView(token.hearingId(), filename), token.dateTime());
    }

    @Override
    public String getViewType() {
        return "public_hearing-update-token";
    }
}
