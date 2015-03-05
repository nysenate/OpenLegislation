package gov.nysenate.openleg.client.view.agenda;

import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.agenda.AgendaInfoCommittee;
import gov.nysenate.openleg.model.agenda.AgendaVoteCommittee;
import gov.nysenate.openleg.service.bill.data.BillDataService;

import java.time.LocalDateTime;

import static java.util.stream.Collectors.toList;

public class AgendaCommAddendumView implements ViewObject
{
    private String addendumId;
    private LocalDateTime modifiedDateTime;
    private boolean hasVotes = false;
    private AgendaMeetingView meeting;
    private ListView<AgendaItemView> bills;
    private AgendaVoteView voteInfo;

    public AgendaCommAddendumView(String addendumId, LocalDateTime modDateTime, AgendaInfoCommittee infoComm,
                                  AgendaVoteCommittee voteComm, BillDataService billDataService) {
        this.addendumId = addendumId;
        if (infoComm != null) {
            this.modifiedDateTime = modDateTime;
            this.meeting = new AgendaMeetingView(infoComm.getChair(), infoComm.getLocation(),
                                                 infoComm.getMeetingDateTime(), infoComm.getNotes());
            this.bills = ListView.of(infoComm.getItems().stream()
                .map(i -> new AgendaItemView(i, billDataService))
                .collect(toList()));
            this.hasVotes = voteComm != null;
            if (this.hasVotes) {
                this.voteInfo = new AgendaVoteView(voteComm);
            }
        }
    }

    public String getAddendumId() {
        return addendumId;
    }

    public LocalDateTime getModifiedDateTime() {
        return modifiedDateTime;
    }

    public boolean isHasVotes() {
        return hasVotes;
    }

    public AgendaMeetingView getMeeting() {
        return meeting;
    }

    public ListView<AgendaItemView> getBills() {
        return bills;
    }

    public AgendaVoteView getVoteInfo() {
        return voteInfo;
    }

    @Override
    public String getViewType() {
        return "agenda-addendum";
    }
}
