package gov.nysenate.openleg.api.legislation.agenda.view;

import gov.nysenate.openleg.api.ListView;
import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.api.legislation.agenda.AgendaBillInfo;
import gov.nysenate.openleg.legislation.agenda.Agenda;
import gov.nysenate.openleg.legislation.agenda.AgendaInfoCommittee;
import gov.nysenate.openleg.legislation.agenda.AgendaVoteCommittee;
import gov.nysenate.openleg.legislation.agenda.CommitteeAgendaAddendumId;
import gov.nysenate.openleg.legislation.bill.Version;
import gov.nysenate.openleg.legislation.committee.CommitteeId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record AgendaCommView(CommitteeId committeeId, ListView<AgendaCommAddendumView> addenda) implements ViewObject {

    public AgendaCommView(CommitteeId committeeId, Agenda agenda, Map<AgendaInfoCommittee, List<AgendaBillInfo>> infoCommMap) {
        this(committeeId, ListView.of(getAddendaList(agenda, committeeId, infoCommMap)));
    }

    private static List<AgendaCommAddendumView> getAddendaList(Agenda agenda,
                                                               CommitteeId committeeId,
                                                               Map<AgendaInfoCommittee, List<AgendaBillInfo>> infoCommMap) {
        List<AgendaCommAddendumView> addendaList = new ArrayList<>();
        if (agenda == null) {
            return addendaList;
        }
        for (String addendumId : agenda.getAddenda()) {
            CommitteeAgendaAddendumId id = new CommitteeAgendaAddendumId(agenda.getId(), committeeId, Version.of(addendumId));
            AgendaInfoCommittee infoComm = null;
            AgendaVoteCommittee voteComm = null;
            LocalDateTime modifiedDateTime = null;
            var infoAddendum = agenda.getAgendaInfoAddendum(addendumId);
            if (infoAddendum != null) {
                infoComm = infoAddendum.getCommitteeInfoMap().get(committeeId);
                modifiedDateTime = infoAddendum.getModifiedDateTime();
            }
            var voteAddendum = agenda.getAgendaVoteAddendum(addendumId);
            if (voteAddendum != null) {
                voteComm = voteAddendum.getCommitteeVoteMap().get(committeeId);
            }
            if (infoComm != null) {
                addendaList.add(new AgendaCommAddendumView(id, modifiedDateTime, infoComm,
                        voteComm, infoCommMap.get(infoComm)));
            }
        }
        return addendaList;
    }

    @Override
    public String getViewType() {
        return "agenda-committee";
    }
}
