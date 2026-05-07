package gov.nysenate.openleg.api.legislation.bill.view;

import gov.nysenate.openleg.api.ListView;
import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.api.legislation.transcripts.session.view.TranscriptIdView;
import gov.nysenate.openleg.legislation.transcripts.session.Position;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptId;

import java.util.Collection;

public record TranscriptMentionView(TranscriptIdView id, ListView<PositionView> positions) implements ViewObject {
    public TranscriptMentionView(TranscriptId id, Collection<Position> positions) {
        this(new TranscriptIdView(id), ListView.of(positions.stream().map(PositionView::new).toList()));
    }

    @Override
    public String getViewType() {
        return "transcript-mention";
    }
}
