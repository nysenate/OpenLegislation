package gov.nysenate.openleg.api.legislation.bill.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.transcripts.session.Position;

public record PositionView(int pageNumStart, int lineNumStart) implements ViewObject {
    public PositionView(Position position) {
        this(position.pageNumStart(), position.lineNumStart());
    }

    @Override
    public String getViewType() {
        return "position";
    }
}