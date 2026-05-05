package gov.nysenate.openleg.api.legislation.transcripts.session.view;

import gov.nysenate.openleg.api.ListView;
import gov.nysenate.openleg.legislation.transcripts.session.BillMention;
import gov.nysenate.openleg.legislation.transcripts.session.Transcript;

public class TranscriptInfoView extends TranscriptIdView {
    private final String location;
    private final String dayType;
    private final ListView<BillMention> linkedBills;

    public TranscriptInfoView(Transcript transcript) {
        super(transcript.getId());
        this.location = transcript.getLocation();
        this.dayType = String.valueOf(transcript.getDayType());
        this.linkedBills = ListView.of(transcript.getLinkedBills());
    }

    public String getLocation() {
        return location;
    }

    public String getDayType() {
        return dayType;
    }

    public ListView<BillMention> getLinkedBills() {
        return linkedBills;
    }

    @Override
    public String getViewType() {
        return "transcript-info";
    }
}
