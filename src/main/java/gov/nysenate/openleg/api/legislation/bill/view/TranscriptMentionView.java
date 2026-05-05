package gov.nysenate.openleg.api.legislation.bill.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.api.legislation.transcripts.session.view.TranscriptIdView;
import gov.nysenate.openleg.legislation.transcripts.session.Position;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptId;

public record TranscriptMentionView(TranscriptIdView id, Position position) implements ViewObject {
    public TranscriptMentionView(TranscriptId id, Position position) {
        this(new TranscriptIdView(id), position);
    }

    @Override
    public String getViewType() {
        return "transcript-mention";
    }
}
