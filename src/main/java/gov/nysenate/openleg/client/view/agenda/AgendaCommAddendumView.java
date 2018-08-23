package gov.nysenate.openleg.client.view.agenda;

import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.agenda.AgendaId;
import gov.nysenate.openleg.model.agenda.AgendaInfoCommittee;
import gov.nysenate.openleg.model.agenda.AgendaVoteCommittee;
import gov.nysenate.openleg.model.agenda.CommitteeAgendaAddendumId;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.entity.CommitteeId;
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
    private AgendaId agendaId;
    private CommitteeId committeeId;

    public AgendaCommAddendumView(CommitteeAgendaAddendumId id, LocalDateTime modDateTime, AgendaInfoCommittee infoComm,
                                  AgendaVoteCommittee voteComm, BillDataService billDataService) {
        if (id != null) {
            this.agendaId = id.getAgendaId();
            this.addendumId = id.getAddendum().getValue();
            this.committeeId = id.getCommitteeId();
        }
        if (infoComm != null) {
            this.modifiedDateTime = modDateTime;
            this.meeting = new AgendaMeetingView(infoComm.getChair(), infoComm.getLocation(),
                                                 infoComm.getMeetingDateTime(), infoComm.getNotes());
            this.bills = ListView.of(infoComm.getItems().stream()
                .map(i -> new AgendaItemView(i, billDataService))
                .collect(toList()));
        }
        if (voteComm != null) {
            this.hasVotes = true;
            this.voteInfo = new AgendaVoteView(voteComm);
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

    public AgendaId getAgendaId() {
        return agendaId;
    }

    public void setAgendaId(AgendaId agendaId) {
        this.agendaId = agendaId;
    }

    public CommitteeId getCommitteeId() {
        return committeeId;
    }

    public void setCommitteeId(CommitteeId committeeId) {
        this.committeeId = committeeId;
    }

    public CommitteeAgendaAddendumId getCommitteeAgendaAddendumId() {
        return new CommitteeAgendaAddendumId(this.agendaId, this.committeeId, Version.of(this.addendumId) );
    }

    @Override
    public String getViewType() {
        return "agenda-addendum";
    }
}
