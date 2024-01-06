package gov.nysenate.openleg.api.legislation.agenda.view;

import gov.nysenate.openleg.api.ListView;
import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.api.legislation.agenda.AgendaBillInfo;
import gov.nysenate.openleg.legislation.agenda.AgendaId;
import gov.nysenate.openleg.legislation.agenda.AgendaInfoCommittee;
import gov.nysenate.openleg.legislation.agenda.AgendaVoteCommittee;
import gov.nysenate.openleg.legislation.agenda.CommitteeAgendaAddendumId;
import gov.nysenate.openleg.legislation.bill.Version;
import gov.nysenate.openleg.legislation.committee.CommitteeId;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A View for a single Agenda Committee addendum, including the list of bills addressed.
 */
public record AgendaCommAddendumView(AgendaId agendaId, String addendumId, CommitteeId committeeId,
                                     LocalDateTime modifiedDateTime, boolean hasVotes,
                                     AgendaMeetingView meeting, ListView<AgendaItemView> bills,
                                     AgendaVoteView voteInfo) implements ViewObject {

    public AgendaCommAddendumView(CommitteeAgendaAddendumId id, LocalDateTime modDateTime,
                                  AgendaInfoCommittee infoComm, AgendaVoteCommittee voteComm,
                                  List<AgendaBillInfo> agendaBillInfos) {
        this(id.getAgendaId(), id.getAddendum().toString(), id.getCommitteeId(), modDateTime,
                voteComm != null, new AgendaMeetingView(infoComm),
                ListView.of(listViews(agendaBillInfos)),
                voteComm == null ? null : new AgendaVoteView(voteComm));
    }

    public CommitteeAgendaAddendumId getCommitteeAgendaAddendumId() {
        return new CommitteeAgendaAddendumId(agendaId, committeeId, Version.of(addendumId));
    }

    private static List<AgendaItemView> listViews(List<AgendaBillInfo> agendaBillInfos) {
        if (agendaBillInfos == null) {
            return List.of();
        }
        return agendaBillInfos.stream().map(info ->
                new AgendaItemView(info.billId(), info.billInfo(), info.message())).toList();
    }

    @Override
    public String getViewType() {
        return "agenda-addendum";
    }
}
