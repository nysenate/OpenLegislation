package gov.nysenate.openleg.client.view.source;

import gov.nysenate.openleg.client.view.base.ViewObject;

import java.time.LocalDateTime;

public class SourceFileView extends SourceIdView implements ViewObject
{
    protected String text;

    public SourceFileView(String sourceType, String sourceId, LocalDateTime sourceDateTime, String text) {
        super(sourceType, sourceId, sourceDateTime);
        this.text = text;
    }

    @Override
    public String getViewType() {
        return "source-file";
    }

    public String getText() {
        return text;
    }
}
