package gov.nysenate.openleg.api.legislation.agenda.view;

import gov.nysenate.openleg.api.ListView;
import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.agenda.AgendaId;
import gov.nysenate.openleg.legislation.agenda.AgendaInfoCommittee;
import gov.nysenate.openleg.legislation.agenda.AgendaVoteCommittee;
import gov.nysenate.openleg.legislation.agenda.CommitteeAgendaAddendumId;
import gov.nysenate.openleg.legislation.bill.Version;
import gov.nysenate.openleg.legislation.bill.dao.service.BillDataService;
import gov.nysenate.openleg.legislation.committee.CommitteeId;

import java.time.LocalDateTime;

public record AgendaCommAddendumView(AgendaId agendaId, String addendumId, CommitteeId committeeId,
                                     LocalDateTime modifiedDateTime, AgendaMeetingView meeting,
                                     ListView<AgendaItemView> bills,
                                     boolean hasVotes, AgendaVoteView voteInfo)
        implements ViewObject {

    public AgendaCommAddendumView(CommitteeAgendaAddendumId id, LocalDateTime modDateTime, AgendaInfoCommittee infoComm,
                                  AgendaVoteCommittee voteComm, BillDataService billDataService) {
        this(id.getAgendaId(), id.getAddendum().toString(), id.getCommitteeId(), modDateTime,
                new AgendaMeetingView(infoComm.getChair(), infoComm.getLocation(), infoComm.getMeetingDateTime(), infoComm.getNotes()),
                ListView.of(infoComm.getItems().stream().map(i -> new AgendaItemView(i, billDataService)).toList()),
                voteComm != null, voteComm == null ? null : new AgendaVoteView(voteComm));
    }

    public CommitteeAgendaAddendumId getCommitteeAgendaAddendumId() {
        return new CommitteeAgendaAddendumId(this.agendaId, this.committeeId, Version.of(this.addendumId));
    }

    @Override
    public String getViewType() {
        return "agenda-addendum";
    }
}
