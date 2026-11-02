package gov.nysenate.openleg.api.legislation.transcripts.hearing;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.api.legislation.transcripts.hearing.view.HearingIdView;
import gov.nysenate.openleg.api.legislation.transcripts.hearing.view.HearingInfoView;
import gov.nysenate.openleg.api.legislation.transcripts.hearing.view.HearingView;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingId;
import gov.nysenate.openleg.legislation.transcripts.hearing.dao.HearingDataService;

/** Builds the hearing views for the listing and search APIs. */
final class HearingViews {
    private HearingViews() {}

    /**
     * The full view if full is set, otherwise the info view if summary is set, otherwise the id view.
     * The id view loads only the filename, not the whole hearing.
     */
    static ViewObject of(HearingDataService hearingData, HearingId id, boolean summary, boolean full) {
        if (full) {
            return new HearingView(hearingData.getHearing(id));
        }
        if (summary) {
            return new HearingInfoView(hearingData.getHearing(id));
        }
        return new HearingIdView(id, hearingData.getFilename(id));
    }
}
