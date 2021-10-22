package gov.nysenate.openleg.api.legislation.agenda.view;

import gov.nysenate.openleg.api.ListView;
import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.agenda.Agenda;
import gov.nysenate.openleg.legislation.agenda.AgendaInfoCommittee;
import gov.nysenate.openleg.legislation.agenda.AgendaVoteCommittee;
import gov.nysenate.openleg.legislation.agenda.CommitteeAgendaAddendumId;
import gov.nysenate.openleg.legislation.bill.Version;
import gov.nysenate.openleg.legislation.bill.dao.service.BillDataService;
import gov.nysenate.openleg.legislation.committee.CommitteeId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record AgendaCommView(CommitteeId committeeId, ListView<AgendaCommAddendumView> addenda) implements ViewObject {

    public AgendaCommView(CommitteeId committeeId, Agenda agenda, BillDataService billDataService) {
        this(committeeId, ListView.of(getAddendaList(agenda, committeeId, billDataService)));
    }

    // TODO: BillDataService should not really be used like this.
    private static List<AgendaCommAddendumView> getAddendaList(Agenda agenda, CommitteeId committeeId, BillDataService billDataService) {
        List<AgendaCommAddendumView> addendaList = new ArrayList<>();
        if (agenda == null)
            return addendaList;
        for (String addendumId : agenda.getAddenda()) {
            CommitteeAgendaAddendumId id = new CommitteeAgendaAddendumId(agenda.getId(), committeeId, Version.of(addendumId));
            AgendaInfoCommittee infoComm = null;
            AgendaVoteCommittee voteComm = null;
            LocalDateTime modifiedDateTime = null;
            if (agenda.getAgendaInfoAddenda().containsKey(addendumId) && agenda.getAgendaInfoAddendum(addendumId).getCommitteeInfoMap().containsKey(committeeId)) {
                infoComm = agenda.getAgendaInfoAddendum(addendumId).getCommitteeInfoMap().get(committeeId);
                modifiedDateTime = agenda.getAgendaInfoAddendum(addendumId).getModifiedDateTime();
            }
            if (agenda.getAgendaVoteAddenda().containsKey(addendumId) &&
                    agenda.getAgendaVoteAddendum(addendumId).getCommitteeVoteMap().containsKey(committeeId)) {
                voteComm = agenda.getAgendaVoteAddendum(addendumId).getCommitteeVoteMap().get(committeeId);
            }
            if (infoComm != null)
                addendaList.add(new AgendaCommAddendumView(id, modifiedDateTime, infoComm, voteComm, billDataService));
        }
        return addendaList;
    }

    @Override
    public String getViewType() {
        return "agenda-committee";
    }
}
