package gov.nysenate.openleg.api.legislation.transcripts.session.view;

import gov.nysenate.openleg.api.ListView;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.legislation.transcripts.session.Transcript;

public class TranscriptInfoView extends TranscriptIdView {
    private final String location;
    private final String dayType;
    private final ListView<String> linkedBills;

    public TranscriptInfoView(Transcript transcript) {
        super(transcript.getId());
        this.location = transcript.getLocation();
        this.dayType = String.valueOf(transcript.getDayType());
        this.linkedBills = ListView.ofStringList(transcript.getLinkedBills().stream().map(BillId::toString).toList());
    }

    public String getLocation() {
        return location;
    }

    public String getDayType() {
        return dayType;
    }

    public ListView<String> getLinkedBills() { return linkedBills; }

    @Override
    public String getViewType() {
        return "transcript-info";
    }
}
